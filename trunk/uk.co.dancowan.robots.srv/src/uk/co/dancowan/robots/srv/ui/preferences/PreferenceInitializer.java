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
package uk.co.dancowan.robots.srv.ui.preferences;

import java.awt.Color;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;

import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.utils.ColourManager;

/**
 * Class used to initialize default preference values for the SRV plugin.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	/**
	 * Initialize defaults.
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
		// Camera view, behaviour and appearance
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.CAMERA_INIT_ON_CONNECT, true);
		store.setDefault(PreferenceConstants.CAMERA_POLL_ON_CONNECT, false);
		store.setDefault(PreferenceConstants.CAMERA_BACKGROUND_COLOUR, Color.GRAY.getRGB());
		store.setDefault(PreferenceConstants.CAMERA_POLL_DELAY, 200);
		store.setDefault(PreferenceConstants.CAMERA_OUTPUT_FORMAT, "jpeg");
		store.setDefault(PreferenceConstants.CAMERA_OUTPUT_FORMAT, System.getProperty("user.home"));

		// Blob Overlay
		PreferenceConverter.setValue(store, PreferenceConstants.BLOB_OVERLAY_COLOUR, ColourManager.getColour(SWT.COLOR_RED).getRGB());
	}
}
