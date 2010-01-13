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
package uk.co.dancowan.robots.srv.ui.panels;

import org.eclipse.swt.widgets.Composite;

/**
 * Interface for a panel.
 * 
 * <p>A panel class is a reusable control with command of a certain area of the
 * robot's behaviour which may appear in a number of views.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public interface Panel
{
	/**
	 * Return the <code>Panel's unique identifier
	 * 
	 * @return String ID
	 */
	public String getID();
	
	/**
	 * Return the panel's widgets layed out on a composite
	 * 
	 * @param parent the parent Composite for this control
	 * @return Composite this control
	 */
	public Composite getPanel(Composite parent);

	/**
	 * Dispose of any resources.
	 */
	public void dispose();
}
