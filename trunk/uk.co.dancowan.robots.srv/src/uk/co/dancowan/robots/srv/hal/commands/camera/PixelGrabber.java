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
package uk.co.dancowan.robots.srv.hal.commands.camera;

import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.camera.ColourBin;
import uk.co.dancowan.robots.srv.hal.camera.YUV;

/**
 * Class to grab a pixel from the camera and set it to a colour bin
 * via the configured receiver class.
 * 
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public class PixelGrabber
{
	private static final Logger ERROR_LOGGER = Logger.getLogger(LoggingService.ERROR_LOGGER);

	private final ColourBin mBin;

	/**
	 * C'tor.
	 *
	 * @param srv the srv1q instance
	 * @param receiver the widget wrapping a colour bin.
	 */
	public PixelGrabber(ColourBin receiver)
	{
		mBin = receiver;
	}

	/**
	 * Sets the contents of the configured colour bin based on a
	 * sample of the image at the passed coordinates.
	 * 
	 * <p>The SRV1q hardware is sampled for the true YUV pixel colour
	 * rather than the faster derivation from the received image. This
	 * prevents colour space translation issues.</p>
	 * 
	 * @param x
	 * @param y
	 */
	public void setBinFrom(int x, int y)
	{
		GrabPixelsCmd grabPixels = new GrabPixelsCmd(x, y);
		grabPixels.addListener(new CommandListener()
		{
			@Override
			public void commandFailed(CommandEvent e)
			{
				// NOP
			}
			
			@Override
			public void commandExecuted(CommandEvent e)
			{
				// NOP
			}
			@Override
			public void commandCompleted(CommandEvent e)
			{
				YUV yuv = parseGrabResult(e.mMessage);
				mBin.setYUV(yuv);
			}
		});

		SrvHal.getCommandQ().addCommand(grabPixels);
	}

	/*
	 * Read a YUV colour from the command response.
	 */
	private YUV parseGrabResult(String result)
	{
		String[] parts = result.split(" ");
		if (parts.length == 4)
		{
			try
			{
				int y = Integer.parseInt(parts[1].trim());
				int u = Integer.parseInt(parts[2].trim());
				int v = Integer.parseInt(parts[3].trim());
				return new YUV(y, u, v);
			}
			catch (NumberFormatException e)
			{
				ERROR_LOGGER.finest("PixelGrabber failed: " + e.getMessage());
			}
		}
		return new YUV(0, 0, 0);
	}
}
