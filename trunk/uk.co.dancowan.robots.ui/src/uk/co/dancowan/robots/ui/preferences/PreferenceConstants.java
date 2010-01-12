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

/**
 * Constant definitions for plug-in preferences.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class PreferenceConstants
{
	// Number of rows of keys in the keypad view
	public static final String KEYPAD_ROWS = "robots.ui.views.keypadRows";

	// Number of columns of keys in the keypad view
	public static final String KEYPAD_COLS = "robots.ui.views.keypadCols";

	// Buffer size for the LogView class's text widget.
	public static final String COMMAND_BUFFER_SIZE = "robots.ui.views.logBufferSize";

	// Unlimited size for the LogView class's text widget.
	public static final String COMMAND_UNLIMITED_BUFFER = "robots.ui.views.logUnlimitedBuffer";

	// Flag to display errors in the LogView.
	public static final String COMMAND_EXCEPTIONS = "robots.ui.views.logShowException";

	// Flag to determine line wrapping.
	public static final String COMMAND_WRAP = "robots.ui.views.logWrap";

	// Flag to wait for new line char on send.
	public static final String COMMAND_WAIT = "robots.ui.views.logWait";

	// Flag for expected command mode: hex or dec
	public static final String COMMAND_HEX_CMD = "robots.ui.views.logHexCmd";

	// Flag for translating dec input into hex commands
	public static final String COMMAND_TRANSLATE = "robots.ui.views.logTranslate";

	// Colour for user input
	public static final String COMMAND_INPUT_COLOUR = "robots.ui.views.logInputColour";

	// Colour for message output
	public static final String COMMAND_OUTPUT_COLOUR = "robots.ui.views.logOutputColour";

	// Colour for error output
	public static final String COMMAND_ERROR_COLOUR = "robots.ui.views.logErrorColour";
	
	// Flag to indicate connect on startup.
	public static final String CONNECTION_ON_START = "robots.ui.views.ConnectionConnectOnStart";

	// Mode, one of NETWORK | COM.
	public static final String CONNECTION_MODE = "robots.ui.views.ConnectionMode";

	// Buffer size for the ConnectionView class's text widget.
	public static final String CONNECTION_BUFFER_SIZE = "robots.ui.views.connectionBufferSize";

	// Unlimited size for the ConnectionView class's text widget.
	public static final String CONNECTION_UNLIMITED_BUFFER = "robots.ui.views.connectionUnlimitedBuffer";

	// Flag to prevent log scrolling.
	public static final String CONNECTION_PIN = "robots.ui.views.connectionPin";

	// Flag to indicate output level.
	public static final String CONNECTION_SHOW_TX = "robots.ui.views.connectionShowTX";

	// Flag to indicate output level.
	public static final String CONNECTION_SHOW_RX = "robots.ui.views.connectionShowRX";

	// Colour of the TX output stream.
	public static final String CONNECTION_TX_COLOUR = "robots.ui.views.connectionTXColour";

	// Colour of the RX output stream.
	public static final String CONNECTION_RX_COLOUR = "robots.ui.views.connectionRXColour";

	// Colour of the message output stream.
	public static final String CONNECTION_MESSAGE_COLOUR = "robots.ui.views.connectionMessageColour";

	// Colour of the error output stream.
	public static final String CONNECTION_ERROR_COLOUR = "robots.ui.views.connectionErrorColour";
	
	// Flag to determine line wrapping.
	public static final String CONNECTION_WRAP = "robots.ui.views.connectionWrap";

	// Network connection mode constant (not itself a preference key)
	public static final String NETWORK = "network";

	// Com port connection mode constant (not itself a preference key)
	public static final String COM = "com";
}
