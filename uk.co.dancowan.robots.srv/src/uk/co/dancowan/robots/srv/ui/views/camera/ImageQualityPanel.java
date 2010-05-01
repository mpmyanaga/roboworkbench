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
/**
 * 
 */
package uk.co.dancowan.robots.srv.ui.views.camera;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.camera.Camera;
import uk.co.dancowan.robots.srv.hal.camera.Camera.ProcessControlEnum;
import uk.co.dancowan.robots.srv.hal.camera.Camera.Resolution;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.EdgeDetectionCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.HorizonDetectionCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.ObsticleDetectionCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.ReferenceCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.SegmentationCmd;
import uk.co.dancowan.robots.srv.ui.views.featuredetector.actions.DetectionModeAction;
import uk.co.dancowan.robots.ui.views.Panel;

/**
 * Class implements a <code>Panel</code> of camera image size and quality controls.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class ImageQualityPanel implements Panel
{
	public static final String ID = "uk.co.dancowan.robots.srv.cameraPanel";

	private final CameraCanvas mCameraCanvas;
	private Button mLastSize;

	/**
	 * C'tor
	 */
	public ImageQualityPanel(CameraCanvas canvas)
	{
		mCameraCanvas = canvas;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.Panel#dispose()
	 */
	@Override
	public void dispose()
	{
		// NOP
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.Panel#getID()
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.Panel#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Image Setup Panel";
	}

	/**
	 * Creates the Composite and listeners for image scaling and overlays
	 * 
	 * @see uk.co.dancowan.robots.ui.views.Panel#getPanel(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite getPanel(Composite parent)
	{
		final Group resolution = new Group(parent, SWT.NONE);
		resolution.setLayout(new GridLayout(4, true));
		resolution.setText("Image");
		
		final Button scale = new Button(resolution, SWT.CHECK);
		scale.setText("Fit to Screen");
		scale.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mCameraCanvas.setZoom(scale.getSelection());
			}
		});
		scale.setLayoutData(new GridData());

		getResolutionButton(resolution, "640 x 512", "Set image resolution to 640x480 pixels", Resolution.MEDIUM);
		final Button small = getResolutionButton(resolution, "320 x 256", "Set image resolution to 320x256 pixels", Resolution.SMALL);
		getResolutionButton(resolution, "160 x 128", "Set image resolution to 160x128 pixels", Resolution.TINY);
		small.setSelection(true);
		mLastSize = small;

		getLabel(resolution, "Controls");
		getProcessButton(resolution, "AGC", "Atomatic Gain Control", ProcessControlEnum.AGC);
		getProcessButton(resolution, "AWB", "Auto White Balance", ProcessControlEnum.AWB);
		getProcessButton(resolution, "AEC", "Atomatic Exposure Control", ProcessControlEnum.AEC);

		getLabel(resolution, "Compression");

		final Slider compression = new Slider(resolution, SWT.HORIZONTAL);
		compression.setValues(4, 1, 8, 1, 1, 4);
		compression.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Camera camera = SrvHal.getCamera();
				camera.setCompression(compression.getSelection());
			}
		});
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		compression.setLayoutData(data);

		resolution.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		resolution.pack();
		return resolution;
	}

	public void addToToolBar(IToolBarManager manager)
	{
		List<DetectionModeAction> modeActions = new ArrayList<DetectionModeAction>();
		modeActions.add(new DetectionModeAction(new ReferenceCmd(), "Grab frame and enable frame referening mode", "icons/cmd_feature-difference.png"));
		modeActions.add(new DetectionModeAction(new SegmentationCmd(), "Enable colour segmentation mode", "icons/cmd_feature-segment.gif"));
		modeActions.add(new DetectionModeAction(new EdgeDetectionCmd(), "Enable edge detection mode", "icons/cmd_feature-edge.gif"));
		modeActions.add(new DetectionModeAction(new HorizonDetectionCmd(), "Enable horizon detection mode", "icons/cmd_feature-horizon.gif"));
		modeActions.add(new DetectionModeAction(new ObsticleDetectionCmd(), "Enable obstacle detection mode", "icons/cmd_feature-obstacle.png"));

		for (DetectionModeAction action : modeActions)
		{
			action.setFamily(modeActions);
			manager.appendToGroup("actions", action);
		}
	}

	private Label getLabel(Composite composite, String text)
	{
		Label label = new Label(composite, SWT.TRAIL);
		label.setText(text);
		label.setLayoutData(new GridData());
		return label;
	}

	private Button getProcessButton(final Composite resolution, final String text, final String tooltip, final ProcessControlEnum mode)
	{
		final Button button = new Button(resolution, SWT.CHECK);
		button.setText(text);
		button.setSelection(true);
		button.setToolTipText("Enable " + tooltip);
		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (button.getSelection())
				{
					SrvHal.getCamera().setProcessControl(mode, true);
					button.setToolTipText("Disable " + tooltip);
				}
				else
				{
					SrvHal.getCamera().setProcessControl(mode, false);
					button.setToolTipText("Enable " + tooltip);
				}
			}
		});
		button.setLayoutData(new GridData());
		return button;
	}

	private Button getResolutionButton(final Composite composite, final String text, final String tooltip, final Resolution resolution)
	{
		final Button button = new Button(composite, SWT.RADIO);
		button.setText(text);
		button.setToolTipText(tooltip);
		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (button.getSelection())
					setResolution(button, resolution);
			}
		});
		button.setLayoutData(new GridData());
		return button;
	}

	/*
	 * Ask the Camera to change the image resolution.
	 */
	private void setResolution(Button source, Resolution resolution)
	{
		CommandButtonMediator mediator = new CommandButtonMediator(source);
		SrvHal.getCamera().setResolution(resolution, mediator);
	}

	/*
	 * Class mediates between an SWT widget, the source of the <code>Command</code>
	 * and acts as a <code>CommandListener</code> which can modify the widget
	 * according to incoming <code>CommandEvent</code>s.
	 * 
	 * Used by the resolution radio set to select the resolution only when the command
	 * was successfully sent.
	 */
	private class CommandButtonMediator implements CommandListener
	{
		private final Button mSource;
	
		/*
		 * C'tor adds this as a listener to the passed Command in order
		 * to update the Button on completion.
		 */
		private CommandButtonMediator(Button source)
		{
			mSource = source;
		}

		/*
		 * @see uk.co.dancowan.srv1q.core.CommandListener#commandCompleted(uk.co.dancowan.srv1q.core.CommandEvent)
		 */
		@Override
		public void commandCompleted(CommandEvent e)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					mLastSize = mSource;
				}
			});
		}

		/*
		 * @see uk.co.dancowan.srv1q.core.CommandListener#commandExecuted(uk.co.dancowan.srv1q.core.CommandEvent)
		 */
		@Override
		public void commandExecuted(CommandEvent e)
		{
			//NOP - not interested in command starting
		}

		/*
		 * @see uk.co.dancowan.srv1q.core.CommandListener#commandFailed(uk.co.dancowan.srv1q.core.CommandEvent)
		 */
		@Override
		public void commandFailed(CommandEvent e)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					silentSelect(mSource, false);
					silentSelect(mLastSize, true);		
				}
			});
		}

		/*
		 * Remove listeners from Button, select 'state' and then replace listeners.
		 */
		private void silentSelect(Button button, boolean state)
		{
			if (state != button.getSelection())
			{
				Listener[] selectionListeners = button.getListeners(SWT.Selection);
				for (Listener listener : selectionListeners)
					button.removeListener(SWT.Selection, listener);
				button.setSelection(state);
				for (Listener listener : selectionListeners)
					button.addListener(SWT.Selection, listener);
			}
		}
	}
}
