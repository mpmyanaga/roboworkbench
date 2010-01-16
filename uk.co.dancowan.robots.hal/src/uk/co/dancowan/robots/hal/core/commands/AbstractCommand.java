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
package uk.co.dancowan.robots.hal.core.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Base class for Commands.
 * 
 * <p>Implements numerous helper methods.</p>
 * <p>Extending classes should...</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 *
 */
public abstract class AbstractCommand implements Command, Comparable<Command>
{
	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);

	private static final int READ_LOOP_TIMEOUT = 300;
	private static final int DEFAULT_PRIORITY = 100;
	private static final byte[] DEFAULT_HEADER = new byte[]{'#'};

	private final List<CommandListener> mListeners;
	private final List<CommandListener> mRemovals;
	
	private final Object mMutex = new Object();
	private boolean mShouldRun;
	private final boolean mIsInterruptable;

	private int mExpectCharacterCount;

	/**
	 * C'tor
	 * 
	 * @param isInteruptable boolean
	 */
	public AbstractCommand(int expectCharCount, boolean isInteruptable)
	{
		mIsInterruptable = isInteruptable;
		mExpectCharacterCount = expectCharCount;

		mListeners = new ArrayList<CommandListener>();
		mRemovals = new ArrayList<CommandListener>();
		mShouldRun = true;
	}

	/**
	 * C'tor
	 * 
	 * @param isInteruptable boolean
	 */
	public AbstractCommand(boolean isInteruptable)
	{
		this(-1, isInteruptable);
	}

	/**
	 * C'tor
	 * 
	 * <p>Default command is not interruptable. Equivalent to:</p>
	 * <pre><code>    new AbstractCommand(-1, false)</code></pre>
	 * <p>Command expects to terminate reading a new line character.</p>
	 */
	public AbstractCommand()
	{
		this(-1, false);
	}

	/**
	 * C'tor
	 * 
	 * <p>Default command is not interruptable. Equivalent to:</p>
	 * <pre><code>    new AbstractCommand(len, false)</code></pre>
	 * <p>Command expects to terminate reading configured number of characters.</p>
	 * 
	 * @param expectedLength the number of characters to read as a response
	 */
	public AbstractCommand(int expectedLength)
	{
		this(expectedLength, false);
	}

	/**
	 * Adds the passed listener to the collection of listeners to be notified of command events.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command
	 * @param listener CommandListener instance
	 */
	@Override
	public void addListener(CommandListener listener)
	{
		mListeners.add(listener);		
	}

	/**
	 * Removes the passed listener from the collection of listeners to be notified of command events.
	 * 
	 * <p>Listener is added to a list for removal outside of listener iteration
	 * to avoid a <code>ConcurrentModificationException</code> being thrown
	 * if the listener needs to remove itself during an event.</p>
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command
	 * @param listener CommandListener instance
	 */
	@Override
	public void removeListener(CommandListener listener)
	{
		mRemovals.add(listener);		
	}

	/**
	 * Called by the <code>CommandQ</code> instance to request this command to perform its action.
	 * 
	 * <p>Implement <code>read()</code> and <code>write()</code> methods to read and write the
	 * command essentials to the robot. Fires execute and completion events to all listeners.</p>
	 *  
	 * @param cmdQ the CommandQ instance
	 */
	@Override
	public void execute(CommandQ cmdQ) // throws execution exception
	{
		try
		{
			if (shouldRun())
			{
				fireExecuteEvent();
				Connection connection = cmdQ.getConnection();
				write(cmdQ);
				int timeout = 0;

				// wait for some data to process
				while (connection.available() <= 0 && timeout < 300 && mShouldRun)
				{
					timeout++;
					try
					{
						Thread.sleep(50);
					}
					catch (InterruptedException ie)
					{
						//NOP
					}
					continue;
				}
				if (connection.available() > 0)
					fireCompletionEvent(read(cmdQ));
				else
					failed("Timed out waiting for initial response.");
			}
		}
		catch (IOException e)
		{
			failed(e.getMessage());
		}
		catch(Exception e)
		{
			failed(e.getMessage());
		}
	}

	/**
	 * Callback for implementations to provide the command string
	 * to execute.
	 * 
	 * @return String the command to execute.
	 */
	protected abstract String getCommandString();

	/**
	 * Writes the byte translation of the result of a call to <code>
	 * getCommandString()</code> to the output stream.
	 * 
	 * @param cmdQ the CommandQ instance
	 */
	protected void write(CommandQ cmdQ) throws IOException
	{
		Connection connection = cmdQ.getConnection();
		if (connection.isConnected())
		{
			String cmd = getCommandString();
			connection.write(cmd);
			connection.writeComplete();
		}
	}

	/**
	 * Read the data from the connection
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#read(CommandQ)
	 * @param cmdQ the CommandQ instance
	 */
	protected String read(CommandQ cmdQ) throws IOException
	{
		Connection connection = cmdQ.getConnection();
		StringBuilder sb = new StringBuilder();
		sb.append(consumeHeader(connection));
		
		int timeout = 0;
		while (timeout < READ_LOOP_TIMEOUT && shouldRun())
		{
			// wait for some data to process
			if (connection.available() <= 0)
			{
				timeout++;
				try
				{
					Thread.sleep(50);
				}
				catch (InterruptedException ie)
				{
					//NOP
				}
				continue;
			}

			// process response stream characters
			while (connection.available() > 0)
			{
				char c = (char) connection.read();
				if (mExpectCharacterCount == -1)
				{
					if (CommandUtils.isEnd(c))
					{
						connection.readComplete();
						return sb.toString();
					}
					else
					{
						sb.append(c);					
					}
				}
				else // expected character count
				{
					sb.append(c);
					if (sb.length() == mExpectCharacterCount)
					{
						connection.readComplete();
						return sb.toString();
					}
				}
			}
		}
		failed(shouldRun() ? "Timed out reading response." : "Interrupted");
		connection.readComplete();
		return sb.toString();
	}

	/**
	 * Return this command's name.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command
	 */
	@Override
	public abstract String getName();

	/**
	 * Called to announce this command failed.
	 * 
	 * <p>Override to add additional action on failure. Default implementation
	 * fires a commandFailed event to listeners.</p>
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command
	 */
	@Override
	public void failed(String reason)
	{
		fireFailedEvent(getName() + " failure: " + reason);
	}

	/**
	 * Default implementation returns a priority of 100.
	 * 
	 * <p>Override to return a different priority.</p>
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#getPriority()
	 */
	@Override
	public int getPriority()
	{
		return DEFAULT_PRIORITY;
	}

	/**
	 * Interrupts the running command.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#interrupt()
	 */
	@Override
	public boolean interrupt()
	{
		if (mIsInterruptable)
		{
			synchronized(mMutex)
			{
				mShouldRun = false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Allow this command to resume running.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#resume()
	 */
	public void resume()
	{
		synchronized(mMutex)
		{
			mShouldRun = true;
		}
	}

	/**
	 * Checks this command's run flag
	 * 
	 * @return boolean
	 */
	protected boolean shouldRun()
	{
		synchronized(mMutex)
		{
			return mShouldRun;
		}
	}

	/**
	 * Inform all <code>CommandListeners</code> that the execution has begun.
	 */
	protected void fireExecuteEvent()
	{
		removeListeners();
		INFO_LOGGER.finer(getName());
		for (CommandListener listener : mListeners)
		{
			listener.commandExecuted(new CommandEvent(this, " executed."));
		}
		removeListeners();
	}

	/**
	 * Pass the failure message to all <code>CommandListeners</code>
	 * @param result
	 */
	protected void fireFailedEvent(String result)
	{
		removeListeners();
		INFO_LOGGER.finer(result);
		for (CommandListener listener : mListeners)
		{
			listener.commandFailed(new CommandEvent(this, result));
		}
		removeListeners();
	}

	/**
	 * Pass the completion result message to all <code>CommandListeners</code>.
	 * 
	 * @param result the response to the command
	 */
	protected void fireCompletionEvent(String result)
	{
		removeListeners();
		INFO_LOGGER.finer(result);
		for (CommandListener listener : mListeners)
		{
			listener.commandCompleted(new CommandEvent(this, result));
		}
		removeListeners();
	}

	/**
	 * Returns the commands response header.
	 * 
	 * @return byte[]
	 */
	protected byte[] getHeader()
	{
		return DEFAULT_HEADER;
	}

	/**
	 * Reads the input stream to consume the command's header.
	 * 
	 * @param connection
	 * @return String the header characters
	 */
	protected String consumeHeader(Connection connection) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int mark = 0;
		int count = 0;
		byte[] header = getHeader();
		while(mark < header.length && shouldRun() && count < READ_LOOP_TIMEOUT)
		{
			count ++;
			while (connection.available() > 0 && mark < getHeader().length && mShouldRun)
			{
				byte b = (byte) connection.read();
				if (getHeader()[mark] == (char) b)
				{
					mark++;
					sb.append((char) b);
				}
				else
				{
					mark = 0;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Implementation of Comparable interface.
	 * 
	 * <p>Allows commands to move up the command queue.</p>
	 */
	@Override
	public int compareTo(Command cmd)
	{
		return getPriority() - cmd.getPriority(); 
	}

	/*
	 * Remove unwanted listeners.
	 */
	private void removeListeners()
	{
		if (mRemovals.size() > 0)
			mListeners.removeAll(mRemovals);
	}
}
