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

import uk.co.dancowan.robots.hal.core.AbstractComponent;
import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.hal.core.commands.CommandUtils;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.commands.camera.PixelGrabber;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.GrabBlobCmd;

/**
 * Virtual hardware object collecting feature detection routines and structures
 * of the SRV1 robot.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class FeatureDetector extends AbstractComponent
{
	public static final String ID = "SRV Feature Detector";
	public static final int BIN_COUNT = 8;

	private final PatternMemory mPatternMemory;
	private final List<ColourBin> mColourBins;

	private List<Blob> mBlobs;
	private int mBin;
	private boolean mInterupt;
	private boolean mWasRunning;

	/**
	 * C'tor.
	 *
	 * Must be no-args for Eclipse architecture.
	 */
	public FeatureDetector()
	{
		mPatternMemory = new PatternMemory();
		mBin = 0;
		mColourBins = new ArrayList<ColourBin>();
		mBlobs = new ArrayList<Blob>();
		mInterupt = false;
		mWasRunning = false;

		createBins();
	}

	/**
	 * Makes calls to set up initial bin and blob state.
	 */
	public void initialise()
	{
		refreshColourBins();
		refreshBlobs();
	}

	/**
	 * Returns the detectors <code>PatternMemory</code> class.
	 * 
	 * @return PatternMemory
	 */
	public PatternMemory getPatternMemory()
	{
		return mPatternMemory;
	}

	/**
	 * @see uk.co.dancowan.robots.hal.core.AbstractComponent#getID()
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * Return the bin with current focus.
	 * 
	 * @return ColourBin
	 */
	public ColourBin getFocusBin()
	{
		return mColourBins.get(mBin);
	}

	/**
	 * Return a <code>ColourBin</code> by index.
	 * 
	 * <p>Cascades an IndexOutOfBoundsException from the underlying
	 * list.</p>
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public ColourBin getBin(int index)
	{
		return mColourBins.get(index);
	}

	/**
	 * Set the focus bin to the passed index.
	 *
	 * <p>Throws and IndexOutOfBoundsException if the bin index
	 * is out of bounds. 0 <= index <= 15.</p>
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public void setFocusBin(int index)
	{
		if (index < 0 || index > BIN_COUNT)
			throw new IndexOutOfBoundsException("Colour bin index out of bounds.");

		mBin = index;
		refreshBlobs();
	}

	/**
	 * Asks the Camera to update the colour in the focus bin from the passed
	 * camera image coordinates.
	 * 
	 * @param x
	 * @param y
	 */
	public void updateColourBinFromCoords(int x, int y)
	{
		interuptCamera(true);
		new PixelGrabber(getFocusBin()).setBinFrom(x, y);
		refreshBlobs();
	}

	/**
	 * Return the current blob list.
	 * 
	 * <p>Only one blob list is maintained, the list is auto
	 * updated when the focus bin is changed and when the focus bin
	 * changes colour</p>
	 * 
	 * @return List the blob list
	 */
	public List<Blob> getBlobs()
	{
		return mBlobs;
	}

	/**
	 * Run the embedded command to get blobs for the current bin
	 */
	public void refreshBlobs()
	{
		Command cmd = new GrabBlobCmd(mBin);
		cmd.addListener(new CommandListener()
		{
			@Override
			public void commandFailed(CommandEvent e)
			{
				interuptCamera(false);
			}
			@Override
			public void commandExecuted(CommandEvent e)
			{
				// NOP
			}
			@Override
			public void commandCompleted(CommandEvent e)
			{
				mBlobs = processBlobData(e.getMessage());
				interuptCamera(false);
			}
		});
		interuptCamera(true);
		SrvHal.getCommandQ().addCommand(cmd);
	}

	/**
	 * Clears the current blob list.
	 */
	public void clearBlobList()
	{
		mBlobs.clear();
	}

	/**
	 * Refresh each colour bin from the hardware.
	 */
	public void refreshColourBins()
	{
		interuptCamera(true);
		for (final ColourBin bin : mColourBins)
		{
			bin.refreshBin();
		}
		interuptCamera(false);
	}

	/*
	 * Stop the Camera from polling images while transmitting other commands
	 */
	private void interuptCamera(boolean state)
	{
		if (mWasRunning && mInterupt && (state == false))
		{
			SrvHal.getCamera().startPolling();
			mWasRunning = false;
		}
		else
		{
			if (SrvHal.getCamera().isPolling())
			{
				mWasRunning = true;
				SrvHal.getCamera().stopPolling();
			}
			while (SrvHal.getCamera().isPolling())
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					// NOP
				}
			}				
		}
		mInterupt = state;
	}

	/*
	 * Initialise the colour bins
	 */
	private void createBins()
	{
		for (int i = 0; i < BIN_COUNT; i ++)
		{
			ColourBin bin = new ColourBin(i);
			mColourBins.add(bin);
		}
	}

	/*
	 * Parse blobs out of the command's response.
	 */
	/*package*/ List<Blob> processBlobData(String message)
	{
		List<Blob> result = new ArrayList<Blob>();
		List<String> lines = split(message);
		for (String line : lines)
		{
			line = line.trim();
			String[] items = line.split(" ");
			if (items.length == 6)
			{
				try
				{
					int pixels = Integer.parseInt(items[0]);
					int x = Integer.parseInt(items[2]);
					int width = Integer.parseInt(items[3]) - x;
					int y = Integer.parseInt(items[4]);
					int height = Integer.parseInt(items[5]) - y;
					result.add(new Blob(pixels, x, y, width, height));
				}
				catch (NumberFormatException nfe)
				{
					// Can't make rectangle - try next line
				}
			}
		}
		return result;
	}

	/*
	 * Split the command result by line endings
	 */
	private List<String> split(String inbound)
	{
		List<String> result = new ArrayList<String>();
		int mark = 0;
		for (int i = 1; i < inbound.length(); i ++)
		{
			if (CommandUtils.isEnd(inbound.charAt(i)))
			{
				String str = inbound.substring(mark, i);
				result.add(str.trim());
				mark = 1 + i;
			}
		}
		String str = inbound.substring(mark, inbound.length());
		result.add(str.trim());
		return result;
	}
}
