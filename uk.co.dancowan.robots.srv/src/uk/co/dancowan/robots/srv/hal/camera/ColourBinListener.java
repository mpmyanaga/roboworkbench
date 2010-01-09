/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (C) 2009  Dan Cowan
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
package uk.co.dancowan.robots.srv.hal.camera;

/**
 * Implementations will be notified of changes to the <code>ColourBin</code>'s
 * colour.
 * 
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public interface ColourBinListener
{
	/**
	 * Fired when the bin's colour changes.
	 * 
	 * @param YUV the new bin colour
	 */
	public void colourChanged(YUV newColour);
}
