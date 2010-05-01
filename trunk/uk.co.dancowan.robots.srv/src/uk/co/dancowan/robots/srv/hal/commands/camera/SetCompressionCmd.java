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
package uk.co.dancowan.robots.srv.hal.commands.camera;

import uk.co.dancowan.robots.hal.core.commands.SendString;

/**
 * Command sets the resolution of the camera image.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class SetCompressionCmd extends SendString
{
	private final int mCompression;

	/**
	 * C'tor.
	 *
	 * @param compression int between 1 and 8 inc.
	 */
	public SetCompressionCmd(int compression)
	{
		// Command is hex, so translate = true
		super("q" + Integer.toString(compression), true);

		mCompression = compression;
	}

	/**
	 * Implementation returns resolution command.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#getName()
	 */
	@Override
	public String getName()
	{
		return "q" + mCompression;
	}
}
