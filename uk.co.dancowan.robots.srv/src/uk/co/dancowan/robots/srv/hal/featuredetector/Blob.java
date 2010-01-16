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

/**
 * Represents an area of pixels matching a given colour bin's
 * content.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class Blob
{
	private final int mPixels;
	private final int mX;
	private final int mY;
	private final int mWidth;
	private final int mHeight;

	/**
	 * C'tor.
	 *
	 * @param pixels
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Blob(int pixels, int x, int y, int width, int height)
	{
		mPixels = pixels;
		mX = x;
		mY = y;
		mWidth = width;
		mHeight = height;
	}

	/**
	 * @return the pixels
	 */
	public int getPixels()
	{
		return mPixels;
	}

	/**
	 * @return the x
	 */
	public int getX()
	{
		return mX;
	}

	/**
	 * @return the y
	 */
	public int getY()
	{
		return mY;
	}

	/**
	 * @return the width
	 */
	public int getWidth()
	{
		return mWidth;
	}

	/**
	 * @return the height
	 */
	public int getHeight()
	{
		return mHeight;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + mHeight;
		result = prime * result + mPixels;
		result = prime * result + mWidth;
		result = prime * result + mX;
		result = prime * result + mY;
		return result;
	}

	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Blob other = (Blob) obj;
		if (mHeight != other.mHeight)
			return false;
		if (mPixels != other.mPixels)
			return false;
		if (mWidth != other.mWidth)
			return false;
		if (mX != other.mX)
			return false;
		if (mY != other.mY)
			return false;
		return true;
	}
}
