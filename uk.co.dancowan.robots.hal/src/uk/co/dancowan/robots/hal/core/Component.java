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
 * Interface to mark a physical components of the SRV1 system.
 * 
 * <p>Interface determines methods used with the <code>ComponentRegistry
 * </code> to allow for managing various components and their dependencies.
 * </p>
 * 
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public interface Component
{
	/**
	 * Return this component's ID string.
	 * 
	 * @return String, the component's ID
	 */
	public String getID();

	/**
	 * Return true if this <code>Component</code> is an extension of a Java
	 * <code>Thread</code> which needs starting.
	 * 
	 * <p><code>Component</code> must implement <code>Thread</code></p>
	 * 
	 * @return boolean
	 */
	public boolean requiresThreadStartup();

	/**
	 * Call-back allows the <code>ComponentRegistry</code> to inform this
	 * <code>Component</code> that another <code>Component</code> has been
	 * registered.
	 * 
	 * <p>This method can be used to register listeners on the newly registered
	 * <code>Component</code>.</p>
	 * 
	 * @param Component the newly registered Component
	 */
	public void componentAdded(Component component);

	/**
	 * Call-back allows the <code>ComponentRegistry</code> to inform this
	 * <code>Component</code> that another <code>Component</code> has been
	 * removed.
	 * 
	 * <p>This method should be used to remove listeners previously registered
	 * <code>Component</code> and otherwise tidy up any relationship.</p>
	 * 
	 * @param Component the Component just removed
	 */
	public void componentRemoved(Component component);
}
