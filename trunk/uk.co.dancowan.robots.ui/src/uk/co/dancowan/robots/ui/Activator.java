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
package uk.co.dancowan.robots.ui;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.ui.utils.TextUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "uk.co.dancowan.robots.ui";

	private static StringBuilder LOG_CACHE = new StringBuilder();
	private static final Logger LOGGER = Logger.getLogger(LoggingService.ROOT_LOGGER); // the root logger
	static
	{
		// Initial handler logs to standard out.
		LOGGER.addHandler(new Handler()
		{
			@Override
			public void publish(LogRecord record)
			{
				System.out.println(record.getMessage());
				if (LOG_CACHE != null)
				{
					LOG_CACHE.append(record.getMessage());
					LOG_CACHE.append(TextUtils.CR);
				}
			}
			@Override
			public void flush()
			{
				System.out.flush();
			}
			@Override
			public void close() throws SecurityException
			{
				// NOP: do not close standard out.
			}
		});
		LOGGER.setLevel(Level.FINER);
	}

	private static Activator sPlugin;

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		sPlugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		sPlugin = null;
		super.stop(context);
	}

	/**
	 * Returns historical logging since startup.
	 * 
	 * <p>Sets the internal cache to null to prevent further caching and unwanted
	 * buildup of memory. It follows this method can only meaningfully be called once.</p>
	 * 
	 * @return String the log so far
	 */
	public String getLogHistory()
	{
		String cache = "";
		if (LOG_CACHE != null)
		{
			cache = LOG_CACHE.toString();
			LOG_CACHE = null;
		}
		return cache;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return sPlugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
