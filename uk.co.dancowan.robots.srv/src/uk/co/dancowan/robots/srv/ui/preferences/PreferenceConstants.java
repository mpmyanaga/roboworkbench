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

/**
 * Constant definitions for SRV plug-in preferences
 */
public class PreferenceConstants
{
	// Flag for Camera object to initialise bins/blobs/etc on connect event.
	public static final String CAMERA_INIT_ON_CONNECT = "robots.ui.srv.camera.initOnConnect";
	
	// Flag for Camera object to begin polling on connect event.
	public static final String CAMERA_POLL_ON_CONNECT = "robots.ui.srv.camera.pollOnConnect";

	// The colour surrounding the Camera's image on screen.
	public static final String CAMERA_BACKGROUND_COLOUR = "robots.ui.srv.camera.backgroundColour";

	// The poll delay in ms
	public static final String CAMERA_POLL_DELAY = "robots.ui.srv.camera.pollDelay";

	// The file format for snapshot images
	public static final String CAMERA_OUTPUT_FORMAT = "robots.ui.srv.camera.outputFormat";

	// Default image location
	public static final String CAMERA_DEFAULT_PATH = "robots.ui.srv.camera.defaultPath";

	// Pattern colour
	public static final String PATTERN_COLOUR = "robots.ui.srv.patterns.patternColour";

	// Pattern colour
	public static final String PATTERN_BACKGROUND_COLOUR = "robots.ui.srv.patterns.patternBackground";

	// Pattern editor's grid colour
	public static final String PATTERN_EDITOR_GRID_COLOUR = "robots.ui.srv.patterns.editorGridColour";

	// Allow similar matches
	public static final String PATTERN_ALLOW_MULTIPLE = "robots.ui.srv.patterns.allowMultiple";

	// Similarity threshold in pixels
	public static final String PATTERN_THRESHOLD = "robots.ui.srv.patterns.threshold";

	// Blob overlay colour
	public static final String BLOB_OVERLAY_COLOUR = "robots.ui.srv.overlays.blob.overlayColour";
}
