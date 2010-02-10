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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

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
		// TODO Auto-generated method stub
		
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
				return file.listFiles();
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
}
