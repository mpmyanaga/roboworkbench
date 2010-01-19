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
package uk.co.dancowan.robots.srv.hal.commands.featuredetector;

import java.io.IOException;

import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.commands.AbstractCommand;
import uk.co.dancowan.robots.hal.core.commands.CommandUtils;
import uk.co.dancowan.robots.srv.hal.SrvHal;

/**
 * Match the largest detected blob against patterns stored in memory.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class NNMatchCmd extends AbstractCommand
{
	public static final String ID = "NNMatch";
	private static final int TIMEOUT = 300;

	private static final byte[] HEADER = new byte[] {'#', '#', 'n', 'b'};
	private static final String COMMAND = "nb";

	/**
	 * C'tor
	 */
	public NNMatchCmd()
	{
		super(-1);
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getName()
	 */
	@Override
	public String getName()
	{
		int bin = SrvHal.getCamera().getDetector().getFocusBin().getBin();
		return ID + "(" + bin + ")";
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	protected String getCommandString()
	{
		int bin = SrvHal.getCamera().getDetector().getFocusBin().getBin();
		return COMMAND + bin;
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
		int expectedLines = 2;
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

	/**
	 * Overrides method in AbstractCommand to supply larger header.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getHeader()
	 */
	@Override
	public byte[] getHeader()
	{
		return HEADER;
	}
}
