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
 * Request blob information for the configured colour bin.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class GrabBlobCmd extends AbstractByteCommand
{
	private static final String COMMAND = "vb";
	private static final int PRIORITY = 1000;

	private int mBin;

	/**
	 * C'tor.
	 *
	 * @param bin the initial reference ColourBin
	 */
	public GrabBlobCmd(int bin)
	{
		mBin = bin;
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
		return COMMAND + mBin;
	}
}
