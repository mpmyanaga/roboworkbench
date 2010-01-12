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
package uk.co.dancowan.robots.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import uk.co.dancowan.robots.ui.views.keypad.CommandButtonDescriptor;


/**
 * Support for reading classes from roboworkbench extension points.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class PluginRegistrySupport
{
	private static final String CMD_BTN_EXT = "uk.co.dancowan.robots.ui.commandButton";

	/**
	 * Read CommandButtonDescriptors from extension their point.
	 * 
	 * <p>The returned list will be sorted by commandButton index.</p>
	 * 
	 * @return List<CommandButtonDescriptor>
	 */
	public static List<CommandButtonDescriptor> parseCommandButtons()
	{
		List<CommandButtonDescriptor> descriptors = new ArrayList<CommandButtonDescriptor>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(CMD_BTN_EXT);
		for (IConfigurationElement element : elements)
		{
			element.getContributor().getName();
			CommandButtonDescriptor desc = new CommandButtonDescriptor(element);
			descriptors.add(desc);
		}

		Collections.sort(descriptors);
		return descriptors;
	}
}
