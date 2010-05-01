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
package uk.co.dancowan.robots.srv.ui.views.camera;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.camera.YUV;
import uk.co.dancowan.robots.srv.hal.featuredetector.FeatureDetector;
import uk.co.dancowan.robots.ui.views.Panel;

public class LocationPanel implements Panel
{
	public static final String ID = "uk.codancowan.robots.srv.LocationPanel";

	private final CameraCanvas mCanvas;

	public LocationPanel(CameraCanvas canvas)
	{
		mCanvas = canvas;
	}

	@Override
	public void dispose()
	{
		// NOP
	}

	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.Panel#getDescription()
	 */
	public String getDescription()
	{
		return "Location Panel";
	}

	@Override
	public Composite getPanel(Composite parent)
	{
		final Group location = new Group(parent, SWT.NONE);
		location.setLayout(new GridLayout(9, true));
		location.setText("Sampling");

		getLabel(location, "X-loc:");
		final Text xLocText = getText(location);

		getLabel(location, "R:");
		final Text rText = getText(location);

		getLabel(location, "        Y:");
		final Text yText = getText(location);

		getLabel(location, "Y-loc:");
		final Text yLocText = getText(location);

		getLabel(location, "G:");
		final Text gText = getText(location);

		getLabel(location, "U:");
		final Text uText = getText(location);

		getLabel(location, "");
		getLabel(location, "");
		getLabel(location, "");

		getLabel(location, "B:");
		final Text bText = getText(location);

		getLabel(location, "V:");
		final Text vText = getText(location);

		mCanvas.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(final MouseEvent e)
			{
				Display.getDefault().syncExec(new Runnable()
				{
					@Override
					public void run()
					{
						int x = e.getX();
						int y = e.getY();
						int pointerX = 0;
						int pointerY = 0;

						Rectangle bounds = mCanvas.getOffsetBounds();
						if (bounds.contains(x, y))
						{
							pointerX = x - bounds.x;
							pointerY = y - bounds.y;


							final FeatureDetector detector = SrvHal.getCamera().getDetector();
							if (! detector.isSampleLocked())
							{
								Point coords = mCanvas.mouseToSRV(pointerX, pointerY);
								detector.setXCoord(coords.x);
								detector.setYCoord(coords.y);
								
								xLocText.setText(Integer.toString(coords.x));
								yLocText.setText(Integer.toString(coords.y));

								//Grab a pixel at the mouse coordinates and calculate RGB and UYV values
								BufferedImage image = mCanvas.getImage();
								int pixel = mCanvas.getImage().getRGB(pointerX, pointerY);

								// NB these values approximate the true YUV values from the hardware
								// camera by sampling the RGB of the Image pixel at the mouse location
								int red = image.getColorModel().getRed(pixel);
								int green = image.getColorModel().getGreen(pixel);
								int blue = image.getColorModel().getBlue(pixel);

								rText.setText(Integer.toString(red));
								gText.setText(Integer.toString(green));
								bText.setText(Integer.toString(blue));
	
								YUV yuv = YUVUtils.getYUV(red, green, blue);
								yText.setText(Integer.toString(yuv.getY()));
								uText.setText(Integer.toString(yuv.getU()));
								vText.setText(Integer.toString(yuv.getV()));
							}
						}
					}
				});
			}
		});

		location.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		location.pack();
		return location;
	}

	public void addToToolBar(IToolBarManager manager)
	{
		// NOP
	}

	/*
	 * Utility method used by location Composite
	 */
	private Text getText(Composite parent)
	{
		final Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		//text.setText("      ");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(data);

		return text;
	}

	/*
	 * Utility method used by location Composite
	 */
	private Label getLabel(Composite parent, String text)
	{
		final Label label = new Label(parent, SWT.TRAIL);
		label.setText(text);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
		data.horizontalSpan = "".equals(text) ? 1 : 2;
		label.setLayoutData(data);

		return label;
	}
}
