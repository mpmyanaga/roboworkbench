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
package uk.co.dancowan.robots.srv.hal.camera;

/**
 * Simple container for a colour's Y, U and V values in the YUV colour space.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public final class YUV
{
	// after the fashion of Java RGB, Point or Rectangle classes
	public final int y;
	public final int u;
	public final int v;

	/**
	 * C'tor.
	 * 
	 * @param y colour's y components.
	 * @param u colour's u components.
	 * @param v colour's v components.
	 */
	public YUV(int y, int u, int v)
	{
		this.y = y;
		this.u = u;
		this.v = v;
	}

	/**
	 * Return colour's Y component.
	 * 
	 * @return y
	 */
	public int getY()
	{
		return this.y;
	}

	/**
	 * Return colour's U component.
	 * 
	 * @return u
	 */
	public int getU()
	{
		return this.u;
	}

	/**
	 * Return colour's V component.
	 * 
	 * @return v
	 */
	public int getV()
	{
		return this.v;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "{" + this.y + " " + this.u + " " + this.v + "}";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.y;
		result = prime * result + this.u;
		result = prime * result + this.v;
		return result;
	}

	/**
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
		YUV other = (YUV) obj;
		if (this.u != other.getU())
			return false;
		if (this.v != other.getV())
			return false;
		if (this.y != other.getY())
			return false;
		return true;
	}
}
