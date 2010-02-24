/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2009 Dan Cowan
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
package uk.co.dancowan.robots.srv.hal.commands.featuredetector;

import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.ui.panels.PatternWidget;

/**
 * Stores a new pattern in the SRV1 memory space.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class NNStoreCmd extends AbstractByteCommand
{
	private static final String COMMAND = "np";

	private final PatternWidget mEditor;

	/**
	 * Sets the <code>PatternWidget</code> from which the pattern is taken.
	 */
	public NNStoreCmd(PatternWidget editor)
	{
		mEditor = editor;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	protected String getCommandString()
	{
		String index = Integer.toHexString(SrvHal.getCamera().getDetector().getFocusPattern().getIndex());
		return COMMAND + index + mEditor.getPattern().toHex();
	}
}