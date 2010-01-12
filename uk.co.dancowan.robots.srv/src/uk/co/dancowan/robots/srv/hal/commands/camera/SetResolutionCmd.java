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
public class SetResolutionCmd extends SendString
{
	public static final String ID = "setResolution";

	public static final String TINY = "a";
	public static final String SMALL = "b";
	public static final String MEDIUM = "c";
	public static final String LARGE = "d";

	private String mResolution;

	/**
	 * C'tor.
	 *
	 * <p>Resolution parameter should be one amongst:
	 * <ol><li>TINY - (160 x 128)</li>
	 * <li>SMALL - (320 x 256)</li>
	 * <li>MEDIUM - (640 x 512)</li>
	 * <li>LARGE - (1280 x 1024)</li></ol></p>
	 * 
	 * @param resolution the image resolution to send
	 */
	public SetResolutionCmd(String resolution)
	{
		// Command is decimal, so translate = true
		// Command response is fixed, no new line
		super(resolution, 2, true);

		mResolution = resolution;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.srv1.commands.SendString#getName()
	 */
	@Override
	public String getName()
	{
		return ID + "(" + mResolution + ")";
	}
}