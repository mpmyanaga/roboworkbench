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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.camera.Camera;
import uk.co.dancowan.robots.srv.hal.camera.YUV;
import uk.co.dancowan.robots.srv.hal.featuredetector.ColourBin;
import uk.co.dancowan.robots.srv.hal.featuredetector.ColourBinListener;
import uk.co.dancowan.robots.srv.ui.views.camera.YUVUtils;
import uk.co.dancowan.robots.ui.utils.ColourManager;

/**
 * Class associates an SWT <code>Composite</code> with a colour bin on the SRV1q.
 * 
 * <p>Displays the associated bin's mean YUV content and exposes action to set
 * and get the colour component ranges from the hardware. Implements <code>ColourBinListener
 * </code> to react to colour changes in the bin.</p>
 * 
 * @see uk.co.dancowan.robots.srv.hal.featuredetector.ColourBinListener
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ColourBinWidget extends Composite implements ColourBinListener, PaintListener
{
	private final ColourBin mBin;

	private boolean mSelected;
	private ColourBinEditor mEditor;

	/**
	 * C'tor.
	 * 
	 * @see uk.co.dancowan.robots.srv.hal.featuredetector.ColourBin
	 * @see org.eclipse.swt.widgets.Composite
	 * @param bin the colour bin on the SRV1q
	 * @param parent the parent Composite
	 */
	public ColourBinWidget(int bin, Composite parent, ColourBinEditor editor)
	{
		super(parent, SWT.NONE);

		Camera camera = SrvHal.getCamera();
		mBin = camera.getDetector().getBin(bin);
		mBin.addListener(this);
		mEditor = editor;

		mSelected = false;

		addMouseListener(new MouseAdapter()
		{
			/**
			 * @see org.eclipse.swt.events.MouseAdapter#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e)
			{
				mBin.refreshBin();
			}
			/**
			 * 
			 */
			@Override
			public void mouseDown(MouseEvent e)
			{
				if ( ! mSelected)
				{
					mSelected = true;
					SrvHal.getCamera().getDetector().setFocusBin(getBin().getBin());
					redraw();
					notifyListeners(SWT.Selection, new Event()); // event is not read so OK just as a token
					mEditor.selectionChanged();
				}
			}
		});
		addPaintListener(this);
		setToolTipText(tooltip());
	}

	/**
	 * Get this widget's underlying <code>ColourBin</code> object.
	 * 
	 * @return ColourBin
	 */
	public ColourBin getBin()
	{
		return mBin;
	}

	/**
	 * Removes self from ColourBin listeners then calls
	 * <code>super.dispose();</code>.
	 */
	@Override
	public void dispose()
	{
		mBin.removeListener(this);
		super.dispose();
	}

	/**
	 * Set the selection state of this widget.
	 * 
	 * @param selected boolean
	 */
	public void setSelection(boolean selected)
	{
		mSelected = selected;
	}

	/**
	 * Set the selection state of this widget.
	 * 
	 * @return boolean
	 */
	public boolean getSelection()
	{
		return mSelected;
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(mBin.toString());

		return sb.toString();
	}

	/**
	 * Update this composite with the latest from the bin
	 * 
	 * @see uk.co.dancowan.robots.srv.hal.featuredetector.ColourBinListener#colourChanged(YUV)
	 */
	public void colourChanged(final YUV yuv)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (! isDisposed()) // check widget
				{
					RGB rgb = YUVUtils.convertYUVtoRGB(yuv);
					setBackground(ColourManager.getInstance().getColour(rgb));
					setToolTipText(tooltip());
					redraw();
					mEditor.selectionChanged();
				}
			}
		});
	}

	/**
	 * Add selection state to the widget's painting.
	 * 
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	@Override
	public void paintControl(PaintEvent e)
	{
		GC gc = e.gc;
		int width = getSize().x;
		int height = getSize().y;

		Color content = getBackground();
		Color highlight, shade, border;
		if (getSelection())
		{
			border = ColourManager.getColour(SWT.COLOR_RED);
			highlight = ColourManager.getInstance().darken(content, 50);
			shade = ColourManager.getInstance().lighten(content, 50);
		}
		else
		{
			border = ColourManager.getColour(SWT.COLOR_DARK_RED);
			shade = ColourManager.getInstance().darken(content, 30);
			highlight = ColourManager.getInstance().lighten(content, 30);
		}

		// paint the button itself
		RGB rgb = YUVUtils.convertYUVtoRGB(mBin.getMaxYUV());
		setBackground(ColourManager.getInstance().getColour(rgb));
		rgb = YUVUtils.convertYUVtoRGB(mBin.getMinYUV());
		setForeground(ColourManager.getInstance().getColour(rgb));
		gc.fillGradientRectangle(0, 0, width, height, false);

		// the surround
		gc.setBackground(border);
		gc.drawRectangle(0, 0, width, height);

		// Top and left highlights
		gc.setForeground(highlight);
		gc.drawLine(1, 1, width - 1, 1);
		gc.drawLine(1, 1, 1, height - 1);
		highlight = ColourManager.getInstance().darken(highlight, 15);
		gc.setForeground(highlight);
		gc.drawLine(2, 2, width - 2, 2);
		gc.drawLine(2, 2, 2, height - 2);

		// Bottom and right highlights
		gc.setForeground(shade);
		gc.drawLine(2, height - 2, width - 2, height - 2);
		gc.drawLine(width - 2, 2, width - 2, height - 2);
		shade = ColourManager.getInstance().darken(shade, 25);
		gc.setForeground(shade);
		gc.drawLine(1, height - 1, width - 1, height - 1);
		gc.drawLine(width - 1, 1, width - 1, height - 1);
	}

	/*
	 * Hack to allow inner classes to call toString on containing class
	 * for tooltip setting
	 */
	private String tooltip()
	{
		return toString();
	}
}
