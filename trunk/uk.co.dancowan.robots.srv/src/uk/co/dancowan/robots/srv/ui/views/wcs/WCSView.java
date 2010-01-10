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

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.srv.SRVConfig;
import uk.co.dancowan.robots.ui.utils.ColourManager;
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

	private static final Logger ERROR_LOGGER = Logger.getLogger(LoggingService.ERROR_LOGGER);
	private static final Point MIN_SIZE = new Point(300, 140);

	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public Control getPartControl(Composite parent)
	{
		final Composite part = new Composite(parent, SWT.BORDER);
		part.setBackground(ColourManager.getColour(ColourManager.ERROR_COLOUR));
		part.setLayout(new FormLayout());

		try
		{
			final Browser browser = new Browser(part, SWT.MOZILLA);
			String server = SRVConfig.sProperties.get("wcs.server");
			String port = SRVConfig.sProperties.get("wcs.port");
			browser.setUrl(server + ":" + port);

			FormData data = new FormData();
			data.top = new FormAttachment(0, 6);
			data.left = new FormAttachment(0, 6);
			data.right = new FormAttachment(100, -6);
			data.bottom = new FormAttachment(100, -6);
			browser.setLayoutData(data);
		}
		catch(SWTException e)
		{
			ERROR_LOGGER.finest(e.getMessage());
		}

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