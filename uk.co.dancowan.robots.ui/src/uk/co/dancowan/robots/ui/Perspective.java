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
package uk.co.dancowan.robots.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import uk.co.dancowan.robots.ui.views.commandConsole.CommandConsole;
import uk.co.dancowan.robots.ui.views.connection.ConnectionView;
import uk.co.dancowan.robots.ui.views.keypad.KeypadView;

/**
 * Eclipse foundation class to generate the default perspective.
 *
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class Perspective implements IPerspectiveFactory
{
	private static final String TOP_LEFT_FOLDER = "TOP_LEFT";
	private static final String LEFT_FOLDER = "LEFT";
	private static final String BOTTOM_RIGHT_FOLDER = "BOTTOM_RIGHT";
	private static final String BOTTOM_LEFT_FOLDER = "BOTTOM_LEFT";

	/**
	 * Initial layout includes:
	 * <pre>  ConnectionView
	 *  CommandConsole
	 *  Keypad</pre>
	 * and a placeholder for a main view (editor/camera ... )
	 * 
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout)
	{
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout topLeft = layout.createFolder(TOP_LEFT_FOLDER, IPageLayout.LEFT, 0.32f, editorArea);
		IFolderLayout left = layout.createFolder(LEFT_FOLDER, IPageLayout.BOTTOM, 0.3f, TOP_LEFT_FOLDER);
		IFolderLayout bottomLeft = layout.createFolder(BOTTOM_LEFT_FOLDER, IPageLayout.BOTTOM, 0.6f, LEFT_FOLDER);

		/*IFolderLayout topRight =*/ layout.createFolder(BOTTOM_RIGHT_FOLDER, IPageLayout.BOTTOM, 0.32f, editorArea);
		
		topLeft.addView(ConnectionView.ID);
		left.addView(CommandConsole.ID);
		bottomLeft.addView(KeypadView.ID);
	}
}
