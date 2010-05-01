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
package uk.co.dancowan.robots.srv.hal.camera;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Takes pixel information from the serial <code>InputStream</code> and creates an
 * <code>java.awt.Image</code> for rendering.
 *
 * <p>Image data is pumped from the <code>Camera</code> instance and pushed out
 * to the <code>CameraImageConsumer</code> instance once decoded.</p>
 */
public class FrameDecoder extends Thread
{
	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);

	private final CameraImageConsumer<Component> mConsumer;
	private final MediaTracker mTracker;
	private final List<Long> mPollTimes;

	private Image mImage;
	private byte[] mImageBytes;
	private String mType;

	private boolean mTerminate;
	private boolean mShouldRun;

	private long mLastFrame;
	private double mFPS;

	private final Object mMutex = new Object();

	/**
	 * C'tor
	 * 
	 * @param consumer
	 */
	public FrameDecoder(CameraImageConsumer<Component> consumer)
	{
		mConsumer = consumer;

		mShouldRun = true;
		mTerminate = false;
		mType = "IMJ3";
		mTracker = new MediaTracker(mConsumer.getTargetWidget());
		mPollTimes = new ArrayList<Long>();

		initImageBuffer();
		mConsumer.setImage(mImage, true);

		setName("ImageDecoder thread");

		INFO_LOGGER.fine("FrameDocoder created.");
	}

	/**
	 * Called by the <code>GrabImageCmd</code> to request a new frame is
	 * decoded and pushed for rendering.
	 * 
	 * @param type String, the image type, IBM or IMJ
	 * @param image byte[], the pixel data for the image
	 */
	public void newRawFrame(String type, byte[] image)
	{
		mType = type;
		mImageBytes = image;
	}

	/**
	 * Return the frames received per second.
	 * 
	 * @return int FPS approximation
	 */
	public double getFPS()
	{
		return mFPS;
	}

	/**
	 * Decodes the stored byte data from a call to <code>FrameDecoder.newRawFrame(...).
	 * </code>.
	 *  
	 * @see java.lang.Thread
	 */
	@Override
	public void run()
	{
		INFO_LOGGER.fine("FrameDocoder ready.");

		while (! shouldTerminate())
		{
			mLastFrame = System.currentTimeMillis();
			while (shouldRun())
			{
				// wait for some data to decode
				if (mImageBytes == null)
				{
					try
					{
						Thread.sleep(50);
					}
					catch (InterruptedException ie)
					{
						// NOP
					}
					continue;
				}
	
				if (mType.startsWith ("IMJ"))
					decodeJpeg();

				mImageBytes = null;
			}

			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException ie)
			{
				// NOP
			}
		}
		INFO_LOGGER.fine("FrameDocoder terminated.");
	}

	/**
	 * Enable or block this operation.
	 * @param shouldRun
	 */
	public void setShouldRun(boolean shouldRun)
	{
		synchronized (mMutex)
		{
			mShouldRun = shouldRun;
			INFO_LOGGER.finer("FrameDocoder loop " + (shouldRun ? "resumed." : "paused."));
		}
	}

	/**
	 * Check the enablement state for this operation
	 */
	public boolean shouldRun()
	{
		synchronized (mMutex)
		{
			return mShouldRun;
		}
	}

	/**
	 * Enable or block this operation.
	 */
	public void terminate()
	{
		synchronized (mMutex)
		{
			mTerminate = true;
		}
	}

	/*
	 * Check the enablement state for this operation
	 */
	public boolean shouldTerminate()
	{
		synchronized (mMutex)
		{
			return mTerminate;
		}
	}

	/*
	 * Decode the byte data as a JPEG image.
	 */
	private void decodeJpeg()
	{
		Image image = Toolkit.getDefaultToolkit().createImage(mImageBytes);

		mTracker.addImage(image, 0);
		try
		{
			mTracker.waitForID(0);
		}
		catch (InterruptedException ie)
		{
			// NOP
		}

		if (!mTracker.isErrorID(0))
		{
			mImage = image;
			updateFPS();
			mConsumer.setImage(image, true);
			mConsumer.paintImage();
		}

		mTracker.removeImage(image);
	}

	/*
	 * Create the initial image and initialise the ImageSource and AWT Image
	 */
	private void initImageBuffer()
	{
		int height = 256;
		int width = 320;
		int[] pixels = new int[width*height];
		int index = 0;
		for (int y = 0; y < height; y++)
		{
			int red = (y * 255) / (height - 1);
			for (int x = 0; x < width; x++)
			{
				int blue = (x * 255) / (width - 1);
				pixels[index++] = (255 << 24) | (red << 16) | blue;
			}
		}
		mImageBytes = new byte[pixels.length];

		MemoryImageSource source = new MemoryImageSource(320, 256, pixels, 0, 320);
		source.setAnimated(true);
		mImage = mConsumer.getTargetWidget().createImage(source);
	}

	/*
	 * Record time of image and average last five for FSP measure
	 */
	private void updateFPS()
	{
		long time = System.currentTimeMillis() - mLastFrame;
		if (mPollTimes.size() == 10)
		{
			mPollTimes.remove(0);
		}
		mPollTimes.add(time);

		long sum = 0;
		int count = 0;
		for (long t : mPollTimes)
		{
			if (t > 0)
			{
				sum += t;
				count ++;
			}
		}
		double delay = sum/count;
		if (delay > 0)
			mFPS = 1000d/delay;
		else
			mFPS = 0d;

		mLastFrame = System.currentTimeMillis();
	}	
}
