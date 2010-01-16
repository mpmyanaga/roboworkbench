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
package uk.co.dancowan.robots.srv.ui.views.camera;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.SetBinCmd;
import uk.co.dancowan.robots.srv.hal.featuredetector.ColourBin;
import uk.co.dancowan.robots.srv.ui.views.camera.overlays.BlobOverlay;
import uk.co.dancowan.robots.ui.utils.ColourManager;

/**
 * Class to enable manual editing of a colour bin's content.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ColourBinEditor extends SelectionAdapter
{
	// static fields used as labels as well as keys to select methods to call on update
	private static final String MAX_Y = "Max Y";
	private static final String MAX_U = "Max U";
	private static final String MAX_V = "Max V";
	private static final String MIN_Y = "Min Y";
	private static final String MIN_U = "Min U";
	private static final String MIN_V = "Min V";

	private final CameraCanvas mCanvas;

	private Text mYmax;
	private Text mUmax;
	private Text mVmax;
	private Text mYmin;
	private Text mUmin;
	private Text mVmin;

	/**
	 * C'tor.
	 *
	 * @param canvas the CameraCanvas object managing contributed overlays
	 */
	public ColourBinEditor(CameraCanvas canvas)
	{
		mCanvas = canvas;
	}

	/**
	 * Callback adds the editor's widgets to an existing <code>Composite</code>.
	 * 
	 * <p>The composite parent is expected to have a grid layout of 8 equal columns.
	 * This call should not pack or otherwise prepare the parent but rather just add
	 * the local editor widgets.</p>
	 * 
	 * @param parent Composite on which to lay widgets
	 */
	public void addWidgets(Composite parent)
	{
		final Button narrow = new Button(parent, SWT.PUSH);
		narrow.setText("<");
		narrow.setToolTipText("Narrow the colour range");
		narrow.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ColourBin bin = SrvHal.getCamera().getDetector().getFocusBin();
				bin.narrow(10);
				updateBin(0, ""); // params skip condition and just send command
			}
		});
		narrow.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button widen = new Button(parent, SWT.PUSH);
		widen.setText(">");
		widen.setToolTipText("Widen the colour range");
		widen.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ColourBin bin = SrvHal.getCamera().getDetector().getFocusBin();
				bin.widen(10);
				updateBin(0, ""); // params skip condition and just send command
			}
		});
		widen.setLayoutData(new GridData(GridData.FILL_BOTH));

		getLabel(parent, MAX_Y);
		mYmax = getText(parent, MAX_Y);
		getLabel(parent, MAX_U);
		mUmax = getText(parent, MAX_U);
		getLabel(parent, MAX_V);
		mVmax = getText(parent, MAX_V);

		if (mCanvas != null)
		{
			final Button overlay = new Button(parent, SWT.TOGGLE);
			overlay.setText("Overlay");
			overlay.setToolTipText("Overlay blobs detected for this colour bin's contents");
			overlay.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					OverlayContributor oc = mCanvas.getOverlayManager().getOverlay(BlobOverlay.ID);
					if (oc != null)
						oc.setShouldRun(overlay.getSelection());
				}
			});
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan = 2;
			overlay.setLayoutData(gd);
		}
		else
		{
			getLabel(parent, "");
			getLabel(parent, "");
		}

		getLabel(parent, MIN_Y);
		mYmin = getText(parent, MIN_Y);
		getLabel(parent, MIN_U);
		mUmin = getText(parent, MIN_U);
		getLabel(parent, MIN_V);
		mVmin = getText(parent, MIN_V);
	}

	/**
	 * Sets the field values according to a change in focused colour bin.
	 */
	public void selectionChanged()
	{
		ColourBin bin = SrvHal.getCamera().getDetector().getFocusBin();
		mYmax.setText("" + bin.getYmax());
		mUmax.setText("" + bin.getUmax());
		mVmax.setText("" + bin.getVmax());
		mYmin.setText("" + bin.getYmin());
		mUmin.setText("" + bin.getUmin());
		mVmin.setText("" + bin.getVmin());
	}

	/*
	 * Utility method used to add a Text with all the relevant listeners
	 */
	private Text getText(Composite parent, final String id)
	{
		final Text text = new Text(parent, SWT.BORDER | SWT.RIGHT);
		text.setLayoutData(new GridData());
		text.setText("        "); // initial content forces the size of the initial grid layout
		text.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				// validation
				if (e.getSource() instanceof Text)
				{
					Text t = (Text) e.getSource();
					try
					{
						int val = Integer.parseInt(t.getText());
						t.setForeground(ColourManager.getColour(SWT.COLOR_BLACK));
						if (val < 0 || val > 255)
							throw new NumberFormatException();
					}
					catch (NumberFormatException nfe)
					{
						t.setForeground(ColourManager.getColour(SWT.COLOR_RED));
					}
				}
			}
		});
		text.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				// set value on focus shift
				if (e.getSource() instanceof Text)
				{
					Text t = (Text) e.getSource();
					try
					{
						int val = Integer.parseInt(t.getText());
						updateBin(val, id);
					}
					catch (NumberFormatException nfe)
					{
						// NOP
					}
				}
			}
		});
		text.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				// set value on enter key
				if (e.keyCode == 13)
				{
					if (e.getSource() instanceof Text)
					{
						Text t = (Text) e.getSource();
						try
						{
							int val = Integer.parseInt(t.getText());
							updateBin(val, id);
						}
						catch (NumberFormatException nfe)
						{
							// NOP
						}
					}
				}
			}
		});
		return text;
	}

	/*
	 * Update the focused ColourBin according to changes
	 */
	private void updateBin(int value, String id)
	{
		ColourBin bin = SrvHal.getCamera().getDetector().getFocusBin();
		if (id.equals(MAX_Y))
		{
			bin.setYmax(value);
		}
		else if (id.equals(MAX_U))
		{
			bin.setUmax(value);
		}
		else if (id.equals(MAX_V))
		{
			bin.setVmax(value);
		}
		else if (id.equals(MIN_Y))
		{
			bin.setYmin(value);
		}
		else if (id.equals(MIN_U))
		{
			bin.setUmin(value);
		}
		else if (id.equals(MIN_V))
		{
			bin.setVmin(value);
		}
		SrvHal.getCommandQ().addCommand(new SetBinCmd(bin));
	}

	/*
	 * Utility method creates labels
	 */
	private Label getLabel(Composite parent, String text)
	{
		final Label label = new Label(parent, SWT.TRAIL);
		label.setText(text);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));

		return label;
	}
}
