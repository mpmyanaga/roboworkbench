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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import uk.co.dancowan.robots.ui.utils.ColourManager;

public class Token
{
	private StyleRange mStyle;
	private String mContent;
	private int mOffset;
	private int mType;

	public Token(String content, int offset, int type)
	{
		mContent = content;
		mOffset = offset;
		mType = type;
		mStyle = createStyle();
	}

	public String getContent()
	{
		return mContent;
	}

	public int getOffset()
	{
		return mOffset;
	}

	public int getLength()
	{
		return mContent.length();
	}

	public void move(int shift)
	{
		mOffset = mOffset + shift;
		mStyle = getStyle();
	}

	public StyleRange createStyle()
	{
		int len = 0;
		switch (mType)
		{
			case CTokenizer.CR : len = 2; break;
			case CTokenizer.TAB : len = 4; break;
			default : len = mContent.length();
		}
		return new StyleRange(mOffset, len, getForeground(), getBackground());	
	}

	public StyleRange getStyle()
	{
		return mStyle;
	}

	private Color getForeground()
	{
		switch (mType)
		{
			case CTokenizer.KEYWORD : return ColourManager.getColour(SWT.COLOR_GREEN);
			case CTokenizer.SYMBOL : return ColourManager.getColour(SWT.COLOR_DARK_GRAY);
			default : return ColourManager.getColour(SWT.COLOR_BLACK);
		}
	}

	private Color getBackground()
	{
		switch (mType)
		{
			//case CTokenizer.SPACE : return ColourManager.getColour(SWT.COLOR_GRAY);
			default : return ColourManager.getColour(SWT.COLOR_WHITE);
		}
	}
}
