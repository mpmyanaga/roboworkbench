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
package uk.co.dancowan.robots.hal.core;


/**
 * Interface for an SRV1 command.

 * @author Dan Cowan
 * @Since version 1.0.0
 */
public interface Command
{
	/**
	 * Adds this listener to the collection of listeners notified
	 * about <code>CommandEvent</code> instances.
	 * 
	 * @see CommandEvent
	 * @param listener CommandListener
	 */
	public void addListener(CommandListener listener);

	/**
	 * Removes this listener from the collection of listeners notified
	 * about <code>CommandEvent</code> instances.
	 * 
	 * @see CommandEvent
	 * @param listener CommandListener
	 */
	public void removeListener(CommandListener listener);
	
	/**
	 * Called to request that this command stop execution at the first
	 * possible opportunity and return control to the <code>SRV1</code>
	 * instance.
	 * 
	 * @return true iff this command can be (and was) interrupted
	 */
	public boolean interrupt();

	/**
	 * Requests this command resume polling.
	 * 
	 * <p>Commands exit their read write thread when interrupted so this method
	 * will do nothing for volatile commands and serves only to enable commands
	 * run in a polling queue which have been interrupted.</p>
	 */
	public void resume();

	/**
	 * Called by the <code>SRV</code> instance to request this
	 * command to perform its action.
	 * 
	 * @param SRV the SRV instance
	 */
	public void execute(CommandQ que);

	/**
	 * Call-back to notify of external failure.
	 * 
	 * <p><code>Command</code> instances should stop executing
	 * as soon as possible and tidy any resources.</p>
	 * 
	 * @param reason String, the reason for the failure
	 */
	public void failed(String reason);

	/**
	 * Returns the priority of this command.
	 * 
	 * <p>Priority should be a positive integer, larger values
	 * indicate a lower priority. Long running processes should
	 * normally be a low priority and enable cancellation.</p>
	 * 
	 * <p>Default priority is 100, long running commands such as
	 * image polling should be 1000.</p>
	 *  
	 * @return int, the priority
	 */
	public int getPriority();
	
	/**
	 * Return this commands name.
	 * 
	 * <p>Commands are often named to look like methods:
	 * <pre>    getImage()
	 *    motors()</pre>
	 * Key parameters are often appended:
	 * <pre>    motors() 100 100 200</pre></p>
	 * 
	 * @return String, the command's name
	 */
	public String getName();
}
