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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.featuredetector.Pattern;
import uk.co.dancowan.robots.srv.hal.featuredetector.PatternListener;
import uk.co.dancowan.robots.srv.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.ui.utils.ColourManager;

/**
 * Class to render a <code>Pattern</code>.
 * 
 * @author Dan Cowan
 */
public class PatternWidget extends Composite implements PaintListener, IPropertyChangeListener, PatternListener
{
	private final Pattern mPattern;

	private Color mForeground;
	private Color mBackground;

	/**
	 * C'tor
	 * 
	 * @param pattern the Pattern instance to render
	 */
	public PatternWidget(Composite parent, Pattern pattern)
	{
		super(parent, SWT.BORDER);

		addPaintListener(this);
		mPattern = pattern;
		mPattern.addListener(this);
		initFromPrefs();
	}

	/**
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	@Override
	public void paintControl(PaintEvent e)
	{
		Point size = getSize();
		int square = size.x > size.y ? size.y : size.x;
		square -= 2;
		int unit = square / 8;

		GC graphics = e.gc;
		graphics.setBackground(mBackground);
		graphics.fillRectangle(0, 0, size.x, size.y);
		graphics.setBackground(mForeground);
		for (int i = 0; i < 8; i ++)
		{
			String str = mPattern.get(i);
			assert str.length() == 8;
			for (int j = 0; j < 8; j ++)
			{
				int x = (size.x-square)/2 + j*unit;
				int y = (size.y-square)/2 + i*unit;
				if (str.charAt(j) == '1')
					graphics.fillRectangle(x, y, unit, unit);
			}
		}
		System.err.println("");
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(PreferenceConstants.PATTERN_COLOUR))
		{
			mForeground = ColourManager.getInstance().getColour((RGB) event.getNewValue());
			redraw();
		}
		else if (event.getProperty().equals(PreferenceConstants.CAMERA_BACKGROUND_COLOUR))
		{
			mBackground = ColourManager.getInstance().getColour((RGB) event.getNewValue());
			redraw();
		}
	}

	/**
	 * Implementation asks the widget to repaint itself.
	 * 
	 * <p>Wraps the repaint call in a runnable asynchronously executed by the SWT thread.</p>
	 * 
	 * @see uk.co.dancowan.robots.srv.hal.featuredetector.PatternListener#patternUpdated()
	 */
	@Override
	public void patternUpdated()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				redraw();
			}
		});
	}

	/*
	 * Set initial behaviour from plugin preferences
	 * Add this class as a change listener
	 */
	private void initFromPrefs()
	{
		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		mForeground = ColourManager.getInstance().getColour(PreferenceConverter.getColor(store, PreferenceConstants.PATTERN_COLOUR));
		mBackground = ColourManager.getInstance().getColour(PreferenceConverter.getColor(store, PreferenceConstants.CAMERA_BACKGROUND_COLOUR));

		store.addPropertyChangeListener(this);
	}
}
