package uk.co.dancowan.robots.hal.core.commands;
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


import junit.framework.TestCase;
import uk.co.dancowan.robots.hal.core.commands.CommandUtils;

/**
 * TextUtils unit test suite.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CommandUtilsTest extends TestCase
{
	/**
	 * C'tor.
	 */
	public CommandUtilsTest(String name)
	{
		super(name);
	}

	/**
	 * Ensure that '1' becomes '001' and that '1000' remains '1000'.
	 */
	public void testFormatInt()
	{
		String result = CommandUtils.formatInt(1, 3);
		assertEquals("001", result);
		result = CommandUtils.formatInt(9, 4);
		assertEquals("0009", result);
		result = CommandUtils.formatInt(10, 3);
		assertEquals("010", result);
		result = CommandUtils.formatInt(19, 3);
		assertEquals("019", result);
		result = CommandUtils.formatInt(100, 3);
		assertEquals("100", result);
		result = CommandUtils.formatInt(100, 4);
		assertEquals("0100", result);
		result = CommandUtils.formatInt(1000, 5);
		assertEquals("01000", result);
		try
		{
			result = CommandUtils.formatInt(1000, 3);
			assertTrue("Misformatted 1000 to size 3.", false);
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(e.getMessage(), true);
		}
	}

	/**
	 * Test decimal bytes to hexadecimal bytes.
	 */
	public void testDecToHex()
	{
		String test = "M";
		String result = CommandUtils.byteArrayToHex(test.getBytes()).trim();
		assertEquals("4D", result);

		test = "l";
		result = CommandUtils.byteArrayToHex(test.getBytes()).trim();
		assertEquals("6C", result);

		test = "L";
		result = CommandUtils.byteArrayToHex(test.getBytes()).trim();
		assertEquals("4C", result);

		test = "255";
		result = CommandUtils.byteArrayToHex(test.getBytes()).trim();
		assertEquals("32 35 35", result);
	}
}
