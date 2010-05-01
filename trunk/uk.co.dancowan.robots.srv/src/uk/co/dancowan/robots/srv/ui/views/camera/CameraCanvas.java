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
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.camera.Camera;
import uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer;
import uk.co.dancowan.robots.srv.hal.camera.CameraListener;
import uk.co.dancowan.robots.srv.ui.views.camera.overlays.OverlayManager;

/**
 * Extension of the AWT Canvas class to display images streamed from the SRV1
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

	private final CameraListener mCameraListener;
	private BufferedImage mImageBuffer;
	private Image mRawImage;

	private boolean mZoom;
	private int mMaxHeight;
	private int mMaxWidth;
	private int mFrameX;
	private int mFrameY;
	private int mTargetHeight;
	private int mTargetWidth;
	private int mCurrentHeight;
	private int mCurrentWidth;

	private final Object mMutex = new Object();

	/**
	 * C'tor.
	 */
	public CameraCanvas(CameraListener listener)
	{
		mCameraListener = listener;
		mOverlayManager = new OverlayManager();

		setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		mTargetHeight = mCurrentHeight = INITIAL_HEIGHT;
		mTargetWidth = mCurrentWidth = INITIAL_WIDTH;

		mImageBuffer = new BufferedImage(INITIAL_WIDTH, INITIAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
		setDisplaySize(2*INITIAL_WIDTH, 2*INITIAL_HEIGHT);

		createPopupMenu();
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
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer#setImage(Image, boolean)
	 */
	public void setImage(Image image, boolean isNew)
	{
		if (isNew)
		{
			mRawImage = image;
			if (! mZoom)
			{
				int height = image.getHeight(null);
				int width = image.getWidth(null);
				if (mCurrentHeight != height) // assume different widths if different heights
				{
					setTargetHeight(height);
					setTargetWidth(width);
					new ZoomAnimation().start();
				}
			}
		}

		// scale image to canvas as necessary
		image = new ImageIcon(image.getScaledInstance(getCurrentWidth(), getCurrentHeight(), Image.SCALE_FAST)).getImage();

		// check BufferedImage fit
		mImageBuffer = resizeBuffer(image);

		// paint incoming Image to BufferedImage
		Graphics2D g = mImageBuffer.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		mCameraListener.newImage();
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
			
			// center the Image in the Canvas
			mFrameX = (canvasWidth - imageWidth) / 2;
			mFrameY = (canvasHeight - imageHeight) / 2;

			if (imageWidth != canvasWidth || imageHeight != canvasHeight)
			{
				g.clearRect(0, 0, canvasWidth, canvasHeight);
				g.drawRoundRect(mFrameX - 7, mFrameY - 7, imageWidth + 14, imageHeight + 14, 14, 14);
			}

			// ask overlays to paint in the buffer
			for (OverlayContributor overlay : mOverlayManager)
			{
				if (overlay.shouldRun())
					overlay.paintOverlay(this);
			}

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
		if (mZoom)
		{
			setTargetHeight(mMaxHeight);
			setTargetWidth(mMaxWidth);
			new ZoomAnimation().start();
		}
		else
		{
			setTargetHeight(mRawImage.getHeight(null));
			setTargetWidth(mRawImage.getWidth(null));		
			new ZoomAnimation().start();
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
	 * @see uk.co.dancowan.robots.srv.hal.camera.CameraImageConsumer#getTargetWidget()
	 */
	public Component getTargetWidget()
	{
		return this;
	}

	/**
	 * Translates the passed SRV coordinates into display space.
	 * 
	 * @param srvX
	 * @param srvY
	 * @return Point
	 */
	public Point srvToMouse(int srvX, int srvY)
	{
		if (mZoom)
		{
			srvX = (int)(((double)srvX/(double)mRawImage.getWidth(null))*getOffsetBounds().width);
			srvY = (int)(((double)srvY/(double)mRawImage.getHeight(null))*getOffsetBounds().height);
		}
		return new Point(srvX, srvY);
	}

	/**
	 * Translates the passed mouse coordinates into SRV image space.
	 * 
	 * @param pointerX
	 * @param pointerY
	 * @return Point
	 */
	public Point mouseToSRV(int pointerX, int pointerY)
	{
		if (mZoom)
		{
			pointerX = (int)(((double)pointerX/(double)getOffsetBounds().width)*mRawImage.getWidth(null));
			pointerY = (int)(((double)pointerY/(double)getOffsetBounds().height)*mRawImage.getHeight(null));
		}
		return new Point(pointerX, pointerY);
	}

	/**
	 * Returns the location of the image within the display area for centering
	 * 
	 * @return Rectangle
	 */
	public Rectangle getOffsetBounds()
	{
		return new Rectangle(mFrameX, mFrameY, mImageBuffer.getWidth(null), mImageBuffer.getHeight(null));
	}

	/*
	 * Creates AWT PopupMenu for CameraComposite
	 */
	private void createPopupMenu()
	{
		final Camera camera = SrvHal.getCamera();
		final PopupMenu menu = new PopupMenu();
		final MenuItem setColour = new MenuItem("Set colour to selected bin");

		setColour.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				camera.getDetector().updateColourBinFromCoords();
				camera.getDetector().setSampleLock(false);
			}
		});
		menu.add(setColour);

		final Component canvas = this;
		add(menu);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// show the context menu when right-click detected within image bounds
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					int x = e.getX();
					int y = e.getY();
					Rectangle bounds = getOffsetBounds();
					if (bounds.contains(x, y))
					{
						camera.getDetector().setSampleLock(true);
						menu.show(canvas, x, y);
					}
				}
			}
		});
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

	/*
	 * Synchonized call for variable used by animation thread
	 */
	private int getTargetHeight()
	{
		synchronized (mMutex)
		{
			return mTargetHeight;
		}
	}

	/*
	 * Synchonized call for variable used by animation thread
	 */
	private int getTargetWidth()
	{
		synchronized (mMutex)
		{
			return mTargetWidth;
		}
	}

	/*
	 * Synchonized call for variable used by animation thread
	 */
	private int getCurrentHeight()
	{
		synchronized (mMutex)
		{
			return mCurrentHeight;
		}
	}

	/*
	 * Synchonized call for variable used by animation thread
	 */
	private int getCurrentWidth()
	{
		synchronized (mMutex)
		{
			return mCurrentWidth;
		}
	}

	/*
	 * Synchonized call for variable used by animation thread
	 */
	private void setTargetHeight(int height)
	{
		synchronized (mMutex)
		{
			mTargetHeight = height;
		}
	}

	/*
	 * Synchonized call for variable used by animation thread
	 */
	private void setTargetWidth(int width)
	{
		synchronized (mMutex)
		{
			mTargetWidth = width;
		}
	}

	/*
	 * Synchonized call for variable used by animation thread
	 */
	private void setCurrentHeight(int height)
	{
		synchronized (mMutex)
		{
			mCurrentHeight = height;
		}
	}

	/*
	 * Synchonized call for variable used by animation thread
	 */
	private void setCurrentWidth(int width)
	{
		synchronized (mMutex)
		{
			mCurrentWidth = width;
		}
	}

	/*
	 * Thread animates the zoom or resolution size changes
	 */
	private class ZoomAnimation extends Thread
	{
		private static final int SPEED = 10; //px per 5ms

		private ZoomAnimation()
		{
			setName("Zoom Animation");
		}

		@Override
		public void run()
		{
			while (getCurrentHeight() != getTargetHeight() || getCurrentWidth() != getTargetWidth())
			{
				if (getCurrentHeight() < getTargetHeight())
				{
					setCurrentHeight(getCurrentHeight() + SPEED);
					if (getCurrentHeight() > getTargetHeight()) setCurrentHeight(getTargetHeight());
				}
				else
				{
					setCurrentHeight(getCurrentHeight() - SPEED);
					if (getCurrentHeight() < getTargetHeight()) setCurrentHeight(getTargetHeight());
				}
				if (getCurrentWidth() < getTargetWidth())
				{
					setCurrentWidth(getCurrentWidth() + SPEED);
					if (getCurrentWidth() > getTargetWidth()) setCurrentWidth(getTargetWidth());
				}
				else
				{
					setCurrentWidth(getCurrentWidth() - SPEED);
					if (getCurrentWidth() < getTargetWidth()) setCurrentWidth(getTargetWidth());
				}

				Display.getDefault().syncExec(new Runnable()
				{
					@Override
					public void run()
					{
						paintImage();
					}
				});

				try
				{
					Thread.sleep(5);
				}
				catch (InterruptedException e)
				{
					return;
				}
			}
		}
	}
}
