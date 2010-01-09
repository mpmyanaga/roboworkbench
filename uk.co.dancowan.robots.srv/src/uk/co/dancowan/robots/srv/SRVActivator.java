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
package uk.co.dancowan.robots.srv;

/**
 * This plugin adds Views, Commands and Components for handling the
 * Surveyor SRV1 Blackfin Robot.
 */
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import uk.co.dancowan.robots.srv.services.NotifyService;
import uk.co.dancowan.robots.srv.services.WCSServer;

/**
 * The activator class controls the plug-in life cycle
 */
public class SRVActivator extends AbstractUIPlugin
{
	public static final String PLUGIN_ID = "uk.co.dancowan.robots.srv";

	private static SRVActivator sInstance;

	/** 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		sInstance = this;

		SRVConfig.loadConfig();
		if ("yes".equals(SRVConfig.sProperties.get("start.wcs")))
		{
			// Start WebCamSat components
			WCSServer.getInstance().start();
		}
		if ("yes".equals(SRVConfig.sProperties.get("start.notify")))
		{
			// Start NotifyService
			new NotifyService().start();
		}
	}

	/** 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		sInstance = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SRVActivator getDefault()
	{
		return sInstance;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
