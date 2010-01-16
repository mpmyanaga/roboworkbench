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
package uk.co.dancowan.robots.srv.ui.views.camera.overlays;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.RGB;

import uk.co.dancowan.robots.srv.hal.camera.YUV;
import uk.co.dancowan.robots.srv.ui.views.camera.YUVUtils;

/**
 * Colour mapping methods test suite.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ColourManagerTest extends TestCase
{
	/**
	 * C'tor.
	 *
	 * @param name
	 */
	public ColourManagerTest(String name)
	{
		super(name);
	}

	public void testBijections()
	{
		bijectionTest(new YUV(127, 127, 127));
		//bijectionTest(new YUV(255, 0, 0));
		bijectionTest(new YUV(0, 255, 0));
		//bijectionTest(new YUV(0, 0, 0));
	}

	public void bijectionTest(YUV yuv)
	{
		RGB rgb = YUVUtils.convertYUVtoRGB(yuv);
		YUV test = YUVUtils.convertRGBtoYUV(rgb);

		assertTrue(epsilonEquals(yuv.getY(), test.getY()));
		assertTrue(epsilonEquals(yuv.getU(), test.getU()));
		assertTrue(epsilonEquals(yuv.getV(), test.getV()));		
	}

	private boolean epsilonEquals(int target, int sample)
	{
		if (sample <= target + 5 && sample >= target - 5)
			return true;
		else
			return false;
	}

	public void testRanges()
	{
		int yMin = Integer.MAX_VALUE;
		int uMin = Integer.MAX_VALUE;
		int vMin = Integer.MAX_VALUE;
		int yMax = 0;
		int uMax = 0;
		int vMax = 0;

		for (int r = 0; r < 256; r ++)
		{
			for (int b = 0; b < 256; b ++)
			{
				for (int g = 0; g < 256; g ++)
				{
					RGB rgb = new RGB(r, b, g);
					YUV yuv = YUVUtils.convertRGBtoYUV(rgb);

					if (yuv.getY() < yMin) yMin = yuv.getY();
					if (yuv.getY() > yMax) yMax = yuv.getY();
					if (yuv.getU() < uMin) uMin = yuv.getU();
					if (yuv.getU() > uMax) uMax = yuv.getU();
					if (yuv.getV() < vMin) vMin = yuv.getV();
					if (yuv.getV() > vMax) vMax = yuv.getV();					
				}
			}
		}

		assertEquals(0, yMin);
		assertEquals(255, yMax);
		assertEquals(0, uMin);
		assertEquals(255, uMax);
		assertEquals(0, vMin);
		assertEquals(255, vMax);
	}
}
