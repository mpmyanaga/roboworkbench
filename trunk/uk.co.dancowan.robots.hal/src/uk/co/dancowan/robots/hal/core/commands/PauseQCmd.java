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
package uk.co.dancowan.robots.hal.core.commands;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.hal.core.CommandQ;

/**
 * Mock command gets to the head of the command queue and causes the command thread to sleep
 * for a time.
 * 
 * <p>To be used to allow for command execution on the hardware before sending another command
 * and risking over-running the system.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 *
 */
public class PauseQCmd implements Command
{
	private final int mDelay;

	/**
	 * C'tor, sets the delay in ms.
	 * 
	 * @param delay
	 */
	public PauseQCmd(int delay)
	{
		mDelay = delay;
	}

	/**
	 * Implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#addListener(uk.co.dancowan.robots.hal.core.CommandListener)
	 */
	@Override
	public void addListener(CommandListener listener)
	{
		// NOP		
	}

	/**
	 * Causes the Thread to sleep for the configured delay.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#execute(uk.co.dancowan.robots.hal.core.CommandQ)
	 */
	@Override
	public void execute(CommandQ que)
	{
		try
		{
			Thread.sleep(mDelay);
		}
		catch (InterruptedException e)
		{
			// NOP
		}
	}

	/**
	 * Implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#failed(java.lang.String)
	 */
	@Override
	public void failed(String reason)
	{
		// NOP
	}

	/**
	 * Implementation returns "".
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#getName()
	 */
	@Override
	public String getName()
	{
		return "";
	}

	/**
	 * Returns default priority of 100.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#getPriority()
	 */
	@Override
	public int getPriority()
	{
		return 100;
	}

	/**
	 * Implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#interrupt()
	 */
	@Override
	public boolean interrupt()
	{
		return false;
	}

	/**
	 * Implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#removeListener(uk.co.dancowan.robots.hal.core.CommandListener)
	 */
	@Override
	public void removeListener(CommandListener listener)
	{
		// NOP
	}

	/**
	 * Implementation does nothing.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#resume()
	 */
	@Override
	public void resume()
	{
		// NOP
	}
}
