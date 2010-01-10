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
package uk.co.dancowan.robots.srv.ui.views.camera;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import uk.co.dancowan.robots.srv.hal.camera.YUV;
import uk.co.dancowan.robots.ui.utils.ColourManager;

/**
 * Static methods for mapping between RGB and YUV colour spaces.
 * 
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public class YUVUtils
{
	/**
	 * Return a YUV instance matching the passed red, green
	 * and blue components.
	 * 
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @return YUV matching the components
	 */
	public static YUV getYUV(int r, int g, int b)
	{
		RGB rgb = new RGB(r, g, b);
		return convertRGBtoYUV(rgb);
	}

	/**
	 * Return an RGB instance matching the passed y, u
	 * and v components.
	 * 
	 * @param y the Y component
	 * @param u the U component
	 * @param v the V component
	 * @return RGB matching the components
	 */
	public static RGB getRGB(int y, int u, int v)
	{
		YUV yuv = new YUV(y, u, v);
		return convertYUVtoRGB(yuv);
	}

	/**
	 * Change an RGB colour to YUV (YCrCb) colour. The colour value conversion is
	 * independent of the colour range. Colours could be 0-1 or 0-255.
	 *
	 * @param rgb The RGB components to convert
	 * @return yuv equivalent YUV
	 */
	public static YUV convertRGBtoYUV(RGB rgb)
	{
		int r = rgb.red;
		int g = rgb.green;
		int b = rgb.blue;

		double y = (0.299*(double)r) + (0.587*(double)g) + (0.114*(double)b);
		double u = (-0.168736*(double)r) - (0.331264*(double)g) + (0.5*(double)b) + 128;
		double v = (0.5*(double)r) - (0.418688*(double)g) - (0.081312*(double)b) + 128;

		return new YUV((int) y, (int) u, (int) v);
	}

	/**
	 * Change an YUV (YCrCb) colour to RGB colour. The colour value conversion is
	 * independent of the colour range. Colours could be 0-1 or 0-255.
	 *
	 * @param yuv The YUV components to convert
	 * @param rgb equivalent RGB
	 */
	public static RGB convertYUVtoRGB(YUV yuv)
	{
		int y = yuv.getY();
		int u = yuv.getU() - 128;
		int v = yuv.getV() - 128;

		double r = (double)y + (double)(1.4075 * ((double)v));
		double g = (double)y - (double)(0.3455 * ((double)u)) - (double)(0.7169*((double)v));
		double b = (double)y + (double)(1.7790 * ((double)u));

		return new RGB(clipAndBound(r), clipAndBound(g), clipAndBound(b));
	}

	/**
	 * Return colour for passed y, u and v components.
	 * 
	 * @param y Y component
	 * @param u U component
	 * @param v V component
	 * @return Color
	 */	
	public static Color getColorForYUV(int y, int u, int v)
	{
		RGB rgb = getRGB(y, u, v);
		return ColourManager.getInstance().getColour(rgb);
	}

	private static int clipAndBound(double d)
	{
		if (d < 0) d = 0;
		if (d > 255) d = 255;
		return (int) d;
	}
}
