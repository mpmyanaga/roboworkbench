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
package uk.co.dancowan.robots.hal.core;


/**
 * Interface for objects wishing to monitor a <code>Command</code>.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public interface CommandListener
{
	/**
	 * Called when a <code>Command</code> begins execution.
	 */
	public void commandExecuted(CommandEvent e);
	
	/**
	 * Called when a <code>Command</code> completes execution.
	 * 
	 * @param result CommandEvent
	 */
	public void commandCompleted(CommandEvent e);

	/**
	 * Called when a <code>Command</code> fails to execute.
	 * 
	 * @param result CommandEvent
	 */
	public void commandFailed(CommandEvent e);
}
