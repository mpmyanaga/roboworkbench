/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (C) 2009  Dan Cowan
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


/**
 * Command to grab a neural network pattern stored in the SRV1's memory.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class GrabPatternCmd extends AbstractByteCommand
{
	private static final String COMMAND = "nd";
	private static final int PRIORITY = 1000;
	
	private final int mIndex;

	/**
	 * C'tor.
	 *
	 * <p>Sets the index of the <code>Pattern</code> to grab from the SRV1.
	 * 
	 * @param index the index of the Pattern 0 <= index < 16
	 */
	public GrabPatternCmd(int index)
	{
		mIndex = index;
	}

	/**
	 * Implementation returns a priority of 1000.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#getPriority()
	 */
	@Override
	public int getPriority()
	{
		return PRIORITY;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	protected String getCommandString()
	{
		return COMMAND + Integer.toHexString(mIndex);
	}
}
