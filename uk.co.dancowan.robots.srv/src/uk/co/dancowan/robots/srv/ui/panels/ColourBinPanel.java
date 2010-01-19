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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import uk.co.dancowan.robots.srv.hal.featuredetector.FeatureDetector;
import uk.co.dancowan.robots.srv.ui.views.camera.CameraCanvas;

/**
 * Class manages a view sub-panel for the low level colour vision in the SRV1.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ColourBinPanel implements Panel
{
	public static final String ID = "uk.co.dancowan.robots.ui.ColourBinPanel";

	private final FeatureWidgetRadioManager mBinSet;
	private final CameraCanvas mCameraCanvas;

	/**
	 * C'tor
	 * 
	 * <p>If the CameraCanvas argument is null then no overlay button will be added
	 * to the panel.</p>
	 * 
	 * @param canvas CameraCanvas, can be null
	 */
	public ColourBinPanel(CameraCanvas canvas)
	{
		mCameraCanvas = canvas;
		mBinSet = new FeatureWidgetRadioManager();
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.panels.Panel#getID()
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.panels.Panel#getPanel(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite getPanel(Composite parent)
	{
		Group panel = new Group(parent, SWT.NONE);
		panel.setText("Colour Bins");
		panel.setLayout(new GridLayout(FeatureDetector.BIN_COUNT, true));

		final ColourBinEditor editor = new ColourBinEditor(mCameraCanvas);
		for (int i = 0; i < FeatureDetector.BIN_COUNT; i ++)
		{
			ColourBinWidget c = new ColourBinWidget(i, panel, editor);
			c.setLayoutData(new GridData(GridData.FILL_BOTH));
			if (i == 0)
				c.setSelection(true);
			mBinSet.addWidget(c);
		}
		
		editor.addWidgets(panel);
		panel.pack();
		editor.selectionChanged();
		return panel;
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.panels.Panel#dispose()
	 */
	public void dispose()
	{
		mBinSet.dispose();
	}
}
