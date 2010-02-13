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
package uk.co.dancowan.robots.ui.views.filebrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * JFace content provider for the FileBrowserView renders a tree representing
 * the file system.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class FileSystemContentProvider implements ITreeContentProvider
{
	private File mInput;

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof File)
		{
			mInput = (File) inputElement;
			return getChildren(mInput);
		}
		return null;
	}

	@Override
	public void dispose()
	{
		// Nothing created, nothing to destroy
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (newInput instanceof File)
		{
			mInput = (File)newInput;
		}
	}

	/**
	 * @param parentElement
	 * @return Object[]
	 */
	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof File)
		{
			File file = (File) parentElement;
			if (file.isDirectory())
			{
				return sort(file.listFiles());
			}
		}
		return new Object[]{};
	}

	/**
	 * @param element
	 * @return Object
	 */
	@Override
	public Object getParent(Object element)
	{
		if (element instanceof File)
		{
			File file = (File) element;
			if (file.getParent() != null)
			return new File(file.getParent());
		}
		return null;
	}

	/**
	 * @param element
	 * @return boolean
	 */
	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof File)
		{
			File file = (File) element;
			if (file.isDirectory())
			{
				if (file.list().length > 0)
					return true;
			}
		}
		return false;
	}

	/*
	 * Order the children
	 */
	private File[] sort(File[] files)
	{
		List<File> fileList = new ArrayList<File>();
		for (File file : files)
			fileList.add(file);
		Collections.sort(fileList, new FileComparator());

		return fileList.toArray(new File[fileList.size()]);
	}

	/*
	 * Directories before files, alphabetic sort of names.
	 */
	private class FileComparator implements Comparator<File>
	{
		@Override
		public int compare(File file1, File file2)
		{
			if (file1.isDirectory() && ! file2.isDirectory())
				return -1;
			else if ( ! file1.isDirectory() && file2.isDirectory())
				return 1;
			else
				return file1.getName().compareTo(file2.getName());
		}	
	}
}
