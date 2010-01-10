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
package uk.co.dancowan.robots.srv.hal.commands.camera;

import java.io.IOException;

import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.commands.AbstractCommand;
import uk.co.dancowan.robots.srv.hal.camera.ColourBin;

/**
 * Extension of AbstractCommand which sets the parameters of a colour bin
 * on the SRV1q.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class SetBinCmd extends AbstractCommand
{
	public static final String ID = "setBin";
	private static final String COMMAND = "vc";

	private final ColourBin mBin;

	/**
	 * C'tor.
	 * 
	 * @param yuv the YUV instance to send
	 * @param threshold the -/= threshold of the bin
	 */
	public SetBinCmd(ColourBin bin)
	{
		// expect newline char
		super(-1, false);

		mBin = bin;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.srv1.commands.AbstractCommand#getName()
	 */
	@Override
	public String getName()
	{
		return ID + "(" + mBin.getParameterString() + ")";
	}

	/**
	 * Creates a hexadecimal command.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.srv1.commands.AbstractCommand#getCommandString()
	 */
	@Override
	public String getCommandString()
	{
		StringBuilder sb = new StringBuilder(COMMAND);
		sb.append(mBin.getParameterString());
		return sb.toString();
	}

	/**
	 * Writes the byte translation of the result of a call to <code>
	 * getCommandString()</code> to the output stream.
	 * 
	 * @param srv the SRV1 instance
	 */
	@Override
	protected void write(CommandQ srv) throws IOException
	{
		Connection connection = srv.getConnection();
		if (connection.isConnected())
		{
			connection.write(getCommandString().getBytes());
			connection.writeComplete();
		}
	}
}
