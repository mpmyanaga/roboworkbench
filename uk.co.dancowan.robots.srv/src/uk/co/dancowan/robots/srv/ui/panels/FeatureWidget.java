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
package uk.co.dancowan.robots.srv.ui.panels;

import org.eclipse.swt.widgets.Listener;

/**
 * Interface for a widget to render feature information.
 * 
 * <p>Usually one of a set of related features such as ColourBins or Patterns which require
 * selection management such as radio button behaviour amongst the set.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public interface FeatureWidget
{
	/**
	 * Requests this widget redraw its bounds.
	 */
	public void redraw();

	/**
	 * Adds a listener to receive events of the passed SWT event type.
	 * 
	 * @param type SWT event type constant
	 * @param listener the Listener for the event
	 */
	public void addListener(int type, Listener listener);

	/**
	 * Removes the listener of the passed SWT event type.
	 * 
	 * @param type SWT event type constant
	 * @param listener the Listener for the event
	 */
	public void removeListener(int type, Listener listener);

	/**
	 * Returns an array of Listeners registered against the passed SWT event type.
	 * 
	 * @param type SWT event type constant
	 * @return Listener[]
	 */
	public Listener[] getListeners(int type);

	/**
	 * Requests that this widget dispose of any resources held.
	 */
	public void dispose();

	/**
	 * Returns if this widget is selected.
	 * 
	 * @see uk.co.dancowan.robots.srv.ui.panels.FeatureWidget#getSelection()
	 */
	public boolean getSelection();

	/**
	 * Sets the selection state of this widget.
	 * 
	 * @see uk.co.dancowan.robots.srv.ui.panels.FeatureWidget#setSelection(boolean)
	 */
	public void setSelection(boolean state);
}
