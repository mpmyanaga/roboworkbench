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
 * Action pins and releases an instance of a <code>Lockable</code> object
 * 
 * @see uk.co.dancowan.robots.ui.views.actions.Lockable
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ScrollLockAction extends Action
{
	private Lockable mTarget;
	private boolean mState;

	/**
	 * C'tor
	 *
	 * @param target the <code>Lockable</code> view
	 */
	public ScrollLockAction(Lockable target)
	{
		super("Lock", Action.AS_CHECK_BOX);

		mTarget = target;
		mState = false;
		
		setToolTipText("Lock scrollbar");
		setImageDescriptor(Activator.getImageDescriptor("icons/cmd_scroll_lock.png"));
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		if (mState)
			mTarget.release();
		else
			mTarget.pin();
		mState = !mState;
	}
}
