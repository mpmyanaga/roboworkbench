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
package uk.co.dancowan.robots.srv.hal.commands.featuredetector;

import java.io.IOException;

import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.commands.AbstractCommand;
import uk.co.dancowan.robots.hal.core.commands.CommandUtils;

/**
 * Command to grab a neural network pattern stored in the SRV1's memory.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class GrabPatternCmd extends AbstractCommand
{
	private static final String HEADER = "##nd";
	private static final String COMMAND = "nd";
	private static final int EXPECTED_LINES = 9;
	private static final int PRIORITY = 1000;
	private static final int TIMEOUT = 300;
	
	private final int mIndex;

	/**
	 * C'tor.
	 *
	 * <p>Sets the index of the <code>Pattern</code> to grab from the SRV1.
	 * 
	 * @param index the index of the Pattern 0 <= index < 16
	 */
	public GrabPatternCmd(int index)
	{
		mIndex = index;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getHeader()
	 */
	@Override
	public byte[] getHeader()
	{
		return HEADER.getBytes();
	}

	/**
	 * Implementation returns a priority of 1000.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#getPriority()
	 */
	@Override
	public int getPriority()
	{
		return PRIORITY;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	protected String getCommandString()
	{
		return COMMAND + Integer.toHexString(mIndex);
	}

	/**
	 * Writes the byte translation of the result of a call to <code>
	 * getCommandString()</code> to the output stream.
	 * 
	 * @param cmdQ the CommandQ instance
	 */
	@Override
	protected void write(CommandQ cmdQ) throws IOException
	{
		Connection connection = cmdQ.getConnection();
		if (connection.isConnected())
		{
			connection.write(getCommandString().getBytes());
			connection.writeComplete();
		}
	}

	/**
	 * Read the data from the connection into a String.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#read(CommandQ)
	 * @param cmdQ the CommandQ instance
	 */
	@Override
	protected String read(CommandQ cmdQ) throws IOException
	{
		Connection connection = cmdQ.getConnection();
		StringBuilder sb = new StringBuilder();
		sb.append(consumeHeader(connection));
		
		int timeout = 0;
		int lineCount = 0;
		while (timeout < TIMEOUT && shouldRun())
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

			char c2 = (char) connection.read();
			while (connection.available() > 0)
			{
				char c1 = (char) connection.read();
				if (CommandUtils.isEnd(c1) && CommandUtils.isEnd(c2)) // looking for /r/n sequence
				{
					lineCount ++;
				}
				else
				{
					sb.append(c2);
					c2 = c1; // shuffle down the lookahead sequence
				}
				if (lineCount == EXPECTED_LINES)
				{
					connection.readComplete();
					return sb.toString();
				}
			}
		}
		failed(shouldRun() ? "Timed out reading response." : "Interrupted");
		connection.readComplete();
		return sb.toString();
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getName()
	 */
	@Override
	public String getName()
	{
		return getCommandString();
	}
}
