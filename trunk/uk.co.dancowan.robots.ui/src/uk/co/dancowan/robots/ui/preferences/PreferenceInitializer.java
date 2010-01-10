package uk.co.dancowan.robots.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;

import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.utils.ColourManager;

/**
 * Class used to initialize default preference values.
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
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		// Keypad
		store.setDefault(PreferenceConstants.KEYPAD_COLS, 5);
		store.setDefault(PreferenceConstants.KEYPAD_ROWS, 3);

		// Command logging view, behaviour and appearance
		store.setDefault(PreferenceConstants.COMMAND_EXCEPTIONS, true);
		store.setDefault(PreferenceConstants.COMMAND_BUFFER_SIZE, 512);
		store.setDefault(PreferenceConstants.COMMAND_UNLIMITED_BUFFER, true);
		store.setDefault(PreferenceConstants.COMMAND_WRAP, true);
		store.setDefault(PreferenceConstants.COMMAND_TRANSLATE, false);
		store.setDefault(PreferenceConstants.COMMAND_HEX_CMD, true);
		store.setDefault(PreferenceConstants.COMMAND_WAIT, true);
		PreferenceConverter.setValue(store, PreferenceConstants.COMMAND_OUTPUT_COLOUR, ColourManager.getColour(SWT.COLOR_DARK_GRAY).getRGB());
		PreferenceConverter.setValue(store, PreferenceConstants.COMMAND_INPUT_COLOUR, ColourManager.getColour(SWT.COLOR_DARK_GREEN).getRGB());
		PreferenceConverter.setValue(store, PreferenceConstants.COMMAND_ERROR_COLOUR, ColourManager.getColour(SWT.COLOR_RED).getRGB());

		// Connection view, behaviour and appearance
		store.setDefault(PreferenceConstants.CONNECTION_ON_START, true);
		store.setDefault(PreferenceConstants.CONNECTION_PIN, false);
		store.setDefault(PreferenceConstants.CONNECTION_SHOW_TX, false);
		store.setDefault(PreferenceConstants.CONNECTION_SHOW_RX, false);
		store.setDefault(PreferenceConstants.CONNECTION_BUFFER_SIZE, 10);
		store.setDefault(PreferenceConstants.CONNECTION_UNLIMITED_BUFFER, true);
		store.setDefault(PreferenceConstants.CONNECTION_MODE, PreferenceConstants.NETWORK);
		store.setDefault(PreferenceConstants.CONNECTION_WRAP, true);
		PreferenceConverter.setValue(store, PreferenceConstants.CONNECTION_TX_COLOUR, ColourManager.getColour(SWT.COLOR_DARK_GRAY).getRGB());
		PreferenceConverter.setValue(store, PreferenceConstants.CONNECTION_RX_COLOUR, ColourManager.getColour(SWT.COLOR_GRAY).getRGB());
		PreferenceConverter.setValue(store, PreferenceConstants.CONNECTION_MESSAGE_COLOUR, ColourManager.getColour(SWT.COLOR_BLUE).getRGB());
		PreferenceConverter.setValue(store, PreferenceConstants.CONNECTION_ERROR_COLOUR, ColourManager.getColour(SWT.COLOR_RED).getRGB());
	}
}
