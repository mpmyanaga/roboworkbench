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
package uk.co.dancowan.robots.srv.hal.neuralnet;

import java.util.Iterator;

import uk.co.dancowan.robots.srv.hal.featuredetector.Pattern;

import junit.framework.TestCase;

/**
 *
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public class PatternTest extends TestCase
{
	private Pattern mPattern;

	public void setUp()
	{
		mPattern = new Pattern();
	}

	/**
	 * Test method for {@link uk.co.dancowan.robots.srv.hal.featuredetector.Pattern#setFromString(java.lang.String, int)}.
	 */
	public void testSetFromString()
	{
		mPattern.setFromString("11111111", 0);
		assertEquals((byte)255, mPattern.get(0));

		mPattern.setFromString("00000000", 1);
		assertEquals((byte)0, mPattern.get(1));

		mPattern.setFromString("11000011", 2);
		assertEquals((byte)195, mPattern.get(2));
		
		mPattern.setFromString("01011010", 2);
		assertEquals((byte)90, mPattern.get(2));

		mPattern.setFromString("10100101", 2);
		assertEquals((byte)165, mPattern.get(2));

		mPattern.setFromString("10010101", 2);
		assertEquals((byte)149, mPattern.get(2));
	}

	/**
	 * Test method for {@link uk.co.dancowan.robots.srv.hal.featuredetector.Pattern#getAsString(int)}.
	 */
	public void testGetAsString()
	{
		mPattern.set((byte)255, 0);
		String result = mPattern.getAsString(0);
		assertEquals("11111111", result);

		mPattern.set((byte)0, 0);
		result = mPattern.getAsString(0);
		assertEquals("00000000", result);

		mPattern.set((byte)195, 0);
		result = mPattern.getAsString(0);
		assertEquals("11000011", result);
		
		mPattern.setFromString("10000001", 0);
		result = mPattern.getAsString(0);
		assertEquals("10000001", result);
		
		mPattern.setFromString("01111110", 0);
		result = mPattern.getAsString(0);
		assertEquals("01111110", result);
		
		mPattern.set((byte)90, 0);
		result = mPattern.getAsString(0);
		assertEquals("01011010", result);
	}

	/**
	 * Test method for {@link uk.co.dancowan.robots.srv.hal.featuredetector.Pattern#iterator()}.
	 */
	public void testIterator()
	{
		for (int i = 0; i < 8; i ++)
			mPattern.set((byte)i, i);

		Iterator<Byte> it = mPattern.iterator();

		for (byte i = 0; i < 8; i ++)
		{
			byte b = it.next();
			assertEquals(i, b);
		}
	}

}
