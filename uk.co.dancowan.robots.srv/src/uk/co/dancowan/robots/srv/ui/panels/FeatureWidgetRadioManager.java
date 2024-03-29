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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


/**
 * Class to manage selection state of a collection of <code>FeatureWidget</code>s
 * as if they were radio buttons.
 * 
 * <p>Only one widget in the collection can be selected at any one time.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class FeatureWidgetRadioManager implements Listener
{
	private final List<FeatureWidget> mFeatures;

	/**
	 * C'tor.
	 */
	public FeatureWidgetRadioManager()
	{
		mFeatures = new ArrayList<FeatureWidget>();
	}

	/**
	 * Add the widget to the internal collection to be managed.
	 * 
	 * @param widget the FeatureWidget to manage
	 */
	public void addWidget(FeatureWidget widget)
	{
		mFeatures.add(widget);
		widget.addListener(SWT.Selection, this);
	}

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void handleEvent(Event event)
	{
		if (event.widget instanceof FeatureWidget)
		{
			FeatureWidget w = (FeatureWidget) event.widget;
			if (w.getSelection())
			{
				for (FeatureWidget widget : mFeatures)
				{
					if (w != widget && widget.getSelection())
					{
						silentSelect(widget);
						widget.redraw();
					}
				}
			}
		}
	}

	/**
	 * Dispose of widgets.
	 */
	public void dispose()
	{
		for (FeatureWidget bin : mFeatures)
		{
			bin.dispose();
		}
	}

	/*
	 * Silently deselect the passed widget by removing its listeners
	 * and adding them back after setting state.
	 */
	private void silentSelect(FeatureWidget widget)
	{
		Listener[] listeners = widget.getListeners(SWT.Selection);
		for (Listener listener : listeners)
		{
			widget.removeListener(SWT.Selection, listener);
		}
		widget.setSelection(false);
		for (Listener listener : listeners)
		{
			widget.addListener(SWT.Selection, listener);
		}
	}
}
