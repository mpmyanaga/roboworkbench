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
package uk.co.dancowan.robots.srv.hal.featuredetector;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.hal.core.commands.CommandUtils;
import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.camera.YUV;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.GrabBinCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.SetBinCmd;

/**
 * Represents and SRV internal colour bin.
 * 
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public class ColourBin
{
	private static final Logger ERROR_LOGGER = Logger.getLogger(LoggingService.ERROR_LOGGER);

	private static final int DEFAULT_THRESHOLD = 10;

	private final int mBin;

	private int mYmin;
	private int mYmax;
	private int mUmin;
	private int mUmax;
	private int mVmin;
	private int mVmax;

	private final List<ColourBinListener> mListeners;

	/** 
	 * C'tor.
	 *
	 * @param bin the number of this bin
	 */
	public ColourBin(int bin)
	{
		mBin = bin;
		
		mListeners = new ArrayList<ColourBinListener>();
	}

	/**
	 * Add this listener to the collection of listeners notified
	 * of colour changes in this bin.
	 * 
	 * @param listener
	 */
	public void addListener(ColourBinListener listener)
	{
		mListeners.add(listener);
	}

	/**
	 * Remove this listener from the collection of listeners notified
	 * of colour changes in this bin.
	 * 
	 * @param listener
	 */
	public void removeListener(ColourBinListener listener)
	{
		mListeners.remove(listener);
	}

	/**
	 * Parse a result string returned from an SRV1q and set internal
	 * parameters accordingly.
	 * 
	 * <p>Method expects parameters of the form:</p>
	 * <pre>    ny1y2u1u2v1v2</pre>
	 * <p>Where n is the bin number, y1y2 Y components min and max values,
	 * u1u2 U components min and max values and , v1v2 V components min and
	 * max values.</p>
	 * 
	 * @param result the string returned from a call to the srv1q's vc command
	 */
	public void readResultString(String result)
	{
		String[] tokens = result.split(" ");
		if (tokens.length == 8)
		{
			try
			{
				mYmin = Integer.parseInt(tokens[2].trim());
				mYmax = Integer.parseInt(tokens[3].trim());
				mUmin = Integer.parseInt(tokens[4].trim());
				mUmax = Integer.parseInt(tokens[5].trim());
				mVmin = Integer.parseInt(tokens[6].trim());
				mVmax = Integer.parseInt(tokens[7].trim());
				notifyListeners();
			}
			catch (NumberFormatException e)
			{
				ERROR_LOGGER.finest("ColourBin.readResultString()" + e.getMessage());
			}
		}
	}

	/**
	 * Sets this bin's contents according to the passed YUV and the default
	 * threshold value.
	 * 
	 * @param YUV
	 */
	public void setYUV(YUV yuv)
	{
		setYmin(yuv.getY() - DEFAULT_THRESHOLD);
		setYmax(yuv.getY() + DEFAULT_THRESHOLD);
		setUmin(yuv.getU() - DEFAULT_THRESHOLD);
		setUmax(yuv.getU() + DEFAULT_THRESHOLD);
		setVmin(yuv.getV() - DEFAULT_THRESHOLD);
		setVmax(yuv.getV() + DEFAULT_THRESHOLD);

		if (true) // add property for synch
		{
			setColour();
		}
		notifyListeners();
	}

	/**
	 * Write the bin's internal parameters to a command string.
	 * 
	 * <p>Method writes parameters, not a complete command string:</p>
	 * <pre>    ny1y2u1u2v1v2</pre>
	 * <p>Where n is the bin number, y1y2 Y components min and max values,
	 * u1u2 U components min and max values and , v1v2 V components min and
	 * max values.</p>
	 * 
	 * @return String the parameter String
	 */
	public String getParameterString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(mBin);
		sb.append(CommandUtils.formatInt(mYmin, 3));
		sb.append(CommandUtils.formatInt(mYmax, 3));
		sb.append(CommandUtils.formatInt(mUmin, 3));
		sb.append(CommandUtils.formatInt(mUmax, 3));
		sb.append(CommandUtils.formatInt(mVmin, 3));
		sb.append(CommandUtils.formatInt(mVmax, 3));
		return sb.toString();
	}

	/**
	 * Returns an <code>YUV</code> instance for painting pixels.
	 * 
	 * @return YUV the mean YUV of the bin
	 */
	public YUV getMeanYUV()
	{
		int y = mYmin + (mYmax - mYmin)/2;
		int u = mUmin + (mUmax - mUmin)/2;
		int v = mVmin + (mVmax - mVmin)/2;
		return new YUV(y, u, v);
	}

	public YUV getMaxYUV()
	{
		return new YUV(mYmax, mUmax, mVmax);
	}

	public YUV getMinYUV()
	{
		return new YUV(mYmin, mUmin, mVmin);
	}

	/**
	 * Send a grabBinCmd for this colour bin and set the colour accordingly.
	 */
	public void refreshBin()
	{
		GrabBinCmd cmd = new GrabBinCmd(mBin);
		cmd.addListener(new CommandListener()
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
			/**
			 * @see uk.co.dancowan.robots.hal.core.CommandListener#commandCompleted(uk.co.dancowan.robots.hal.core.CommandEvent)
			 */
			@Override
			public void commandCompleted(CommandEvent e)
			{
				readResultString(e.getMessage());
			}
		});
		SrvHal.getCommandQ().addCommand(cmd);
	}

	/**
	 * Send the colour of this bin to the SRV1
	 */
	public void setColour()
	{
		SetBinCmd cmd = new SetBinCmd(this);
		SrvHal.getCommandQ().addCommand(cmd);		
	}

	/**
	 * Return true if the passed <RGB>YUV</code> lies within this bin.
	 * 
	 * @param YUV
	 */
	public boolean withinBin(YUV yuv)
	{
		return within(yuv.getY(), yuv.getU(), yuv.getV());
	}

	/**
	 * Return true if the passed components are within this bin.
	 * 
	 * @param y
	 * @param u
	 * @param v
	 */
	public boolean within(int y, int u, int v)
	{
		if ((y > mYmin && y < mYmax) &&
			(u > mUmin && u < mUmax) &&
			(v > mVmin && v < mVmax))
			return true;
		else
			return false;
	}

	/**
	 * @return the y component min
	 */
	public int getYmin()
	{
		return mYmin;
	}

	/**
	 * Set to value with bounding.
	 * 
	 * @param ymin the y component to set
	 */
	public void setYmin(int ymin)
	{
		mYmin = bound(ymin);
		notifyListeners();
	}

	/**
	 * @return the y component max
	 */
	public int getYmax()
	{
		return mYmax;
	}

	/**
	 * Set to value with bounding.
	 * 
	 * @param ymax the y component to set
	 */
	public void setYmax(int ymax)
	{
		mYmax = bound(ymax);
		notifyListeners();
	}

	/**
	 * @return the u component min
	 */
	public int getUmin()
	{
		return mUmin;
	}

	/**
	 * Set to value with bounding.
	 * 
	 * @param umin the u component to set
	 */
	public void setUmin(int umin)
	{
		mUmin = bound(umin);
		notifyListeners();
	}

	/**
	 * @return the u component max
	 */
	public int getUmax()
	{
		return mUmax;
	}

	/**
	 * Set to value with bounding.
	 * 
	 * @param umax the u component to set
	 */
	public void setUmax(int umax)
	{
		mUmax = bound(umax);
		notifyListeners();
	}

	/**
	 * @return the v component min
	 */
	public int getVmin()
	{
		return mVmin;
	}

	/**
	 * Set to value with bounding.
	 * 
	 * @param vmin the v component to set
	 */
	public void setVmin(int vmin)
	{
		mVmin = bound(vmin);
		notifyListeners();
	}

	/**
	 * @return the v component max
	 */
	public int getVmax()
	{
		return mVmax;
	}

	/**
	 * Set to value with bounding.
	 * 
	 * @param vmax the v component to set
	 */
	public void setVmax(int vmax)
	{
		mVmax = bound(vmax);
	}

	/**
	 * @return the bin
	 */
	public int getBin()
	{
		return mBin;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ColourBin: " + mBin + " {Ymin = " + mYmin + ", Ymax = " + mYmax + 
									  ", Umin = " + mUmin + ", Umax = " + mUmax +
									  ", Vmin = " + mVmin + ", Vmax = " + mVmax + "}";
	}

	/*
	 * Bound to within 0-255 by clipping
	 */
	private int bound(int value)
	{
		if (value < 0) value = 0;
		if (value > 255) value = 255;
		return value;
	}

	private void notifyListeners()
	{
		for (ColourBinListener listener : mListeners)
		{
			YUV yuv = getMeanYUV();
			listener.colourChanged(yuv);
		}
	}
}
