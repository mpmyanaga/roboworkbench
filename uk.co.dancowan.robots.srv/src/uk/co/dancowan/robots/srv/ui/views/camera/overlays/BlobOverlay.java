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
package uk.co.dancowan.robots.srv.ui.views.camera.overlays;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.featuredetector.Blob;
import uk.co.dancowan.robots.srv.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.srv.ui.views.camera.CameraCanvas;
import uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor;

/**
 * Overlay requests blob information from the Camera
 * and paints the blobs to the screen.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class BlobOverlay implements OverlayContributor, IPropertyChangeListener
{
	public static final String ID = "uk.co.dancowan.robots.srv.blobOverlay";

	private Color mColour;
	private boolean mShouldRun;

	/**
	 * C'tor.
	 */
	public BlobOverlay()
	{
		mShouldRun = false;

		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);

		initFromPrefs();
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor#getID()
	 */
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.overlays.AbstractBinOverlay#getName()
	 */
	public String getName()
	{
		return "Blob Overlay";
	}

	/**
	 * Outlines most recent blob information..
	 * 
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.overlays.AbstractBinOverlay#paintOverlay(CameraCanvas)
	 */
	@Override
	public void paintOverlay(CameraCanvas canvas)
	{
		if (canvas.getImage() != null)
		{
			Graphics g = canvas.getImage().getGraphics();
			g.setColor(mColour);
			List<Blob> blobs = SrvHal.getCamera().getDetector().getBlobs();
			for (Blob blob : blobs)
			{
				Point c = canvas.srvToMouse(blob.getX(), blob.getY());
				Point d = canvas.srvToMouse(blob.getWidth(), blob.getHeight());
				g.drawRect(c.x, c.y, d.x, d.y);
				g.drawRect(c.x + 2, c.y + 2, d.x - 4, d.y - 4);
			}
		}
	}

	/**
	 * Requests that the current <code>Blob</code> list from the hardware.
	 */
	public void refresh()
	{
		SrvHal.getCamera().getDetector().refreshBlobs();
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor#setShouldRun(boolean)
	 */
	@Override
	public void setShouldRun(boolean shouldRun)
	{
		mShouldRun = shouldRun;		
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor#shouldRun()
	 */
	@Override
	public boolean shouldRun()
	{
		return mShouldRun;
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(PreferenceConstants.BLOB_OVERLAY_COLOUR))
		{
			RGB rgb = (RGB) event.getNewValue();
			mColour = new Color(rgb.red, rgb.green, rgb.blue);
		}
	}

	/*
	 * Initialise class variables from preference store
	 */
	private void initFromPrefs()
	{
		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		RGB rgb = PreferenceConverter.getColor(store, PreferenceConstants.BLOB_OVERLAY_COLOUR);
		mColour = new Color(rgb.red, rgb.green, rgb.blue);
	}
}
