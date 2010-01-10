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

import java.util.Iterator;

/**
 * Class represents a potential pattern.
 * 
 * <p>The SRV stores and recognises patterns encoded as a series of 8 8 bit
 * characters, much like the original ASCII characters of yore. A pattern is
 * stored thus:</p>
 * <pre> **    **    195
 * **    **    195
 * **    **    195
 * ********    255
 * ********    255
 * **    **    195
 * **    **    195
 * **    **    195</pre>
 * <p>Patterns are stored internally to this class as an array of bytes.</p>
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class Pattern
{
	private byte[] mPattern;

	/**
	 * C'tor.
	 */
	public Pattern()
	{
		mPattern = new byte[8];
	}

	/**
	 * Returns the byte at the passed index.
	 * 
	 * <p>Index value should be between 0 and 7 inclusive.</p>
	 * 
	 * @throws IndexOutOfBoundsException
	 * @param index the index to set the value at
	 */
	public byte get(int index)
	{
		if (index < 0 || index > 7)
			throw new IndexOutOfBoundsException("Index out of bounds, should be between 0 and 7 inclusive but found " + index);

		return mPattern[index];
	}

	/**
	 * Sets the byte at the passed index.
	 * 
	 * <p>Index value should be between 0 and 7 inclusive.</p>
	 * 
	 * @throws IndexOutOfBoundsException
	 * @param value the byte value to set
	 * @param index the index to set the value at
	 */
	public void set(byte value, int index)
	{
		if (index < 0 || index > 7)
			throw new IndexOutOfBoundsException("Index out of bounds, should be between 0 and 7 inclusive but found " + index);

		mPattern[index] = value;
	}

	/**
	 * Sets the byte at the passed index from the passed String.
	 * 
	 * <p>The String should be a binary representation of the pattern's
	 * line information.</p>
	 * 
	 * <p>Index value should be between 0 and 7 inclusive.</p>
	 * 
	 * @throws IndexOutOfBoundsException
	 * @param value the String value to encode and set
	 * @param index the index to set the value at
	 */
	public void setFromString(String value, int index)
	{
		if (index < 0 || index > 7)
			throw new IndexOutOfBoundsException("Index out of bounds, should be between 0 and 7 inclusive but found " + index);

		mPattern[index] = (byte) encode(value);
	}

	/**
	 * Returns the byte at the passed index as a decoded String.
	 * 
	 * <p>String will be in the form 1001010 derived from parsing
	 * the byte as binary.
	 * 
	 * <p>Index value should be between 0 and 7 inclusive.</p>
	 * 
	 * @throws IndexOutOfBoundsException
	 * @param value the byte value to set
	 * @param index the index to set the value at
	 */
	public String getAsString(int index)
	{
		if (index < 0 || index > 7)
			throw new IndexOutOfBoundsException("Index out of bounds, should be between 0 and 7 inclusive but found " + index);

		return decode(mPattern[index]);
	}
	
	/**
	 * Returns a byte iterator for this pattern.
	 * 
	 * @return Iterator
	 */
	public Iterator<Byte> iterator()
	{
		return new PatternIterator(mPattern);
	}

	/*
	 * Takes an  8 char String of the form 10010101 and returns
	 * a byte decoded via binary.
	 */
	private int encode(String value)
	{
		assert value.length() == 8 : "Incorrect String passed: " + value;

		int val = 0;
		for (int i = 0; i < 8; i ++)
		{
			if (value.charAt(7 - i) != '0')
				val += Math.pow(2, i);
		}
		return val;
	}

	/*
	 * Return a String of the form 1001010 for a passed byte.
	 */
	private String decode(byte b)
	{
		StringBuilder sb = new StringBuilder();

		int unsigned = unsignedByteToInt(b);
		for (int i = 7; i >= 0; i --)
		{
			if ((unsigned & (int)Math.pow(2, i)) == (int)Math.pow(2, i))
			{
				sb.append("1");
			}
			else
			{
				sb.append("0");
			}
		}
		return sb.toString();
	}

	/*
	 * Unsign the byte (must then be an int)
	 */
	private int unsignedByteToInt(byte b)
	{
		return (int) b & 0xFF;
	}

	/*
	 * Simple Iterator to read bytes out of the pattern.
	 */
	private class PatternIterator implements Iterator<Byte>
	{
		private final byte[] mPattern;
		private int mIndex;

		/*
		 * Private constructor checks pattern correctness
		 */
		private PatternIterator(byte[] pattern)
		{
			assert pattern.length == 8 : "Miscoded pattern detected. " + pattern;

			mPattern = pattern;
			mIndex = 0;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext()
		{
			return mIndex < 8;
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Byte next()
		{
			// Note that mIndex is post incremented
			return mPattern[mIndex ++];
		}

		/**
		 * Not supported.
		 * 
		 * @throws UnsupportedOperationException
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Cannot remove a byte from a Pattern.");	
		}	
	}
}
