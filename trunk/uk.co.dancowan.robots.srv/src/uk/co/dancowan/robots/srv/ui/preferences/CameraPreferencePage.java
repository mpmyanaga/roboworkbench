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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uk.co.dancowan.robots.srv.SRVActivator;

/**
 * Camera Preference page controls settings for the Camera and CameraView classes.
 * 
 * <p>This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.</p>
 */
public class CameraPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	public CameraPreferencePage()
	{
		super(GRID);
		setPreferenceStore(SRVActivator.getDefault().getPreferenceStore());
		setDescription("Specify how the Camera behaves");
	}

	/**
	 * Creates the field editors.
	 */
	@Override
	public void createFieldEditors()
	{
		new Label(getFieldEditorParent(), SWT.NONE);
		addField(new BooleanFieldEditor(PreferenceConstants.CAMERA_INIT_ON_CONNECT, "&Initialize camera on connection ", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.CAMERA_POLL_ON_CONNECT, "&Start polling on connection ", getFieldEditorParent()));

		Label label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gData = new GridData(GridData.FILL_HORIZONTAL);
		gData.horizontalSpan = 3;
		label.setLayoutData(gData);

		addField(new ColorFieldEditor(PreferenceConstants.CAMERA_BACKGROUND_COLOUR, "&Background colour ", getFieldEditorParent()));

		label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		gData = new GridData(GridData.FILL_HORIZONTAL);
		gData.horizontalSpan = 3;
		label.setLayoutData(gData);

		addField(new RadioGroupFieldEditor(PreferenceConstants.CAMERA_OUTPUT_FORMAT, "Image output format:", 1, new String[][]{{"JPEG", "jpeg"}, {"PNG", "png"}}, getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.CAMERA_DEFAULT_PATH, "Save in ", getFieldEditorParent()));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		// NOP
	}
}