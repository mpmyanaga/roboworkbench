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
package uk.co.dancowan.robots.srv.ui.views.camera.overlays;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.camera.Camera;
import uk.co.dancowan.robots.srv.hal.featuredetector.ColourBin;

/**
 * OverlayContributor implementation to highlight pixels within a given colour bin.
 * 
 * <p>Although this overlay is intended to show a user what the SRV1q 'sees'
 * in a particular colour bin the mapping between RGB and YUV colour spaces
 * does not biject accurately. The overlay is therefore an approximation.</p>
 * 
 * <p>Use <code>BlobOverlay</code> for an alternative approximation.</p>
 * 
 * @see uk.co.dancowan.srv1q.views.camera.BlobOverlay
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ColourBinOverlay extends AbstractBinOverlay
{
	public static final String ID = "uk.co.dancowan.robots.srv.colourBinOverlay";

	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);

	/**
	 * C'tor.
	 *
	 * @param item
	 */
	public ColourBinOverlay(ColourBin bin)
	{
		super(bin);
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor#getID()
	 */
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.overlays.AbstractBinOverlay#getName()
	 */
	@Override
	public String getName()
	{
		return "Colour Overlay";
	}

	public void refresh()
	{
		Camera camera = SrvHal.getCamera();
		ColourBin bin = camera.getDetector().getFocusBin();
		setBin(bin);
	}

	/**
	 * Paints those pixels falling within the configured colour bin's bounds in a
	 * fixed colour.
	 * 
	 * @see uk.co.dancowan.srv1q.views.camera.AbstractBinOverlay#paintOverlay()
	 */
	@Override
	public void paintOverlay(BufferedImage image)
	{
		INFO_LOGGER.finest("Colour Overlay painting");
		int pixels = 0;
		if (image != null)
		{
			for (int x = 0; x < image.getWidth(); x ++)
			{
				for (int y = 0; y < image.getHeight(); y ++)
				{
					int pixel = image.getRGB(x, y);
					int red = image.getColorModel().getRed(pixel);
					int green = image.getColorModel().getGreen(pixel);
					int blue = image.getColorModel().getBlue(pixel);
					
					if (getColourBin().within(red, green, blue))
					{
						image.setRGB(x, y, 0);
						pixels ++;
					}
				}
			}
		}
		INFO_LOGGER.finest("Painted " + pixels + " pixels.");
	}
}
