package uk.co.dancowan.robots.ui.preferences;

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

import uk.co.dancowan.robots.ui.Activator;

/**
 * Logger Preference page controls settings for the LogView class.
 * 
 * <p>This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.</p>
 */
public class CommandConsolePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private IntegerFieldEditor mBufferEditor;
	private BooleanFieldEditor mTranslateEditor;

	public CommandConsolePreferencePage()
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Specify how the Command Console behaves");
	}

	/**
	 * Creates the field editors.
	 */
	public void createFieldEditors()
	{
		new Label(getFieldEditorParent(), SWT.NONE);
		new Label(getFieldEditorParent(), SWT.NONE);
		addField(new ColorFieldEditor(PreferenceConstants.COMMAND_OUTPUT_COLOUR, "&Output stream colour ", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.COMMAND_INPUT_COLOUR, "&Input stream colour ", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.COMMAND_ERROR_COLOUR, "&Error stream colour ", getFieldEditorParent()));

		Label label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gData = new GridData(GridData.FILL_HORIZONTAL);
		gData.horizontalSpan = 2;
		label.setLayoutData(gData);

		addField(new BooleanFieldEditor(PreferenceConstants.COMMAND_HEX_CMD, "Commands sent in &Hex ", getFieldEditorParent()));
		mTranslateEditor = new BooleanFieldEditor(PreferenceConstants.COMMAND_TRANSLATE, "&Translate Decimal input into Hex commands before sending", getFieldEditorParent());
		addField(mTranslateEditor);
		addField(new BooleanFieldEditor(PreferenceConstants.COMMAND_WAIT, "&Wait for new-line character before sending ", getFieldEditorParent()));
		
		label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		gData = new GridData(GridData.FILL_HORIZONTAL);
		gData.horizontalSpan = 2;
		label.setLayoutData(gData);

		addField(new BooleanFieldEditor(PreferenceConstants.COMMAND_UNLIMITED_BUFFER, "&Limit log buffer ", getFieldEditorParent()));
		mBufferEditor = new IntegerFieldEditor(PreferenceConstants.COMMAND_BUFFER_SIZE, "&Buffer size (k): ", getFieldEditorParent());
		mBufferEditor.setValidRange(1, 1024);
		addField(mBufferEditor);

		addField(new BooleanFieldEditor(PreferenceConstants.COMMAND_WRAP, "&Wrap log lines ", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.COMMAND_EXCEPTIONS, "Log program &Exceptions in view ", getFieldEditorParent()));

		setFromPreferences();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		super.propertyChange(event);

		if (PreferenceConstants.COMMAND_UNLIMITED_BUFFER.equals(((FieldEditor)event.getSource()).getPreferenceName()))
		{
			boolean enabled = (Boolean) event.getNewValue();
			mBufferEditor.setEnabled(enabled, getFieldEditorParent());
		}
		else if (PreferenceConstants.COMMAND_HEX_CMD.equals(((FieldEditor)event.getSource()).getPreferenceName()))
		{
			boolean hex = (Boolean) event.getNewValue();
			mTranslateEditor.setEnabled(hex, getFieldEditorParent());
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
		boolean enabled = prefs.getBoolean(PreferenceConstants.COMMAND_UNLIMITED_BUFFER);
		mBufferEditor.setEnabled(enabled, getFieldEditorParent());
	}
}