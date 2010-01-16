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
package uk.co.dancowan.robots.srv.hal.featuredetector;

/**
 * This class acts as a container for stored Patterns.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class PatternMemory
{
	private static final int MAX_PATTERNS = 16;

	private Pattern[] mPatternArray;

	public PatternMemory()
	{
		mPatternArray = new Pattern[MAX_PATTERNS];
		createPatterns();
	}

	/**
	 * Returns the <code>Pattern</code> at the passed index.
	 * 
	 * @param index the pattern to return
	 * @throws IndexOutOfBoundsException
	 * @return Pattern the pattern
	 */
	public Pattern getPattern(int index)
	{
		if (index < 0 || index > MAX_PATTERNS)
			throw new IndexOutOfBoundsException("Requested pattern " + index + " out of a range from 0 to " + MAX_PATTERNS);

		return mPatternArray[index];
	}

	/**
	 * Sets the <code>Pattern</code> in the passed index.
	 * 
	 * @param pattern the Pattern to set
	 * @param index
	 * @throws IndexOutOfBoundsException
	 */
	public void setPattern(Pattern pattern, int index)
	{
		if (index < 0 || index > MAX_PATTERNS)
			throw new IndexOutOfBoundsException("Requested pattern " + index + " out of a range from 0 to " + MAX_PATTERNS);

		mPatternArray[index] = pattern;
	}

	/**
	 * Request data for each stored Pattern
	 */
	public void refreshPatterns()
	{
		for (Pattern pattern : mPatternArray)
		{
			pattern.refreshPattern();
		}
	}

	private void createPatterns()
	{
		for (int i = 0; i < MAX_PATTERNS; i ++)
		{
		setPattern(new Pattern(i), i);	
		}
	}
}
