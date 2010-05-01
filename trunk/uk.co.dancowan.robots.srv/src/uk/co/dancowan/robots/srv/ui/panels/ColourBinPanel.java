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

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import uk.co.dancowan.robots.srv.hal.featuredetector.FeatureDetector;
import uk.co.dancowan.robots.srv.ui.views.camera.CameraCanvas;
import uk.co.dancowan.robots.srv.ui.views.featuredetector.actions.BlobOverlayAction;
import uk.co.dancowan.robots.srv.ui.views.featuredetector.actions.RefreshBinsAction;
import uk.co.dancowan.robots.srv.ui.views.featuredetector.actions.RefreshBlobsAction;
import uk.co.dancowan.robots.ui.views.Panel;

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

	private Group mColourBinComposite;

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
	 * @see uk.co.dancowan.robots.ui.views.Panel#getID()
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.Panel#getDescription()
	 */
	public String getDescription()
	{
		return "Colour Bin Panel";
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.Panel#getPanel(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite getPanel(Composite parent)
	{
		mColourBinComposite = new Group(parent, SWT.NONE);
		mColourBinComposite.setText("Colour Bins");
		mColourBinComposite.setLayout(new GridLayout(FeatureDetector.BIN_COUNT*2, true));

		final ColourBinEditor editor = new ColourBinEditor(mCameraCanvas);
		for (int i = 0; i < FeatureDetector.BIN_COUNT; i ++)
		{
			ColourBinWidget c = new ColourBinWidget(i, mColourBinComposite, editor);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan = 2;
			c.setLayoutData(gd);
			if (i == 0)
				c.setSelection(true);
			mBinSet.addWidget(c);
		}
		
		editor.addWidgets(mColourBinComposite);
		mColourBinComposite.pack();
		editor.selectionChanged();
		return mColourBinComposite;
	}

	public void addToToolBar(IToolBarManager manager)
	{
		manager.appendToGroup("actions", new RefreshBinsAction(mColourBinComposite));
    	manager.appendToGroup("actions", new RefreshBlobsAction());
    	manager.appendToGroup("actions", new Separator());
    	manager.appendToGroup("actions", new BlobOverlayAction(mCameraCanvas));
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.Panel#dispose()
	 */
	public void dispose()
	{
		mBinSet.dispose();
	}
}
