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
package uk.co.dancowan.robots.srv.hal.commands.motors;

import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.commands.AbstractCommand;

/**
 * Command sets the resolution of the camera image.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class MotorsCmd extends AbstractCommand
{
	public static final String ID = "motors";

	private static final String COMMAND = "4D";

	private int mDuration;
	private  int mLeftMotor;
	private int mRightMotor;

	/**
	 * C'tor.
	 * 
	 * @param left int, left motor speed
	 * @param right int, right motor speed
	 * @param duration int, millisecond run time 
	 */
	public MotorsCmd(int left, int right, int duration)
	{
		//translation to hex is local, fixed response, no newline
		super(2, false);
		
		mLeftMotor = left;
		mRightMotor = right;
		mDuration = duration;
	}

	/**
	 * Write the command request to the connection
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#read(CommandQ)
	 */
	@Override
	protected String getCommandString()
	{
		// root command is already hex
		StringBuilder sb = new StringBuilder(COMMAND);

		// params are decimal, so translate
		sb.append(Integer.toHexString(mLeftMotor));
		sb.append(Integer.toHexString(mRightMotor));
		sb.append(Integer.toHexString(mDuration));
		
		return sb.toString();
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getName()
	 */
	@Override
	public String getName()
	{
		return ID + "(" + mLeftMotor + ", " + mRightMotor + ", " + mDuration + ")";
	}
}
