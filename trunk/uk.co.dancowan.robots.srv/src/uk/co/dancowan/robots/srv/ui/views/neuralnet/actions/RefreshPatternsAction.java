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
package uk.co.dancowan.robots.srv.ui.views.neuralnet.actions;

import org.eclipse.jface.action.Action;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.featuredetector.PatternMemory;

/**
 * ViewPart action to request that the SRV return stored pattern information.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class RefreshPatternsAction extends Action
{
	/**
	 * C'tor.
	 */
	public RefreshPatternsAction()
	{
		super("Patterns", Action.AS_PUSH_BUTTON);

		setToolTipText("Refresh patterns");
		setImageDescriptor(SRVActivator.getImageDescriptor("icons/cmd_grab_patterns.png"));
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		// TODO hook this up properly
		PatternMemory pm = new PatternMemory();
		pm.refreshPatterns();
	}
}
