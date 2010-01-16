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
package uk.co.dancowan.robots.srv.ui.panels;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import uk.co.dancowan.robots.srv.hal.SrvHal;
import uk.co.dancowan.robots.srv.hal.featuredetector.Pattern;
import uk.co.dancowan.robots.srv.hal.featuredetector.PatternMemory;

/**
 * Class manages a view sub-panel for the pattern recognition algorithms of the SRV1.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class PatternPanel implements Panel
{
	public static final String ID = "uk.co.dancowan.robots.ui.PatternPanel";

	private final List<PatternWidget> mPatterns;

	public PatternPanel()
	{
		mPatterns = new ArrayList<PatternWidget>();
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.panels.Panel#getID()
	 */
	@Override
	public String getID()
	{
		return ID;
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.panels.Panel#getPanel(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite getPanel(Composite parent)
	{
		Group panel = new Group(parent, SWT.NONE);
		panel.setText("Patterns");
		panel.setLayout(new GridLayout(8, true));

		PatternMemory memory = SrvHal.getCamera().getDetector().getPatternMemory();
		for (int i = 0; i < 16; i ++)
		{
			Pattern pattern = memory.getPattern(i);
			PatternWidget pw = new PatternWidget(panel, pattern);
			pw.setLayoutData(new GridData(GridData.FILL_BOTH));
			mPatterns.add(pw);
		}
		return panel;
	}

	/**
	 * @see uk.co.dancowan.robots.srv.ui.panels.Panel#dispose()
	 */
	public void dispose()
	{
		//NOP
	}
}
