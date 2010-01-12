/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (C) 2009 Dan Cowan
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
package uk.co.dancowan.robots.ui.views.connection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.ConnectionListener;
import uk.co.dancowan.robots.hal.core.HALRegistry;
import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.ui.utils.ColourManager;
import uk.co.dancowan.robots.ui.utils.TextUtils;
import uk.co.dancowan.robots.ui.views.ScrolledView;
import uk.co.dancowan.robots.ui.views.actions.ClearAction;
import uk.co.dancowan.robots.ui.views.actions.Clearable;
import uk.co.dancowan.robots.ui.views.actions.Lockable;
import uk.co.dancowan.robots.ui.views.actions.ScrollLockAction;


/**
 * The Connection View displays the current connection status and allows for
 * control of the connection between a robot and the workbench.
 * 
 * <p>Current connection types supported include:</p>
 * <ul><li>Network</li>
 * <li>Com</li></ul>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ConnectionView extends ScrolledView implements ConnectionListener, IPropertyChangeListener, Lockable, Clearable
{
	private static final int UNLIMITED = -1;

	public static final String ID = "uk.co.dancowan.robots.ui.connectionView";

	private static final Point MIN_SIZE = new Point(300, 140);

	private final Connection mConnection;

	private Button mConnectButton;
	private StyledText mText;

	private boolean mPin;
	private boolean mShowTX;
	private boolean mShowRX;
	private long mMaxBufferSize;
	private boolean mWrap;

	private Color mTXColour;
	private Color mRXColour;
	private Color mMessageColour;
	private Color mErrorColour;

	/**
	 * C'tor.
	 * 
	 * <p>Must be no-args for Eclipse extension point.</p>
	 * 
	 * <p>The constructor for the view will get a reference to the <code>
	 * Connection</code> object from the base HAL, assuming one has been
	 * registered. The view is initialised from the plugin's preference store.</p>
	 */
	public ConnectionView()
	{
		mConnection = HALRegistry.getInsatnce().getCommandQ().getConnection();
		mConnection.addConnectionListener(this);

		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		initializeFromPreferences();
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public Control getPartControl(Composite parent)
	{
		final Composite part = new Composite(parent, SWT.BORDER);
		part.setLayout(new FormLayout());

		final Composite networkComposite = getNetworkComposite(part);
		final Composite comPortComposite = getComPortComposite(part);

		final Button comPort = new Button(part, SWT.RADIO);
		comPort.setText("Com Port");
		comPort.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mConnection.setComPort(comPort.getText());
				mConnection.setNetwork(false);
				networkComposite.setVisible(false);
				comPortComposite.setVisible(true);
			}
		});

		final Button network = new Button(part, SWT.RADIO);
		network.setText("Network");
		network.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mConnection.setNetwork(true);
				networkComposite.setVisible(true);
				comPortComposite.setVisible(false);	
			}
		});

		mConnectButton = new Button(part, SWT.PUSH);
		mConnectButton.setText("Connect");
		mConnectButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				connect(! mConnection.isConnected());
			}
		});

		mText = new StyledText(part, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.READ_ONLY);
		mText.setWordWrap(mWrap);
		mText.setText("RoboWorkbench Connection console");
		mText.append(TextUtils.CR);
		mText.append("=======================");
		mText.append(TextUtils.CR);
		mText.setMenu(getContextMenu());

		//Com port Radio Button layout
		FormData data = new FormData();
		data.top = new FormAttachment(0, 9);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(0, 80);
		comPort.setLayoutData(data);

		//Network Radio Button layout
		data = new FormData();
		data.top = new FormAttachment(0, 9);
		data.left = new FormAttachment(comPort, 5, SWT.RIGHT);
		data.right = new FormAttachment(comPort, 120, SWT.RIGHT);
		network.setLayoutData(data);

		//Connect Button layout
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(network, 5, SWT.RIGHT);
		data.right = new FormAttachment(100, -5);
		mConnectButton.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(comPort, 16, SWT.BOTTOM);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(comPort, 48, SWT.BOTTOM);
		comPortComposite.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(comPort, 16, SWT.BOTTOM);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(comPort, 48, SWT.BOTTOM);
		networkComposite.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(networkComposite, 5, SWT.BOTTOM);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(100, -5);
		mText.setLayoutData(data);

		createToolbar();

		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		String mode = prefs.getString(PreferenceConstants.CONNECTION_MODE);
		if (PreferenceConstants.NETWORK.equals(mode))
		{
			network.setSelection(true);
			networkComposite.setVisible(true);
			comPortComposite.setVisible(false);
		}
		else
		{
			comPort.setSelection(true);
			networkComposite.setVisible(false);
			comPortComposite.setVisible(true);
		}
		boolean connect = prefs.getBoolean(PreferenceConstants.CONNECTION_ON_START);
		connect(connect);

		part.pack();
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
	 * Sets widget state according to connection events.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#connected()
	 */
	@Override
	public void connected()
	{
		final StringBuilder sb = new StringBuilder("Connected to ");
		if (mConnection.isNetworkPort())
		{
			sb.append(mConnection.getHost());
			sb.append(":");
			sb.append(mConnection.getNetworkPort());
		}
		else
		{
			sb.append("com port: ");
			sb.append(mConnection.getComPort());
		}
		insertMessage(sb.toString(), mMessageColour);
		newLine();
	}

	/**
	 * Sets widget state according to connection events.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#disconnected()
	 */
	@Override
	public void disconnected()
	{
		String message = "Disconnected";
		insertMessage(message, mMessageColour);
		newLine();
	}

	/**
	 * Sets widget state according to connection events.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#error(java.lang.String)
	 */
	@Override
	public void error(final String message)
	{
		insertMessage(message, mErrorColour);
		newLine();
	}

	/**
	 * Sets widget state according to connection events.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#rx(java.lang.String)
	 */
	public void rx(final String message)
	{
		if (mShowRX)
			insertMessage(message.toString(), mRXColour);
	}

	/**
	 * Sets widget state according to connection events.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#tx(java.lang.String)
	 */
	public void tx(String message)
	{
		if (mShowTX)
			insertMessage(message, mTXColour);
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.actions.Lockable#pin()
	 */
	@Override
	public void pin()
	{
		mPin = true;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.actions.Lockable#release()
	 */
	@Override
	public void release()
	{
		mPin = false;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.actions.Clearable#clear()
	 */
	public void clear()
	{
		mText.setText("");
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (PreferenceConstants.CONNECTION_BUFFER_SIZE.equals(event.getProperty()))
		{
			int buffer = (Integer) event.getNewValue();
			setBufferSizeK(buffer);
		}
		else if (PreferenceConstants.CONNECTION_UNLIMITED_BUFFER.equals(event.getProperty()))
		{
			boolean unlimited = (Boolean) event.getNewValue();
			if (unlimited)
			{
				setBufferSizeK(UNLIMITED);
			}
			else
			{
				IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
				setBufferSizeK(prefs.getInt(PreferenceConstants.CONNECTION_BUFFER_SIZE));
			}
		}
		else if (PreferenceConstants.CONNECTION_SHOW_TX.equals(event.getProperty()))
		{
			mShowTX = (Boolean) event.getNewValue();
		}
		else if (PreferenceConstants.CONNECTION_SHOW_RX.equals(event.getProperty()))
		{
			mShowRX = (Boolean) event.getNewValue();
		}
		else if (PreferenceConstants.CONNECTION_WRAP.equals(event.getProperty()))
		{
			mWrap = (Boolean) event.getNewValue();
			if (! mText.isDisposed())
				mText.setWordWrap(mWrap);
		}
		else if (PreferenceConstants.CONNECTION_TX_COLOUR.equals(event.getProperty()))
		{
			RGB rgb = (RGB) event.getNewValue();
			mTXColour = ColourManager.getInstance().getColour(rgb);
		}
		else if (PreferenceConstants.CONNECTION_RX_COLOUR.equals(event.getProperty()))
		{
			RGB rgb = (RGB) event.getNewValue();
			mRXColour = ColourManager.getInstance().getColour(rgb);
		}
		else if (PreferenceConstants.CONNECTION_MESSAGE_COLOUR.equals(event.getProperty()))
		{
			RGB rgb = (RGB) event.getNewValue();
			mMessageColour = ColourManager.getInstance().getColour(rgb);
		}
		else if (PreferenceConstants.CONNECTION_ERROR_COLOUR.equals(event.getProperty()))
		{
			RGB rgb = (RGB) event.getNewValue();
			mErrorColour = ColourManager.getInstance().getColour(rgb);
		}
	}

	/*
	 * Initialise widgets from preferences.
	 */
	private void initializeFromPreferences()
	{
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();

		boolean unlimited = prefs.getBoolean(PreferenceConstants.CONNECTION_UNLIMITED_BUFFER);
		if (unlimited)
		{
			setBufferSizeK(UNLIMITED);
		}
		else
		{
			setBufferSizeK(prefs.getInt(PreferenceConstants.CONNECTION_BUFFER_SIZE));
		}

		mPin = prefs.getBoolean(PreferenceConstants.CONNECTION_PIN);
		mShowTX = prefs.getBoolean(PreferenceConstants.CONNECTION_SHOW_TX);
		mShowRX = prefs.getBoolean(PreferenceConstants.CONNECTION_SHOW_RX);
		mWrap = prefs.getBoolean(PreferenceConstants.CONNECTION_WRAP);

		mTXColour = ColourManager.getInstance().getColour(PreferenceConverter.getColor(prefs, PreferenceConstants.CONNECTION_TX_COLOUR));
		mRXColour = ColourManager.getInstance().getColour(PreferenceConverter.getColor(prefs, PreferenceConstants.CONNECTION_RX_COLOUR));
		mMessageColour = ColourManager.getInstance().getColour(PreferenceConverter.getColor(prefs, PreferenceConstants.CONNECTION_MESSAGE_COLOUR));
		mErrorColour = ColourManager.getInstance().getColour(PreferenceConverter.getColor(prefs, PreferenceConstants.CONNECTION_ERROR_COLOUR));
	}

	/*
	 * Sets the internal buffer size of this log viewer in kb.
	 *
	 * <p>Buffer size should be > 0. A value of -1 will set no limit on the buffer size.</p>
	 * 
	 * @throws IllegalArgumentException if the buffer size < 1k and != -1
	 */
	private void setBufferSizeK(long maxLength)
	{
		if (maxLength != UNLIMITED)
		{
			if (maxLength < 1)
				throw new IllegalArgumentException("Buffer length out of bounds (must be > -1) :" + maxLength);
			maxLength *= 1024;
		}
		mMaxBufferSize = maxLength;
	}

	/*
	 * Write a new line char to the console window.
	 */
	private void newLine()
	{
		insertMessage(TextUtils.CR, mMessageColour);
	}

	/*
	 * Handle the connection action.
	 * 
	 * TODO place this in a thread so timeout isn't blocking.
	 */
	private void connect(final boolean connect)
	{
		if (connect)
			mConnection.openConnection();
		else
			mConnection.closeConnection();

		if (mConnection.isConnected())
			mConnectButton.setText("Disconnect");
		else
			mConnectButton.setText("Connect");
	}

	/*
	 * Create toolbar.
	 */
    private void createToolbar()
    {
    	IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    	mgr.add(new ScrollLockAction(this));
    	mgr.add(new ClearAction(this));
	}

	/*
	 * Gets the StyledText widget's context menu
	 */
	private Menu getContextMenu()
	{
		final Menu menu = new Menu(mText);

		final MenuItem showTX = new MenuItem(menu, SWT.CHECK);
		showTX.setText("Show TX");
		showTX.setSelection(mShowTX);
		showTX.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (mShowTX)
					mShowTX = false;
				else
					mShowTX = true;
				showTX.setSelection(mShowTX);
				IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
				prefs.setValue(PreferenceConstants.CONNECTION_SHOW_TX, mShowTX);
			}
		});

		final MenuItem showRX = new MenuItem(menu, SWT.CHECK);
		showRX.setText("Show RX");
		showRX.setSelection(mShowRX);
		showRX.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (mShowRX)
					mShowRX = false;
				else
					mShowRX = true;
				showRX.setSelection(mShowRX);
				IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
				prefs.setValue(PreferenceConstants.CONNECTION_SHOW_RX, mShowRX);
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		final MenuItem copy = new MenuItem(menu, SWT.PUSH);
		copy.setText("Copy");
		copy.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mText.copy();
			}
		});

		final MenuItem clear = new MenuItem(menu, SWT.PUSH);
		clear.setText("Clear");
		//clear.setImage(image);
		clear.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				clear();
			}
		});

		menu.addMenuListener(new MenuAdapter()
		{
			@Override
			public void menuShown(MenuEvent e)
			{
				showTX.setSelection(mShowTX);
				showRX.setSelection(mShowRX);
			}
		});

		return menu;
	}

	/*
	 * Creates the composite for network connection
	 */
	private Composite getNetworkComposite(Composite parent)
	{
		final Composite networkComposite = new Composite(parent, SWT.NONE);
		networkComposite.setLayout(new FormLayout());

		final Label ipLabel = new Label(networkComposite, SWT.NONE);
		ipLabel.setText("IP Address: ");

		final Text ipAddress = new Text(networkComposite, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		ipAddress.addModifyListener(new ModifyListener()
		{	
			@Override
			public void modifyText(ModifyEvent e)
			{
				if (e.getSource() instanceof Text)
				{
					// Validate IP address here
				}
			}
		});
		ipAddress.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				if (e.getSource() instanceof Text)
				{
					final Text text = (Text) e.getSource();
					final String host = text.getText();
					mConnection.setHost(host);
				}
			}
		});
		ipAddress.setText(mConnection.getHost());

		final Label portLabel = new Label(networkComposite, SWT.RIGHT);
		portLabel.setText("Port:");

		final Text port = new Text(networkComposite, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		port.addModifyListener(new ModifyListener()
		{	
			@Override
			public void modifyText(ModifyEvent me)
			{
				if (me.getSource() instanceof Text)
				{
					final Text text = (Text) me.getSource();
					final String p = text.getText();
					try
					{
						Integer.parseInt(p);
						text.setForeground(ColourManager.getColour(SWT.COLOR_BLACK));
					}
					catch (NumberFormatException nfe)
					{
						text.setForeground(ColourManager.getColour(ColourManager.ERROR_COLOUR));
					}
				}
			}
		});
		port.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				if (e.getSource() instanceof Text)
				{
					Text text = (Text) e.getSource();
					final String p = text.getText();
					try
					{
						int port = Integer.parseInt(p);
						mConnection.setNetworkPort(port);
					}
					catch (NumberFormatException nfe)
					{
						// NOP
					}
				}
			}
		});
		port.setText(Integer.toString(mConnection.getNetworkPort()));

		//IPAddress label layout
		FormData data = new FormData();
		data.top = new FormAttachment(0, 9);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(0, 60);
		ipLabel.setLayoutData(data);

		//IPAddress Text field layout
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(ipLabel, 5, SWT.RIGHT);
		data.right = new FormAttachment(portLabel, -5, SWT.LEFT);
		ipAddress.setLayoutData(data);

		//Port Label layout
		data = new FormData();
		data.top = new FormAttachment(0, 9);
		data.left = new FormAttachment(port, -48, SWT.LEFT);
		data.right = new FormAttachment(port, -5, SWT.LEFT);
		portLabel.setLayoutData(data);

		//Port Text field layout
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(100, -65);
		data.right = new FormAttachment(100, -5);
		port.setLayoutData(data);

		networkComposite.pack();
		return networkComposite;
	}	

	/*
	 * Creates the composite for com port connection
	 */
	private Composite getComPortComposite(Composite parent)
	{
		final Composite comPortComposite = new Composite(parent, SWT.NONE);
		comPortComposite.setLayout(new FormLayout());

		final Label portLabel = new Label(comPortComposite, SWT.NONE);
		portLabel.setText("Port: ");
		FormData data = new FormData();
		data.top = new FormAttachment(0, 9);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(0, 48);
		portLabel.setLayoutData(data);
		
		final Combo ports = new Combo(comPortComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		List<String> portList = mConnection.getSerialPorts();
		ports.setItems(portList.toArray(new String[portList.size()]));
		ports.select(1);
		ports.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				if (e.getSource() instanceof Combo)
				{
					Combo source = (Combo) e.getSource();
					String newPort = source.getItem(source.getSelectionIndex());
					mConnection.setComPort(newPort);
				}
			}
		});
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(portLabel, 5, SWT.RIGHT);
		data.right = new FormAttachment(portLabel, 60, SWT.RIGHT);
		ports.setLayoutData(data);

		comPortComposite.pack();
		return comPortComposite;
	}

	/*
	 * Append an incoming message to the StyledText widget.
	 * 
	 * Trim the buffer length the the max size as necessary.
	 */
	private void insertMessage(final String message, final Color colour)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if ( ! mText.isDisposed())
				{
					String msg = message;

					int length = msg.length();
					int start = mText.getText().length();
					StyleRange range = new StyleRange(start, length, colour, ColourManager.getColour(SWT.COLOR_WHITE));
					mText.append(msg);
					mText.setStyleRange(range);

					trimBuffer();
				}
			}
		});	
	}

	/*
	 * Trim the buffer to the max length
	 */
	private void trimBuffer()
	{
		// check buffer size and trim as necessary
		while (mMaxBufferSize != UNLIMITED && mText.getText().length() > mMaxBufferSize)
		{
			int start = (int) (mText.getText().length() - mMaxBufferSize);
			String newStr = mText.getText().substring(start);

			List<StyleRange> newRanges = replaceRanges(start, (int)mMaxBufferSize, mText.getStyleRanges());
			mText.setText(newStr);
			mText.replaceStyleRanges(0, (int) mMaxBufferSize, newRanges.toArray(new StyleRange[newRanges.size()]));
		}

		mText.setCaretOffset((int) mMaxBufferSize);
		if (!mPin)
			mText.showSelection();
	}

	/*
	 * Adjusts the styles to match the widget's content when the content is trimmed to
	 * a maximum length.
	 */
	private List<StyleRange> replaceRanges(int start, int length, StyleRange[] ranges)
	{
		// inbound ranges are initially expected to cover 0 to > length
		// start and length mark the substring which will be shifted to 0 -> length
		// outbound ranges should cover 0 to length if possible, or r1.start to length

		List<StyleRange> newRanges = new ArrayList<StyleRange>();
		StyleRange first = ranges[0]; // the initial range: may need clipping at the front

		if (first.start < start)
		{
			// range starts before new text start, clip front
			int len = first.length - (start - first.start);
			// check the fit of the length
			if (len > length)
				len = length;
			if (len > 0)
			{
				StyleRange firstRep = new StyleRange(0, len, first.foreground, first.background);
				newRanges.add(firstRep);
			}
		}
		else
		{
			// range starts after new text start : new range start adjusted
			int st = first.start - start;
			if (st < 0)
				st = 0;
			int len = first.length;
			// check the fit of the length
			if (len > length)
				len = length;
			StyleRange firstRep = new StyleRange(st, len, first.foreground, first.background);
			newRanges.add(firstRep);
		}

		for (int i = 1; i < ranges.length; i ++)
		{
			StyleRange next = ranges[i];
			if (next.start > start + length) // escape if next rang is out of bounds (should never happen)
				return newRanges;

			// range starts after new text start : new range start adjusted
			int st = next.start - start;
			int len = next.length;
			if (st < 0)
			{
				len += st; // (len = len - abs(st)
				st = 0;
			}
			// check the fit of the length
			if (len > start + next.length)
				len = length;
			StyleRange nextRep = new StyleRange(st, len, next.foreground, next.background);
			newRanges.add(nextRep);
		}

		return newRanges;
	}
}