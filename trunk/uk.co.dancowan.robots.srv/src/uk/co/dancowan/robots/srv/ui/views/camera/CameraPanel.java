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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.camera.CameraListener;
import uk.co.dancowan.robots.srv.ui.panels.Panel;
import uk.co.dancowan.robots.srv.ui.preferences.PreferenceConstants;

public class CameraPanel implements Panel, IPropertyChangeListener, CameraListener
{
	public static final String ID = "uk.co.dancowan.robots.srv.cameraPanel";

	private final CameraCanvas mCameraCanvas;
	private Color mColour;

	public CameraPanel()
	{
		mCameraCanvas = new CameraCanvas(this);	

		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);
		initFromPrefs();
	}

	public CameraCanvas getCameraCanvas()
	{
		return mCameraCanvas;
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Implementation of IPropertyChangeListener interface so widgets can respond
	 * to changes on a preference page.
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (PreferenceConstants.CAMERA_BACKGROUND_COLOUR.equals(event.getProperty()))
		{
			RGB rgb = (RGB) event.getNewValue();
			mColour = new Color(rgb.red, rgb.green, rgb.blue);
			mCameraCanvas.setBackground(mColour);
			mCameraCanvas.repaint(10);
		}
	}

	/**
	 * Returns this Panel's unique identifier
	 * 
	 * @return String
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	@Override
	public Composite getPanel(Composite parent)
	{
		// This SWT Composite embeds the AWT Frame, and Canvas widgets
		final Composite awtComposite = new Composite(parent, SWT.EMBEDDED);
		awtComposite.setLayout(new FillLayout());
		awtComposite.setSize(320, 256);
		awtComposite.addControlListener(new ControlAdapter()
		{
			@Override
			public void controlResized(ControlEvent e)
			{
				if (e.getSource() instanceof Composite)
				{
					Composite c = (Composite) e.getSource();
					mCameraCanvas.setDisplaySize(c.getSize().x - 20, c.getSize().y - 20);
					mCameraCanvas.paintImage();
				}
			}
		});
		final Frame awtFrame = SWT_AWT.new_Frame(awtComposite);
		awtFrame.setBackground(mColour);
		awtFrame.setLayout(new BorderLayout(3, 3));
		awtFrame.add("Center", mCameraCanvas);
		return awtComposite;
	}

	/*
	 * Initialise variables based on preference store
	 */
	private void initFromPrefs()
	{
		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		RGB rgb = PreferenceConverter.getColor(store, PreferenceConstants.CAMERA_BACKGROUND_COLOUR);
		mColour = new Color(rgb.red, rgb.green, rgb.blue);
	}

	@Override
	public void newImage()
	{
		// TODO Auto-generated method stub
		
	}
}
