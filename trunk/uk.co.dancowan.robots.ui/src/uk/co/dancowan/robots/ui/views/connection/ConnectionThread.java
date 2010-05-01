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
package uk.co.dancowan.robots.ui.views.connection;

import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.HALRegistry;
import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Class used to establish and monitor a connection to a robot.
 * 
 * <p>This class's run method starts by getting the <code>Connection</code> object from
 * the ,code>HALRegisrty</code> instance and attempts to open a connection. The thread then
 * monitors the connection status reporting forced closure to the <code>Connection</code> object.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 *
 */
public class ConnectionThread extends Thread
{
	private static final Logger LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);

	private final Object mMutex = new Object();

	private boolean mAlive;

	/**
	 * C'tor
	 */
	public ConnectionThread()
	{
		mAlive = true;
		setName("Connection Thread");
	}

	/**
	 * Thread starts by making a connection, then polls the connection reporting closure
	 * back to the connection object itself in case of forced closure elsewhere.
	 */
	public void run()
	{
		LOGGER.finest("Connection thread started");
		Connection connection = HALRegistry.getInsatnce().getCommandQ().getConnection();
		connection.openConnection();
		connection = null;

		while (isRunning())
		{
			try
			{
				sleep(10000); // 10 seconds
			}
			catch (InterruptedException e)
			{
				return;
			}

			Connection con = HALRegistry.getInsatnce().getCommandQ().getConnection();
			if (con == null || ! con.checkConnection())
			{
				con.notifyDisconnection();
				LOGGER.finest("Connection thread exited due to external failure");
				return;
			}
		}
		LOGGER.finest("Connection thread terminated");
	}

	/**
	 * Called to check if this thread should continue to execute.
	 * 
	 * <p>Method is synchronised.</p>
	 * 
	 * @return boolean
	 */
	public boolean isRunning()
	{
		synchronized (mMutex)
		{
			return mAlive;
		}
	}

	/**
	 * Called to cause this thread to stop execution.
	 * 
	 * <p>Method is synchronised. When called, at the start of the next process
	 * loop the thread will return and die.</p>
	 */
	public void endConnection()
	{
		synchronized (mMutex)
		{
			mAlive = false;
		}
	}
}
