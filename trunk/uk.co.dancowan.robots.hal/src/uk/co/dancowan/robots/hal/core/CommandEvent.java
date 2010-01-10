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
package uk.co.dancowan.robots.hal.core;


/**
 * Event class thrown by <code>Command</code> instances to indicate
 * events in the life cycle of the <code>Command</code>.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CommandEvent
{
	// Event fields are traditionally public :(
	public final Command mSource;
	public final String mCommandID;
	public final String mMessage;
	public final long mTime;

	/**
	 * C'tor.
	 * 
	 * @param id the Command's ID field
	 * @param message the event's message
	 */
	public CommandEvent(Command source, String message)
	{
		mSource = source;
		mCommandID = mSource.getName();
		mMessage = message;
		mTime = System.currentTimeMillis();
	}

	/**
	 * Returns the <code>Command</code> which caused this event.
	 * 
	 * @return Command the source of this event
	 */
	public Command getSource()
	{
		return mSource;
	}

	/**
	 * Returns the event's Command ID.
	 * 
	 * @return String Command ID
	 */
	public String getCommandID()
	{
		return mCommandID;
	}

	/**
	 * Returns the event's message.
	 * 
	 * @return String, the ebent's message
	 */
	public String getMessage()
	{
		return mMessage;
	}

	/**
	 * Returns the time in ms that the event was created (fired).
	 * 
	 * @return time of event in ms
	 */
	public long getTime()
	{
		return mTime;
	}
}
