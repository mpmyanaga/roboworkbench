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
package uk.co.dancowan.robots.hal.core;


/**
 * Abstract base class for <code>Component</code>s.
 * 
 * <p>Implements the interface with no-operation methods.</p>
 * 
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public abstract class AbstractComponent implements Component
{
	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#getID()
	 */
	@Override
	public abstract String getID();

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#componentAdded(uk.co.dancowan.robots.hal.core.Component)
	 */
	@Override
	public void componentAdded(Component component)
	{
		// NOP
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#componentRemoved(uk.co.dancowan.robots.hal.core.Component)
	 */
	@Override
	public void componentRemoved(Component component)
	{
		// NOP
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.Component#requiresThreadStartup()
	 */
	@Override
	public boolean requiresThreadStartup()
	{
		return false;
	}
}
