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
package uk.co.dancowan.robots.srv.services;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.srv.SRVConfig;

/**
 * Thread to push frames to the <code>WCSServer</code> instance.
 * 
 * <p>Derived from code written by the Surveyor Corporation (c) 2005 2009.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class WCSImageStreamer extends Thread
{
	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);
	private static final Logger ERROR_LOGGER = Logger.getLogger(LoggingService.ERROR_LOGGER);

	private boolean mShouldPush = false;
	private byte[] mWCSFrame = null;
	private Object mFrameLock = new Object();

	private Socket mSocket = null;
	private OutputStream mOutputStream = null;
	
	private String mServer;
	private String mPort;
	private String mCamID;
	private String mPassword;
	
	private boolean mInit;

	/**
	 * C'tor.
	 *
	 */
	public WCSImageStreamer()
	{
		mInit = false;
		setName("WCSPush thread");
	}

	/**
	 * Connects to the configured port and enables the push thread.
	 */
	public void connect()
	{
	    mShouldPush = true;
	}

	/**
	 * Closes the connection and pauses the push thread.
	 */
	public void disconnect()
	{
		mShouldPush = false;
		mWCSFrame = null;

		closeConnection();
	}

	/**
	 * Sets the internal frame to the passed <code>byte[]</code>.
	 * 
	 * <p>Method is synchronised.</p>
	 * 
	 * @param frame
	 */
	public void newFrame(byte[] frame)
	{
		if (WCSServer.getInstance().isStarted())
		{
			synchronized (mFrameLock)
			{
				mWCSFrame = frame;
				INFO_LOGGER.finest("WSCPush recieved a new frame.");
			}
		}
	}

	/**
	 * Starts this thread running.
	 * 
	 * <p>Thread uses lazy initialisation waiting for <code>Config.sProperties</code>
	 * to load. If a frame needs pushing and the service is connected then the current
	 * frame is sent and set <code>null</code>. Thread can be controlled by calling
	 * <code>disconnect()</code> or <code>connect()</code> accordingly.</p>
	 */
	public void run()
	{
		INFO_LOGGER.fine("WebCamSat push service started.");
		if (!mInit)
		{
			while (!SRVConfig.sIsReady)
			{
				try
				{
					Thread.sleep(50);
				}
				catch (InterruptedException e)
				{
					// NOP
				}

				mServer = SRVConfig.sProperties.get("wcs.server");
			    mPort = SRVConfig.sProperties.get("wcs.port");
			    mCamID = SRVConfig.sProperties.get("wcs.camID");
			    mPassword = SRVConfig.sProperties.get("wcs.pass");

			    mInit = true;
			}
		}

		while (mOutputStream != null)
		{
			if (!mShouldPush || mWCSFrame == null)
			{
				try
				{
					Thread.sleep (250);
				}
				catch (InterruptedException e)
				{
					// NOP
				}
				continue;
			}

			if (mOutputStream == null)
			{
				openConnection();
			}
            
			synchronized(mFrameLock)
			{
				String frameHead = "SVFR            ";
				try
				{
					mOutputStream.write(frameHead.getBytes ());
					mOutputStream.write(mWCSFrame);
					mOutputStream.flush();
				}
				catch (IOException e)
				{
					ERROR_LOGGER.finest("WCSPush.run(): " + e.getMessage());
					closeConnection();
		        }
        		mWCSFrame = null;
			}
		}
	}

	/*
	 * Close the connection on the port and clear up resources.
	 */
	private void closeConnection()
	{
		try
		{
			if (mOutputStream != null)
			{
				mOutputStream.close();
			}
			if (mSocket != null)
			{
				mSocket.close();
			}
			mOutputStream = null;
			mSocket = null;
		}
		catch (IOException e)
		{
			ERROR_LOGGER.finest("WCSPush.closeConnection(): " + e.getMessage());
		}

		INFO_LOGGER.fine("WCS closed connection to " + mServer + ":" + mPort);
	}

	/*
	 * Open a connection on the configured port and get the OutputStream.
	 */
	private void openConnection()
	{
		try
	    {
	    	mSocket = new Socket (mServer, Integer.parseInt(mPort));

	    	mOutputStream = new BufferedOutputStream (mSocket.getOutputStream ());
	    	mOutputStream.write (("SOURCE " + mCamID + " " + mPassword + " HTTP/1.1\r\n\r\n\r\n").getBytes ());
	    
	    }
	    catch(NumberFormatException e)
	    {
			ERROR_LOGGER.finest("WCSPush.openConnection(): " + e.getMessage());
		}
	    catch(IOException e)
	    {
			ERROR_LOGGER.finest("WCSPush.openConnection(): " + e.getMessage());
		}

		INFO_LOGGER.fine("WCS opened connection to " + mServer + ":" + mPort);
	}
}
