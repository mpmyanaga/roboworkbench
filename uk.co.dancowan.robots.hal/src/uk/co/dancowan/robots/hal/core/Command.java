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
 * Interface for a robot command.

 * @author Dan Cowan
 * @since version 1.0.0
 */
public interface Command
{
	/**
	 * Adds the passed listener to the collection of listeners notified
	 * about <code>CommandEvent</code>s.
	 * 
	 * @see CommandEvent
	 * @param listener CommandListener
	 */
	public void addListener(CommandListener listener);

	/**
	 * Removes the passed listener from the collection of listeners notified
	 * about <code>CommandEvent</code> instances.
	 * 
	 * @see CommandEvent
	 * @param listener CommandListener
	 */
	public void removeListener(CommandListener listener);
	
	/**
	 * Called to request that this command stop execution at the first
	 * possible opportunity.
	 * 
	 * <p>Command instances should make every endeavour to stop quickly but remain responsible
	 * for releasing resources and memory if necessary.</p>
	 * 
	 * @return true iff this command can be (and was) interrupted
	 */
	public boolean interrupt();

	/**
	 * Requests this command resume polling.
	 * 
	 * <p>Commands should exit their read-write code when interrupted so this method
	 * will do nothing for single operation commands and serves only to enable/disable
	 * commands run repeatedly in a polling thread which have been interrupted.</p>
	 */
	public void resume();

	/**
	 * Called by the <code>CommandQ</code> instance to request this command to
	 * begin performing it's action.
	 * 
	 * @param CommandQ the command queue executing this command
	 */
	public void execute(CommandQ que);

	/**
	 * Call-back to notify a <code>Command<code> of some external failure.
	 * 
	 * <p><code>Command</code> instances should stop executing as soon as possible on
	 * receiving the failed notification and tidy any resources as necessary.</p>
	 * 
	 * @param reason String, the reason for the failure
	 */
	public void failed(String reason);

	/**
	 * Returns the priority of this command.
	 * 
	 * <p>Priority should be a positive integer, larger values indicate a lower priority.
	 * Long running processes should normally be a low priority and enable cancellation.</p>
	 * 
	 * <p>Default priority is 100, long running commands such as image polling should be 1000.</p>
	 *  
	 * @return int, the priority of this Command
	 */
	public int getPriority();

	/**
	 * Return this commands display name.
	 * 
	 * <p>Commands are often named to look like methods:
	 * <pre>    getImage()
	 *    motors()</pre>
	 * Key parameters may be rendered:
	 * <pre>    motors(100 100 200)</pre></p>
	 * 
	 * <p>Essentially users are free to return whatever string they'd like to see appear
	 * in the Command Console on execution.</p>
	 * 
	 * @return String, the command's name
	 */
	public String getName();
}
