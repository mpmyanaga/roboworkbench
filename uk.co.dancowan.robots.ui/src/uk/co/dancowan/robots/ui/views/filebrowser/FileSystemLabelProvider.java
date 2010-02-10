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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import uk.co.dancowan.robots.ui.Activator;

public class FileSystemLabelProvider implements ILabelProvider
{
	@Override
	public Image getImage(Object element)
	{
		if (element instanceof File)
		{
			File file = (File) element;
			if (file.isDirectory())
				return Activator.getImageDescriptor("icons/obj_directory.gif").createImage();
			return Activator.getImageDescriptor("icons/obj_file.gif").createImage();
		}
		return Activator.getImageDescriptor("icons/obj_unknown.gif").createImage();
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof File)
		{
			File file = (File) element;
			return file.getName();
		}
		return "unknown element";
	}

	@Override
	public void addListener(ILabelProviderListener listener)
	{
		// NOP
	}

	@Override
	public void dispose()
	{
		// NOP
	}

	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener)
	{
		// NOP
	}	
}