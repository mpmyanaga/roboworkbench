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

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.swt.widgets.Text;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.camera.Camera;
import uk.co.dancowan.robots.srv.hal.camera.CameraListener;
import uk.co.dancowan.robots.srv.ui.panels.ColourBinPanel;
import uk.co.dancowan.robots.srv.ui.panels.PatternPanel;
import uk.co.dancowan.robots.srv.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.ui.utils.ColourManager;
import uk.co.dancowan.robots.ui.views.MultiPanel;
import uk.co.dancowan.robots.ui.views.MultiPanelChangeListener;
import uk.co.dancowan.robots.ui.views.Panel;
import uk.co.dancowan.robots.ui.views.ScrolledView;

/**
 * Instance of an Eclipse <code>ViewPart</code> to enable vision related
 * commands and view camera output in the application.
 * 
 * <p>Extends <code>ScrollableView</code> to enable scroll bars at a
 * preset minimum size.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CameraView extends ScrolledView implements IPropertyChangeListener, CameraListener, MultiPanelChangeListener
{
	public static final String ID = "uk.co.dancowan.robots.srv.cameraView";

	private static final Point MIN_SIZE = new Point(450, 500);
	private static final NumberFormat NF = NumberFormat.getNumberInstance();
	{
		NF.setMaximumFractionDigits(1);
	}

	private final CameraPanel mCameraPanel;
	private final MultiPanel mPanels;

	private Text mFPSText;

	private boolean mReady;
	private boolean mPoll;
	private boolean mInit;

	private String mOutputFormat;
	private String mPath;

	/**
	 * C'tor.
	 */
	public CameraView()
	{
		mReady = false;
		
		mCameraPanel = new CameraPanel(this);

		mPanels = new MultiPanel();
		mPanels.addListener(this);
		mPanels.addPanel(new ImageQualityPanel(mCameraPanel.getCameraCanvas()));
		mPanels.addPanel(new ColourBinPanel(mCameraPanel.getCameraCanvas()));
		mPanels.addPanel(new LocationPanel(mCameraPanel.getCameraCanvas()));
		mPanels.addPanel(new PatternPanel());

		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);
		initFromPrefs();

		SrvHal.getCamera().setConsumer(mCameraPanel.getCameraCanvas());
	}

	/**
	 * Creates and lays out the widgets for this component on the passed
	 * <code>Composite</code>.
	 * 
	 * <p>Although essentially based on SWT widgets and methodology this class
	 * uses an AWT <code>Image</code> placed on an AWT <code>Canvas</code> instance
	 * to assist with rendering the streamed image from the SRV1 Camera.</p>
	 * 
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getPartControl(Composite)
	 * @param parent the Composite to add widgets to
	 * @return Composite the container of view's controls
	 */
	@Override
	public Control getPartControl(Composite parent)
	{
		final Composite part = new Composite(parent, SWT.BORDER);
		part.setLayout(new FormLayout());

		final Composite awtComposite = mCameraPanel.getPanel(part); // SWT Composite embeds AWT Frame, and Canvas widgets
		final Composite pollComposite = getPollComposite(part);
		final Composite multiPanel = mPanels.createControl(part);

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
		data.left = new FormAttachment(50, -130);
		data.right = new FormAttachment(50, 130);
		pollComposite.setLayoutData(data);

		// MultiPanel Composite layout
		data = new FormData();
		data.top = new FormAttachment(pollComposite, 5, SWT.BOTTOM);
		data.left = new FormAttachment(50, -200);
		data.right = new FormAttachment(50, 200);
		data.bottom = new FormAttachment(100, -5);
		multiPanel.setLayoutData(data);

		createToolbar();

		part.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		doSetReady();
		return part;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getID()
	 */
	@Override
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
		mReady = false;
		super.dispose();
		mPanels.dispose();
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
			// Called from FrameDecoder thread so async into SWT thread
			Display.getDefault().asyncExec(new Runnable()
			{
				@Override
				public void run()
				{
					Camera camera = SrvHal.getCamera();
					mFPSText.setText(NF.format(camera.getFPS()));
				}
			});
		}
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
		if (PreferenceConstants.CAMERA_DEFAULT_PATH.equals(event.getProperty()))
		{
			mPath = (String) event.getNewValue();
		}
		else if (PreferenceConstants.CAMERA_OUTPUT_FORMAT.equals(event.getProperty()))
		{
			mOutputFormat = (String) event.getNewValue();
		}
	}

	/**
	 * Implementation of the <code>PanelChangeListener</code> interface allows this view to
	 * update view actions according to the visible panel.
	 * 
	 * @see uk.co.dancowan.robots.ui.views.MultiPanelChangeListener#panelChanged(uk.co.dancowan.robots.ui.views.Panel)
	 * @param newPanel the panel just revealed
	 */
	@Override
	public void panelChanged(Panel newPanel)
	{
		createToolbar();
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
	}

	/*
	 * Creates the Composite and listeners for image polling
	 */
	private Composite getPollComposite(Composite parent)
	{
		final Group pollComposite = new Group(parent, SWT.NONE);
		pollComposite.setLayout(new GridLayout(7, true));
		pollComposite.setText("Polling");

		final Button poll = new Button(pollComposite, SWT.TOGGLE);
		poll.setText("Poll");
		poll.setToolTipText("Start the camera polling for images");
		poll.addSelectionListener(new SelectionAdapter()
		{
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

		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		poll.setLayoutData(data);

		final Text pollDelay = new Text(pollComposite, SWT.BORDER | SWT.RIGHT);
		pollDelay.setText(Integer.toString(SrvHal.getCamera().getPollDelay()));
		pollDelay.setToolTipText("Poll interval in 10ms units");
		addPollDelayListeners(pollDelay);
		pollDelay.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label label = new Label(pollComposite, SWT.TRAIL);
		label.setText("FPS:");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));

		mFPSText = new Text(pollComposite, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		mFPSText.setText("      ");
		mFPSText.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button save = new Button(pollComposite, SWT.PUSH);
		save.setText("Save");
		save.setToolTipText("Save a snapshot of the video stream");
		save.addSelectionListener(new SaveEventListener());
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		save.setLayoutData(data);

		if (mPoll)
		{
			poll.setSelection(true);
			SrvHal.getCamera().startPolling();
			poll.setText("Stop");
			poll.setToolTipText("Stop the camera from polling");
		}

		pollComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		pollComposite.pack();
		return pollComposite;
	}

	/*
	 * Add all the delay setting listeners to the text
	 */
	private void addPollDelayListeners(final Text pollDelay)
	{
		// validation
		pollDelay.addModifyListener(new ModifyListener()
		{
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

		// set on focus loss
		pollDelay.addFocusListener(new FocusAdapter()
		{
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

		// set on enter key
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
	}

	/*
	 * Create tool-bar.
	 */
	private void createToolbar()
	{
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.removeAll();

		IContributionItem actions = new GroupMarker("actions");
		manager.add(actions);

		mPanels.getCurrentPanel().addToToolBar(manager);

		manager.update(true);
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
	 * Handle the save button event
	 */
	private class SaveEventListener extends SelectionAdapter
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
					ImageIO.write(mCameraPanel.getCameraCanvas().getImage(), mOutputFormat, file);
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
	}
}
