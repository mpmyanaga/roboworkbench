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
package uk.co.dancowan.robots.hal.core.commands;

import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;

/**
 * Implementation of the <code>CommandListener</code> interface which logs all
 * command event messages to the standard error stream (<code>system.err</code>).
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class DebugCommandListener implements CommandListener
{
	/**
	 * Command completed message sent to <code>system.err</code>.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.CommandListener#commandCompleted(uk.co.dancowan.robots.hal.core.CommandEvent)
	 */
	@Override
	public void commandCompleted(CommandEvent e)
	{
		System.err.println(e.getMessage());
	}

	/**
	 * Command execution started message sent to <code>system.err</code>.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.CommandListener#commandExecuted(uk.co.dancowan.robots.hal.core.CommandEvent)
	 */
	@Override
	public void commandExecuted(CommandEvent e)
	{
		System.err.println(e.getMessage());
	}

	/**
	 * Command failure message sent to <code>system.err</code>.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.CommandListener#commandFailed(uk.co.dancowan.robots.hal.core.CommandEvent)
	 */
	@Override
	public void commandFailed(CommandEvent e)
	{

		System.err.println("Failure " + e.getMessage());
	}
}
