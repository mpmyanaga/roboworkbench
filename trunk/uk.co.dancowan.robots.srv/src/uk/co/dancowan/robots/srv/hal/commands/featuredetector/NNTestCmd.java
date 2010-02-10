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

import uk.co.dancowan.robots.srv.hal.SrvHal;

/**
 * Checks for a match against patterns in memory using the current focus pattern.
 * 
 * <p>Used to test that training against a certain pattern holds well enough for use.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class NNTestCmd extends AbstractByteCommand
{
	private static final String COMMAND = "nx";

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	protected String getCommandString()
	{
		String pattern = SrvHal.getCamera().getDetector().getFocusPattern().toHex();
		return COMMAND + pattern;
	}
}
