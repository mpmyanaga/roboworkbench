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
 * Interface for objects needing to monitor a <code>Command</code>.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public interface CommandListener
{
	/**
	 * Called when a <code>Command</code> begins execution.
	 * 
	 * @param event CommandEvent
	 */
	public void commandExecuted(CommandEvent event);
	
	/**
	 * Called when a <code>Command</code> completes execution.
	 * 
	 * @param event CommandEvent
	 */
	public void commandCompleted(CommandEvent event);

	/**
	 * Called when a <code>Command</code> fails to execute.
	 * 
	 * @param event CommandEvent
	 */
	public void commandFailed(CommandEvent event);
}
