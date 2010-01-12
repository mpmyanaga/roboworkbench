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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;

import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.ui.utils.ColourManager;

/**
 * ModifyListener to paint user input in the colour determined by the preferences.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class InputRenderer implements ExtendedModifyListener
{
	private final CommandConsole mConsole;
	private RGB mTextColour;

	/**
	 * C'tor.
	 *
	 * @param text the StyledText widget to parse
	 */
	public InputRenderer(CommandConsole console)
	{
		mConsole = console;
		configureFromPreferences();
	}

	/**
	 * @see org.eclipse.swt.custom.ExtendedModifyListener#modifyText(org.eclipse.swt.custom.ExtendedModifyEvent)
	 */
	@Override
	public void modifyText(ExtendedModifyEvent e)
	{
		int start = e.start;
		int length = e.length;
		StyleRange range = new StyleRange(start, length, ColourManager.getInstance().getColour(mTextColour), ColourManager.getColour(SWT.COLOR_WHITE));
		mConsole.setStyleRange(range);
		mConsole.trimBuffer();
	}

	/**
	 * Set the colour to paint with from stored preferences.
	 */
	public void configureFromPreferences()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		mTextColour = PreferenceConverter.getColor(store, PreferenceConstants.COMMAND_INPUT_COLOUR);
	}
}
