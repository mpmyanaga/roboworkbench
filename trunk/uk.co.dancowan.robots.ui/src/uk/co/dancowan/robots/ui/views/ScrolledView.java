/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2009 Dan Cowan
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
package uk.co.dancowan.robots.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Abstract base class for Eclipse <code>ViewPart</code> classes which need scroll-bar
 * behaviour when the view size falls below a minimum amount.
 * 
 * <p>Extending classes must implement</P><pre>
 * <code>Control getPartControl(Composite)</code></pre><p>to create the view's
 * widgets and layout, the returned <code>Control</code> will then be wrapped with
 * scroll bars. Extending classes must also implement</p><pre>
 * <code>Point getMinSize()</code></pre><p> to return the size in pixels at which
 * the scroll bars will appear.</p>
 *
 * @author Dan Cowan
 * @since version 1.0.0
 */
public abstract class ScrolledView extends ViewPart
{
	private ScrolledComposite mComposite;

	/**
	 * Implements <code>ViewPart.createPartControl(Composite)</code> and makes a call back
	 * to <code>getPartControl(Composite)</code> for the part's widgetry.
	 * 
	 * <p>The view's base composite will be registered with the Help Context subsystem against
	 * the view's ID and the focus passed to that composite to enable the context.</p>
	 * 
	 * @param parent the parent Composite for the widgets
	 */
	public final void createPartControl(Composite parent)
	{
		mComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		mComposite.setLayout(new FillLayout());

		PlatformUI.getWorkbench().getHelpSystem().setHelp(mComposite, getID());

		mComposite.setMinSize(getMinSize());
		mComposite.setContent(getPartControl(mComposite));
		mComposite.setExpandHorizontal(true);
		mComposite.setExpandVertical(true);
	}

	/**
	 * Passing the focus request to the view's base composite.
	 */
	public void setFocus()
	{
		mComposite.setFocus();
	}

	/**
	 * Implement to provide widgets for this view.
	 * 
	 * @param parent the parent Composite for the widgets
	 * @return Control the view's widget container
	 */
	public abstract Control getPartControl(Composite parent);
	
	/**
	 * Return the size below which scroll bars should appear.
	 * 
	 * @return Point the Control's minimum size in pixels
	 */
	public abstract Point getMinSize();

	/**
	 * Return the part's ID string.
	 * 
	 * <p>The ID string should follow the convention:</p>
	 * <pre>pugin.name.partName</pre>
	 * <p>Help contexts are registered against the parts getID() method</p>
	 * 
	 * @return String the part's ID
	 */
	 public abstract String getID();
}
