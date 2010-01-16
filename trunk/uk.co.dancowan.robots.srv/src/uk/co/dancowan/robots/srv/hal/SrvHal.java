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
package uk.co.dancowan.robots.srv.hal;

import uk.co.dancowan.robots.hal.core.HALRegistry;
import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.srv.hal.camera.Camera;

/**
 * Helper class provides static methods to get standard SRV
 * hardware components from the HAL.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class SrvHal
{
	private static final HALRegistry sRegistry = HALRegistry.getInsatnce();

	/**
	 * Convenience method for more direct access.
	 * 
	 * @return CommandQ
	 */
	public static CommandQ getCommandQ()
	{
		return (CommandQ) sRegistry.get(CommandQ.class);
	}

	/**
	 * Convenience method for more direct access.
	 * 
	 * @return Connection
	 */
	public static Connection getConnection()
	{
		return ((CommandQ) sRegistry.get(CommandQ.class)).getConnection();
	}

	/**
	 * Convenience method for more direct access.
	 * 
	 * @return Camera
	 */
	public static Camera getCamera()
	{
		return (Camera) sRegistry.get(Camera.class);
	}

	/**
	 * Convenience method for more direct access.
	 * 
	 * @return Motors
	 */
	public static Motors getMotors()
	{
		return (Motors) sRegistry.get(Motors.class);
	}
}
