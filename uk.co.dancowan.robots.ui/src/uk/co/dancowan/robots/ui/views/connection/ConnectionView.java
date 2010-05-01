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

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.ConnectionListener;
import uk.co.dancowan.robots.hal.core.HALRegistry;
import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.ui.utils.ColourManager;
import uk.co.dancowan.robots.ui.views.ScrolledView;


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
public class ConnectionView extends ScrolledView implements ConnectionListener
{
	public static final String ID = "uk.co.dancowan.robots.ui.connectionView";

	private static final Point MIN_SIZE = new Point(300, 70);

	private Button mConnectButton;

	/**
	 * C'tor.
	 * 
	 * <p>Must be no-args for Eclipse extension point.</p>
	 * 
	 * <p>The view is initialised from the plugin's preference store.</p>
	 */
	public ConnectionView()
	{
		HALRegistry.getInsatnce().getCommandQ().getConnection().addConnectionListener(this);
	}

	/**
	 * Creates and lays out the widgets for this view.
	 * 
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
				getConnection().setComPort(comPort.getText());
				getConnection().setNetwork(false);
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
				getConnection().setNetwork(true);
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
				connect(! getConnection().isConnected());
			}
		});

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
		data.right = new FormAttachment(comPort, 80, SWT.RIGHT);
		network.setLayoutData(data);

		//Connect Button layout
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(100, -107);
		data.right = new FormAttachment(100, -5);
		mConnectButton.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(comPort, 8, SWT.BOTTOM);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(comPort, 40, SWT.BOTTOM);
		comPortComposite.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(comPort, 8, SWT.BOTTOM);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(comPort, 40, SWT.BOTTOM);
		networkComposite.setLayoutData(data);

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

	/**
	 * Returns the view's unique identifier.
	 */
	public String getID()
	{
		return ID;
	}

	/**
	 * Returns the minimum size of this view.
	 * 
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getMinSize()
	 */
	@Override
	public Point getMinSize()
	{
		return MIN_SIZE;
	}

	/**
	 * ConnectionListener interface implementation updates button state.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#error(java.lang.String)
	 */	
	@Override
	public void connected()
	{
		// error may be thrown from the command execution thread
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (!mConnectButton.isDisposed())
					mConnectButton.setText("Disconnect");
			}
		});
	}

	/**
	 * ConnectionListener interface implementation updates button state.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#error(java.lang.String)
	 */	
	@Override
	public void disconnected()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				
				if (!mConnectButton.isDisposed())
					mConnectButton.setText("Connect");
			}
		});
	}

	/**
	 * Implementation of the ConnectionListener interface, ensures the connection is closed
	 * and resets the connect button state.
	 * 
	 * <p>May be called by a command failing within the execution thread.</p>
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#error(java.lang.String)
	 */
	@Override
	public void error(String error)
	{
		// error may be thrown from the command execution thread
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				connect(false);
				if (!mConnectButton.isDisposed())
					mConnectButton.setText("Connect");
			}
		});
	}

	/**
	 * ConnectionListener interface implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#error(java.lang.String)
	 */	
	@Override
	public void rx(String readChars)
	{
		// NOP
	}

	/**
	 * ConnectionListener interface implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#error(java.lang.String)
	 */	
	@Override
	public void tx(String writeChars)
	{
		// NOP	
	}

	/*
	 * Helper method to get the connection object.
	 */
	private Connection getConnection()
	{
		return HALRegistry.getInsatnce().getCommandQ().getConnection();
	}

	/*
	 * Handle the connection action.
	 * 
	 * TODO place this in a thread so timeout isn't blocking.
	 */
	private void connect(final boolean connect)
	{
		if (connect)
		{
			Thread connectionThread = new ConnectionThread();
			connectionThread.start();
		}
		else
		{
			getConnection().closeConnection();
		}
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
					getConnection().setHost(host);
				}
			}
		});
		ipAddress.setText(getConnection().getHost());

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
						getConnection().setNetworkPort(port);
					}
					catch (NumberFormatException nfe)
					{
						// NOP
					}
				}
			}
		});
		port.setText(Integer.toString(getConnection().getNetworkPort()));

		//IPAddress label layout
		FormData data = new FormData();
		data.top = new FormAttachment(0, 4);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(0, 60);
		ipLabel.setLayoutData(data);

		//IPAddress Text field layout
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(ipLabel, 5, SWT.RIGHT);
		data.right = new FormAttachment(portLabel, -5, SWT.LEFT);
		ipAddress.setLayoutData(data);

		//Port Label layout
		data = new FormData();
		data.top = new FormAttachment(0, 4);
		data.left = new FormAttachment(port, -48, SWT.LEFT);
		data.right = new FormAttachment(port, -5, SWT.LEFT);
		portLabel.setLayoutData(data);

		//Port Text field layout
		data = new FormData();
		data.top = new FormAttachment(0, 0);
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
		data.top = new FormAttachment(0, 4);
		data.left = new FormAttachment(0, 2);
		data.right = new FormAttachment(0, 48);
		portLabel.setLayoutData(data);
		
		final Combo ports = new Combo(comPortComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		List<String> portList = getConnection().getSerialPorts();
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
					getConnection().setComPort(newPort);
				}
			}
		});
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(portLabel, 5, SWT.RIGHT);
		data.right = new FormAttachment(portLabel, 68, SWT.RIGHT);
		ports.setLayoutData(data);

		comPortComposite.pack();
		return comPortComposite;
	}
}