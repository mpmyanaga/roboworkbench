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
package uk.co.dancowan.robots.ui.utils;

/**
 * Utility class for string operations.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class TextUtils
{
	/**
	 * Platform specific new line character.
	 */
	public static final String CR = System.getProperty ("line.separator");

	/**
	 * Clips the passed int at the passed max value.
	 * 
	 * @param value
	 * @param max
	 * @return int
	 */
	public static int cap(int value, int max)
	{
		return value < max ? value : max;
	}

	/**
	 * Clips the passed int at the passed min value.
	 * 
	 * @param value
	 * @param min
	 * @return
	 */
	public static int collar(int value, int min)
	{
		return value > min ? value : min;
	}

	/**
	 * Wraps the inbound string at the defined size.
	 */
	public static final String wrap(String message, int lineLength)
	{
		if (message.length() < lineLength)
			return message;

		int index;
		for (index = lineLength; index >= 0; index --)
		{
			if (message.charAt(index) == ' ')
				break;
		}
		boolean hyphonate = false;
		if (index == 0)
		{
			index = lineLength - 1;
			hyphonate = true;
			
		}
		//System.err.print("Index at " + index);
		//System.err.println(" yields '" + message.substring(0, index) + "'");

		StringBuilder sb = new StringBuilder();
		sb.append(message.substring(0, index));
		if (hyphonate)
			sb.append("-");
		sb.append(TextUtils.CR);
		sb.append(TextUtils.wrap(message.substring(index), lineLength).trim());
		return sb.toString();
	}
}
