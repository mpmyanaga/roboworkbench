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
package uk.co.dancowan.robots.srv.ui.views.featuredetector.actions;

import org.eclipse.jface.action.Action;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.ui.views.camera.CameraCanvas;
import uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor;
import uk.co.dancowan.robots.srv.ui.views.camera.overlays.BlobOverlay;

/**
 * ViewPart action to request that the SRV camera object refresh the blob
 * list for the current colour bin.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class BlobOverlayAction extends Action
{
	private final CameraCanvas mCanvas;

	private boolean mState;

	/**
	 * C'tor.
	 */
	public BlobOverlayAction(CameraCanvas canvas)
	{
		super("Overlay", AS_PUSH_BUTTON);

		mCanvas = canvas;
		mState= false;

		setToolTipText("Overlay blobs");
		setImageDescriptor(SRVActivator.getImageDescriptor("icons/cmd_overlay_blobs.gif"));
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		OverlayContributor oc = mCanvas.getOverlayManager().getOverlay(BlobOverlay.ID);
		if (oc != null)
		{
			oc.setShouldRun(mState);
			mState = !mState;
		}
	}
}
