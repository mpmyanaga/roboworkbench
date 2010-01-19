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

/**
 * Enable obstacle detection mode.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ObsticleDetectionCmd extends AbstractCommand
{
	public static final String ID = "ObstacleDetection";

	private static final byte[] HEADER = new byte[] {'#', '#', 'g'};
	private static final String COMMAND = "g4";

	/**
	 * C'tor
	 */
	public ObsticleDetectionCmd()
	{
		super(4);
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getName()
	 */
	@Override
	public String getName()
	{
		return ID + "()";
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	protected String getCommandString()
	{
		return COMMAND;
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
