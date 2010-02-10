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
package uk.co.dancowan.robots.ui.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.HALRegistry;

/** 
 * Class wraps a <code>uk.co.dancowan.robots.hal.core.Command</code> adapting it to
 * a JFace UI command.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 *
 */
public class CommandAction extends Action
{
	private final Command mCmd;

	/**
	 * C'tor
	 * 
	 * <p>The configured command object should have all listeners attached outside of this class.</p>
	 * 
	 * @param cmd Command
	 * @param text String the text of the command in the UI
	 * @param tooltip String the command's tooltip
	 * @param desc ImageDescriptor the command's icon image
	 */
	public CommandAction(Command cmd, String text, String tooltip, ImageDescriptor desc)
	{
		super();

		mCmd = cmd;

		setText(text);
		setToolTipText(tooltip);
		setImageDescriptor(desc);
	}

	/**
	 * Returns this actions internal <code>Command</code>.
	 * 
	 * @return Command
	 */
	public Command getCommand()
	{
		return mCmd;
	}

	/**
	 * Runs the configured command
	 */
	public void run()
	{
		CommandQ cmdQ = HALRegistry.getInsatnce().getCommandQ();
		cmdQ.addCommand(mCmd);
	}
}
