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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

import uk.co.dancowan.robots.ui.utils.TextUtils;

public class Model
{
	private final StyledText mTarget;
	private final List<String> mLines;
	private final CTokenizer mTokenizer;

	public Model(StyledText text)
	{
		mTarget = text;
		mLines = new ArrayList<String>();
		mTokenizer = new CTokenizer();
	}

	public void setText(String input)
	{
		String[] lines = input.split(TextUtils.CR);
		for (String line : lines)
		{
			mLines.add(line);
		}
		createStyles(input);
	}

	private void createStyles(String line)
	{
		List<Token> tokens = mTokenizer.tokenize(line);
		for (Token token : tokens)
		{
			System.err.println("Token '" + token.getContent() + "' offset  " + token.getOffset() + " length " + token.getLength() + " style " + token.getStyle());
			StyleRange range = token.getStyle();
			if (range != null)
				mTarget.setStyleRange(range);
		}
	}
}
