/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2009 Dan Cowan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details (www.gnu.org/licenses)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package uk.co.dancowan.robots.srv.ui.views.camera;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.camera.Camera;
import uk.co.dancowan.robots.srv.hal.camera.CameraListener;
import uk.co.dancowan.robots.srv.hal.camera.YUV;
import uk.co.dancowan.robots.srv.hal.commands.camera.SetResolutionCmd;
import uk.co.dancowan.robots.srv.ui.panels.ColourBinPanel;
import uk.co.dancowan.robots.srv.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.srv.ui.views.featuredetector.actions.RefreshBinsAction;
import uk.co.dancowan.robots.srv.ui.views.featuredetector.actions.RefreshBlobsAction;
import uk.co.dancowan.robots.srv.ui.views.featuredetector.actions.RefreshPatternsAction;
import uk.co.dancowan.robots.ui.utils.ColourManager;
import uk.co.dancowan.robots.ui.views.ScrolledView;

/**
 * Instance of an Eclipse <code>ViewPart</code> to enable vision command
 * and camera output display in the application.
 * 
 * <p>Extends <code>ScrollableView</code> to enable scroll bars at a
 * preset minimum size.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CameraView extends ScrolledView implements IPropertyChangeListener, CameraListener
{
	public static final String ID = "uk.co.dancowan.robots.srv.cameraView";

	private static final Point MIN_SIZE = new Point(600, 500);

	private final ColourBinPanel mColourBinPanel;
	private final CameraCanvas mCameraCanvas;

	private Composite mColourBinComposite;
	private Text mFPSText;
	private Text mFrameText;
	private Button mLastSize;

	private boolean mGrabberLock;
	private int mSampleX;
	private int mSampleY;
	private boolean mReady;
	private boolean mPoll;
	private boolean mInit;
	private Color mColour;

	private String mOutputFormat;
	private String mPath;

	/**
	 * C'tor.
	 */
	public CameraView()
	{
		mCameraCanvas = new CameraCanvas(this);
		mColourBinPanel = new ColourBinPanel(mCameraCanvas);

		mReady = false;
		mGrabberLock = false;

		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);
		initFromPrefs();

		SrvHal.getCamera().setConsumer(mCameraCanvas);
	}

	/**
	 * Creates and lays out the widgets for this component on the passed
	 * <code>Composite</code>.
	 * 
	 * <p>Although essentially based on SWT widgets and methodology this class
	 * uses an AWT <code>Image</code> placed on an AWT <code>Canvas</code> instance
	 * to assist with rendering the streamed image from the SRV1 Camera.</p>
	 * 
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getPartControl(Composite))
	 * @param parent, the Composite to add widgets to
	 * @return Composite the container of view's controls
	 */
	@Override
	public Control getPartControl(Composite parent)
	{
		final Composite part = new Composite(parent, SWT.BORDER);
		part.setLayout(new FormLayout());

		// This SWT Composite embeds the AWT Frame, and Canvas widgets
		final Composite awtComposite = new Composite(part, SWT.EMBEDDED);
		awtComposite.setLayout(new FillLayout());
		awtComposite.setSize(320, 256);
		awtComposite.addControlListener(new ControlAdapter()
		{
			@Override
			public void controlResized(ControlEvent e)
			{
				if (e.getSource() instanceof Composite)
				{
					Composite c = (Composite) e.getSource();
					mCameraCanvas.setDisplaySize(c.getSize().x - 20, c.getSize().y - 20);
					mCameraCanvas.paintImage();
				}
			}
		});
		final Frame awtFrame = SWT_AWT.new_Frame(awtComposite);
		awtFrame.setBackground(mColour);
		awtFrame.setLayout(new BorderLayout(3, 3));
		awtFrame.add("Center", mCameraCanvas);

		final Composite pollComposite = getPollComposite(part);
		final Composite locationComposite = getLocationComposite(part);
		final Composite actionComposite = getActionComposite(part);
		mColourBinComposite = mColourBinPanel.getPanel(part);

		// Image Canvas/Frame layout
		FormData data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(100, -180);
		awtComposite.setLayoutData(data);

		// Poll Composite layout
		data = new FormData();
		data.top = new FormAttachment(awtComposite, 5, SWT.BOTTOM);
		data.left = new FormAttachment(0, 5);
		//data.bottom = new FormAttachment(awtComposite, 42, SWT.BOTTOM);
		data.right = new FormAttachment(locationComposite, 0, SWT.RIGHT);
		pollComposite.setLayoutData(data);

		// Image size Composite layout
		data = new FormData();
		data.top = new FormAttachment(awtComposite, 5, SWT.BOTTOM);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(pollComposite, 0, SWT.BOTTOM);
		actionComposite.setLayoutData(data);

		// Location data Composite layout
		data = new FormData();
		data.top = new FormAttachment(pollComposite, 5, SWT.BOTTOM);
		data.left = new FormAttachment(0, 5);
		data.bottom = new FormAttachment(100, -5);
		locationComposite.setLayoutData(data);

		// Colour bin data Composite layout
		data = new FormData();
		data.top = new FormAttachment(actionComposite, 5, SWT.BOTTOM);
		data.left = new FormAttachment(actionComposite, 0, SWT.LEFT);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(100, -5);
		mColourBinComposite.setLayoutData(data);

		// Add menu and tool-bar
		createPopupMenu(); //AWT popup menu
		createToolbar();

		part.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		doSetReady();
		return part;
	}

	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getMinSize()
	 */
	@Override
	public Point getMinSize()
	{
		return MIN_SIZE;
	}

	/**
	 * Disposes of <code>ColourBin</code> widgets and then calls super.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		mColourBinPanel.dispose();
		super.dispose();
		mReady = false;
	}

	/**
	 * Call-back from the <code>CameraCanvas</code> when a new AWT image has
	 * been received. Allows this class to refresh widgets as necessary.
	 * 
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraListener#newImage()
	 */
	@Override
	public void newImage()
	{
		if (mReady)
		{
			// Called from Camera threads so async into SWT thread
			Display.getDefault().asyncExec(new Runnable()
			{
				/**
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run()
				{
					Camera cam = SrvHal.getCamera();
					mFPSText.setText(formatFPS(cam.getFPS()));
					mFrameText.setText(new Long(cam.getFrameTime()).toString());
				}
			});
		}
	}

	private String formatFPS(double fps)
	{
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(1);
		return nf.format(fps);
	}

	/**
	 * Implementation of IPropertyChangeListener interface so widgets can respond
	 * to changes on a preference page.
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (PreferenceConstants.CAMERA_BACKGROUND_COLOUR.equals(event.getProperty()))
		{
			RGB rgb = (RGB) event.getNewValue();
			mColour = new Color(rgb.red, rgb.green, rgb.blue);
			mCameraCanvas.setBackground(mColour);
			mCameraCanvas.repaint(10);
		}
		else if (PreferenceConstants.CAMERA_DEFAULT_PATH.equals(event.getProperty()))
		{
			mPath = (String) event.getNewValue();
		}
		else if (PreferenceConstants.CAMERA_OUTPUT_FORMAT.equals(event.getProperty()))
		{
			mOutputFormat = (String) event.getNewValue();
		}
	}

	/*
	 * Update the path in the preferences if it was changed
	 */
	private void updatePath(String path)
	{
		int index = path.lastIndexOf(System.getProperty("file.separator"));
		if (index >= 0)
		{
			path = path.substring(0, index);
			if ( ! mPath.equals(path))
			{
				mPath = path;
				IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
				store.setValue(PreferenceConstants.CAMERA_DEFAULT_PATH, mPath);
			}
		}
	}

	/*
	 * Initialise variables based on preference store
	 */
	private void initFromPrefs()
	{
		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		mPoll = store.getBoolean(PreferenceConstants.CAMERA_POLL_ON_CONNECT);
		mInit = store.getBoolean(PreferenceConstants.CAMERA_INIT_ON_CONNECT);
		mPath = store.getString(PreferenceConstants.CAMERA_DEFAULT_PATH);
		mOutputFormat = store.getString(PreferenceConstants.CAMERA_OUTPUT_FORMAT);
		RGB rgb = PreferenceConverter.getColor(store, PreferenceConstants.CAMERA_BACKGROUND_COLOUR);
		mColour = new Color(rgb.red, rgb.green, rgb.blue);
	}

	/*
	 * Creates the Composite and listeners for image polling
	 */
	private Composite getPollComposite(Composite parent)
	{
		final Composite pollComposite = new Group(parent, SWT.NONE);
		pollComposite.setLayout(new GridLayout(3, true));

		final Button poll = new Button(pollComposite, SWT.TOGGLE);
		poll.setText("Poll");
		poll.setToolTipText("Start the camera polling for images");
		poll.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (poll.getSelection())
				{
					SrvHal.getCamera().startPolling();
					poll.setText("Stop");
					poll.setToolTipText("Stop the camera from polling");
				}
				else
				{
					SrvHal.getCamera().stopPolling();
					poll.setText("Poll");
					poll.setToolTipText("Start the camera polling for images");
				}
			}
		});
		if (mPoll)
		{
			poll.setSelection(true);
			SrvHal.getCamera().startPolling();
			poll.setText("Stop");
			poll.setToolTipText("Stop the camera from polling");
		}
		poll.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Text pollDelay = new Text(pollComposite, SWT.BORDER | SWT.RIGHT);
		pollDelay.setText(Integer.toString(SrvHal.getCamera().getPollDelay()));
		pollDelay.setToolTipText("Poll interval in 10ms units");
		pollDelay.addModifyListener(new ModifyListener()
		{
			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e)
			{
				try
				{
					Integer.parseInt(pollDelay.getText());
					pollDelay.setForeground(ColourManager.getColour(SWT.COLOR_BLACK));
				}
				catch (NumberFormatException nfe)
				{
					pollDelay.setForeground(ColourManager.getColour(ColourManager.ERROR_COLOUR));
				}
			}
		});
		pollDelay.addFocusListener(new FocusAdapter()
		{
			/**
			 * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
			 */
			@Override
			public void focusLost(FocusEvent e)
			{
				try
				{
					int delay = Integer.parseInt(pollDelay.getText());
					SrvHal.getCamera().setPollDelay(delay);
				}
				catch (NumberFormatException nfe)
				{
					// NOP
				}
			}
		});
		pollDelay.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				try
				{
					int delay = Integer.parseInt(pollDelay.getText());
					SrvHal.getCamera().setPollDelay(delay);
				}
				catch (NumberFormatException nfe)
				{
					// NOP
				}				
			}
		});
		pollDelay.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button save = new Button(pollComposite, SWT.PUSH);
		save.setText("Save");
		save.setToolTipText("Save a snapshot of the video stream");
		save.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// stop the camera for a mo
				Camera camera = SrvHal.getCamera();
				boolean wasPolling = camera.isPolling();
				if (wasPolling)
					camera.stopPolling();
				// make sure it has stopped
				while (camera.isPolling())
				{
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e1)
					{
						// NOP
					}
				}
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
				dialog.setText("Save Image");
				dialog.setFilterPath(mPath);
				dialog.setFilterExtensions(new String[]{"*." + mOutputFormat});
				String path = dialog.open();
				if (path != null)
				{
					if (!path.endsWith(mOutputFormat))
						path = path + "." + mOutputFormat;
					File file = new File(path);
					try
					{
						ImageIO.write(mCameraCanvas.getImage(), mOutputFormat, file);
						updatePath(path);
					}
					catch (IOException ioe)
					{
						//TODO add an error handling layer to the main UI Activator class
						System.err.println("IOException");
					}
				}
				if (wasPolling)
					camera.startPolling();
			}
		});
		save.setLayoutData(new GridData(GridData.FILL_BOTH));

		pollComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		pollComposite.pack();
		return pollComposite;
	}

	/*
	 * Creates the Composite and listeners for image scaling and overlays
	 */
	private Composite getActionComposite(Composite parent)
	{
		final Composite actionComposite = new Group(parent, SWT.NONE);
		actionComposite.setLayout(new GridLayout(4, true));
		
		final Button scale = new Button(actionComposite, SWT.CHECK);
		scale.setText("Fit to Screen");
		scale.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mCameraCanvas.setZoom(scale.getSelection());
			}
		});
		scale.setLayoutData(new GridData());

		final Button medium = new Button(actionComposite, SWT.RADIO);
		medium.setText("640 x 512");
		medium.setToolTipText("Set image resolution to 640x480 pixels");
		medium.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (medium.getSelection())
					setSize(medium, SetResolutionCmd.MEDIUM);
			}
		});
		medium.setLayoutData(new GridData());

		final Button small = new Button(actionComposite, SWT.RADIO);
		small.setText("320 x 256");
		small.setToolTipText("Set image resolution to 320x256 pixels");
		small.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (small.getSelection())
					setSize(small, SetResolutionCmd.SMALL);
			}
		});
		small.setLayoutData(new GridData());
		small.setSelection(true);
		mLastSize = small;

		final Button tiny = new Button(actionComposite, SWT.RADIO);
		tiny.setText("160 x 128");
		tiny.setToolTipText("Set image resolution to 160x128 pixels");
		tiny.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (tiny.getSelection())
					setSize(tiny, SetResolutionCmd.TINY);
			}
		});
		tiny.setLayoutData(new GridData());

		actionComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		actionComposite.pack();
		return actionComposite;
	}

	/*
	 * Ask the Camera to change the image resolution.
	 */
	private void setSize(Button source, String size)
	{
		CommandButtonMediator mediator = new CommandButtonMediator(source);
		SrvHal.getCamera().setResolution(size, mediator);
	}

	/*
	 * Create the composite and listeners for cursor location and colour inspection
	 */
	private Composite getLocationComposite(Composite parent)
	{
		final Composite locationComposite = new Group(parent, SWT.NONE);
		locationComposite.setLayout(new GridLayout(6, false));

		getLabel(locationComposite, "FPS:");
		mFPSText = getText(locationComposite);

		getLabel(locationComposite, "Frame:");
		mFrameText = getText(locationComposite);

		getLabel(locationComposite, "");
		getLabel(locationComposite, "");

		getLabel(locationComposite, "X-loc:");
		final Text xLocText = getText(locationComposite);

		getLabel(locationComposite, "R:");
		final Text rText = getText(locationComposite);

		getLabel(locationComposite, "        Y:");
		final Text yText = getText(locationComposite);

		getLabel(locationComposite, "Y-loc:");
		final Text yLocText = getText(locationComposite);

		getLabel(locationComposite, "G:");
		final Text gText = getText(locationComposite);

		getLabel(locationComposite, "U:");
		final Text uText = getText(locationComposite);

		getLabel(locationComposite, "");
		getLabel(locationComposite, "");

		getLabel(locationComposite, "B:");
		final Text bText = getText(locationComposite);

		getLabel(locationComposite, "V:");
		final Text vText = getText(locationComposite);

		mCameraCanvas.addMouseMotionListener(new MouseMotionAdapter()
		{
			/**
			 * @see java.awt.event.MouseMotionAdapter#mouseMoved(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseMoved(final MouseEvent e)
			{
				Display.getDefault().syncExec(new Runnable()
				{
					@Override
					public void run()
					{
						int x = e.getX();
						int y = e.getY();

						int pointerX = 0;
						int pointerY = 0;
						Rectangle bounds = mCameraCanvas.getOffsetBounds();
						if (bounds.contains(x, y))
						{
							pointerX = x - bounds.x;
							pointerY = y - bounds.y;

							xLocText.setText(Integer.toString(pointerX));
							yLocText.setText(Integer.toString(pointerY));

							//Grab a pixel at the mouse coordinates and calculate RGB and UYV values
							BufferedImage image = mCameraCanvas.getImage();
							int pixel = mCameraCanvas.getImage().getRGB(pointerX, pointerY);

							if (! mGrabberLock)
							{
								mSampleX = pointerX;
								mSampleY = pointerY;

								// NB these values approximate the true YUV values from the hardware
								// camera by sampling the RGB of the Image pixel at the mouse location
								int red = image.getColorModel().getRed(pixel);
								int green = image.getColorModel().getGreen(pixel);
								int blue = image.getColorModel().getBlue(pixel);

								rText.setText(Integer.toString(red));
								gText.setText(Integer.toString(green));
								bText.setText(Integer.toString(blue));
	
								YUV yuv = YUVUtils.getYUV(red, green, blue);
								yText.setText(Integer.toString(yuv.getY()));
								uText.setText(Integer.toString(yuv.getU()));
								vText.setText(Integer.toString(yuv.getV()));
							}
						}
					}
				});
			}
		});

		locationComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		locationComposite.pack();
		return locationComposite;
	}

	/*
	 * Create tool-bar.
	 */
    private void createToolbar()
    {
    	IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    	mgr.add(new RefreshBinsAction(mColourBinComposite));
    	mgr.add(new RefreshBlobsAction());
    	mgr.add(new RefreshPatternsAction());
	}

	/*
	 * Creates AWT PopupMenu for CameraComposite
	 */
	private void createPopupMenu()
	{
		final Camera camera = SrvHal.getCamera();
		final PopupMenu menu = new PopupMenu();
		final MenuItem setColour = new MenuItem("Set colour to selected bin");

		setColour.addActionListener(new ActionListener()
		{
			/**
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e)
			{
				camera.getDetector().updateColourBinFromCoords(mSampleX, mSampleY);
				mGrabberLock = false;
			}
		});
		menu.add(setColour);

		mCameraCanvas.add(menu);
		mCameraCanvas.addMouseListener(new MouseAdapter()
		{
			/**
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// show the context menu when right-click detected
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					mGrabberLock = true;
					menu.show(mCameraCanvas, e.getX(), e.getY());
				}
			}
		});
	}

	/*
	 * Handle startup calls and set Ready flag.
	 */
	private void doSetReady()
	{
		mReady = true;
		if (mInit)
			SrvHal.getCamera().initialise();
	}

	/*
	 * Utility method used by location Composite
	 */
	private Text getText(Composite parent)
	{
		final Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		text.setText("      ");
		text.setLayoutData(new GridData());

		return text;
	}

	/*
	 * Utility method used by location Composite
	 */
	private Label getLabel(Composite parent, String text)
	{
		final Label label = new Label(parent, SWT.TRAIL);
		label.setText(text);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));

		return label;
	}

	/*
	 * Class mediates between an SWT widget, the source of the <code>Command</code>
	 * and acts as a <code>CommandListener</code> which can modify the widget
	 * according to incoming <code>CommandEvent</code>s.
	 * 
	 * Used by the resolution radio set to select the resolution only when the command
	 * was successfully sent.
	 */
	private class CommandButtonMediator implements CommandListener
	{
		private final Button mSource;
	
		/*
		 * C'tor adds this as a listener to the passed Command in order
		 * to update the Button on completion.
		 */
		private CommandButtonMediator(Button source)
		{
			mSource = source;
		}

		/*
		 * @see uk.co.dancowan.srv1q.core.CommandListener#commandCompleted(uk.co.dancowan.srv1q.core.CommandEvent)
		 */
		@Override
		public void commandCompleted(CommandEvent e)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					mLastSize = mSource;
				}
			});
		}

		/*
		 * @see uk.co.dancowan.srv1q.core.CommandListener#commandExecuted(uk.co.dancowan.srv1q.core.CommandEvent)
		 */
		@Override
		public void commandExecuted(CommandEvent e)
		{
			//NOP - not interested in command starting
		}

		/*
		 * @see uk.co.dancowan.srv1q.core.CommandListener#commandFailed(uk.co.dancowan.srv1q.core.CommandEvent)
		 */
		@Override
		public void commandFailed(CommandEvent e)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					silentSelect(mSource, false);
					silentSelect(mLastSize, true);		
				}
			});
		}

		/*
		 * Remove listeners from Button, select 'state' and then replace listeners.
		 */
		private void silentSelect(Button button, boolean state)
		{
			if (state != button.getSelection())
			{
				Listener[] selectionListeners = button.getListeners(SWT.Selection);
				for (Listener listener : selectionListeners)
					button.removeListener(SWT.Selection, listener);
				button.setSelection(state);
				for (Listener listener : selectionListeners)
					button.addListener(SWT.Selection, listener);
			}
		}
	}
}
