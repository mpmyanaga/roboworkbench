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
package uk.co.dancowan.robots.srv.ui.views.featuredetector.actions;

import java.util.List;

import org.eclipse.jface.action.Action;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.commands.PauseQCmd;
import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.G_Cmd;

/**
 * ViewPart action to request g* mode changes in the feature detector.
 * 
 * <p>The SRV1 supports:</p>
 * <ul><li>Frame differencing</li>
 * <li>Colour segmentation</li>
 * <li>Edge detection</li>
 * <li>Horizon detection</li>
 * <li>Obstacle detection</li></ul>
 * <p>This <code>Action</code> implementation can be configured to switch between
 * detection modes. Internally the action is of type <i>CHECK_BOX</i>, switching
 * state will turn the configured mode on or off. Actions belong to an action family
 * amongst which radio button behaviour is also supported; selecting a new mode
 * will cancel any other mode that is enabled. Only one mode can be enabled at a time</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class DetectionModeAction extends Action
{
	private final Command mEnableCommand;

	private List<DetectionModeAction> mFamily;

	/**
	 * C'tor.
	 *
	 * @param enable Command to execute on select
	 * @param tooltip text String to display
	 * @param imagePath plugin relative path to icon image
	 */
	public DetectionModeAction(Command enable, String tooltip, String imagePath)
	{
		super("Feature", AS_CHECK_BOX);

		mEnableCommand = enable;
		setToolTipText(tooltip);
		setImageDescriptor(SRVActivator.getImageDescriptor(imagePath));
	}

	/**
	 * Sets the family of actions to which this action belongs.
	 * 
	 * <p>Only one action in a family may be selected. Unlike a standard
	 * radio group a family may have no actions selected, actions being toggle
	 * in nature.</p>
	 *  
	 * @param family
	 */
	public void setFamily(List<DetectionModeAction> family)
	{
		mFamily = family;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		boolean wasPolling = SrvHal.getCamera().isPolling();
		if (wasPolling)
			SrvHal.getCamera().stopPolling();

		SrvHal.getCommandQ().addCommand(new G_Cmd()); // turn off all modes
		if (isChecked())
		{
			deselectFamily();
			SrvHal.getCommandQ().addCommand(mEnableCommand);
		}

		if (wasPolling)
		{
			SrvHal.getCommandQ().addCommand(new PauseQCmd(100));
			SrvHal.getCamera().startPolling();
		}
	}

	/*
	 * Turns off other actions.
	 */
	private void deselectFamily()
	{
		for (DetectionModeAction action : mFamily)
		{
			if (action != this)
				action.setChecked(false);
		}
	}
}
