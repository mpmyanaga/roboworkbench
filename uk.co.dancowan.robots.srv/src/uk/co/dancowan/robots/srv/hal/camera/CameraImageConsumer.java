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

import java.awt.Image;

/**
 * Provides methods to allow an <code>ImageDecoder</code> instance to push
 * images for rendering.
 * 
 * <p>The generic type <T> of this consumer must match the underlying widget
 * which will perform the rendering of the pushed image. The <code>ImageDecoder
 * </code> instance may use this method to get a reference to the rendering
 * widget to assist with image creation at runtime.</p>
 */
public interface CameraImageConsumer<T>
{
	/**
	 * Sets the image to be painted.
	 * 
	 * <p>The flag <code>isNew</code> should be true when the calling class
	 * is providing a genuinely new image from a camera source. If the method
	 * generates an image from an existing frame then <code>isNew</code>
	 * should be false.</p>
	 * 
	 * @param image the AWT Image to paint
	 * @param isNew true iff the image is a new frame
	 */
	public void setImage(Image image, boolean isNew);

	/**
	 * Requests that the consumer repaint the image.
	 */
	public void paintImage();

	/**
	 * Return the target widget.
	 * 
	 * @return T a widget of this class's generic type
	 */
	public T getTargetWidget();
}