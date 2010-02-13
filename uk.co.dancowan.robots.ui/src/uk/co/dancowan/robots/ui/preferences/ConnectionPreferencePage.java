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
package uk.co.dancowan.robots.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uk.co.dancowan.robots.ui.Activator;

/**
 * Connection Preference page controls settings for the ConnectionView class.
 * 
 * <p>This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ConnectionPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * C'tor
	 * 
	 * <p>Sets the preference store for this page. Uses a grid layout.</p>
	 */
	public ConnectionPreferencePage()
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Specify how the Connection View behaves");
	}

	/**
	 * Creates the field editors.
	 */
	public void createFieldEditors()
	{
		new Label(getFieldEditorParent(), SWT.NONE);
		addField(new BooleanFieldEditor(PreferenceConstants.CONNECTION_ON_START, "&Connect on startup ", getFieldEditorParent()));

		String [][] data = new String[][]{{"Network", PreferenceConstants.NETWORK}, {"Com Port", PreferenceConstants.COM}};
		addField(new RadioGroupFieldEditor(PreferenceConstants.CONNECTION_MODE, "&Mode ", 2, data, getFieldEditorParent(), true));
	}

	/**
	 * Default implementation does nothing.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		// NOP
	}
}