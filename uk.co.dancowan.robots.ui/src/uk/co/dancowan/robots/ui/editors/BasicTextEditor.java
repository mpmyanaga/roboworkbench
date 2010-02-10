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
package uk.co.dancowan.robots.ui.editors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import uk.co.dancowan.robots.ui.Activator;

/**
 * Class provides basic text editing abilities tareted at picoC.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class BasicTextEditor implements IEditorPart, StyledTextSource
{
	private StyledText mText;
	private IEditorSite mSite;
	private List<IPropertyListener> mPropertyListeners;
	private FileEditorInput mInput;
	private boolean mDirty;

	/**
	 * C'tor
	 */
	public BasicTextEditor()
	{
		mPropertyListeners = new ArrayList<IPropertyListener>();
	}

	/**
	 * @see uk.co.dancowan.robots.ui.editors.StyledTextSource#getText()
	 */
	public StyledText getText()
	{
		return mText;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		mText.dispose();
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#getEditorInput()
	 */
	@Override
	public IEditorInput getEditorInput()
	{
		return mInput;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#getEditorSite()
	 */
	@Override
	public IEditorSite getEditorSite()
	{
		return mSite;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		mSite = site;
		mInput = (FileEditorInput) input;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#addPropertyListener(org.eclipse.ui.IPropertyListener)
	 */
	@Override
	public void addPropertyListener(IPropertyListener listener)
	{
		mPropertyListeners.add(listener);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		mText = new StyledText(parent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		mText.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				makeDirty(true);
			}
		});

		setText(mInput.getContent());
		makeDirty(false);
	}

	/**
	 * Sets the passed text string as the editor's content
	 * 
	 * @param text
	 */
	public void setText(String text)
	{
		if (! mText.isDisposed())
		{
			mText.setText(text);
			makeDirty(true);
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#getSite()
	 */
	@Override
	public IWorkbenchPartSite getSite()
	{
		return mSite;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#getTitle()
	 */
	@Override
	public String getTitle()
	{
		if (mInput == null)
			return "";
		else
			return mInput.getName();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
	 */
	@Override
	public Image getTitleImage()
	{
		return Activator.getImageDescriptor("icons/editor_basic-text.gif").createImage();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
	 */
	@Override
	public String getTitleToolTip()
	{
		return "Basic text editor.";
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#removePropertyListener(org.eclipse.ui.IPropertyListener)
	 */
	@Override
	public void removePropertyListener(IPropertyListener listener)
	{
		mPropertyListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		mText.setFocus();
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		String path = mInput.getFilePath();
		doSave(path);
		makeDirty(false);
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
		fd.setFilterExtensions(new String[]{"pcc", "c"});
		//fd.setFilterPath(string);
		String path = fd.open();
		if (path != null)
		{
			doSave(path);
			makeDirty(false);
		}
	}

	/**
	 * Returns true iff editor has changed.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isDirty()
	{
		return mDirty;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return true;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
	 */
	@Override
	public boolean isSaveOnCloseNeeded()
	{
		return true;
	}

	/*
	 * Mark this editor as dirty and fire prop change as necessary
	 */
	private void makeDirty(boolean state)
	{
		if (mDirty != state)
		{
			mDirty = state;
			firePropertyChange(PROP_DIRTY);
		}
	}

	/*
	 * Fire property change events of the passed property ID
	 */
	private void firePropertyChange(int propertyID)
	{
		for (IPropertyListener listener : mPropertyListeners)
		{
			listener.propertyChanged(this, propertyID);
		}
	}
	/*
	 * Write the text contents to the supplied path
	 */
	private void doSave(String path)
	{
		String content = mText.getText();
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(new File(path));
			writer.write(content);
		}
		catch (IOException e)
		{
			String message =  "An error occured writing the file to disc.\n\t" + e.getLocalizedMessage();
			Activator.hanldeError("Save Error", message);
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch(IOException e1)
			{
				//NOP
			}
		}	
	}
}
