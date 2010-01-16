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
package uk.co.dancowan.robots.srv.hal.commands.motors;


/**
 * Generates motor commands based on the x and y coordinates of a point
 * located on a fixed square.
 * 
 * <p>Taking the centre of the square as an origin 'N' should generate full forward,
 * 'S' generates full reverse, 'E' and 'W' should generate spins and all points in
 * between should generate smooth motor transitions from one extreme to another.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 *
 */
public class MotorCommandGenerator
{
	private int mWidth;
	private int mHeight;
	private int mDuration;

	/**
	 * C'tor
	 * 
	 * <p>Takes the width and height of the target panel against
	 * which the motor commands are generated.</p>
	 * 
	 * @param width
	 * @param height
	 */
	public MotorCommandGenerator(int width, int height)
	{
		mWidth = width;
		mHeight = height;
		mDuration = 100; // 10ms units
	}

	/**
	 * Translate integer parameters into hex motor command.
	 * 
	 * <p>Motor values must be between 0 and 256 and duration > 0.
	 * Motor values are between 0 and 127 forwards and 128 and 256 for
	 * reverse. Duration is in 10 millisecond units.</p>
	 * 
	 * @param left left motor value
	 * @param right right motor value
	 * @param duration duration of command in 10ms units
	 * @throws IllegalArgumentException if parameters are out of bounds.
	 * @return MotorCmd the command to execute
	 */
	public static MotorsCmd getCommand(int left, int right, int duration)
	{
		if (left < 0 || left > 255 || right < 0 || right > 255 || duration < 0)
			throw new IllegalArgumentException("Motor parameters out of bounds. {l:" + left + " r:" + right + " d:" + duration + "}");

		return new MotorsCmd(left, right, duration);
	}

	/**
	 * Generates a motor command based on the passed x and y coordinates.
	 * 
	 * <p>Returns a command of the form:</p><pre>    D4llrrdd</pre><p>where
	 * <b>l</b> is the left motor value, <b>r</b> the right motor and <b>d</b>
	 * the duration in ms. Motor values are between 0 and 127 forwards and
	 * 128 and 256 reverse. The command is returned formatted as a hexadecimal
	 * string.</p>
	 * 
	 * @param xLocation 0 -> MotorCommandGenerator#width
	 * @param yLocation 0 -> MotorCommandGenerator#height
	 * @return MotorCmd the command to execute
	 */
	public MotorsCmd getCommand(int xLocation, int yLocation)
	{
		int power = getPower(yLocation);
		int[] outputs = getCommands(power, xLocation);
		outputs = modForSpins(xLocation, yLocation, outputs);

		/*StringBuilder sb2 = new StringBuilder();
		sb2.append("Which means ");
		sb2.append("l=");
		sb2.append(outputs[0]);
		sb2.append(" r=");
		sb2.append(outputs[1]);
		sb2.append(" d=");
		sb2.append(mDuration);
		System.err.println(sb2.toString());*/

		return new MotorsCmd(outputs[0], outputs[1], mDuration);
	}

	/**
	 * Sets the time in 10 millisecond units for which the command will be executed.
	 * 
	 * <p>Duration of 0 is continuous.</p>
	 * @param duration time in 10ms units
	 */
	public void setDuration(int duration)
	{
		mDuration = duration;
	}

	/*
	 * Modify the command signals for spins within a boundary around
	 * the height midpoint. X-axis should still control speed. 
	 */
	private int[] modForSpins(int xLoc, int yLoc, int[] command)
	{
		int halfway = mHeight/2;
		int direction = xLoc > mWidth/2 ? 0 : 1;
		int boundary = (int) (25 * (double)xLoc/(mWidth/2));
		if (yLoc > halfway && yLoc < halfway + boundary)
		{
			// increment and wrap at 256 to reverse at same speed
			int dir = command[direction] + 128;
			if (dir > 256)
				dir -= 256;
			command[direction] = dir;
		}
		else if (yLoc < halfway && yLoc > halfway - boundary)
		{
			// increment and wrap at 256 to reverse at same speed
			int dir = command[direction] + 128;
			if (dir > 256)
				dir -= 256;
			command[direction] = dir;
		}
		return command;
	}

	/*
	 * Derive decimal motor commands with respect to y-location offset
	 */
	private int[] getCommands(int power, int xLocation)
	{
		int halfway = mWidth/2;
		int[] command = new int[]{power, power};
		double multiplier = 1.0;
		if (xLocation < halfway)
		{
			multiplier = (double)xLocation/(double)halfway;
			command[0] = (int) (0.2*power + multiplier*0.8*power);
		}
		else
		{
			multiplier = 1 - (double)(xLocation-halfway)/(double)halfway;
			command[1] = (int) (0.2*power + multiplier*0.8*power);
		}
		return command;
	}

	/*
	 * Return the decimal motor power value as a function of the
	 * location of x in the control pad.
	 */
	private int getPower(int yLocation)
	{
		// motor codes are 0-127 forwards and 128 to 256 backwards;
		// therefore the ratio of the x-location : height times
		// the max motor signal should yield the correct values.
		// If the x-location is above the half way line (forward)
		// then the raw value must be flipped (f = 127 - f).
		int rawValue = (int) (((double)yLocation/(double)mHeight)*256);
		if (rawValue <= 127)
			return 127 - rawValue;
		else
			return 128 + (255 - rawValue);
	}
}
