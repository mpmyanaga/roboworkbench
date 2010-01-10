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
package uk.co.dancowan.robots.hal;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import uk.co.dancowan.robots.hal.core.Config;

/**
 * The activator class controls the plug-in life cycle.
 */
public class HALActivator extends Plugin
{
	public static final String PLUGIN_ID = "uk.co.dancowan.robots.hal";

	private static HALActivator sPlugin;

	/**
	 * C'tor.
	 * 
	 * <p>Causes the initial hardware configuration file "./etc/.config" to be loaded.</p>
	 */
	public HALActivator()
	{
		Config.loadConfig();
	}

	/**
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		sPlugin = this;
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		sPlugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static HALActivator getDefault()
	{
		return sPlugin;
	}
}
