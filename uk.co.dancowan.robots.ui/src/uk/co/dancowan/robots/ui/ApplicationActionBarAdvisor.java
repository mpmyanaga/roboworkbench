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

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import uk.co.dancowan.robots.ui.editors.actions.NewFileAction;
import uk.co.dancowan.robots.ui.editors.actions.OpenFileAction;

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
	private IWorkbenchAction mPreferencesAction;
	private IWorkbenchAction mAboutAction;
	private IWorkbenchAction mHelpAction;
	private IWorkbenchAction mHelpSearchAction;
	private IWorkbenchAction mHelpDynamicAction;

	private IAction mNewAction;
	private IAction mOpenAction;
	private IAction mSaveAction;
	private IAction mSaveAsAction;
	private IAction mCutAction;
	private IAction mCopyAction;
	private IAction mPasteAction;
	//private IAction mUndoAction;
	//private IAction mRedoAction;

	/**
	 * C'tor.
	 *
	 * @param configurer the IActionBarConfigurer
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
	{
		super(configurer);
	}

	/**
	 * Creates the actions.
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

		mPreferencesAction = ActionFactory.PREFERENCES.create(window);
		register(mPreferencesAction);

		mHelpAction = ActionFactory.HELP_CONTENTS.create(window);
		register(mHelpAction);

		mHelpSearchAction = ActionFactory.HELP_SEARCH.create(window);
		register(mHelpSearchAction);

		mHelpDynamicAction = ActionFactory.DYNAMIC_HELP.create(window);
		register(mHelpDynamicAction);

		mNewAction = new NewFileAction();

		mOpenAction = new OpenFileAction();

		mSaveAction = ActionFactory.SAVE.create(window);
		register(mSaveAction);

		mSaveAsAction = ActionFactory.SAVE_AS.create(window);
		register(mSaveAsAction);

		mCutAction = ActionFactory.CUT.create(window);
		register(mCutAction);

		mCopyAction = ActionFactory.COPY.create(window);
		register(mCopyAction);

		mPasteAction = ActionFactory.PASTE.create(window);
		register(mPasteAction);
	}

	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillMenuBar(IMenuManager menuBar)
	{
		MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
		MenuManager editMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
		MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
		MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);

		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));		
		fileMenu.add(mNewAction);		
		fileMenu.add(mOpenAction);		
		fileMenu.add(mSaveAction);		
		fileMenu.add(mSaveAsAction);
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		fileMenu.add(new Separator());
		fileMenu.add(mExitAction);
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));

		editMenu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_START));		
		editMenu.add(mCutAction);		
		editMenu.add(mCopyAction);		
		editMenu.add(mPasteAction);
		editMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		editMenu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_END));

		windowMenu.add(new GroupMarker("defaultParts"));
		windowMenu.add(new Separator());
		windowMenu.add(new GroupMarker("robotExtensions"));
		windowMenu.add(new Separator());
		windowMenu.add(new GroupMarker(IWorkbenchActionConstants.WINDOW_EXT));		
		windowMenu.add(mPreferencesAction);
		
		helpMenu.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
		helpMenu.add(mAboutAction);
		helpMenu.add(new Separator());
		helpMenu.add(mHelpAction);
		helpMenu.add(mHelpSearchAction);
		helpMenu.add(mHelpDynamicAction);
		helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		helpMenu.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
	}

	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillCoolBar(org.eclipse.jface.action.ICoolBarManager)
	 */
	protected void fillCoolBar(ICoolBarManager coolBar)
	{
		IToolBarManager fileBar = new ToolBarManager(SWT.FLAT | SWT.LEFT);
		IToolBarManager editBar = new ToolBarManager(SWT.FLAT | SWT.LEFT);

		fileBar.add(mNewAction);
		fileBar.add(mOpenAction);
		fileBar.add(mSaveAction);
		fileBar.add(mSaveAsAction);

		editBar.add(mCutAction);
		editBar.add(mCopyAction);
		editBar.add(mPasteAction);

		coolBar.add(new ToolBarContributionItem(fileBar, "file"));
		coolBar.add(new ToolBarContributionItem(editBar, "edit"));
	}
}
