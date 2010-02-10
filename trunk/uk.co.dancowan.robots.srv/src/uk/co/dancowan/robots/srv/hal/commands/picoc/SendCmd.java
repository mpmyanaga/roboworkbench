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
package uk.co.dancowan.robots.srv.hal.commands.picoc;

import org.eclipse.swt.widgets.Display;

import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.commands.CommandChain;
import uk.co.dancowan.robots.hal.core.commands.CommandUtils;

/**
 * Series of commands to send editor content to the SRV1's flash buffer.
 * 
 * <p>Sequence of actions include:</p>
 * <ol><li>zc    Clears the buffer</li>
 * <li>E    Opens the line editor</li>
 * <li>I Starts insertion mode</li>
 * <li><i>inserts editor content</i></li>
 * <li>ESC Exits insertion mode</li>
 * <li>X Leave the editor</li></ol>
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class SendCmd extends CommandChain
{
	private BufferContentProvider mProvider;

	/**
	 * Sets the content provider for the buffer's content
	 * 
	 * @param provider
	 */
	public void setProvider(BufferContentProvider provider)
	{
		mProvider = provider;
	}

	/**
	 * Adds the set of commands to execute at the last minute to ensure the
	 * <code>SendCmd</code>'s content is fresh.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.CommandChain#execute(uk.co.dancowan.robots.hal.core.CommandQ)
	 */
	@Override
	public void execute(CommandQ cmdQ)
	{
		CommandStringProvider provider = new CommandStringProvider();
		Display.getDefault().syncExec(provider);
		clearCommands();

		addCommand(new ClearFlashCmd());
		addCommand(CommandUtils.getHexCommandForDec("E"));
		addCommand(CommandUtils.getHexCommandForDec("I"));
		addCommand(CommandUtils.getHexCommandForDec(provider.getResult()));
		addCommand(new EscCmd());
		addCommand(CommandUtils.getHexCommandForDec("X"));

		super.execute(cmdQ);
	}

	/*
	 * Roundabout way of getting the buffer content from another thread
	 */
	private class CommandStringProvider implements Runnable
	{
		private String mCommandString;

		public void run()
		{
			mCommandString = mProvider.getText();
		}

		private String getResult()
		{
			return mCommandString;
		}
	}
}
