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
package uk.co.dancowan.robots.ui.views.actions;

/**
 * Interface for the target of a <code>ClearAction</code>
 * 
 * <p>A <code>Clearable</code> object is usually a text widget or other container which may need
 * to be reset by an action but tis is not a restriction on use.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public interface Clearable
{
	/**
	 * Clears this clearable.
	 */
	public void clear();
}
