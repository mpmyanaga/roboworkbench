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

import uk.co.dancowan.robots.srv.hal.featuredetector.ColourBin;

/**
 * Extension of AbstractCommand which sets the parameters of a colour bin
 * on the SRV1q.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class SetBinCmd extends AbstractByteCommand
{
	private static final String COMMAND = "vc";

	private final ColourBin mBin;

	/**
	 * C'tor.
	 * 
	 * @param bin ColourBin
	 */
	public SetBinCmd(ColourBin bin)
	{
		mBin = bin;
	}

	/**
	 * Creates a hexadecimal command.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	public String getCommandString()
	{
		StringBuilder sb = new StringBuilder(COMMAND);
		sb.append(mBin.getParameterString());
		return sb.toString();
	}
}
