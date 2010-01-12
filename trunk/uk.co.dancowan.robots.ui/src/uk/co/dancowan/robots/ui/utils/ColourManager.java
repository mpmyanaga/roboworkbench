/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2009 Dan Cowan
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
package uk.co.dancowan.robots.ui.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Manages creation and disposal of colours.
 * 
 * <p>Class also provides static methods for mapping between RGB and YUV colour spaces.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ColourManager
{
	public static final int ERROR_COLOUR = SWT.COLOR_RED;
	public static final int MESSAGE_COLOUR = SWT.COLOR_BLUE;
	public static final int TX_COLOUR = SWT.COLOR_DARK_GREEN;
	public static final int RX_COLOUR = SWT.COLOR_GREEN;

	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);
	
	private static final ColourManager sInstance = new ColourManager();

	private Map<RGB, Color> mRGBColourMap;

	/*
	 * Ensure singleton.
	 */
	private ColourManager()
	{
		mRGBColourMap = new HashMap<RGB, Color>();

		INFO_LOGGER.fine("ColourManager created.");
	}

	/**
	 * Get the <code>ColourManager</code> instance
	 * 
	 * @return ColourManager singleton instance
	 */
	public static ColourManager getInstance()
	{
		return sInstance;
	}

	/**
	 * Get a system colour using SWT constants.
	 * 
	 * @param swt int constant
	 * @return
	 */
	public static Color getColour(int swt)
	{
		return Display.getDefault().getSystemColor(swt);
	}

	public Color lighten(Color color, int points)
	{
		int r = TextUtils.cap(color.getRGB().red + points, 255);
		int g = TextUtils.cap(color.getRGB().green + points, 255);
		int b = TextUtils.cap(color.getRGB().blue + points, 255);

		return getColourForRGB(r, g, b);
	}

	public Color darken(Color color, int points)
	{
		int r = TextUtils.collar(color.getRGB().red - points, 0);
		int g = TextUtils.collar(color.getRGB().green - points, 0);
		int b = TextUtils.collar(color.getRGB().blue - points, 0);

		return getColourForRGB(r, g, b);
	}

	/**
	 * Return colour for passed r, g and b components.
	 * 
	 * @param r R component
	 * @param g G component
	 * @param b B component
	 * @return Color
	 */	
	public Color getColourForRGB(int r, int g, int b)
	{
		RGB rgb = new RGB(r, g, b);
		{
			return getColour(rgb);
		}
	}

	/**
	 * Return colour for passed RGB.
	 * 
	 * @param rgb
	 * @return Color
	 */
	public Color getColour(RGB rgb)
	{
		if (mRGBColourMap.containsKey(rgb))
		{
			return mRGBColourMap.get(rgb);
		}
		else
		{
			Color colour = new Color(Display.getDefault(), rgb);
			mRGBColourMap.put(rgb, colour);
			return colour;
		}
	}

	/**
	 * Dispose of all created colours.
	 */
	public void dispose()
	{
		for (RGB key : mRGBColourMap.keySet())
		{
			Color colour = mRGBColourMap.get(key);
			colour.dispose();
		}
		mRGBColourMap.clear();
		INFO_LOGGER.fine("ColourManager disposed of cached colours.");
	}
}
