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
package uk.co.dancowan.robots.srv.hal;

import uk.co.dancowan.robots.hal.core.AbstractComponent;
import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.srv.hal.commands.motors.MotorCommandGenerator;

/**
 *
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public class Motors extends AbstractComponent
{
	public static final String ID = "SRV Motor Controllers";

	private int mLMotor;
	private int mRMotor;
	private int mDuration;
	private int mLastTrim;

	/**
	 * C'tor.
	 *
	 * <p>Has a default duration of 200ms.</p>
	 */
	public Motors()
	{
		mLMotor = 0;
		mRMotor = 0;
		mDuration = 20; //(20 x 10ms = 200ms)
		mLastTrim = 100;
	}

	/**
	 * Execute a command representing the object's internal state
	 * on the hardware.
	 */
	public void fireCommand()
	{
		Command cmd = MotorCommandGenerator.getCommand(mLMotor, mRMotor, mDuration);
		SrvHal.getCommandQ().addCommand(cmd);
	}

	/**
	 * Create a motor value from a given range.
	 * 
	 * <p>The range is between 0 and <code>max</code> with values between
	 * 0 and <code>max</code>/2 being reverse and <code>max</code>/2 to <code>max</code>
	 * being forwards. The value <code>max</code>/2 is stationary and the extreme
	 * values 0 and <code>max</code> are full speed reverse and forwards.</p>
	 * 
	 * <p>This method is used to translate Slider style widget settings into motor commands.</p>
	 * 
	 * @param selection the value in the range
	 * @param max the range: 0 to max
	 * @return int the motor value in hardware motor space
	 */
	public int getMotorValue(int selection, int max)
	{
		int value = (int)(256 * (double)selection/(double)max);
		if (value < 128)
			value = 127 - value;
		else
			value = 128 + (255 - value);
		return value;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.AbstractComponent#getID()
	 */
	public String getID()
	{
		return ID;
	}

	/**
	 * @return the left motor value
	 */
	public int getLeftMotor()
	{
		return mLMotor;
	}

	/**
	 * @param lMotor the left motor value to set
	 */
	public void setLeftMotor(int lMotor)
	{
		mLMotor = lMotor;
	}

	/**
	 * @return the right motor value
	 */
	public int getRightMotor()
	{
		return mRMotor;
	}

	/**
	 * @param rMotor the right motor value to set
	 */
	public void setRightMotor(int rMotor)
	{
		mRMotor = rMotor;
	}

	/**
	 * @return the command duration in 10ms units
	 */
	public int getDuration()
	{
		return mDuration;
	}

	/**
	 * @param duration the duration to set in 10ms units
	 */
	public void setDuration(int duration)
	{
		mDuration = duration;
	}

	/**
	 * @return the lastTrim value
	 */
	public int getLastTrim()
	{
		return mLastTrim;
	}

	/**
	 * @param lastTrim the lastTrim value to set
	 */
	public void setLastTrim(int lastTrim)
	{
		mLastTrim = lastTrim;
	}
}
