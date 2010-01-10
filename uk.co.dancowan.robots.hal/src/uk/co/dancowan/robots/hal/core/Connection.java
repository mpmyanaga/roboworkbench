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
package uk.co.dancowan.robots.hal.core;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import gnu.io.PortInUseException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.core.commands.CommandUtils;
import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Handles establishing a connection to the SRV.
 * 
 * <p>Creates low level methods to connect to, read to and
 * write from the connection's I/O streams.</p>
 * 
 * <p><code>ConnectionListener</code>s may be added to track
 * low level communications.</p>
 *
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class Connection implements Component
{
	public static final String ID = "Connection";

	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);
	private static final Logger ERROR_LOGGER = Logger.getLogger(LoggingService.ERROR_LOGGER);

	private static final String CONNECTED = "connected";
	private static final String DISCONNECTED = "disconnected";
	private static final int BAUD_RATE = 115200;

	private OutputStream mOutputStream;
	private InputStream mInputStream;

	private String mHost;
	private int mPort;
	private String mComPort;
	private Socket mSocket;
	private SerialPort mSerialPort;
	private boolean mIsNetwork;

	private List<ConnectionListener> mConnectionListeners;

	/**
	 * C'tor
	 */
	public Connection()
	{
		mHost = Config.sProperties.get("network.srv.host");
		String p = Config.sProperties.get("network.srv.port");
		try
		{
			mPort = Integer.parseInt(p);
		}
		catch(NumberFormatException e)
		{
			INFO_LOGGER.finest("Failed parsing network port from property: " + p);
			INFO_LOGGER.finest("  Using default port 10001");
			mPort = 10001;
		}
		mIsNetwork = true;
		
		mConnectionListeners = new ArrayList<ConnectionListener>();
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#getID()
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#requiresThreadStartup()
	 */
	@Override
	public boolean requiresThreadStartup()
	{
		return false;
	}

	/**
	 * Adds a <code>ConnectionListener</code> to the collection of listeners
	 * notified of connection events.
	 * 
	 * @param listener the listener to be added
	 */
	public void addConnectionListener(ConnectionListener listener)
	{
		mConnectionListeners.add(listener);
	}

	/**
	 * Removes the passed <code>ConnectionListener</code> from the collection
	 * of listeners notified of connection events.
	 * 
	 * @param listener the listener to be removed
	 */
	public void removeConnectionListener(ConnectionListener listener)
	{
		mConnectionListeners.remove(listener);
	}

	/**
	 * Open a connection to the SRV.
	 * 
	 * <p>If configuration indicates this is a networked SRV then a network
	 * connection is opened, otherwise a serial connection on a com port is opened.
	 * The WCSPush service is started in its own thread to push images to the WebCamSat
	 * service.</p>
	 */
	public void openSRVConnection()
	{
		if (isNetworkPort())
		{
			if (openNetworkSRV())
			{
				INFO_LOGGER.fine("SRV1Connection network opened");
				fireConnectionEvent(CONNECTED);
			}
		}
		else
		{
			if (openSerialPort())
			{
				INFO_LOGGER.fine("SRV1Connection com port opened");
				fireConnectionEvent(DISCONNECTED);
			}
		}
	}

	/**
	 * Closes the connection and tidies up the resources.
	 */
	public void closeSRVConnection()
	{
		if (! isConnected())
			return;

		if ( ! isNetworkPort())
			closeSerialPort();

		try
		{
			mOutputStream.close();
		}
		catch(IOException e)
		{
			// Aaaargh
		}
		mOutputStream = null;

		try
		{
			mInputStream.close();
		}
		catch(IOException e)
		{
			// Again!!!
		}
		mInputStream = null;

		try
		{
			mSocket.close();
		}
		catch(IOException e)
		{
			// Noooooooooo
		}
		mSocket = null;
		
		INFO_LOGGER.log(Level.FINE, "SRV1Connection closed");
		for (ConnectionListener listener : mConnectionListeners)
		{
			listener.disconnected();
		}
	}

	/**
	 * Sets the connection mode.
	 * 
	 * <p>Mode could be networked wireless or serial over com.</p>
	 * @param isNetwork
	 */
	public void setNetwork(boolean isNetwork)
	{
		mIsNetwork = isNetwork;
	}

	/**
	 * Sets the port number in wireless mode.
	 * @param port the port number >= 0
	 */
	public void setNetworkPort(int port)
	{
		mPort = port;
	}

	/**
	 * Sets the host name or IP-address of the SRV.
	 * @param host
	 */
	public void setHost(String host)
	{
		mHost = host;
	}

	/**
	 * Returns the configured network port.
	 * @return int
	 */
	public int getNetworkPort()
	{
		return mPort;
	}

	/**
	 * Returns the configured host name or ip-address.
	 * @return
	 */
	public String getHost()
	{
		return mHost;
	}

	/**
	 * Sets the com port to use for the connection.
	 * 
	 * @param port the port ID to connect to.
	 */
	public void setComPort(String port)
	{
		mComPort = port;
	}

	/**
	 * Returns the com port configured for this connection.
	 * 
	 * @return String
	 */
	public String getComPort()
	{
		return mComPort;
	}

	/**
	 * Returns a list of available com serial ports.
	 * 
	 * @return List<String>
	 */
	public List<String> getSerialPorts()
	{
		List<String> portNames = new ArrayList<String>();

		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements())
		{
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement ();
			if (portId.getPortType () == CommPortIdentifier.PORT_SERIAL)
			{
				portNames.add(portId.getName());
			}
		}

		return portNames;
	}

	/**
	 * Returns the length of the InputStream remaining.
	 * 
	 * <p>Wraps a call to <code>java.io.InputStream.available()</code>.</p>
	 * 
	 * @see java.io.InputStream#available()
	 * @return the length of the InputStream remaining
	 * @throws IOException
	 */
	public int available() throws IOException
	{
		if (mInputStream != null)
		{
			return mInputStream.available();
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Reads from the InputStream into the passed <code>byte[]</code> frame.
	 * 
	 * <p>Wraps a call to <code>int java.io.InputStream.read(byte[], int, int)</code>.</p>
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 * @return int, the status of the call
	 * @throws IOException
	 */	
	public int read(byte[] frame, int pos, int len) throws IOException
	{
		int read = mInputStream.read(frame, pos, len);
		fireRXEvent(new String(frame));
		return read;
	}

	/**
	 * Reads a single <code>byte</code> from the InputStream.
	 * 
	 * <p>Wraps a call to <code>java.io.InputStream.read()</code>.</p>
	 * 
	 * @see java.io.InputStream#read()#read()
	 * @return int, the byte read from the stream
	 * @throws IOException
	 */	
	public int read() throws IOException
	{
		int read = mInputStream.read();
		fireRXEvent(new String(Character.toChars(read)));
		return read;
	}

	/**
	 * Should be called by users after reading sequences of characters from
	 * the input stream once the read operations for the command are complete.
	 */
	public void readComplete()
	{
		for (ConnectionListener listener : mConnectionListeners)
		{
			listener.rx(CommandUtils.CR);
		}
	}

	/**
	 * Should be called by users after writing sequences of characters to
	 * the output stream once the write operations for the command are
	 * complete.
	 */
	public void writeComplete()
	{
		for (ConnectionListener listener : mConnectionListeners)
		{
			listener.tx(CommandUtils.CR);
		}
	}

	/**
	 * Writes <code>byte</code> array to the OutputStream.
	 * 
	 * <p>Wraps a call to <code>java.io.OutputStream.write(byte[])</code>.</p>
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 * @throws IOException
	 */	
	public void write(byte[] bytes) throws IOException
	{
		mOutputStream.write(bytes);

		if (isNetworkPort())
		{
			flushOutput();
		}
		fireTXEvent(new String(bytes));
	}

	/**
	 * Writes a single <code>byte</code> to the OutputStream.
	 * 
	 * <p>Wraps a call to <code>java.io.OutputStream.write(byte)</code>.</p>
	 * 
	 * @see java.io.OutputStream#write(byte)
	 * @param byte the byte to write
	 * @throws IOException
	 */	
	public void write(byte single) throws IOException
	{
		mOutputStream.write(single);

		if (isNetworkPort())
		{
			flushOutput();
		}
		fireRXEvent(Byte.toString(single));
	}

	/**
	 * Write the passed  string to the output stream.
	 * 
	 * @param cmd
	 * @throws IOException
	 */
	public void write(String cmd) throws IOException
	{
		if (cmd == null || "".equals(cmd))
		{
			return;
		}

		if (isConnected())
		{
			int length = cmd.length() / 2;
	
			byte[] cmdBytes = new byte[length];
			int index = 0;
			for (int i = 0; i < cmd.length() / 2; i++)
			{
				String cb = cmd.substring(2*i, 2*i + 2);
				cmdBytes[index++] = (byte) Integer.parseInt(cb, 16);
			}

			write(cmdBytes);
			if (isNetworkPort())
			{
				flushOutput();
			}
			fireRXEvent(new String(cmdBytes));
		}
	}

	/**
	 * Flushes the OutputStream.
	 * 
	 * <p>Wraps a call to <code>int java.io.OutputStream.flush()</code>.</p>
	 * 
	 * @see int java.io.OutputStream
	 * @throws IOException
	 */
	public void flushOutput() throws IOException
	{
		mOutputStream.flush();
	}

	/**
	 * Flushes the InputStream.
	 * 
	 * <p>Wraps a call to <code>int java.io.InputStream.flush()</code>.</p>
	 * 
	 * @see int java.io.InputStream
	 * @throws IOException
	 */
	public String flushIntput() throws IOException
	{
		StringBuilder sb = new StringBuilder("Flushed: ");
		while (isConnected() && mInputStream.available() > 0)
		{
			sb.append(mInputStream.read());
		}
		return sb.toString();
	}

	/**
	 * Returns true if the connection is live.
	 * 
	 * <p>In fact this method checks if both the <code>InputStream</code>
	 * and <code>OutputStream</code> are not <code>null</code> thus indicating
	 * that the connection is live.</p>
	 * 
	 * @return true iff connection is live
	 */
	public boolean isConnected()
	{
		return mOutputStream != null && mInputStream != null;
	}

	/**
	 * Returns true if this connection uses a network port.
	 * 
	 * <p>A network port is distinguished from a standard Comm port.</p>
	 * 
	 * @return true if this connection uses a network port
	 */
	public boolean isNetworkPort()
	{
		return mIsNetwork;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#componentAdded(uk.co.dancowan.robots.hal.core.Component)
	 */
	@Override
	public void componentAdded(Component component)
	{
		// No Components effect this component.
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#componentRemoved(uk.co.dancowan.robots.hal.core.Component)
	 */
	@Override
	public void componentRemoved(Component component)
	{
		// No Components effect this component.
	}

	/*
	 * Opens the serial port to establish communication.
	 */
	private boolean openSerialPort()
	{
		if (mComPort == null || "".equals (mComPort))
		{

			for (ConnectionListener listener : mConnectionListeners)
			{
				listener.error("Port not configured.");
			}
			return false;
		}
		
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements())
		{
			CommPortIdentifier portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				if (mComPort.equals(portId.getName()))
				{
					try
					{
						mSerialPort = (SerialPort) portId.open("SRV Console", 0);
					}
					catch (PortInUseException e)
					{
						ERROR_LOGGER.finest("SRV1Connection.openSerialPort(): " + e.getMessage());
						fireErrorEvent(e.getMessage());
						return false;
					}

					setBps(BAUD_RATE);

					try
					{
						mOutputStream = mSerialPort.getOutputStream();
						mInputStream = new BufferedInputStream(mSerialPort.getInputStream(), 16384);
					}
					catch (IOException e)
					{
						ERROR_LOGGER.finest("SRV1Connection.openSerialPort(): " + e.getMessage());
						fireErrorEvent(e.getMessage());
						return false;
					}

					mSerialPort.setDTR(false);
					mSerialPort.setRTS(false);

					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Communication error, fire event
	 */
	private void fireTXEvent(String message)
	{
		for (ConnectionListener listener : mConnectionListeners)
		{
			listener.tx(message);
		}
	}

	/*
	 * Communication error, fire event
	 */
	private void fireRXEvent(String message)
	{
		for (ConnectionListener listener : mConnectionListeners)
		{
			listener.rx(message);
		}
	}

	/*
	 * Communication error, fire event
	 */
	private void fireErrorEvent(String message)
	{
		for (ConnectionListener listener : mConnectionListeners)
		{
			listener.error(message);
		}
	}

	/*
	 * Connection made or broken, fire event
	 */
	private void fireConnectionEvent(String id)
	{
		for (ConnectionListener listener : mConnectionListeners)
		{
			if (CONNECTED.equals(id))
				listener.connected();
			else
				listener.disconnected();
		}
	}

	/*
	 * Close all the streams and ports.
	 */
	private void closeSerialPort()
	{
		try
		{
			if (mOutputStream != null)
			{
				mOutputStream.close();
			}
			if (mInputStream != null)
			{
				mInputStream.close();
			}
			if (mSerialPort != null)
			{
				mSerialPort.close();
			}
			mOutputStream = null;
			mInputStream = null;
			mSerialPort = null;
		}
		catch (IOException e)
		{
			fireErrorEvent(e.getMessage());
			ERROR_LOGGER.finest("SRV1Connection.closeSerialPort(): " + e.getMessage());
		}
	}

	/*
	 * Open connection to networked SRV
	 */
	private boolean openNetworkSRV()
	{	
		if (mHost != null && mPort > 0)
		{
			try
			{
				mSocket = new Socket(mHost, mPort);
				mInputStream = new BufferedInputStream(mSocket.getInputStream());
				mOutputStream = new BufferedOutputStream(mSocket.getOutputStream());
				return true;
			}
			catch (IOException e)
			{
				for (ConnectionListener listener : mConnectionListeners)
				{
					listener.error(e.getMessage());
				}
				ERROR_LOGGER.finest("SRV1Connection.openNetworkSRV(): " + e.getMessage());
				return false;
			}
		}
		else
		{
			String msg = "Network host and port not properly configured.";
			for (ConnectionListener listener : mConnectionListeners)
			{
				listener.error(msg);
			}
			INFO_LOGGER.finest("SRV1Connection.openNetworkSRV():" + msg);
			return false;
		}
	}

	/*
	 * Set the serial port parameters
	 */
	private boolean setBps(int baud)
	{
		boolean sucess = false;
		try
		{
			mSerialPort.setSerialPortParams (baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			mSerialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			sucess = true;
		}
		catch (UnsupportedCommOperationException e)
		{
			INFO_LOGGER.finest("SRV1Connection.setBPS(): " + e.getMessage());
			fireErrorEvent(e.getMessage());
		}

		return sucess;
	}
}
