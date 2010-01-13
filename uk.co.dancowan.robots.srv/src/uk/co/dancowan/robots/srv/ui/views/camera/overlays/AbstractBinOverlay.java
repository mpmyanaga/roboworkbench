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

import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.srv.hal.featuredetector.ColourBin;
import uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor;

/**
 * Overlay requests information for a particular colour bin
 * and paints any processed result to the screen.
 * 
 * <p>Extending classes should implement the <code>paintOverlay(BufferedImage)</code>
 * method and paint to the provided image's graphics context. The passed image will
 * have the camera output already painted. The <code>ColourBin</code> used as a reference
 * will be the current 'focus' bin in the <code>Camera</code> instance. As this bin
 * changes this overlay's reference bin will be changed. Extending classes may override
 * <code>setBin(ColourBin)</code> to react to a change of reference bin.</p>
 * 
 * @see uk.co.dancowan.robots.srv.ui.views.camera.overlays.ColourBinOverlay
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public abstract class AbstractBinOverlay implements OverlayContributor
{
	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);

	private boolean mShouldRun;
	private ColourBin mBin;

	/**
	 * C'tor.
	 *
	 * <p>Configures the overlay with a <code>ColourBin</code> as a source
	 * of the overlay's colour bin information.</p>
	 * 
	 * @param item the source PalletteItem
	 */
	public AbstractBinOverlay(ColourBin bin)
	{
		mBin = bin;

		// start inactive by default
		mShouldRun = false;
	}

	/**
	 * Returns the name of this overlay.
	 * 
	 * <p>Extending classes should override to return correct name. Default
	 * implementation returns 'bin overlay'.</p>
	 * 
	 * @return String the name of the overlay
	 */
	public String getName()
	{
		return "Bin Overlay";
	}

	/**
	 * Returns the configured <code>ColourBin<code>.
	 * 
	 * *return item
	 */
	public ColourBin getColourBin()
	{
		return mBin;
	}

	/**
	 * Extending classes must implement to paint overlay.
	 * 
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor#paintOverlay()
	 */
	@Override
	public abstract void paintOverlay(BufferedImage image);

	/**
	 * Checks this overlay's run flag.
	 * 
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor#shouldRun()
	 */
	@Override
	public boolean shouldRun()
	{
		return mShouldRun;
	}

	/**
	 * Sets this overlay's run flag.
	 * 
	 * @see uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor#setShouldRun(boolean)
	 */
	public void setShouldRun(boolean run)
	{
		mShouldRun = run;
		String msg = getName() + (shouldRun() ? " on." : " off");
		INFO_LOGGER.finer(msg);
	}

	/**
	 * Sets the <code>ColourBin</code> as the source of overlay parameters.
	 * 
	 * @param bin
	 */
	public void setBin(ColourBin bin)
	{
		mBin = bin;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		if (mBin != null)
		{
			sb.append(mBin.toString());
		}
		return sb.toString();
	}
}
