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
 * Grab the contents of the configured colour bin.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class GrabBinCmd extends AbstractCommand
{
	public static final String ID = "grabBin";

	private static final byte[] HEADER = new byte[] {'#', '#', 'v', 'r'};
	private static final String COMMAND = "vr";

	private int mBin;

	/**
	 * Sets the <code>PalletteItem</code> upon which this command operates
	 * 
	 * @param item the receiving PalletteItem
	 */
	public GrabBinCmd(int bin)
	{
		super();

		mBin = bin;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.srv1.commands.AbstractCommand#getName()
	 */
	@Override
	public String getName()
	{
		return ID + "(" + mBin + ")";
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.srv1.commands.AbstractCommand#getCommandString()
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
	 * @param srv the SRV1 instance
	 */
	protected void write(CommandQ srv) throws IOException
	{
		Connection connection = srv.getConnection();
		if (connection.isConnected())
		{
			connection.write(getCommandString().getBytes());
			connection.writeComplete();
		}
	}

	/**
	 * Overrides method in AbstractCommand to supply larger header.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.srv1.commands.AbstractCommand#getHeader()
	 */
	@Override
	public byte[] getHeader()
	{
		return HEADER;
	}
}
