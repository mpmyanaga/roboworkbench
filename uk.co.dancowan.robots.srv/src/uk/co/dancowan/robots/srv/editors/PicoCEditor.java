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
package uk.co.dancowan.robots.srv.editors;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.ui.editors.BasicTextEditor;

/**
 * Class adds picoC tokenising and highlighting for BasicTextEditor
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class PicoCEditor extends BasicTextEditor
{
	private Model mModel;

	/**
	 * Sets the passed text string as the editor's content
	 * 
	 * @param text
	 */
	@Override
	public void setText(String text)
	{
		super.setText(text);
		if (mModel != null)
			mModel.setText(text);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		mModel = new Model(getText());
		//HACK to set string in the text a second time once the model is created
		mModel.setText(getText().getText());
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
	 */
	@Override
	public Image getTitleImage()
	{
		return SRVActivator.getImageDescriptor("icons/editor_picoC.gif").createImage();
	}
}
