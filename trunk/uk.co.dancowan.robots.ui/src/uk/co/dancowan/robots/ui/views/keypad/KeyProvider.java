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
package uk.co.dancowan.robots.ui.views.keypad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.HALRegistry;
import uk.co.dancowan.robots.hal.core.commands.SendString;
import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.PluginRegistrySupport;
import uk.co.dancowan.robots.ui.preferences.PreferenceConstants;

/**
 * Class creates and manages KeyPad buttons.
 * 
 * <p>On construction the class requests descriptors from the plugin
 * registry. When passed a parent <code>Composite</code> the buttons are
 * created from their descriptors and added to the parent.</p> 
 * 
 * <p>Buttons may be configured to belong to radio sets which this class will
 * create and maintain.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class KeyProvider
{
	private final List<CommandButtonDescriptor> mDescriptors;
	private final Map<String, List<Button>> mRadioSets;

	private int mColumns;
	private int mRows;
	private int mButtons;

	/**
	 * C'tor.
	 */
	public KeyProvider()
	{
		mDescriptors = PluginRegistrySupport.parseCommandButtons();
		mRadioSets = new HashMap<String, List<Button>>();
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		mColumns = store.getInt(PreferenceConstants.KEYPAD_COLS);
		mRows = store.getInt(PreferenceConstants.KEYPAD_ROWS);;
		mButtons = mColumns*mRows;
	}

	/**
	 * Add extensions to the passed <code>Composite</code> in the form
	 * of <code>Button</p> instances.
	 * 
	 * @param parent Composite
	 */
	public void createButtons(Composite parent)
	{
		int action = 0; // tracks actual actions against blank buttons
		for (int index = 0; index < mButtons; index ++)
		{
			CommandButtonDescriptor desc = null;
			if (action < mDescriptors.size())
				desc = mDescriptors.get(action);

			if (desc != null && desc.getIndex() != -1 && desc.getIndex() <= index)
			{
				createButton(desc, parent);
				action ++;
			}
			else
			{
				createEmptyButton(index, parent);
			}
		}
	}

	/*
	 * If the identified set doesn't exist, create it
	 */
	private void checkRadioSet(String id, Button button)
	{
		List<Button> set;
		if (mRadioSets.containsKey(id))
			set = mRadioSets.get(id);
		else
		{
			set = new ArrayList<Button>();
			mRadioSets.put(id, set);
		}
		set.add(button);
	}

	/*
	 * Helper method toggles buttons within the radio set
	 */
	private void toggleButtons(Button pressed, String id)
	{
		List<Button> set = mRadioSets.get(id);
		for (Button other : set)
		{
			if (other != pressed)
			{
				if (other.getSelection() == true)
					other.setSelection(false);
			}
		}
	}

	/*
	 * Creates a button from a descriptor
	 */
	private Button createButton(final CommandButtonDescriptor desc, Composite parent)
	{
		int rl = desc.getReturn();
		final boolean encode = desc.shouldEncode();
		final Command onCmd = new SendString(desc.getOnHex(), rl, encode);
		final Command offCmd = desc.getOffHex() == null ? null : new SendString(desc.getOffHex(), rl, encode);
		
		int swt = desc.getType().equals("push") ? SWT.PUSH : SWT.TOGGLE;
		final Button button = new Button(parent, swt);

		if (desc.getIcon() != null)
		{ 
			button.setImage(desc.getImage());
		}
		else
			button.setText(desc.getName());

		if (desc.getTooltip() != null)
			button.setToolTipText(desc.getTooltip());
		
		if (desc.getRadioID() != null)
			checkRadioSet(desc.getRadioID(), button);

		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (button.getSelection())
				{
					HALRegistry.getInsatnce().getCommandQ().addCommand(onCmd);
					List<Button> rs = mRadioSets.get(desc.getRadioID());
					if (rs != null && rs.contains(button))
						toggleButtons(button, desc.getRadioID());
				}
				else
				{
					// for PUSH buttons (no selection flag), send onCommand, otherwise offCmd
					Command cmd = offCmd == null ? onCmd : offCmd;
					HALRegistry.getInsatnce().getCommandQ().addCommand(cmd);
					
					// let push buttons participate in radio sets
					if (desc.getRadioID() != null)
						toggleButtons(button, desc.getRadioID());
				}
			}
		});
		button.setLayoutData(new GridData(GridData.FILL_BOTH));
		return button;
	}

	/*
	 * Create an actionless push button with no text
	 */
	private Button createEmptyButton(int index, Composite parent)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Button " + index + " not assigned.");
		button.setLayoutData(new GridData(GridData.FILL_BOTH));
		return button;
	}
}
