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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uk.co.dancowan.robots.srv.hal.featuredetector.Pattern;

/**
 * Class extends <code>PatternWidget</code> and adds listeners to make the
 * underlying <code>Pattern</code> editable with mouse clicks.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class PatternEditor extends PatternWidget implements MouseListener, MouseMoveListener, Listener
{
	private int mXPos;
	private int mYPos;

	/**
	 * C'tor
	 * 
	 * @param parent
	 * @param pattern
	 */
	public PatternEditor(Composite parent, Pattern pattern)
	{
		super(parent, pattern, true);

		mXPos = 0;
		mYPos = 0;

		addMouseListener(this);
		addMouseMoveListener(this);
		addPaintListener(this);
	}

	/**
	 * Sets the widgets pattern to match another pattern.
	 * 
	 * <p>Used to make the selected <code>Pattern</code> editable.</p>
	 * 
	 * @param newPattern
	 */
	public void setPattern(Pattern newPattern)
	{
		getPattern().set(newPattern);
	}

	/**
	 * Implementation does nothing.
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(MouseEvent e)
	{
		// NOP
	}

	/**
	 * Implementation does nothing.
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(MouseEvent e)
	{
		// NOP
	}

	/**
	 * Flips the pixel under the pointer.
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(MouseEvent e)
	{
		String line = getPattern().get(mYPos);
		char flipped = line.charAt(mXPos) == '0' ? '1' : '0';
		StringBuffer sb = new StringBuffer(line);
		sb.setCharAt(mXPos, flipped);
		getPattern().set(sb.toString(), mYPos);
		redraw();
	}

	/**
	 * Tracks the mouse pointer location in terms of element indecies in the pattern.
	 * 
	 * <p>Mouse location is translated into pattern space: 0 <= x <= 7, 0 <= y <= 7.</p>
	 * 
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseMove(MouseEvent e)
	{
		mXPos = (e.x - getXIndent())/getUnit();
		mYPos = (e.y - getYIndent())/getUnit();
	}

	/**
	 * Looks for selection events from PatternWidget</code>s and updates the internal pattern
	 * accordingly.
	 * 
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void handleEvent(Event event)
	{
		if (event.widget instanceof PatternWidget)
		{
			PatternWidget widget = (PatternWidget) event.widget;
			Pattern pattern = widget.getPattern();
			getPattern().set(pattern);
			redraw();
		}	
	}
}