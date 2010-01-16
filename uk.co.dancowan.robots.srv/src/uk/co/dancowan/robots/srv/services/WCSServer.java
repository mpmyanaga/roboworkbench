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

import uk.co.dancowan.robots.srv.SRVConfig;

import com.surveyor.wstreamd.StreamServer;

/**
 * Class starts a WebCamSat web server instance and establishes a thread (<code>
 * WCSImageStreamer</code> to stream images output from the SRV1 hardware.
 *
 * <p>Derived from code written by the Surveyor Corporation (c) 2005 2009.</p>
 *
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class WCSServer extends Thread
{
	private static final WCSServer sInstance = new WCSServer();

	private final Object mMutex = new Object();
	private final WCSImageStreamer mStreamer;
	private boolean mStarted;

	/*
	 * Singleton private constructor
	 */
	private WCSServer()
	{
		mStreamer = new WCSImageStreamer();
		setName("WCSServer thread");
		setStarted(false);
	}

	/**
	 * Get the singleton instance of this class.
	 * 
	 * @return WCSServer the instance
	 */
	public static WCSServer getInstance()
	{
		return sInstance;
	}

	/**
	 * Enable the thread which starts this web server.
	 * 
	 * <p>Also enables the <code>WCSImageStreamer</code> instance.</p>
	 */
	public void connect()
	{
		mStreamer.connect();
		setStarted(true);
	}

	/**
	 * Stops the image streamer and sets the 'started' state to false.
	 */
	public void disconnect()
	{
		mStreamer.disconnect();
		setStarted(false);
	}

	/**
	 * Checks to see if this service has started.
	 * 
	 * @return boolean
	 */
	public boolean isStarted()
	{
		synchronized(mMutex)
		{
			return mStarted;
		}
	}

	/**
	 * Returns the <code>WCSImageStreamer</p> instance.
	 * 
	 * <p>Classes which decode SRV1 hardware images into a <code>byte[]</code>
	 * can get the streamer to pass on images to the web server.</p>
	 * 
	 * @return WCSImageStreamer
	 */
	public WCSImageStreamer getImageStreamer()
	{
		return mStreamer;
	}

	/**
	 * Start the web service.
	 * 
	 * <p>Should be a short running thread.</p>
	 */
	@Override
	public void run()
	{
		// wait to ensure properties have loaded
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
		}
		// start the service with a port taken from the Config properties
		StreamServer.main(new String[] { SRVConfig.sProperties.get("wcs.port") });
		setStarted(true);
	}

	/*
	 * Sets the started flag
	 */
	private void setStarted(boolean started)
	{
		synchronized(mMutex)
		{
			mStarted = started;
		}
	}
}
