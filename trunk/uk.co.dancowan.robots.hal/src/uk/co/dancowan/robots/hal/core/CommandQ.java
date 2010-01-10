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
package uk.co.dancowan.robots.hal.core;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.logger.LoggingService;


/**
 * Manages the command communication between the console and the hardware SRV.
 * 
 * <p>Class uses a <code>BlockingQue</code> to pipe <code>Command</code> objects
 * from client code into a dispatch loop. Class creates an <code>SRV1Connection</code>
 * instance enabling two way communication between the dispatched <code>Command</code>
 * and the SRV hardware.</p>
 * 
 * <p>Where possible long running <code>Command</code> instances should be cancellable
 * by higher priority <code>Command</code>s at the top of the queue. All <Code>Command
 * </code> instances are ordered by priority in the queue.</p>
 */
public class CommandQ extends Thread implements Component
{
	public static final String ID = "CommandQ";

	private static final Logger LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);

	private final Object sMutex = new Object();
	private final Connection mConnection;
	private final BlockingQueue<Command> mCommandQ;

	private Command mCurrentCommand;
	private boolean mShouldRun;

	/**
	 * Creates a new SRV1 instance.
	 * 
	 * <p>Object will create a <code>Connection<code> instance
	 * for communication with the target device.</p>
	 */
	public CommandQ()
	{
		mConnection = new Connection();

		mCommandQ = new PriorityBlockingQueue<Command>();
		mShouldRun = true;

		setName("CommandQ thread");
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#getID()
	 */
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#requiresThreadStartup()
	 */
	public boolean requiresThreadStartup()
	{
		return true;
	}

	/**
	 * Returns the queue's <code>SRV1Connection</code> instance.
	 * 
	 * @return SRV1Connection
	 */
	public Connection getConnection()
	{
		return mConnection;
	}

	/**
	 * Implementation of <code>Thread.run()</code> method.
	 * 
	 * <p>Waits on commands to execute. If the run flag is set to <code>false
	 * <code> the thread will sleep briefly before checking the flag again and
	 * not execute the next command.</p>
	 * 
	 * @see java.lang.Thread
	 */
	public void run() 
	{
		while(true)
		{
			if(shouldRun())
			{
				try
				{
					mCurrentCommand = mCommandQ.poll(50, TimeUnit.MILLISECONDS);
					if (mCurrentCommand != null)
					{
						if (mConnection.isConnected())
						{
							mCurrentCommand.execute(this);
						}
						else
						{
							mCurrentCommand.failed("Not connected.");
						}
					}
				}
				catch (InterruptedException e)
				{
					// NOP
				}
	    	}
			else
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
		}
	}

	/**
	 * Stops the thread running additional commands.
	 * 
	 * <p>Uses a flag in the command dispatch loop to halt the loop, the
	 * <code>Thread</code> is not stopped or paused using normal Thread methods.</p>
	 */
	public void pause()
	{
		synchronized(sMutex)
		{
			mShouldRun = false;
		}
	}

	/**
	 * Checks if the thread can run additional commands.
	 * 
	 * @return the run flag for this queue
	 */
	public boolean shouldRun()
	{
		synchronized(sMutex)
		{
			return mShouldRun;
		}
	}

	/**
	 * Adds the Command to the command queue.
	 * 
	 * <p>Uses <code>BlockingQueue.offer(e)</code> method to insert
	 * the command only if there is room in the queue.</p>
	 * 
	 * @param cmd the Command to add
	 */
	public void addCommand(Command cmd)
	{
		if (cmd != null)
		{
			if (mConnection.isConnected())
			{
				if (mCommandQ.contains(cmd))
					LOGGER.finest(cmd.getName() + " already in the command queue, command not added.");
				else
					mCommandQ.offer(cmd);
				checkInteruptions();
			}
		}
	}

	/**
	 * <code>SRV1</code> will add a <code>Connection</code> instance to the <code>ComponentRegistry</code>.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Component#componentAdded(uk.co.dancowan.robots.hal.core.Component)
	 */
	@Override
	public void componentAdded(Component component)
	{
		//if (component.getID().equals(ID))
			//HALRegistry.getInsatnce().register(mConnection);
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#componentRemoved(uk.co.dancowan.robots.hal.core.Component)
	 */
	@Override
	public void componentRemoved(Component component)
	{
		// No Components effect this component, it holds an internal reference
		// to its Connection class and so cannot be 'broken'.
	}

	/*
	 * Checks if the head of the command queue should interrupt the
	 * running command and does so if possible.
	 */
	private void checkInteruptions()
	{
		Command next = mCommandQ.peek();
		if (next != null && mCurrentCommand != null)
		{
			if (next.getPriority() < mCurrentCommand.getPriority())
			{
				LOGGER.finest(mCommandQ.peek().getName() + " interupting " + mCurrentCommand.getName());
				mCurrentCommand.interrupt();
				try
				{
					mConnection.flushIntput();
					mConnection.flushOutput();
				}
				catch (IOException e)
				{
					// NOP
				}
			}
		}
	}
}
