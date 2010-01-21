/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (C) 2009 Dan Cowan
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
package uk.co.dancowan.robots.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Shows a ViewPart for the given ID.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public abstract class ShowViewHandler extends AbstractHandler
{
	private final String mViewPart;

	/**
	 * C'tor.
	 * 
	 * <p>Extending classes should call <code>super(String)</code> to set the ID of
	 * the view to be opened.</p>
	 * 
	 * @param viewPart
	 */
	public ShowViewHandler(String viewPart)
	{
		mViewPart = viewPart;
	}

	/**
	 * Execute the command logic in the given context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		try
		{
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(mViewPart);
		}
		catch (PartInitException e)
		{
			throw new ExecutionException("Error while opening view", e);
		}

		return null;
	}
}
