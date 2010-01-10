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
package uk.co.dancowan.robots.srv.ui.views.camera;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.eclipse.swt.graphics.Rectangle;

import uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer;
import uk.co.dancowan.robots.srv.ui.views.camera.overlays.OverlayManager;

/**
 * Extension of the AWT Canvas class to display images streamed from the SRV1q
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CameraCanvas extends Canvas implements CameraImageConsumer<Component>
{
	private static final long serialVersionUID = -2020219845714922486L;

	// Initial canvas and buffer sizes
	private static final int INITIAL_WIDTH = 320;
	private static final int INITIAL_HEIGHT = 256;
	private static final double ASPECT = (double)INITIAL_WIDTH/(double)INITIAL_HEIGHT;

	private final OverlayManager mOverlayManager;

	private final CameraView mView;
	private BufferedImage mImageBuffer;
	private Image mRawImage;

	private boolean mZoom;
	private int mMaxHeight;
	private int mMaxWidth;
	private int mFrameX;
	private int mFrameY;

	/**
	 * C'tor.
	 */
	public CameraCanvas(CameraView view)
	{
		mView = view;

		mOverlayManager = new OverlayManager();

		setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		mImageBuffer = new BufferedImage(INITIAL_WIDTH, INITIAL_HEIGHT, BufferedImage.TYPE_INT_RGB);

		setDisplaySize(2*INITIAL_WIDTH, 2*INITIAL_HEIGHT);
	}

	/**
	 * Set the max image display size.
	 * 
	 * <p>Respects the aspect ratio of the image to display.</p>
	 * 
	 * @param width the pixel width of the receiver
	 * @param height the pixel height of the receiver
	 */
	public void setDisplaySize(int width, int height)
	{
		int xDiff = (int) Math.abs(height - height*ASPECT);
		int yDiff = (int) Math.abs(width - width/ASPECT);
		if (xDiff < yDiff)
		{
			mMaxWidth = (int) (height*ASPECT);
			mMaxHeight = height;
		}
		else
		{
			mMaxWidth = width;
			mMaxHeight = (int)(width/ASPECT);
		}
	}

	/**
	 * Receive a new <code>Image</code> from the <code>FrameDecoder</code>, scale buffer
	 * as necessary and paint the incoming image to the buffer.
	 * 
	 * @param image incoming from a FrameDecoder
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer#setImage(java.awt.Image)
	 */
	public void setImage(Image image, boolean isNew)
	{
		if (isNew)
			mRawImage = image;

		// scale image to canvas size if requested
		if (mZoom)
		{
			if (image != null)
				image = new ImageIcon(image.getScaledInstance(mMaxWidth, mMaxHeight, Image.SCALE_FAST)).getImage();
		}

		// check BufferedImage fit
		mImageBuffer = resizeBuffer(image);

		// paint incoming Image to BufferedImage
		Graphics2D g = mImageBuffer.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		mView.newImage();
	}

	/**
	 * Return the <code>BufferedImage<code> used as the internal buffer.
	 * 
	 * @return BufferedImage the image buffer used for painting
	*/
	public BufferedImage getImage()
	{
		return mImageBuffer;
	}

	/**
	 * Implementation of <code>java.awt.Canvas.paint(Graphics)</code>
	 * 
	 * <p>Paints the internal image buffer to the AWT <code>Canvas</code> instance.</p>
	 * 
	 * <p>Contributed <code>OverlayContributor</code> classes are queried
	 * and allowed to paint as necessary.</p>
	 * 
	 * @param g the Graphics context
	 */
	@Override
	public void paint(Graphics g)
	{
		if (g != null && mImageBuffer != null)
		{
			int canvasWidth = getWidth();
			int canvasHeight = getHeight();
			int imageWidth = mImageBuffer.getWidth(null);
			int imageHeight = mImageBuffer.getHeight(null);

			if (imageWidth != canvasWidth || imageHeight != canvasHeight)
			{
				g.clearRect(0, 0, canvasWidth, canvasHeight);
				g.drawRoundRect(mFrameX - 5, mFrameY - 5, imageWidth + 10, imageHeight + 10, 10, 10);
			}

			// ask overlays to paint in the buffer
			for (OverlayContributor overlay : mOverlayManager)
			{
				if (overlay.shouldRun())
					overlay.paintOverlay(mImageBuffer);
			}

			// center the Image in the Canvas
			mFrameX = (canvasWidth - imageWidth) / 2;
			mFrameY = (canvasHeight - imageHeight) / 2;

			// paint the buffer to the screen
			g.drawImage(mImageBuffer, mFrameX, mFrameY, null);
		}
	}

	/**
	 * Enable zoom mode.
	 * 
	 * @param zoom boolean, true to zoom, false for standard image
	 */
	public void setZoom(boolean zoom)
	{
		mZoom = zoom;
		if (! mZoom)
		{
			setImage(mRawImage, false);
			paint(getGraphics());
		}
		else
		{
			paintImage();
		}
	}

	/**
	 * Return the camera's <code>OverlayManager</code> instance.
	 * 
	 * @return OverlayManager
	 */
	public OverlayManager getOverlayManager()
	{
		return mOverlayManager;
	}

	/**
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer#paintImage()
	 */
	public void paintImage()
	{
		setImage(mImageBuffer, false);
		paint(getGraphics());
	}

	/**
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer#get(int)
	 */
	public Component getTargetWidget()
	{
		return this;
	}

	/**
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer#getImageHeight()
	 */
	public int getImageHeight()
	{
		return getHeight();
	}

	/**
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer#getImageWidth()
	 */
	public int getImageWidth()
	{
		return getWidth();
	}

	/**
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer#setImageHeight(int)
	 */
	public void setImageHeight(int height)
	{
		setSize(getWidth(), height);
	}

	/**
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer#setImageWidth(int)
	 */
	public void setImageWidth(int width)
	{
		setSize(width, getHeight());
	}

	/*
	 * Returns the location of the image within the display area for centering
	 */
	/*package*/ Rectangle getOffsetBounds()
	{
		return new Rectangle(mFrameX, mFrameY, mImageBuffer.getWidth(null), mImageBuffer.getHeight(null));
	}

	/*
	 * Check the size match of the image buffer and recreate as necessary.
	 */
	private BufferedImage resizeBuffer(Image image)
	{
		if (image == null)
			return mImageBuffer;

		int width = image.getWidth(null);
		int height = image.getHeight(null);
		if (width == mImageBuffer.getWidth() && height == mImageBuffer.getHeight())
			return mImageBuffer;
		else
			return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
}
