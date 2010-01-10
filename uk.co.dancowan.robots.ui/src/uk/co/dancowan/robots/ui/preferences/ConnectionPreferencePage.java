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
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
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
 */
public class ConnectionPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private IntegerFieldEditor mBufferEditor;

	/**
	 * C'tor.
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

		Label label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gData = new GridData(GridData.FILL_HORIZONTAL);
		gData.horizontalSpan = 2;
		label.setLayoutData(gData);

		addField(new ColorFieldEditor(PreferenceConstants.CONNECTION_MESSAGE_COLOUR, "Message stream colour ", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.CONNECTION_ERROR_COLOUR, "Error stream colour ", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.CONNECTION_TX_COLOUR, "TX stream colour ", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.CONNECTION_RX_COLOUR, "RX stream colour ", getFieldEditorParent()));

		label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		gData = new GridData(GridData.FILL_HORIZONTAL);
		gData.horizontalSpan = 2;
		label.setLayoutData(gData);

		addField(new BooleanFieldEditor(PreferenceConstants.CONNECTION_UNLIMITED_BUFFER, "&Limit log buffer ", getFieldEditorParent()));
		mBufferEditor = new IntegerFieldEditor(PreferenceConstants.CONNECTION_BUFFER_SIZE, "&Buffer size (k): ", getFieldEditorParent());
		mBufferEditor.setValidRange(1, 1024);
		addField(mBufferEditor);
		
		addField(new BooleanFieldEditor(PreferenceConstants.CONNECTION_WRAP, "&Wrap lines ", getFieldEditorParent()));

		setFromPreferences();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		super.propertyChange(event);

		if (PreferenceConstants.CONNECTION_UNLIMITED_BUFFER.equals(((FieldEditor)event.getSource()).getPreferenceName()))
		{
			boolean enabled = (Boolean) event.getNewValue();
			mBufferEditor.setEnabled(enabled, getFieldEditorParent());
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		// NOP
	}

	/*
	 * Set initial widget enablement from preference store.
	 */
	private void setFromPreferences()
	{
		IPreferenceStore prefs = getPreferenceStore();
		boolean enabled = prefs.getBoolean(PreferenceConstants.CONNECTION_UNLIMITED_BUFFER);
		mBufferEditor.setEnabled(enabled, getFieldEditorParent());
	}
}