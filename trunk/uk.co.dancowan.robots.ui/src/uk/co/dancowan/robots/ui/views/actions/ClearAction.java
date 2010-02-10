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
package uk.co.dancowan.robots.ui.views.actions;

import org.eclipse.jface.action.Action;

import uk.co.dancowan.robots.ui.Activator;

/**
 * Action is associated with an instance of a <code>Clearable</code> object.
 * 
 * @see uk.co.dancowan.robots.ui.views.actions.Clearable
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ClearAction extends Action
{
	private Clearable mTarget;

	/**
	 * C'tor.
	 *
	 * @param target the Clearable view
	 */
	public ClearAction(Clearable target)
	{
		super("Clear", Action.AS_PUSH_BUTTON);

		mTarget = target;
		
		setToolTipText("Clear contents");
		setImageDescriptor(Activator.getImageDescriptor("icons/action_clear.gif"));
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		mTarget.clear();
	}
}
