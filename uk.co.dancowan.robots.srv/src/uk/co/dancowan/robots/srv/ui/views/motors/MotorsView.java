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
package uk.co.dancowan.robots.srv.ui.views.motors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.srv.hal.Motors;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.commands.motors.MotorCommandGenerator;
import uk.co.dancowan.robots.srv.hal.commands.motors.MotorsCmd;
import uk.co.dancowan.robots.ui.utils.ColourManager;
import uk.co.dancowan.robots.ui.views.ScrolledView;

/**
 * Eclipse <code>View</code> implementation exposes motor drive commands
 * in several aspects.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class MotorsView extends ScrolledView
{
	public static final String ID = "uk.co.dancowan.robots.srv.motorsView";

	private static final Point MIN_SIZE= new Point(300, 150);

	private final CommandQ mCmdQ;
	private final Motors mMotors;

	private Button mSend;
	private Color mStickColour;
	private int mStickX;
	private int mStickY;

	/**
	 * C'tor.
	 */
	public MotorsView()
	{
		mCmdQ = SrvHal.getCommandQ();
		mMotors = SrvHal.getMotors();		
		mStickColour = ColourManager.getColour(SWT.COLOR_BLACK);
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getMinSize()
	 */
	@Override
	public Point getMinSize()
	{
		return MIN_SIZE;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control getPartControl(Composite parent)
	{
		final Composite part = new Composite(parent, SWT.NONE);
		part.setLayout(new FormLayout());

		final Composite joystickComposite = getjoystickComposite(part);
		final Composite speedComposite = getSpeedComposite(part);
		
		part.addListener(SWT.Resize, new Listener()
		{
			@Override
			public void handleEvent(Event e)
			{
				if (e.type == SWT.Resize)
				{
					int xOffset = (part.getSize().x - 250)/2;
					int yOffset = (part.getSize().y - 150)/2;
					FormData d = (FormData) joystickComposite.getLayoutData();
					d.left = new FormAttachment(0, 100 + xOffset);
					d.right = new FormAttachment(100, -xOffset);
					d.top = new FormAttachment(0, yOffset);
					d.bottom = new FormAttachment(100, -yOffset);
				}
			}
		});
		
		int xOffset = (part.getSize().x - 250)/2;
		int yOffset = (part.getSize().y - 150)/2;
		
		FormData data = new FormData();
		data.left = new FormAttachment(0, xOffset);
		data.right = new FormAttachment(100, -xOffset);
		data.top = new FormAttachment(0, yOffset);
		data.bottom = new FormAttachment(100, -yOffset);
		joystickComposite.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0, 6);
		data.right = new FormAttachment(0, 98);
		data.bottom = new FormAttachment(100, -6);
		data.top = new FormAttachment(0, 6);
		speedComposite.setLayoutData(data);

		return part;
	}

	@Override
	public String getID()
	{
		return ID;
	}

	/*
	 * return the composite for setting motor speed and direct drive command set.
	 */
	private Composite getSpeedComposite(Composite parent)
	{
		Composite speedComposite = new Composite(parent, SWT.NONE);
		speedComposite.setLayout(new FormLayout());

		final Label label = new Label(speedComposite, SWT.NONE);
		label.setText("Duration");

		final Text duration = new Text(speedComposite, SWT.BORDER);
		duration.setText(Integer.toString(mMotors.getDuration()));
		duration.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				if (e.getSource() instanceof Text)
				{
					Text text = (Text) e.getSource();
					try
					{
						Integer.parseInt(text.getText());
						duration.setForeground(ColourManager.getColour(SWT.COLOR_BLACK));
					}
					catch (NumberFormatException nfe)
					{
						text.setForeground(ColourManager.getColour(ColourManager.ERROR_COLOUR));
					}
				}
			}
		});
		duration.addFocusListener(new FocusAdapter()
		{	
			@Override
			public void focusLost(FocusEvent e)
			{
				Text text = (Text) e.getSource();
				try
				{
					mMotors.setDuration(Integer.parseInt(text.getText()));
				}
				catch (NumberFormatException nfe)
				{
					// NOP
				}
			}
		});

		mSend = new Button(speedComposite, SWT.PUSH);
		mSend.setText("Send");
		mSend.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mMotors.fireCommand(); 
			}
		});

		final Slider lMotor = new Slider(speedComposite, SWT.VERTICAL);
		lMotor.setValues(100, 0, 200, 1, 1, 10);
		lMotor.setToolTipText("Left motor power: " + getDisplayMotorValue(lMotor.getSelection(), 200));
		lMotor.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (e.getSource() instanceof Slider)
				{
					Slider sl = (Slider) e.getSource();
					mMotors.setLeftMotor(mMotors.getMotorValue(sl.getSelection(), sl.getMaximum()));
					sl.setToolTipText("Left motor power: " + getDisplayMotorValue(sl.getSelection(), 200));
				}
			}
		});

		final Slider rMotor = new Slider(speedComposite, SWT.VERTICAL);
		rMotor.setValues(100, 0, 200, 1, 1, 10);
		rMotor.setToolTipText("Right motor power: " + getDisplayMotorValue(rMotor.getSelection(), 200));
		rMotor.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (e.getSource() instanceof Slider)
				{
					Slider sl = (Slider) e.getSource();
					mMotors.setRightMotor(mMotors.getMotorValue(sl.getSelection(), sl.getMaximum()));
					sl.setToolTipText("Right motor power: " + getDisplayMotorValue(sl.getSelection(), 200));
				}
			}
		});

		final Slider bothMotors = new Slider(speedComposite, SWT.VERTICAL);
		bothMotors.setValues(100, 0, 200, 1, 1, 10);
		bothMotors.setToolTipText("Both motors");
		bothMotors.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (e.getSource() instanceof Slider)
				{
					Slider sl = (Slider) e.getSource();
					lMotor.setSelection(sl.getSelection());
					rMotor.setSelection(sl.getSelection());

					mMotors.setLeftMotor(mMotors.getMotorValue(sl.getSelection(), sl.getMaximum()));
					mMotors.setRightMotor(mMotors.getMotorValue(sl.getSelection(), sl.getMaximum()));
					lMotor.setToolTipText("Left motor power: " + getDisplayMotorValue(lMotor.getSelection(), 200));
					rMotor.setToolTipText("Right motor power: " + getDisplayMotorValue(rMotor.getSelection(), 200));
				}
			}
		});

		final Slider trim = new Slider(speedComposite, SWT.READ_ONLY);
		trim.setValues(10, 0, 20, 1, 1, 5);
		trim.setToolTipText("Motor trim.");
		trim.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (e.getSource() instanceof Spinner)
				{
//					Slider sp = (Slider) e.getSource();
//					if (sp.getSelection() > mMotors.getLastTrim())
//						mSRV.addCommand(uk.co.dancowan.robots.hal.core.commands.TRIM_RIGHT);
//					else
//						mSRV.addCommand(uk.co.dancowan.robots.hal.core.commands.TRIM_LEFT);
//					mMotors.setLastTrim(sp.getSelection());
				}
			}
		});

		FormData data = new FormData();
		data.left = new FormAttachment(0, 2);
		data.right = new FormAttachment(100, -2);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(0, 24);
		mSend.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0, 2);
		data.right = new FormAttachment(0, 48);
		data.top = new FormAttachment(mSend, 2, SWT.BOTTOM);
		data.bottom = new FormAttachment(mSend, 26, SWT.BOTTOM);
		label.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(label, 2, SWT.RIGHT);
		data.right = new FormAttachment(100, -2);
		data.top = new FormAttachment(mSend, 2, SWT.BOTTOM);
		data.bottom = new FormAttachment(mSend, 26, SWT.BOTTOM);
		duration.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0, 2);
		data.right = new FormAttachment(100, -2);
		data.top = new FormAttachment(100, -24);
		data.bottom = new FormAttachment(100, 0);
		trim.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 2);
		data.right = new FormAttachment(0, 24);
		data.top = new FormAttachment(duration, 4, SWT.BOTTOM);
		data.bottom = new FormAttachment(trim, -6, SWT.TOP);
		lMotor.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(lMotor, 8, SWT.RIGHT);
		data.right = new FormAttachment(lMotor, 32, SWT.RIGHT);
		data.top = new FormAttachment(duration, 4, SWT.BOTTOM);
		data.bottom = new FormAttachment(trim, -6, SWT.TOP);
		bothMotors.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(bothMotors, 8, SWT.RIGHT);
		data.right = new FormAttachment(bothMotors, 32, SWT.RIGHT);
		data.top = new FormAttachment(duration, 4, SWT.BOTTOM);
		data.bottom = new FormAttachment(trim, -6, SWT.TOP);
		rMotor.setLayoutData(data);

		speedComposite.pack();
		return speedComposite;
	}

	/*
	 * return the joy-stick control composite
	 */
	private Composite getjoystickComposite(Composite parent)
	{
		mStickX = 75;
		mStickY = 75;

		final Composite joystickComposite = new Composite(parent, SWT.NONE);
		joystickComposite.setSize(150, 150);
		joystickComposite.addPaintListener(new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent e)
			{
				final GC graphics = e.gc;
				graphics.setBackground(ColourManager.getColour(SWT.COLOR_GRAY));

				int width = joystickComposite.getSize().x - 1;
				int height = joystickComposite.getSize().y - 1;

				graphics.setForeground(ColourManager.getColour(SWT.COLOR_GRAY));

				graphics.drawRectangle(0, 0, width, height);
				graphics.drawArc(0, 0, width/2, height, 270, 180);
				graphics.drawArc(width/2, 0, width/2, height, 90, 180);

				graphics.drawLine(width/2, 0, width/2, height);
				graphics.drawLine(0, height/2, width, height/2);

				graphics.drawOval(45, height/2-15, 30, 30);
				graphics.drawOval(75, height/2-15, 30, 30);
				graphics.drawOval(15, height/2-30, 60, 60);
				graphics.drawOval(75, height/2-30, 60, 60);
				graphics.drawOval(-15, height/2-45, 90, 90);
				graphics.drawOval(75, height/2-45, 90, 90);

				graphics.setForeground(mStickColour);
				graphics.setBackground(mStickColour);
				graphics.drawOval(mStickX - 5, mStickY - 5, 10, 10);
				graphics.fillOval(mStickX - 3, mStickY - 3, 6, 6);
			}
		});
		final MotorCommandGenerator generator = new MotorCommandGenerator(150, 150);
		joystickComposite.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseUp(MouseEvent e)
			{
				mStickColour = ColourManager.getColour(SWT.COLOR_BLACK);
				joystickComposite.redraw();
			}
			@Override
			public void mouseDown(MouseEvent e)
			{
				mStickColour = ColourManager.getColour(ColourManager.ERROR_COLOUR);
				MotorsCmd cmd = generator.getCommand(mStickX, mStickY);
				mCmdQ.addCommand(cmd);
				joystickComposite.redraw();
			}
		});
		joystickComposite.addMouseMoveListener(new MouseMoveListener()
		{
			@Override
			public void mouseMove(MouseEvent e)
			{
				mStickX = e.x;
				mStickY = e.y;
				joystickComposite.redraw();
			}
		});
		joystickComposite.addMouseTrackListener(new MouseTrackAdapter()
		{
			@Override
			public void mouseExit(MouseEvent e)
			{
				mStickX = 75;
				mStickY = 75;
				joystickComposite.redraw();
			}
		});
		return joystickComposite;
	}

	/*
	 * Translates slider value into display motor value (-100 m-> 100) for tool-tips
	 */
	private int getDisplayMotorValue(int selection, int max)
	{
		int value = (int)(200 * (double)selection/max);
		if (value < 100)
			value = 100 - value;
		else
			value = -1 * 100 + (200 - value);
		return value;
	}
}
