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
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uk.co.dancowan.robots.srv.SRVActivator;

/**
 * Pattern Preference page controls settings for the Pattern class and related views.
 * 
 * <p>This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.</p>
 */
public class PatternPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private FieldEditor mThreshold;
	
	public PatternPreferencePage()
	{
		super(GRID);
		setPreferenceStore(SRVActivator.getDefault().getPreferenceStore());
		setDescription("Specify how the Pattern recognition behaves");
	}

	/**
	 * Creates the field editors.
	 */
	@Override
	public void createFieldEditors()
	{
		new Label(getFieldEditorParent(), SWT.NONE);
		new Label(getFieldEditorParent(), SWT.NONE);
		addField(new ColorFieldEditor(PreferenceConstants.PATTERN_COLOUR, "&Pattern colour ", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.PATTERN_BACKGROUND_COLOUR, "&Pattern background colour ", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.PATTERN_EDITOR_GRID_COLOUR, "&Editor's &grid colour ", getFieldEditorParent()));

		Label label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gData = new GridData(GridData.FILL_HORIZONTAL);
		gData.horizontalSpan = 3;
		label.setLayoutData(gData);

		addField(new BooleanFieldEditor(PreferenceConstants.PATTERN_ALLOW_MULTIPLE, "Match multiple patterns", getFieldEditorParent()));
		mThreshold = new IntegerFieldEditor(uk.co.dancowan.robots.srv.ui.preferences.PreferenceConstants.PATTERN_THRESHOLD, "Similarity threshold", getFieldEditorParent());
		addField(mThreshold);

		setFromPreferences();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		// NOP
	}

	/**
	 * Implementation changes widget enablement depending on preference changes within the page.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		super.propertyChange(event);

		if (PreferenceConstants.PATTERN_ALLOW_MULTIPLE.equals(((FieldEditor)event.getSource()).getPreferenceName()))
		{
			boolean enabled = (Boolean) event.getNewValue();
			mThreshold.setEnabled(enabled, getFieldEditorParent());
		}
	}

	/*
	 * Set initial widget enablement from preference store.
	 */
	private void setFromPreferences()
	{
		IPreferenceStore prefs = getPreferenceStore();
		boolean enabled = prefs.getBoolean(PreferenceConstants.PATTERN_ALLOW_MULTIPLE);
		mThreshold.setEnabled(enabled, getFieldEditorParent());
	}
}