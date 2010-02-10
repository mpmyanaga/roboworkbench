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
package uk.co.dancowan.robots.hal.core.commands;

/**
 * Sends the configured string to the robot.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class SendString extends AbstractCommand
{
	private String mCommand;
	private boolean mEncode;

	/**
	 * C'tor.
	 * 
	 * <p>The command should be a single string without whitespace. By default
	 * the string will not be encoded in hexadecimal prior to transmition, this
	 * enables sending of decimal or hexadecimal strings directly.</p>
	 *
	 *@param command the String command to send
	 */
	public SendString(String command)
	{
		// don't encode, expect length
		super(false);

		mEncode = false;
		mCommand = command;
	}

	/**
	 * C'tor.
	 * 
	 * <p>The command should be a single string without whitespace encoded in
	 * hexadecimal, encoding will happen prior to translation. Equivalent to:
	 * </p><pre><code>    new SendString(command, false);</code></pre>
	 *
	 * @param command the String command to send
	 * @param encode boolean flag to enable hex encoding
	 */
	public SendString(String command, boolean encode)
	{
		// encode as necessary, expect line end
		super();
	
		mEncode = encode;
		mCommand = command;
	}

	/**
	 * Returns thiis Command's internal command to send.
	 * 
	 * <p>Command is translated to hexadecimal if required.</p>
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	protected String getCommandString()
	{
		String cmd = mCommand;
		if (mEncode)
			cmd = CommandUtils.byteArrayToHex(mCommand.getBytes());
		return cmd;
	}
}
