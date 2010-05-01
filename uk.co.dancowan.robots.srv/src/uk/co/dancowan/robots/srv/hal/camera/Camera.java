/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (C) 2009  Dan Cowan
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

import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.Component;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.ConnectionListener;
import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.commands.camera.SetCompressionCmd;
import uk.co.dancowan.robots.srv.hal.commands.camera.SetImageProcessingCmd;
import uk.co.dancowan.robots.srv.hal.commands.camera.SetResolutionCmd;
import uk.co.dancowan.robots.srv.hal.featuredetector.FeatureDetector;

/**
 * Domain object to manage SRV camera data.
 * 
 * <p>Exposes methods to allow colour bin setting, blob grabbing and
 * associated vision actions. All vision commands should be directed through
 * the Camera instance as a facade to enable state and correct response decoding.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class Camera implements ConnectionListener, Component
{
	public static final String ID = "SRV Camera";

	private FeatureDetector mDetector;
	private FrameDecoder mFrameDecoder;
	private CameraPollThread mPollThread;

	private int mProcessControl;

	//private final List<CameraListener> mListeners;

	/**
	 * C'tor.
	 */
	public Camera()
	{
		//mListeners = new ArrayList<CameraListener>();
		mProcessControl = 7;
	}

	/**
	 * Configures the <Code>Camera</code> with a <code>CameraImageConsumer</code>
	 * instance.
	 * 
	 * @param consumer the typed CamerImageConsumer to use
	 */
	public void setConsumer(CameraImageConsumer<java.awt.Component> consumer)
	{
		// Destroy any existing consumer and the poll thread which used it
		if (mPollThread != null)
		{
			mPollThread.terminate();
			mPollThread = null;
			if (mFrameDecoder != null)
				mFrameDecoder.terminate();
		}

		mFrameDecoder = new FrameDecoder(consumer);
		mPollThread = new CameraPollThread(mFrameDecoder);
		mPollThread.start();
	}

	/**
	 * Returns the configured <code>FrameDecoder</code> instance.
	 * 
	 * <p>Method returns the object passed in the last call to</p>
	 * <pre><code>Camera.setFrameDecoder(FrameDecoder)</code></pre>
	 * <p>or null if no call has yet been made.
	 * 
	 * @return FrameDecoder the configured decoder
	 */
	public FrameDecoder getFrameDecoder()
	{
		return mFrameDecoder;
	}

	/**
	 * Returns the <code>FeatureDetector</code> subsystem object.
	 * 
	 * @return FeatureDetector
	 */
	public FeatureDetector getDetector()
	{
		return mDetector;
	}

	/**
	 * Makes calls to set up initial bin and blob state.
	 */
	public void initialise()
	{
		if (mDetector != null)
		{
			mDetector.initialise();
		}
	}

//	public void addListener(CameraListener listener)
//	{
//		mListeners.add(listener);
//	}
//
//	public void removeListener(CameraListener listener)
//	{
//		mListeners.remove(listener);
//	}

	/**
	 * Resumes polling for images.
	 */
	public void startPolling()
	{
		if (mPollThread != null)
		{
			mFrameDecoder.setShouldRun(true);
			mPollThread.restartPoll();
		}
		else
		{
			Logger.getLogger(LoggingService.ERROR_LOGGER).fine("Camera.startPolling found no FrameDecoder set");
		}
	}

	/**
	 * Stops the Camera from polling further images.
	 */
	public void stopPolling()
	{
		if (mPollThread != null)
		{
			mPollThread.stopPoll();
			mFrameDecoder.setShouldRun(false);
		}
		else
		{
			Logger.getLogger(LoggingService.ERROR_LOGGER).fine("Camera.stopPolling() found no FrameDecoder set");
		}
	}

	public boolean isPolling()
	{
		if (mPollThread == null)
			return false;
		else
			return mPollThread.isPolling();
	}

	/**
	 * Sets the poll delay.
	 * 
	 * @param delay new poll delay in 10 ms units
	 */
	public void setPollDelay(int delay)
	{
		if (mPollThread != null)
		{
			mPollThread.setPollDelay(delay);
		}
		else
		{
			Logger.getLogger(LoggingService.ERROR_LOGGER).fine("Camera.setPollDelay() found no FrameDecoder set");
		}
	}

	/**
	 * Return the current poll delay.
	 * 
	 * @return int poll delay in 10ms units
	 */
	public int getPollDelay()
	{
		return mPollThread.getPollDelay();
	}

	/**
	 * Return the approximate frame rate.
	 * 
	 * @return double frames per second
	 */
	public double getFPS()
	{
		if (mFrameDecoder != null)
			return mFrameDecoder.getFPS();
		else
			return 0.0;
	}

	public void setCompression(int compression)
	{
		CommandQ cmdQ = SrvHal.getCommandQ();
		if (cmdQ.shouldRun())
		{
			Command cmd = new SetCompressionCmd(compression);
			cmdQ.addCommand(cmd);
		}
	}


	public void setProcessControl(ProcessControlEnum mode, boolean state)
	{
		mProcessControl += state ? mode.getValue() : -mode.getValue();
		CommandQ cmdQ = SrvHal.getCommandQ();
		if (cmdQ.shouldRun())
		{
			Command cmd = new SetImageProcessingCmd(mProcessControl);
			cmdQ.addCommand(cmd);
		}
	}

	/**
	 * Sets the camera resolution.
	 * 
	 * <p>This method may be called by widgets with an interest in the
	 * command events so the Command is returned to allow listeners to
	 * be attached.</P>
	 */
	public void setResolution(Resolution resolution, CommandListener listener)
	{
		CommandQ cmdQ = SrvHal.getCommandQ();
		if (cmdQ.shouldRun())
		{
			Command cmd = new SetResolutionCmd(resolution);
			cmdQ.addCommand(cmd);
		}
	}

	/**
	 * Calls <code>Camera.initialize()</code> to fill bins and blobs.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#connected()
	 */
	@Override
	public void connected()
	{
		initialise();
	}

	/**
	 * Default implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#disconnected()
	 */
	@Override
	public void disconnected()
	{
		// NOP
	}

	/**
	 * Default implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#error(java.lang.String)
	 */
	@Override
	public void error(String error)
	{
		// NOP
	}

	/**
	 * Register this class as a listener to the Connection object.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Component#componentAdded(uk.co.dancowan.robots.hal.core.Component)
	 */
	@Override
	public void componentAdded(Component component)
	{
		if (component == this)
		{
			registerConnectionListener();
		}
		else if (component.getID().equals(CommandQ.ID) && component instanceof CommandQ)
		{
			Connection connection  = ((CommandQ) component).getConnection();
			connection.addConnectionListener(this);
		}
		else if (component.getID().equals(FeatureDetector.ID) && component instanceof FeatureDetector)
		{
			mDetector = (FeatureDetector) component;
		}
	}

	/**
	 * Unregisters the listener on the Connection object if the <code>
	 * CommandQ</code> is removed.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Component#componentRemoved(uk.co.dancowan.robots.hal.core.Component)
	 */
	@Override
	public void componentRemoved(Component component)
	{
		if (component.getID().equals(CommandQ.ID) && component instanceof CommandQ)
		{
			Connection connection  = ((CommandQ) component).getConnection();
			connection.removeConnectionListener(this);
		}
		else if (component.getID().equals(FeatureDetector.ID) && component instanceof FeatureDetector)
		{
			mDetector = null;
		}
	}

	/**
	 * Returns this class's static ID.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Component#getID()
	 * @return String the class ID
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * This class does not require starting, is not a Thread itself.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Component#requiresThreadStartup()
	 */
	@Override
	public boolean requiresThreadStartup()
	{
		return false;
	}

	/**
	 * Default implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#rx(java.lang.String)
	 */
	@Override
	public void rx(String readChars)
	{
		// NOP
	}

	/**
	 * Default implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#tx(java.lang.String)
	 */
	@Override
	public void tx(String writeChars)
	{
		// NOP
	}

	/*
	 * Attempt to register a listener on the Connection object
	 */
	private void registerConnectionListener()
	{
		Connection connection = SrvHal.getConnection();
		if (connection != null)
		{
			connection.addConnectionListener(this);
		}
	}

	public enum ProcessControlEnum
	{
		AGC (4),
		AWB (2),
		AEC (1);
	
		private final int mVal;
		private ProcessControlEnum(int val)
		{
			mVal = val;
		}

		public int getValue()
		{
			return mVal;
		}
	}

	public enum Resolution
	{
		TINY ("a"),
		SMALL ("b"),
		MEDIUM ("c"),
		LARGE ("d");

		private final String mCommand;
		private Resolution(String command)
		{
			mCommand = command;
		}

		public String getCommand()
		{
			return mCommand;
		}
	}
}
