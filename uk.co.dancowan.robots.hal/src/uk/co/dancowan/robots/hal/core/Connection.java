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
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.core.commands.CommandUtils;
import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Handles establishing a connection to a robotic platform.
 * 
 * <p>Creates low level methods to connect to, read to and write from the connection's
 * I/O streams.</p>
 * 
 * <p><code>ConnectionListener</code>s may be added to track low level communications.</p>
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
	private static final int PING_TIMEOUT = 2000;
	private static final int COMMAND_TIMEOUT = 10000;

	private OutputStream mOutputStream;
	private InputStream mInputStream;

	private String mHost;
	private int mPort;
	private String mComPort;
	private Socket mSocket;
	private SerialPort mSerialPort;
	private boolean mIsNetwork;
	private long mLastCmd;

	private List<ConnectionListener> mConnectionListeners;

	private final Object mMutex = new Object();

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
		mLastCmd = System.currentTimeMillis();
	}

	/**
	 * Return's the connections identifier: 'Connection'.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Component#getID()
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * Connection does not require thread startup.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Component#requiresThreadStartup()
	 */
	@Override
	public boolean requiresThreadStartup()
	{
		return false;
	}

	/**
	 * Adds the passed <code>ConnectionListener</code> to the collection of listeners
	 * notified of connection events.
	 * 
	 * @param listener the listener to be added
	 */
	public void addConnectionListener(ConnectionListener listener)
	{
		synchronized(mMutex)
		{
			mConnectionListeners.add(listener);
		}
	}

	/**
	 * Removes the passed <code>ConnectionListener</code> from the collection
	 * of listeners notified of connection events.
	 * 
	 * @param listener the listener to be removed
	 */
	public void removeConnectionListener(ConnectionListener listener)
	{
		synchronized(mMutex)
		{
			mConnectionListeners.remove(listener);
		}
	}

	/**
	 * Open a connection to the robot hardware.
	 * 
	 * <p>If configuration indicates this is a networked robot then a network
	 * connection is opened, otherwise a serial connection on a Com port is opened.</p>
	 */
	public void openConnection()
	{
		if (isNetworkPort())
		{
			if (openNetworkConnection())
			{
				INFO_LOGGER.fine("Connection network opened");
				fireConnectionEvent(CONNECTED);
			}
		}
		else
		{
			if (openSerialPort())
			{
				INFO_LOGGER.fine("Connection Com port opened");
				fireConnectionEvent(CONNECTED);
			}
		}
	}

	/**
	 * Closes the connection and tidies up the resources.
	 */
	public void closeConnection()
	{
		if (! isConnected())
			return;

		if ( ! isNetworkPort())
			closeSerialPort();

		fireConnectionEvent(DISCONNECTED);

		try
		{
			mOutputStream.close();
			mOutputStream = null;
		}
		catch(IOException e)
		{
			// NOP
		}
		finally
		{
			if (mOutputStream != null)
			{
				try
				{
					mOutputStream.close();
				}
				catch (IOException e2)
				{
					// NOP
				}
			}
			mOutputStream = null;
		}

		try
		{
			mInputStream.close();
			mInputStream = null;
		}
		catch(IOException e)
		{
			// NOP
		}
		finally
		{
			if (mInputStream != null)
			{
				try
				{
					mInputStream.close();
				}
				catch (IOException e2)
				{
					// NOP
				}
			}
			mInputStream = null;
		}

		try
		{
			mSocket.close();
		}
		catch(IOException e)
		{
			// NOP
		}
		mSocket = null;
		
		INFO_LOGGER.log(Level.FINE, "Connection closed");
		fireConnectionEvent(DISCONNECTED);
	}

	/**
	 * Sets the connection mode.
	 * 
	 * <p>Mode could be networked wireless or serial over Com.</p>
	 * 
	 * @param isNetwork
	 */
	public void setNetwork(boolean isNetwork)
	{
		mIsNetwork = isNetwork;
	}

	/**
	 * Sets the port number in wireless mode.
	 * 
	 * @param port the port number >= 0
	 */
	public void setNetworkPort(int port)
	{
		mPort = port;
	}

	/**
	 * Sets the host name or IP-address of the robot in network mode.
	 * 
	 * @param host
	 */
	public void setHost(String host)
	{
		mHost = host;
	}

	/**
	 * Returns the configured network port.
	 * 
	 * @return int the network port number
	 */
	public int getNetworkPort()
	{
		return mPort;
	}

	/**
	 * Returns the configured host name or ip-address.
	 * 
	 * @return String host name or address
	 */
	public String getHost()
	{
		return mHost;
	}

	/**
	 * Sets the Com port to use for the connection.
	 * 
	 * @param port the port ID to connect to.
	 */
	public void setComPort(String port)
	{
		mComPort = port;
	}

	/**
	 * Returns the Com port configured for this connection.
	 * 
	 * @return String
	 */
	public String getComPort()
	{
		return mComPort;
	}

	/**
	 * Returns a list of available Com ports.
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
	 */
	public int available()
	{
		try
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
		catch (IOException e)
		{
			fireErrorEvent(e.getMessage());
			return 0;
		}
	}

	/**
	 * Reads from the InputStream into the passed <code>byte[]</code> chunk.
	 * 
	 * <p>Wraps a call to <code>int java.io.InputStream.read(byte[], int, int)</code>.</p>
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 * @return int, the status of the call
	 * @throws IOException
	 */	
	public int read(byte[] chunk, int pos, int len) throws IOException
	{
		try
		{
			int read = mInputStream.read(chunk, pos, len);
			fireRXEvent(new String(chunk));
			return read;
		}
		catch (IOException e)
		{
			fireErrorEvent(e.getMessage());
			throw e;
		}
	}

	/**
	 * Reads a single <code>byte</code> from the InputStream.
	 * 
	 * <p>Wraps a call to <code>java.io.InputStream.read()</code>.</p>
	 * 
	 * @see java.io.InputStream#read()
	 * @return int, the byte read from the stream
	 * @throws IOException
	 */	
	public int read() throws IOException
	{
		try
		{
			int read = mInputStream.read();
			fireRXEvent(new String(Character.toChars(read)));
			return read;
		}
		catch (IOException e)
		{
			fireErrorEvent(e.getMessage());
			throw e;
		}
	}

	/**
	 * Must be called by users after reading sequences of characters from
	 * the input stream once the read operations for the command are complete.
	 * 
	 * <p>Signals loggers and other listeners that the read cycle is complete. If
	 * not called by users of the <code>Connection.read()</code> method the
	 * consequences are unknown.</p>
	 */
	public void readComplete()
	{
		synchronized(mMutex)
		{
			for (ConnectionListener listener : mConnectionListeners)
			{
				listener.rx(CommandUtils.CR);
			}
		}
	}

	/**
	 * Must be called by users after writing sequences of characters to
	 * the output stream once the write operations for the command are
	 * complete.
	 * 
	 * <p>Signals loggers and other listeners that the write cycle is complete. If
	 * not called by users of the <code>Connection.write(...)</code> methods the
	 * consequences are unknown.</p>
	 */
	public void writeComplete()
	{
		synchronized(mMutex)
		{
			for (ConnectionListener listener : mConnectionListeners)
			{
				listener.tx(CommandUtils.CR);
			}
		}
		mLastCmd = System.currentTimeMillis();
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
		try
		{
			if (mOutputStream != null)
			{
				mOutputStream.write(bytes);
		
				if (isNetworkPort())
				{
					flushOutput();
				}
				fireTXEvent(new String(bytes));
			}
		}
		catch (IOException e)
		{
			fireErrorEvent(e.getMessage());
			throw e;
		}
	}

	/**
	 * Writes a single <code>int</code> to the OutputStream.
	 * 
	 * <p>Wraps a call to <code>java.io.OutputStream.write(int)</code>.</p>
	 * 
	 * @see java.io.OutputStream#write(int)
	 * @param single the int to write
	 * @throws IOException
	 */	
	public void write(int single) throws IOException
	{
		try
		{
			if (mOutputStream != null)
			{
				mOutputStream.write(single);
		
				if (isNetworkPort())
				{
					flushOutput();
				}
				fireRXEvent(Integer.toString(single));
			}
		}
		catch (IOException e)
		{
			fireErrorEvent(e.getMessage());
			throw e;
		}
	}

	/**
	 * Writes a single <code>byte</code> to the OutputStream.
	 * 
	 * <p>Wraps a call to <code>java.io.OutputStream.write(byte)</code>.</p>
	 * 
	 * @see java.io.OutputStream#write(int)
	 * @param single the byte to write
	 * @throws IOException
	 */	
	public void write(byte single) throws IOException
	{
		try
		{
			if (mOutputStream != null)
			{
				mOutputStream.write(single);
		
				if (isNetworkPort())
				{
					flushOutput();
				}
				fireRXEvent(Byte.toString(single));
			}
		}
		catch (IOException e)
		{
			fireErrorEvent(e.getMessage());
			throw e;
		}
	}

	/**
	 * Write the passed  string to the output stream.
	 * 
	 * @param cmd
	 * @throws IOException
	 */
	public void write(String cmd) throws IOException
	{
		try
		{
			if (cmd == null || "".equals(cmd))
			{
				return;
			}
	
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
		catch (IOException e)
		{
			fireErrorEvent(e.getMessage());
			throw e;
		}
	}

	/**
	 * Flushes the OutputStream.
	 * 
	 * <p>Wraps a call to <code>int java.io.OutputStream.flush()</code>.</p>
	 * 
	 * @see java.io.OutputStream
	 * @throws IOException
	 */
	public void flushOutput() throws IOException
	{
		mOutputStream.flush();
	}

	/**
	 * Flushes the InputStream.
	 * 
	 * <p>Reads remaining bytes from the stream into the return string until the stream is empty.</p>
	 * 
	 * @see java.io.InputStream
	 * @throws IOException
	 * @return String the remaining bytes that were 'flushed' from the stream
	 */
	public String flushInput() throws IOException
	{
		try
		{
			StringBuilder sb = new StringBuilder("Flushed: ");
			while (mInputStream != null && mInputStream.available() > 0)
			{
				sb.append(mInputStream.read());
			}
			return sb.toString();

		}
		catch (IOException e)
		{
			fireErrorEvent(e.getMessage());
			throw e;
		}
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
	 * Pings the host to test connection.
	 * 
	 * <p>Command uses a timeout and is blocking.</p>
	 * 
	 * @return boolean
	 */
	public boolean ping()
	{
		try
		{
			return InetAddress.getByName(mHost).isReachable(PING_TIMEOUT);
		}
		catch (UnknownHostException e)
		{
			return false;
		}
		catch (IOException e)
		{
			return false;
		}
	}

	/**
	 * Checks the status of the connection.
	 * 
	 * <p>Command returns true if a command has been sent within the last 10 seconds
	 * and returns the result of a call to <code>Connection.ping()</code>. otherwise.</p>
	 * 
	 * @return boolean
	 */
	public boolean checkConnection()
	{
		if (System.currentTimeMillis() - mLastCmd > COMMAND_TIMEOUT)
			return ping();
		else
			return true;
	}

	/**
	 * Returns true if this connection uses a network port.
	 * 
	 * <p>A network port is distinguished from a standard Com port.</p>
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

	/**
	 * Callers can use this method to indicate that the connection has disconnected.
	 * 
	 * <p>Called by the <code>ConnectionThread</code> class which monitors the connection.
	 * A <code>null</code> message may be sent to fire the listeners to change states
	 * without reporting anything.</p>
	 */
	public void notifyDisconnection()
	{
		closeConnection();
		fireErrorEvent(null);
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
						ERROR_LOGGER.finest("Connection.openSerialPort(): " + e.getMessage());
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
						ERROR_LOGGER.finest("Connection.openSerialPort(): " + e.getMessage());
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
		synchronized(mMutex)
		{
			for (ConnectionListener listener : mConnectionListeners)
			{
				listener.tx(message);
			}
		}
	}

	/*
	 * Communication error, fire event
	 */
	private void fireRXEvent(String message)
	{
		synchronized(mMutex)
		{
			for (ConnectionListener listener : mConnectionListeners)
			{
				listener.rx(message);
			}
		}
	}

	/*
	 * Communication error, fire event
	 */
	private void fireErrorEvent(String message)
	{
		synchronized(mMutex)
		{
			for (ConnectionListener listener : mConnectionListeners)
			{
				listener.error(message);
			}
		}
	}

	/*
	 * Connection made or broken, fire event
	 */
	private void fireConnectionEvent(String id)
	{
		synchronized(mMutex)
		{
			for (ConnectionListener listener : mConnectionListeners)
			{
				if (CONNECTED.equals(id))
					listener.connected();
				else
					listener.disconnected();
			}
		}
	}

	/*
	 * Close all the streams and ports.
	 */
	private void closeSerialPort()
	{
		if (mOutputStream != null)
		{
			try
			{
				mOutputStream.close();
			}
			catch (IOException e)
			{
				fireErrorEvent(e.getMessage());
				ERROR_LOGGER.finest("Connection.closeSerialPort(): " + e.getMessage());
			}
			finally
			{
				mOutputStream = null;
			}
		}
		if (mInputStream != null)
		{
			try
			{
				mInputStream.close();
			}
			catch (IOException e)
			{
				fireErrorEvent(e.getMessage());
				ERROR_LOGGER.finest("Connection.closeSerialPort(): " + e.getMessage());
			}
			finally
			{
				mInputStream = null;
			}
		}
		if (mSerialPort != null)
		{
			mSerialPort.close();
			mSerialPort = null;
		}
	}

	/*
	 * Open connection to networked robot
	 */
	private boolean openNetworkConnection()
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
				synchronized(mMutex)
				{
					for (ConnectionListener listener : mConnectionListeners)
					{
						listener.error(e.getMessage());
					}
				}
				ERROR_LOGGER.finest("Connection.openNetworkConnection(): " + e.getMessage());
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
			INFO_LOGGER.finest("Connection.openNetworkConnection():" + msg);
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
			INFO_LOGGER.finest("Connection.setBPS(): " + e.getMessage());
			fireErrorEvent(e.getMessage());
		}

		return sucess;
	}
}
