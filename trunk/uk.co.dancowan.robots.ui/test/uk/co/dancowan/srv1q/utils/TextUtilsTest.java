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
package uk.co.dancowan.srv1q.utils;

import junit.framework.TestCase;
import uk.co.dancowan.robots.ui.utils.TextUtils;

/**
 * TextUtils unit test suite.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class TextUtilsTest extends TestCase
{
	/**
	 * C'tor.
	 * 
	 * @see junit.framework.TextCase(java.lang.String)
	 */
	public TextUtilsTest(String name)
	{
		super(name);
	}

	public void testWrap()
	{
		String line = "The quick brown fox jumped over the lazy dog.";
		//System.out.println(line);
		String newLine = TextUtils.wrap(line, 20);
		//System.out.println(newLine);
		//newLine = TextUtils.wrap(line, 15);
		//System.out.println(newLine);
		//newLine = TextUtils.wrap(line, 10);
		//System.out.println(newLine);
		
		line = "1 22 333 4444 55555 666666 7777777 88888888 999999999 0000000000";
		System.out.println(line);
		newLine = TextUtils.wrap(line, 7);
		System.out.println(newLine);
		newLine = TextUtils.wrap(line, 10);
		System.out.println(newLine);
	}
}
