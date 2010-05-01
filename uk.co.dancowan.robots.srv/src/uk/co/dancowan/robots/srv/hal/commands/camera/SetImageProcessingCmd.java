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
public class SetImageProcessingCmd extends SendString
{
	private final int mControl;

	/**
	 * C'tor.
	 * 
	 * @param controlLevel
	 */
	public SetImageProcessingCmd(int controlLevel)
	{
		// Command is decimal, so translate = true
		super("va" + Integer.toString(controlLevel), true);

		mControl = controlLevel;
	}

	/**
	 * Implementation returns resolution command.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#getName()
	 */
	@Override
	public String getName()
	{
		return "va" + mControl;
	}
}
