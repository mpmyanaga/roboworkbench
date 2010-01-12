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
package uk.co.dancowan.robots.ui.views.keypad;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uk.co.dancowan.robots.ui.views.ScrolledView;

/**
 * Eclipse <code>View</code> implementation to display commands.
 * 
 * <p>Keypad displays a number of configurable function keys which
 * can be used with atomic commands from the <code>AtomicCommandFactory</code>
 * class.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class KeypadView extends ScrolledView
{
	public static final String ID = "uk.co.dancowan.robots.ui.keypadView";

	private static final Point MIN_SIZE= new Point(300, 150);

	private KeyProvider mProvider;

	/**
	 * C'tor
	 * 
	 * <p>Must remain no-args for Eclipse extension point.</p>
	 */
	public KeypadView()
	{
		mProvider = new KeyProvider();
	}
	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getMinSize()
	 */
	@Override
	public Point getMinSize()
	{
		return MIN_SIZE;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control getPartControl(Composite parent)
	{
		final Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(5, true));
		
		try
		{
			mProvider.createButtons(buttonComposite);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		//PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, ID);
		return buttonComposite;
	}

	/**
	 * Return the view's unique identifier.
	 * 
	 * @return String
	 */
	public String getID()
	{
		return ID;
	}
}
