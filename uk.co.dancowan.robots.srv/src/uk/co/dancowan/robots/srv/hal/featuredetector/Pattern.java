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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.GrabPatternCmd;

/**
 * Class represents a potential pattern.
 * 
 * <p>The SRV stores and recognises patterns encoded as a series of 8 8 bit
 * characters, much like the original ASCII characters of yore. A pattern is
 * stored thus:</p>
 * <pre> **    **    11000011
 * **    **    11000011
 * **    **    11000011
 * ********    11111111
 * ********    11111111
 * **    **    11000011
 * **    **    11000011
 * **    **    11000011</pre>
 * <p>Patterns are stored internally to this class as an array of bytes.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class Pattern
{
	private final List<PatternListener> mListeners;
	private final int mIndex;
	private String[] mPattern;

	/**
	 * C'tor.
	 * 
	 * @param index int
	 */
	public Pattern(int index)
	{
		mListeners = new ArrayList<PatternListener>();
		mIndex = index;
		mPattern = new String[8];
	}

	/**
	 * Add this listener to the collection of listeners notified
	 * of changes to this pattern.
	 * 
	 * @param listener
	 */
	public void addListener(PatternListener listener)
	{
		mListeners.add(listener);
	}

	/**
	 * Remove this listener from the collection of listeners notified
	 * of changes to this pattern.
	 * 
	 * @param listener
	 */
	public void removeListener(PatternListener listener)
	{
		mListeners.remove(listener);
	}

	/**
	 * Returns the String encoding at the passed index.
	 * 
	 * <p>Index value should be between 0 and 7 inclusive.</p>
	 * 
	 * @param index the index to set the value at
	 * @throws IndexOutOfBoundsException
	 */
	public String get(int index)
	{
		if (index < 0 || index > 7)
			throw new IndexOutOfBoundsException("Index out of bounds, should be between 0 and 7 inclusive but found " + index);

		return mPattern[index];
	}

	/**
	 * Sets the String at the passed index.
	 * 
	 * <p>Index value should be between 0 and 7 inclusive.</p>
	 * 
	 * @param value the String value to set
	 * @param index the index to set the value at
	 * @throws IndexOutOfBoundsException
	 */
	public void set(String value, int index)
	{
		if (index < 0 || index > 7)
			throw new IndexOutOfBoundsException("Index out of bounds, should be between 0 and 7 inclusive but found " + index);

		mPattern[index] = value;
		notifyListeners();
	}

	/**
	 * Send a grabBinCmd for this colour bin and set the colour accordingly.
	 */
	public void refreshPattern()
	{
		GrabPatternCmd cmd = new GrabPatternCmd(mIndex);
		cmd.addListener(new CommandListener()
		{
			@Override
			public void commandFailed(CommandEvent e)
			{
				// NOP		
			}
			@Override
			public void commandExecuted(CommandEvent e)
			{
				// NOP
			}
			/**
			 * @see uk.co.dancowan.robots.hal.core.CommandListener#commandCompleted(uk.co.dancowan.robots.hal.core.CommandEvent)
			 */
			@Override
			public void commandCompleted(CommandEvent e)
			{
				decodePatternInfo(e.getMessage());
				notifyListeners();
			}
		});
		SrvHal.getCommandQ().addCommand(cmd);
	}
	
	/**
	 * Returns a byte iterator for this pattern.
	 * 
	 * @return Iterator
	 */
	public Iterator<String> iterator()
	{
		return new PatternIterator(mPattern);
	}

	/*
	 * Notify listeners of a change
	 */
	private void notifyListeners()
	{
		for (PatternListener listener : mListeners)
		{
			listener.patternUpdated();
		}
	}

	/*
	 * Parse a Pattern object from the command result.
	 */
	private void decodePatternInfo(String info)
	{
		String[] lines = info.split("\r");
		for (int i = 1; i < lines.length; i ++)
		{
			String line = lines[i];
			set(parseLine(line), i - 1);
		}
	}

	/*
	 * Parse a line from SRV format into Command format
	 * ' **    **    **    **   ' = '10101010'
	 */
	private String parseLine(String line)
	{
		assert line.length() == 24 : "Bad line length: " + line.length() + "/24";

		StringBuilder sb = new StringBuilder();
		for (int i = 2; i < 24; i += 3)
		{
			if (line.charAt(i) == '*')
				sb.append("1");
			else
				sb.append("0");
		}
		return sb.toString();
	}

	/*
	 * Simple Iterator to read String lines out of the pattern.
	 */
	private class PatternIterator implements Iterator<String>
	{
		private final String[] mPattern;
		private int mIndex;

		/*
		 * Private constructor checks pattern correctness
		 */
		private PatternIterator(String[] pattern)
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
		public String next()
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
			throw new UnsupportedOperationException("Cannot remove a line from a Pattern.");	
		}	
	}
}
