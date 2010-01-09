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

import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.featuredetector.commands.GrabPatternCmd;

/**
 * This class acts as a container for stored Patterns.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class PatternMemory implements CommandListener
{
	private static final int MAX_PATTERNS = 16;

	private Pattern[] mPatternArray;

	public PatternMemory()
	{
		mPatternArray = new Pattern[MAX_PATTERNS];
	}

	/**
	 * Returns the <code>Pattern</code> at the passed index.
	 * 
	 * @throws IndexOutOfBoundsException
	 * @param index the pattern to return
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
	 * @throws IndexOutOfBoundsException
	 * @param pattern the Pattern to set
	 * @param index
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
		CommandQ cmdQ = SrvHal.getCommandQ();
		for (int i = 0; i < MAX_PATTERNS; i ++)
		{
			GrabPatternCmd cmd = new GrabPatternCmd(i);
			cmd.addListener(this);
			cmdQ.addCommand(cmd);
		}
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.CommandListener#commandExecuted(uk.co.dancowan.robots.hal.core.CommandEvent)
	 */
	@Override
	public void commandExecuted(CommandEvent e)
	{
		// NOP
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.CommandListener#commandFailed(uk.co.dancowan.robots.hal.core.CommandEvent)
	 */
	@Override
	public void commandFailed(CommandEvent e)
	{
		// NOP
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.CommandListener#commandCompleted(uk.co.dancowan.robots.hal.core.CommandEvent)
	 */
	@Override
	public void commandCompleted(CommandEvent e)
	{
		String result = e.getMessage();
		int index = ((GrabPatternCmd) e.getSource()).getPatternIndex();
		decodePatternInfo(result, index);
	}

	/*
	 * Parse a Pattern object from the command result.
	 */
	private void decodePatternInfo(String info, int index)
	{
		Pattern pattern = new Pattern();
		String[] lines = info.split("\r");
		for (int i = 1; i < lines.length; i ++)
		{
			String line = lines[i];
			pattern.setFromString(parseLine(line), i - 1);
		}
		setPattern(pattern, index);
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
}
