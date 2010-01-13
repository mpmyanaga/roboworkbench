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
package uk.co.dancowan.robots.srv.ui.views.camera.actions;

import org.eclipse.jface.action.Action;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.SrvHal;

/**
 * ViewPart action to request that the SRV camera object refresh the blob
 * list for the current colour bin.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class RefreshBlobsAction extends Action
{
	/**
	 * C'tor.
	 */
	public RefreshBlobsAction()
	{
		super("Blobs", Action.AS_PUSH_BUTTON);

		setToolTipText("Refresh blobs");
		setImageDescriptor(SRVActivator.getImageDescriptor("icons/cmd_grab_blobs.gif"));
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		SrvHal.getCamera().getDetector().refreshBlobs();
	}
}
