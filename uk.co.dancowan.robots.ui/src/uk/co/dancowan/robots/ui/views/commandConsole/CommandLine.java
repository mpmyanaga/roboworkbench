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
package uk.co.dancowan.robots.ui.views.commandConsole;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.HALRegistry;
import uk.co.dancowan.robots.hal.core.commands.CommandUtils;
import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.preferences.PreferenceConstants;

/**
 * Class executes a line of text set to its current command buffer.
 * 
 * <p>Passed command text may be translated from decimal into hexidecimal
 * as set in preferences.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CommandLine
{
	private final List<String> mCommandHistory;
	private int mHistoryIndex;
	private String mCurrentText;

	private boolean mTranslate;
	private boolean mIsHexOut;

	/**
	 * C'tor.
	 */
	public CommandLine()
	{
		mCommandHistory = new ArrayList<String>();
		mHistoryIndex = -1;
		mCurrentText = "";
	}

	/**
	 * Returns the previous command in the command history.
	 * 
	 * @return String the command text
	 */
	public String last()
	{
		if (mHistoryIndex <= 0)
			return null;
		mHistoryIndex --;
		return mCommandHistory.get(mHistoryIndex);
	}

	/**
	 * Returns the next command in the command history.
	 * 
	 * @return String the command text
	 */
	public String next()
	{
		if (mHistoryIndex >= mCommandHistory.size())
			return null;
		mHistoryIndex ++;
		return mCommandHistory.get(mHistoryIndex);
	}

	/**
	 * Set the command to execute.
	 * 
	 * @param text
	 */
	public void setText(String text)
	{
		mCurrentText = text;
	}

	/**
	 * Executes the current command.
	 */
	public void execute()
	{
		Command cmd = null;
		String str = parseSpecialCommands();

		if (mIsHexOut)
		{
			if (mTranslate)
				cmd = CommandUtils.getHexCommandForDec(str);
			else
				cmd = CommandUtils.getHexCommandForHex(str);
		}
		else
		{
			cmd = CommandUtils.getDecCommandForDec(str);
		}
		if (cmd != null)
		{
			HALRegistry.getInsatnce().getCommandQ().addCommand(cmd);
		}
		storeCommand();
	}

	/**
	 * Configure this class's parameters from the plugin local preference store.
	 */
	public void initFromPreferences()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		mTranslate = store.getBoolean(PreferenceConstants.COMMAND_TRANSLATE);
		mIsHexOut = store.getBoolean(PreferenceConstants.COMMAND_HEX_CMD);
	}

	/*
	 * Default implementation does nothing, here for later purposes 
	 */
	private String parseSpecialCommands()
	{
		return mCurrentText;
	}

	/*
	 * Store the current command in the history
	 */
	private void storeCommand()
	{
		mCommandHistory.add(mCurrentText);
		mHistoryIndex = mCommandHistory.size();
	}
}
