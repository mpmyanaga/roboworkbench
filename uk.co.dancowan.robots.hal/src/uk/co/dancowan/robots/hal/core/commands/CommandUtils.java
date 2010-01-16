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
package uk.co.dancowan.robots.hal.core.commands;

import uk.co.dancowan.robots.hal.core.Command;

/**
 * Utility class for string operations.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CommandUtils
{
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D','E', 'F' };

	/**
	 * Platform specific new line character.
	 */
	public static final String CR = System.getProperty ("line.separator");

	/**
	 * Returns an integer value as a 0 padded string.
	 * 
	 * <p>formatInt(3, 3) -> 300</p>
	 * 
	 * @param i the value to format
	 * @param size the length of the return string
	 */
	public static String formatInt(int i, int size)
	{
		String str = Integer.toString(i);
		if (str.length() > size)
			throw new IllegalArgumentException("Formatting error, number is longer than target size: '" + str + "' longer than " + size);
		while (str.length() < size)
		{
			str = "0" + str;
		}
		return str;
	}

	/**
	 * Detect new line character to terminate reading output stream.
	 * 
	 * <p>Detects a single char of codepoint 13 or codepoint 10 (/r or /n).</p>
	 * 
	 * @param end the character to inspect
	 */
	public static boolean isEnd(Character end)
	{
		if (end.toString().codePointAt(0) == 13 || end.toString().codePointAt(0) == 10)
			return true;
		else
			return false;
	}

	/**
	 * This converts a <code>byte[]</code> to a <code>String</code>
	 * in hexadecimal format.
	 *
	 * @param input <code>byte[]</code> to convert
	 * @return <code>String</code> - resulting Hex String
	 */
	public static String byteArrayToHex(byte[]input)
	{
		StringBuilder result = new StringBuilder();
		byte highNibble, lowNibble;
		if (input != null)
		{
			for (int i = 0; i < input.length; i++)
			{
				highNibble = (byte) ((input[i] >>> 4) & 0x0F);
				lowNibble = (byte) (input[i] & 0x0F);
				result.append(HEX_DIGITS[highNibble]);
				result.append(HEX_DIGITS[lowNibble]);
				result.append(" ");
			}
		}
		return result.toString();
	}

	/**
	 * Constructs a <code>SendString</code> command around the passed decimal string.
	 * 
	 * <p>The command string will not be translated into hexadecimal before execution.</p>
	 * 
	 * @param command
	 * @return Command
	 */
	public static final Command getDecCommandForDec(String command)
	{
		return new SendString(command, false);
	}

	/**
	 * Constructs a <code>SendString</code> command around the passed decimal string.
	 * 
	 * <p>The decimal command string will be translated into hexadecimal before execution.</p>
	 * 
	 * @param decCommand
	 * @return Command
	 */
	public static final Command getHexCommandForDec(String decCommand)
	{
		return new SendString(decCommand, true);
	}

	/**
	 * Constructs a <code>SendString</code> command around the passed hex string.
	 * 
	 * @param hexCommand
	 * @return Command
	 */
	public static final Command getHexCommandForHex(String hexCommand)
	{
		return new SendString(hexCommand);
	}
}
