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
package uk.co.dancowan.robots.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import uk.co.dancowan.robots.ui.Activator;

/**
 * Custom widget handles the animated display of a list of <code>Panel</code> objects.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class MultiPanel
{
	private static final int SPEED = 3;

	private final List<MultiPanelChangeListener> mListeners;
	private final List<Panel> mPanels;
	private final List<Composite> mComposites;

	private Composite mPanelComposite;
	private int mIndex;

	/**
	 * C'tor
	 */
	public MultiPanel()
	{
		mListeners = new ArrayList<MultiPanelChangeListener>();
		mPanels = new ArrayList<Panel>();
		mComposites = new ArrayList<Composite>();
	}

	/**
	 * Add the passed listener to the group of listeners informed of panel
	 * change events
	 * 
	 * @param listener
	 */
	public void addListener(MultiPanelChangeListener listener)
	{
		mListeners.add(listener);
	}

	/**
	 * Remove the passed listener from the group of listeners informed of panel
	 * change events
	 * 
	 * @param listener
	 */
	public void removeListener(MultiPanelChangeListener listener)
	{
		mListeners.remove(listener);
	}

	/**
	 * Add the passed <code>Panel</code> instance to the list of those panels
	 * managed by this widget.
	 * 
	 * @param panel
	 */
	public void addPanel(Panel panel)
	{
		mPanels.add(panel);
	}

	/**
	 * Remove the passed <code>Panel</code> instance from the list of those panels
	 * managed by this widget.
	 * 
	 * @param panel
	 */
	public void removePanel(Panel panel)
	{
		mPanels.remove(panel);
	}

	/**
	 * Dispose of resources
	 * 
	 * <p>Implementation calls <cde>dispose()</code> on each of it's managed
	 * <code>Panel</code>s.</p>
	 */
	public void dispose()
	{
		for (Panel panel : mPanels)
			panel.dispose();
	}

	/**
	 * Returns the <code>Panel</code> currently in view
	 * 
	 * @return Panel
	 */
	public Panel getCurrentPanel()
	{
		return mPanels.get(mIndex);
	}

	/**
	 * Create this Panel's controls
	 * 
	 * @param parent Composite
	 * @return Composite
	 */
	public Composite createControl(Composite parent)
	{
		mPanelComposite = new Composite(parent, SWT.NONE);
		mPanelComposite.setLayout(new FormLayout());

		final Button up = new Button(mPanelComposite, SWT.PUSH);
		final Button down = new Button(mPanelComposite, SWT.PUSH);

		up.setImage(Activator.getImageDescriptor("icons/action_up.png").createImage());
		up.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				changePanel(-1);
				setTooltips(up, down);
			}
		});

		down.setImage(Activator.getImageDescriptor("icons/action_down.png").createImage());
		down.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				changePanel(1);
				setTooltips(up, down);
			}
		});

		FormData data = new FormData();
		data.top = new FormAttachment(50, -28);
		data.bottom = new FormAttachment(50, -2);
		data.left = new FormAttachment(0, 4);
		data.right = new FormAttachment(0, 28);
		up.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(50, 2);
		data.bottom = new FormAttachment(50, 28);
		data.left = new FormAttachment(0, 4);
		data.right = new FormAttachment(0, 28);
		down.setLayoutData(data);

		for (Panel child : mPanels)
		{
			final Composite composite = child.getPanel(mPanelComposite);
			data = new FormData();
			data.top = new FormAttachment(0, 4);
			data.bottom = new FormAttachment(100, -4);
			data.left = new FormAttachment(up, 4, SWT.RIGHT);
			data.right = new FormAttachment(100, -4);
			mComposites.add(composite);
			composite.setLayoutData(data);
		}

		setTooltips(up, down);
		mIndex = 0;//mPanels.size() - 1;
		return mPanelComposite;		
	}

	/*
	 * Inform listeners that the panel has changed
	 */
	private void firePanelEvent(Panel newPanel)
	{
		for (MultiPanelChangeListener listener : mListeners)
		{
			listener.panelChanged(newPanel);
		}
	}

	/*
	 * Update the change buttons' tooltips
	 */
	private void setTooltips(Button up, Button down)
	{
		up.setToolTipText(mPanels.get(getNextIndex(-1)).getDescription());
		down.setToolTipText(mPanels.get(getNextIndex(1)).getDescription());
	}

	/*
	 * Compute the next index in the passed direction <-1 | 1>
	 */
	private int getNextIndex(int dir)
	{
		if (dir < -1 || dir > 1)
			return mIndex;

		int index = mIndex + dir;
		if (index < 0)
			index = mPanels.size() - 1;
		else if (index >= mPanels.size())
			index = 0;
		return index;
	}

	/*
	 * Display the next panel in the given direction <-1 | 1>
	 */
	private void changePanel(int dir)
	{
		if (dir < -1 || dir > 1)
			return;

		Composite currentPanel = mComposites.get(mIndex);

		mIndex = getNextIndex(dir);

		Composite nextPanel = mComposites.get(mIndex);
		if (dir == 1)
		{
			FormData data = (FormData) nextPanel.getLayoutData();
			data.top = new FormAttachment(0, 4);
			data.bottom = new FormAttachment(0, 4);
			firePanelEvent(mPanels.get(mIndex));
		}
		else if (dir == -1)
		{
			FormData data = (FormData) nextPanel.getLayoutData();
			data.top = new FormAttachment(100, -4);
			data.bottom = new FormAttachment(100, -4);
			firePanelEvent(mPanels.get(mIndex));
		}

		Composite top = dir == -1 ? currentPanel : nextPanel;
		Composite bottom = dir == 1 ? currentPanel : nextPanel;
		new AnimationThread(top, bottom, SPEED, dir).start();
	}

	/*
	 * Inner class allows for an animated show/hide routine based on changing the
	 * size of the Panel's layouts
	 */
	private class AnimationThread extends Thread
	{
		private final Composite mTop;
		private final Composite mBottom;
		private final int mSpeed;
		private final int mDirection;

		/*
		 * C'tor
		 */
		private AnimationThread(Composite top, Composite bottom, int speed, int dir)
		{
			mTop = top;
			mBottom = bottom;
			mSpeed = speed;
			mDirection = dir;

			setName("PanelAnimation");
		}

		/*
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			//int height = mTop.getSize().y > mBottom.getSize().y ? mTop.getSize().y : mBottom.getSize().y;
			//int width = mTop.getSize().x > mBottom.getSize().x ? mTop.getSize().x : mBottom.getSize().x;
			for (int i = 0; i < (100 + mSpeed); i += mSpeed)
			{
				int j = (i > 100) ? 100 : i;
				final int offset = mDirection == 1 ? j : 100 - j;
				Display.getDefault().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						
						FormData data = (FormData) mTop.getLayoutData();
						data.bottom = new FormAttachment(offset, -4);
						
						data = (FormData) mBottom.getLayoutData();
						data.top = new FormAttachment(offset, 4);
						
						mPanelComposite.layout();
						mPanelComposite.redraw();
					}
				});
			}
		}
	}
}
