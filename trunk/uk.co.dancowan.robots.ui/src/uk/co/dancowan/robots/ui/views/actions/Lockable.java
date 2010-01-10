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
 * Interface for objects which are 'lockable'.
 * 
 * <p>A lockable object is normally a text widget in which the
 * focus follows the caret as additional lines are appended. Pinning
 * the widget will stop the focus from following the end of the
 * text widget's content.</p>
 * 
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public interface Lockable
{
	/**
	 * Stops this lockable following the content.
	 */
	public void pin();
	/**
	 * Unlocks this lockable.
	 */
	public void release();
}
