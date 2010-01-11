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
package uk.co.dancowan.robots.ui;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * Eclipse foundation class to create the action bar.
 * 
 * <p>An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.</p>
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
	private IWorkbenchAction mExitAction;
	private IContributionItem mShowAction;
	private IWorkbenchAction mPreferencesAction;
	private IWorkbenchAction mAboutAction;
	private IWorkbenchAction mHelpAction;
	private IWorkbenchAction mHelpSearchAction;
	private IWorkbenchAction mHelpDynamicAction;

	/**
	 * C'tor.
	 *
	 * @param configurer
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
	{
		super(configurer);
	}

	/**
	 * Create the actions here.
	 */
	protected void makeActions(final IWorkbenchWindow window)
	{
		// Creates the actions and registers them. Registering is needed to ensure
		// that key bindings work. The corresponding commands keybindings are
		// defined in the plugin.xml file. Registering also provides automatic
		// disposal of the actions when the window is closed.

		mExitAction = ActionFactory.QUIT.create(window);
		register(mExitAction);

		mAboutAction = ActionFactory.ABOUT.create(window);
		register(mAboutAction);

		mShowAction = ContributionItemFactory.VIEWS_SHORTLIST.create(window);

		mPreferencesAction = ActionFactory.PREFERENCES.create(window);
		register(mAboutAction);

		mHelpAction = ActionFactory.HELP_CONTENTS.create(window);
		register(mHelpAction);

		mHelpSearchAction = ActionFactory.HELP_SEARCH.create(window);
		register(mHelpSearchAction);

		mHelpDynamicAction = ActionFactory.DYNAMIC_HELP.create(window);
		register(mHelpDynamicAction);
	}

	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillMenuBar(IMenuManager menuBar)
	{
		MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
		MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
		MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
		menuBar.add(fileMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
		
		fileMenu.add(mExitAction);
		
		windowMenu.add(mShowAction);
		windowMenu.add(new Separator());
		windowMenu.add(mPreferencesAction);
		
		helpMenu.add(mAboutAction);
		helpMenu.add(new Separator());
		helpMenu.add(mHelpAction);
		helpMenu.add(mHelpSearchAction);
		helpMenu.add(mHelpDynamicAction);
	}
}