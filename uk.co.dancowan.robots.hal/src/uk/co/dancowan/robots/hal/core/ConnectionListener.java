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
 * Interface for connection listeners.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public interface ConnectionListener
{
	/**
	 * Notification of a successful connection.
	 */
	public void connected();
	/**
	 * Notification of a disconnection event.
	 */
	public void disconnected();
	/**
	 * Notification of a connection error.
	 * 
	 * @param error the error message
	 */
	public void error(String error);
	/**
	 * Notification of a connection error.
	 * 
	 * @param writeChars the characters written to the stream
	 */
	public void tx(String writeChars);
	/**
	 * Notification of a connection error.
	 * 
	 * @param readChars the characters read from the stream
	 */
	public void rx(String readChars);
}
