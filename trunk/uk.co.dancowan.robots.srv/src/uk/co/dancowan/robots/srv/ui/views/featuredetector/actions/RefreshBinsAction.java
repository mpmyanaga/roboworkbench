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
import org.eclipse.swt.widgets.Composite;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.SrvHal;

/**
 * ViewPart action to request that the SRV Camera object refreshes
 * the colour bin contents.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class RefreshBinsAction extends Action
{
	private Composite mRepaintComposite;
	/**
	 * C'tor.
	 * 
	 * <p>The <code>Composite</code> argument will receive a redraw
	 * request once the bins have been refreshed to allow repainting
	 * of representations of the bins.</p>
	 *
	 * @param composite
	 */
	public RefreshBinsAction(Composite composite)
	{
		super("Bins", Action.AS_PUSH_BUTTON);

		mRepaintComposite = composite;
		setToolTipText("Refresh colour bins");
		setImageDescriptor(SRVActivator.getImageDescriptor("icons/cmd_grab_bins.gif"));
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		SrvHal.getCamera().getDetector().refreshColourBins();
		mRepaintComposite.redraw();
	}
}
