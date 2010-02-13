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
package uk.co.dancowan.robots.ui.views.filebrowser;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.ui.utils.FileUtils;
import uk.co.dancowan.robots.ui.views.ScrolledView;

/**
 * Eclipse <code>View</code> implementation renders the local file system as a navigable tree
 * 
 * <p>Root directory of the tree is set from the preference:</p>
 * <pre>FILE_BROWSER_ROOT (robots.ui.views.FileBroswerRoot)</pre>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class FileBrowserView extends ScrolledView
{
	public static final String ID = "uk.co.dancowan.robots.ui.fileBrowserView";

	private static final Point MIN_SIZE= new Point(300, 150);

	/**
	 * C'tor.
	 */
	public FileBrowserView()
	{
		//NOP
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
		part.setLayout(new FillLayout());

		final TreeViewer viewer = new TreeViewer(part, SWT.FULL_SELECTION);
		viewer.setContentProvider(new FileSystemContentProvider());
		viewer.setLabelProvider(new FileSystemLabelProvider());
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof File)
				{
					File file = (File) obj;
					if (!file.isDirectory())
					{
						FileUtils.openFile(file);
					}
					else
					{
						viewer.setExpandedState(obj, ! viewer.getExpandedState(obj));
					}
				}
			}
		});
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String root = store.getString(PreferenceConstants.FILE_BROWSER_ROOT);
		viewer.setInput(new File(root));

		return part;
	}

	@Override
	public String getID()
	{
		return ID;
	}
}
