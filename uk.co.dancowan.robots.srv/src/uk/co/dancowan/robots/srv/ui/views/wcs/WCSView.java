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
package uk.co.dancowan.robots.srv.ui.views.wcs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uk.co.dancowan.robots.srv.SRVConfig;
import uk.co.dancowan.robots.ui.views.ScrolledView;

/**
 * Eclipse <code>View</code> to display the WebCamSat interface in an embedded browser.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class WCSView extends ScrolledView
{
	public static final String ID = "robots.srv.WCSView";

	private static final Point MIN_SIZE = new Point(300, 140);

	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control getPartControl(Composite parent)
	{
		Browser part = new Browser(parent, SWT.BORDER);
		String server = SRVConfig.sProperties.get("wcs.server");
		String port = SRVConfig.sProperties.get("wcs.port");
		part.setUrl("http://" + server + ":" + port);
		part.pack();
		return part;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getMinSize()
	 */
	@Override
	public Point getMinSize()
	{
		return MIN_SIZE;
	}
}