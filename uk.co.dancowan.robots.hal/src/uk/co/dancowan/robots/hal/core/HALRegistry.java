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
package uk.co.dancowan.robots.hal.core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Top level container class for hardware components.
 * 
 * <p>This class should be initialized with a call to <code>getInstance</code> at
 * the beginning of any application's life cycle.</P>
 * 
 * <p>As <code>Component</code> instances are registered those which are <code>Thread
 * </code>s are also auto-started as necessary.</p>
 * 
 * @see package uk.co.dancowan.robots.hal.core.Component 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class HALRegistry
{
	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);
	private static final Logger ERROR_LOGGER = Logger.getLogger(LoggingService.ERROR_LOGGER);

	private static HALRegistry sInstance;

	private final Map<Class<? extends Component>, Component> mComponentMap;

	/*
	 * Ensure singleton
	 */
	private HALRegistry()
	{
		mComponentMap = new HashMap<Class<? extends Component>, Component>();
	}

	/**
	 * Returns the registry instance, creating the instance of necessary.
	 * 
	 * <p>A standard <code>CommandQ</code> and it's <code>Connection</code>
	 * are both registered and then the extension point registry is
	 * queried for additional hardware in any plugins.</p>
	 * 
	 * @return ComponentsRegistry the singleton instance
	 */
	public static HALRegistry getInsatnce()
	{
		if (sInstance == null)
		{
			sInstance = new HALRegistry();

			// Consider moving these two to an extension point
			CommandQ cmdQ = new CommandQ();
			sInstance.register(cmdQ);
			sInstance.register(cmdQ.getConnection());

			sInstance.loadExtensions();
		}
		return sInstance;
	}

	/**
	 * Register the passed <code>Component</code>.
	 * 
	 * <p>If the component owns a thread which requires starting then the
	 * component will be started on registration.</p>
	 * 
	 * <p>Throws an <code>IllegalArgumentException</code> if another
	 * components of the same class has already been registered.</p>
	 * 
	 * @throws IllegalArgumentException
	 * @param component
	 */
	public void register(Component component)
	{
		if (mComponentMap.containsKey(component.getClass()))
			throw new IllegalArgumentException(component.getID() + " already registered.");

		mComponentMap.put(component.getClass(), component);

		if (component.requiresThreadStartup())
		{
			if (component instanceof Thread)
			{
				Thread thread  = (Thread) component;
				thread.start();
			}
			else
			{
				INFO_LOGGER.finest(component.getID() + " was found not to be a Thread: not started.");
			}
		}

		for (Class<? extends Component> key : mComponentMap.keySet())
		{
			Component c = mComponentMap.get(key);
			c.componentAdded(component);
		}

		INFO_LOGGER.fine(component.getID() + " registered.");
	}

	/**
	 * Return the <code>Component</code> for the given ID.
	 * 
	 * <p>Calling classes should not hold on to references to Components
	 * because they may be removed from the registry or replaced.</p>
	 * 
	 * @param id the Component ID
	 * @return the Component
	 */
	public Component get(Class<? extends Component> cls)
	{
		return mComponentMap.get(cls);
	}

	/**
	 * Return the CommandQ if available.
	 * 
	 * <p>Helper method equivalent to the common call:</p>
	 * <pre><code>    HALRegistry.getInstance().get(CommandQ.class);</code></pre>
	 * 
	 * @return CommandQ
	 */
	public CommandQ getCommandQ()
	{
		return (CommandQ) get(CommandQ.class);
	}

	/*
	 * Load and register extension point contributions
	 */
	private void loadExtensions()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor("uk.co.dancowan.robots.hal.component");
		for (IConfigurationElement element : elements)
		{
			try
			{
				Component component = (Component) element.createExecutableExtension("class");
				register(component);
			}
			catch (CoreException e)
			{
				ERROR_LOGGER.finest(element.getName() + " failed to load a Component.");
			}
		}
	}
}
