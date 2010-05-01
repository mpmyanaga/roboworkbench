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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.featuredetector.Pattern;
import uk.co.dancowan.robots.srv.hal.featuredetector.PatternListener;
import uk.co.dancowan.robots.srv.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.ui.utils.ColourManager;

/**
 * Class to render a <code>Pattern</code>.
 * 
 * @author Dan Cowan
 */
public class PatternWidget extends Composite implements PaintListener, IPropertyChangeListener, PatternListener, FeatureWidget
{
	private final Pattern mPattern;
	private final boolean mGrid;

	private Color mForeground;
	private Color mBackground;
	private Color mGridColour;

	private boolean mSelected;
	private boolean mInvert;

	private int mXIndent;
	private int mYIndent;
	private int mUnit;

	/**
	 * C'tor
	 * 
	 * @param pattern the Pattern instance to render
	 */
	public PatternWidget(Composite parent, Pattern pattern, boolean paintGrid)
	{
		super(parent, SWT.BORDER);

		mPattern = pattern;
		mPattern.addListener(this);
		mGrid = paintGrid;
		mSelected = false;
		mInvert = false;

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				selectWidget();
			}
		});

		addPaintListener(this);
		initFromPrefs();
	}

	public void selectWidget()
	{
		if ( ! mSelected)
		{
			mSelected = true;
			SrvHal.getCamera().getDetector().setFocusPattern(mPattern.getIndex());
			redraw();
			notifyListeners(SWT.Selection, new Event()); // event is not read so OK just as a token
		}
	}

	/**
	 * Returns this widget's <code>Pattern</code>.
	 * 
	 * @return Pattern
	 */
	public Pattern getPattern()
	{
		return mPattern;
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
		mUnit = square / 8;
		mXIndent = (size.x-square)/2;
		mYIndent = (size.y-square)/2;

		Color foreground = mInvert ? mBackground : mForeground;
		Color background = mInvert ? mForeground : mBackground;

		GC graphics = e.gc;
		if (mSelected && ! mGrid)
			graphics.setBackground(ColourManager.getColour(SWT.COLOR_WHITE));
		else
			graphics.setBackground(background);
		graphics.fillRectangle(0, 0, size.x, size.y);
		graphics.setBackground(foreground);
		for (int i = 0; i < 8; i ++)
		{
			String str = mPattern.get(i);
			assert str.length() == 8;
			for (int j = 0; j < 8; j ++)
			{
				int x = mXIndent + j*mUnit;
				int y = mYIndent/2 + i*mUnit;
				if (str.charAt(j) == '1')
					graphics.fillRectangle(x, y, mUnit, mUnit);

				if (mGrid)
				{
					graphics.setForeground(mGridColour);
					graphics.drawPolygon(new int[]{x, y, x + mUnit, y, x + mUnit, y + mUnit, x, y + mUnit});
				}
			}
		}
	}

	/**
	 * Returns the X-axis indent of the left edge of the pattern of the composite in pixels.
	 * 
	 * @return int
	 */
	protected int getXIndent()
	{
		return mXIndent;
	}

	/**
	 * Returns the Y-axis indent of the left edge of the pattern of the composite in pixels.
	 * 
	 * @return int
	 */
	protected int getYIndent()
	{
		return mYIndent;
	}

	/**
	 * Returns the unit size of pattern element in pixels.
	 * 
	 * @return int
	 */
	protected int getUnit()
	{
		return mUnit;
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
		else if (event.getProperty().equals(PreferenceConstants.PATTERN_BACKGROUND_COLOUR))
		{
			mBackground = ColourManager.getInstance().getColour((RGB) event.getNewValue());
			redraw();
		}
		else if (event.getProperty().equals(PreferenceConstants.PATTERN_EDITOR_GRID_COLOUR))
		{
			mGridColour = ColourManager.getInstance().getColour((RGB) event.getNewValue());
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

	/**
	 * Returns if this widget is selected.
	 * 
	 * @see uk.co.dancowan.robots.srv.ui.panels.FeatureWidget#getSelection()
	 */
	@Override
	public boolean getSelection()
	{
		return mSelected;
	}

	/**
	 * Sets the selection state of this widget.
	 * 
	 * @see uk.co.dancowan.robots.srv.ui.panels.FeatureWidget#setSelection(boolean)
	 */
	@Override
	public void setSelection(boolean state)
	{
		mSelected = state;
	}

	public void matched()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for (int i = 0; i < 10; i ++)
				{
					Display.getDefault().asyncExec(new Runnable()
					{
						@Override
						public void run()
						{
							mInvert =  ! mInvert;
							redraw();
						}
					});
					try
					{
						Thread.sleep(200);
					}
					catch (InterruptedException e)
					{
						// NOP
					}
				}
			}
		});
		thread.start();
	}

	/*
	 * Set initial behaviour from plugin preferences
	 * Add this class as a change listener
	 */
	private void initFromPrefs()
	{
		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		mForeground = ColourManager.getInstance().getColour(PreferenceConverter.getColor(store, PreferenceConstants.PATTERN_COLOUR));
		mBackground = ColourManager.getInstance().getColour(PreferenceConverter.getColor(store, PreferenceConstants.PATTERN_BACKGROUND_COLOUR));
		mGridColour = ColourManager.getInstance().getColour(PreferenceConverter.getColor(store, PreferenceConstants.PATTERN_EDITOR_GRID_COLOUR));

		store.addPropertyChangeListener(this);
	}

}
