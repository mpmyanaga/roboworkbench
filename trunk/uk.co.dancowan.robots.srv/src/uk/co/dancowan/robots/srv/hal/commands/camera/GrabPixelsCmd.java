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

import uk.co.dancowan.robots.hal.core.commands.CommandUtils;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.AbstractByteCommand;

/**
 * Grab the colour information for the configured area.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class GrabPixelsCmd extends AbstractByteCommand
{
	private static final String COMMAND = "vp";

	private final int mX;
	private final int mY;

	/**
	 * Sets the <code>PalletteItem</code> upon which this command operates
	 * 
	 * @param x
	 * @param y
	 */
	public GrabPixelsCmd(int x, int y)
	{
		super();

		mX = x;
		mY = y;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	protected String getCommandString()
	{
		return COMMAND + CommandUtils.formatInt(mX, 4) + CommandUtils.formatInt(mY, 4);
	}
}
