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
package uk.co.dancowan.robots.srv.ui.views.featuredetector;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.CommandEvent;
import uk.co.dancowan.robots.hal.core.CommandListener;
import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.NNGrabCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.NNInitCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.NNMatchCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.NNStoreCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.NNTestCmd;
import uk.co.dancowan.robots.srv.hal.commands.featuredetector.NNTrainCmd;
import uk.co.dancowan.robots.srv.hal.featuredetector.Pattern;
import uk.co.dancowan.robots.srv.ui.panels.PatternEditor;
import uk.co.dancowan.robots.srv.ui.panels.PatternPanel;
import uk.co.dancowan.robots.srv.ui.panels.PatternWidget;
import uk.co.dancowan.robots.srv.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.srv.ui.views.featuredetector.actions.GrabPatternsAction;
import uk.co.dancowan.robots.ui.views.ScrolledView;

/**
 * Eclipse <code>View</code> to expose the neural network features.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class FeatureView extends ScrolledView implements IPropertyChangeListener
{
	public static final String ID = "uk.co.dancowan.robots.srv.featureView";

	private static final Point MIN_SIZE = new Point(300, 140);

	private PatternPanel mPatternPanel;
	private PatternEditor mEditor;

	private int mThreshold;
	private boolean mMultiple;

	public FeatureView()
	{
		IPreferenceStore store = SRVActivator.getDefault().getPreferenceStore();
		mThreshold = store.getInt(PreferenceConstants.PATTERN_THRESHOLD);
		mMultiple = store.getBoolean(PreferenceConstants.PATTERN_ALLOW_MULTIPLE);
	}
	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getID()
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control getPartControl(Composite parent)
	{
		Composite part = new Composite(parent, SWT.BORDER);
		part.setLayout(new FormLayout());

		mPatternPanel = new PatternPanel();
		Control patternComposite = mPatternPanel.getPanel(part);

		mEditor = new PatternEditor(part, new Pattern(0));
		configureListeners(mPatternPanel.getPatternWidgets());

		Composite buttonComposite = getButtonComposite(part);

		FormData data = new FormData();
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(50, -2);
		data.top = new FormAttachment(0, 5);
		patternComposite.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(50, -2);
		data.bottom = new FormAttachment(100, -5);
		data.top = new FormAttachment(50, 3);
		mEditor.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(50, 3);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(100, -5);
		data.top = new FormAttachment(50, 3);
		buttonComposite.setLayoutData(data);

		createToolbar();
		part.pack();
		return part;
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
	 * 
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (PreferenceConstants.PATTERN_ALLOW_MULTIPLE.equals(event.getProperty()))
			mMultiple = (Boolean) event.getNewValue();
		else if (PreferenceConstants.PATTERN_THRESHOLD.equals(event.getProperty()))
			mThreshold = (Integer) event.getNewValue();
	}

	/*
	 * Creates the button composite
	 */
	private Composite getButtonComposite(Composite parent)
	{
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, true));

		getButton(buttonComposite, "Init", "Initialise network with random weights", new NNInitCmd());
		getButton(buttonComposite, "Train", "Train network from patterns in memory", new NNTrainCmd());

		Command test = new NNTestCmd();
		test.addListener(new MatchListener());
		getButton(buttonComposite, "Test", "Test matching against the selected pattern", test);

		Command match = new NNMatchCmd();
		match.addListener(new MatchListener());
		getButton(buttonComposite, "Match", "Look for a pattern", match);
		
		Command store = new NNStoreCmd(mEditor);
		store.addListener(new RefreshListener());
		getButton(buttonComposite, "Store", "Store editor pattern to memory", store);
		
		Command grab = new NNGrabCmd();
		grab.addListener(new RefreshListener());
		getButton(buttonComposite, "Grab", "Grab a new pattern using colour blobs", grab);

		return buttonComposite;
	}

	/*
	 * Creates the action buttons for the view
	 */
	private Button getButton(Composite parent, String text, String tooltip, final Command cmd)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setToolTipText(tooltip);
		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				SrvHal.getCommandQ().addCommand(cmd);
			}
		});
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return button;
	}

	/*
	 * Create tool-bar.
	 */
    private void createToolbar()
    {
    	IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    	mgr.add(new GrabPatternsAction());
	}

    /*
     * Add the pattern editor as a listener to each widget
     */
    private void configureListeners(List<PatternWidget> widgets)
    {
    	for (PatternWidget widget : widgets)
    		widget.addListener(SWT.Selection, mEditor);
    }

    /*
     * Refreshes a pattern expected to have changed
     */
    private class RefreshListener implements CommandListener
    {
    	private final int mIndex;

    	public RefreshListener()
    	{
    		mIndex = SrvHal.getCamera().getDetector().getFocusPattern().getIndex();
    	}

		@Override
		public void commandCompleted(CommandEvent event)
		{
			SrvHal.getCamera().getDetector().refreshPattern(mIndex);
		}

		@Override
		public void commandExecuted(CommandEvent event)
		{
			// NOP
		}

		@Override
		public void commandFailed(CommandEvent event)
		{
			// NOP
		}    	
    }

    /*
     * Listens for a match amongst patterns in memory
     */
    private class MatchListener implements CommandListener
    {
    	@Override
		public void commandCompleted(CommandEvent event)
		{
			String result = event.getMessage();
			result = result.substring(result.indexOf("\r")).trim() + "  ";

			int[] scores = new int[16];
			StringBuilder sb = new StringBuilder();
			boolean reading = false;
			int index = 0;
			for (int i = 0; i < result.length(); i ++)
			{
				char c = result.charAt(i);
				if (c != ' ')
				{
					if (Character.isDigit(c))
					{
						sb.append(c);
						reading = true;
					}
				}
				else //is space
				{
					if (reading)
					{
						int number = 0;
						try
						{
							number = Integer.parseInt(sb.toString());
						}
						catch (NumberFormatException e)
						{
							// NOP, default to zero 
						}
						scores[index ++] = number; // Note postfix increment operator
						sb = new StringBuilder();
						reading = false;
					}
				}
			}
			List<Integer> matches = new ArrayList<Integer>();
			int highest = 0;
			int highestIndex = 0;
			for (int i = 0; i < scores.length; i ++)
			{
				try
				{
					if (scores[i] > highest)
					{
						if (scores[i] > highest + mThreshold)
							matches.clear();
						highest = scores[i];
						highestIndex = i;
						matches.add(i);
					}
				}
				catch (NumberFormatException e)
				{
					// NOP, skip it and carry on
				}
			}

			if (mMultiple)
			{
				for (int match : matches)
				{
					mPatternPanel.getPatternWidgets().get(match).matched();
				}
			}
			else
			{
				mPatternPanel.getPatternWidgets().get(highestIndex).matched();
			}

			final PatternWidget widget = mPatternPanel.getPatternWidgets().get(highestIndex);
			Display.getDefault().syncExec(new Runnable()
			{
				@Override
				public void run()
				{
					widget.selectWidget();
				}
			});
		}

		@Override
		public void commandExecuted(CommandEvent event)
		{
			// NOP
		}

		@Override
		public void commandFailed(CommandEvent event)
		{
			// NOP
		}
    }
}