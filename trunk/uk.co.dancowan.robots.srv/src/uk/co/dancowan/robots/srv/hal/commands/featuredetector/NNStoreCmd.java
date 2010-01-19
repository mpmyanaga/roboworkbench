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
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.ui.panels.PatternWidget;

/**
 * Stores a new pattern in the SRV1 memory space.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class NNStoreCmd extends AbstractCommand
{
	public static final String ID = "NNStore";

	private static final byte[] HEADER = new byte[] {'#', '#', 'n', 'p'};
	private static final String COMMAND = "np";

	private final PatternWidget mEditor;

	/**
	 * Sets the <code>PatternWidget</code> from which the pattern is taken.
	 */
	public NNStoreCmd(PatternWidget editor)
	{
		super(6);

		mEditor = editor;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getName()
	 */
	@Override
	public String getName()
	{
		String index = Integer.toHexString(SrvHal.getCamera().getDetector().getFocusPattern().getIndex());
		String hex = mEditor.getPattern().toHex();
		return ID + "(" + index + " " + hex + ")";
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	protected String getCommandString()
	{
		String index = Integer.toHexString(SrvHal.getCamera().getDetector().getFocusPattern().getIndex());
		return COMMAND + index + mEditor.getPattern().toHex();
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
