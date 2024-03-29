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
package uk.co.dancowan.robots.ui.views.actions;

import java.io.IOException;

import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.commands.AbstractCommand;

public class EscCmd extends AbstractCommand
{
	private static final String ESC = "ESC";

	/**
	 * C'tor
	 * 
	 * <p>Command anticipates no result string
	 */
	public EscCmd(Boolean result)
	{
		if (! result)
			noResult();
	}

	@Override
	protected String getCommandString()
	{
		return ESC;
	}

	/**
	 * Writes the escape character to the output stream
	 * 
	 * @param cmdQ the CommandQ instance
	 */
	@Override
	protected void write(CommandQ cmdQ) throws IOException
	{
		Connection connection = cmdQ.getConnection();
		connection.write((byte) 27);
		connection.writeComplete();
	}

	/**
	 * Implementation returns "".
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#getName()
	 */
	@Override
	public String getName()
	{
		return ESC;
	}
}
