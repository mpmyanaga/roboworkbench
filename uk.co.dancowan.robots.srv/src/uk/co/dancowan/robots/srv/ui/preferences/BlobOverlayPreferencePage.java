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

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uk.co.dancowan.robots.srv.SRVActivator;

/**
 * Blob Overlay Preference page controls settings for the BlobOverlay class.
 * 
 * <p>This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.</p>
 */
public class BlobOverlayPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	public BlobOverlayPreferencePage()
	{
		super(GRID);
		setPreferenceStore(SRVActivator.getDefault().getPreferenceStore());
		setDescription("Specify how Blob overlay looks");
	}

	/**
	 * Creates the field editors.
	 */
	public void createFieldEditors()
	{
		new Label(getFieldEditorParent(), SWT.NONE);
		new Label(getFieldEditorParent(), SWT.NONE);
		addField(new ColorFieldEditor(PreferenceConstants.BLOB_OVERLAY_COLOUR, "&Blob overlay colour ", getFieldEditorParent()));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		// NOP
	}
}