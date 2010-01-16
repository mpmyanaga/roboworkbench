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
package uk.co.dancowan.robots.srv.hal.camera;

import org.eclipse.jface.preference.IPreferenceStore;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.commands.camera.GrabImageCmd;
import uk.co.dancowan.robots.srv.ui.preferences.PreferenceConstants;

/**
 * Poll worker thread adds <code>GetImage</code> commands to the
 * SRV with a configurable delay.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CameraPollThread extends Thread
{
	private final GrabImageCmd mCommand;
	private final Object mMutex = new Object();

	private int mPollDelay;
	private boolean mShouldRun;
	private boolean mRunning;
	private boolean mShouldTerminate;

	/**
	 * C'tor.
	 * 
	 * @param decoder a FrameDecoder instance to handle the byte stream
	 */
	public CameraPollThread(FrameDecoder decoder)
	{
		mCommand = new GrabImageCmd(decoder);

		mShouldRun = false;
		mRunning = false;
		mShouldTerminate = false;
		
		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		mPollDelay = store.getInt(PreferenceConstants.CAMERA_POLL_DELAY);
		
		setName("Camera Poll Thread");
	}

	/**
	 * Adds the <code>Command</code> to the srv1 command queue and then
	 * delays for <code>pollDelay</code> ms before repeating.
	 * 
	 * @see java.lang.Thread
	 */
	@Override
	public void run()
	{
		long lastPoll = System.currentTimeMillis();
		while ( ! shouldTerminate())
		{
			if (System.currentTimeMillis() > lastPoll + getPollDelay())
			{
				if (shouldRun())
				{
					mRunning = true;
					mCommand.resume();
					lastPoll = System.currentTimeMillis();
					SrvHal.getCommandQ().addCommand(mCommand);
				}
				else
				{
					mRunning = false;
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						// NOP
					}
				}
			}
			else
			{
				try
				{
					Thread.sleep(200);
				}
				catch (InterruptedException e)
				{
					// NOP
				}
			}
		}
	}

	/**
	 * Stop this thread from polling for images.
	 * 
	 * <p>synchronized</p>
	 */
	public void stopPoll()
	{
		synchronized(mMutex)
		{
			mShouldRun = false;
		}
	}

	public boolean isPolling()
	{
		return mRunning;
	}

	/**
	 * Cause this thread to resume polling for images.
	 * 
	 * <p>synchronized</p>
	 */
	public void restartPoll()
	{
		synchronized(mMutex)
		{
			mShouldRun = true;
		}
	}

	/**
	 * Cause this thread to end.
	 */
	public void terminate()
	{
		synchronized(mMutex)
		{
			mShouldTerminate = true;
		}
	}

	/**
	 * Set the Poll delay in milliseconds.
	 * 
	 * <p>synchronized</p>
	 * 
	 * @param delay millisecond delay
	 */
	public void setPollDelay(int delay)
	{
		synchronized(mMutex)
		{
			mPollDelay = delay;
			IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
			store.setValue(PreferenceConstants.CAMERA_POLL_DELAY, mPollDelay);
		}
	}

	/**
	 * Returns the delay in milliseconds.
	 * 
	 * <p>Synchronized</p>
	 * 
	 * @return int delay in milliseconds
	 */
	public int getPollDelay()
	{
		synchronized(mMutex)
		{
			return mPollDelay;
		}
	}

	/*
	 * Synchronized check on the run flag.
	 */
	private boolean shouldRun()
	{
		synchronized(mMutex)
		{
			return mShouldRun;
		}
	}

	/*
	 * Synchronized check on the terminate flag.
	 */
	private boolean shouldTerminate()
	{
		synchronized(mMutex)
		{
			return mShouldTerminate;
		}
	}
}
