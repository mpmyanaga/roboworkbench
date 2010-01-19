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
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.commands.AbstractCommand;
import uk.co.dancowan.robots.hal.core.commands.CommandUtils;
import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Request blob information for the configured colour bin.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class GrabBlobCmd extends AbstractCommand
{
	private static final Logger ERROR_LOGGER = Logger.getLogger(LoggingService.ERROR_LOGGER);

	private static final String HEADER = "##vb";
	private static final String COMMAND = "vb";
	private static final int PRIORITY = 1000;
	private static final int TIMEOUT = 300;

	private int mBin;

	/**
	 * C'tor.
	 *
	 * @param bin the initial reference ColourBin
	 */
	public GrabBlobCmd(int bin)
	{
		mBin = bin;
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
		return COMMAND + mBin;
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
		int expectedLines = 1;
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
					if (lineCount == 0)
					{
						// first CR so trace back to parse expected line count
						expectedLines = countLines(sb.toString());
					}
					lineCount ++;
				}
				else
				{
					sb.append(c2);
					c2 = c1; // shuffle down the lookahead sequence
				}
				if (lineCount == expectedLines)
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

	private int countLines(String response)
	{
		int lines = 1; // include the header line
		if (response.contains(HEADER))
		{
			String part = response.substring(response.lastIndexOf(' ')).trim();
			try
			{
				lines += Integer.parseInt(part);
			}
			catch (NumberFormatException e)
			{
				ERROR_LOGGER.finest("Failed parsing line count from " + part);
			}
		}
		return lines;
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
