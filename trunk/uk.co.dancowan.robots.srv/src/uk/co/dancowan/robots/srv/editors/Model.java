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

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import uk.co.dancowan.robots.srv.editors.output.PicoCLexer;
import uk.co.dancowan.robots.ui.utils.ColourManager;

/**
 * Class mediates between the <code>StyledText</code> widget of the editor and the additional
 * editor features such as syntax highlighting.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class Model
{
	private final StyledText mTarget;

	private Thread mLexerThread;

	/**
	 * C'tor
	 * @param text the editor's StyledText widget
	 */
	public Model(StyledText text)
	{
		mTarget = text;
	}

	/**
	 * Sets the passed text in the model and starts a tokenizer thread
	 * for syntax highlighting
	 * 
	 * @param input
	 */
	public void setText(String input)
	{
		if (mLexerThread != null)
		{
			mLexerThread.interrupt();
			mLexerThread = null;
		}
		try
		{
			mLexerThread = new LexerThread(this, input);
			mLexerThread.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Called from the tokenizing thread to set <code>StyleRange<code<code>> objects
	 * in the <code>StyledText</code> widget of the editor
	 * 
	 * @param tokens
	 */
	public void createStyles(List<CommonToken> tokens)
	{
		for (final CommonToken token : tokens)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				@Override
				public void run()
				{
					StyleRange range = createStyle(token);
					mTarget.setStyleRange(range);
				}
			});
		}
		mLexerThread = null;
	}

	/*
	 * Create a StyledRange for the given CommonToken
	 */
	private StyleRange createStyle(CommonToken token)
	{
		int start = token.getStartIndex();
		int length = (token.getStopIndex() + 1) - start;
		Color colour;
		switch (token.getType())
		{
			case PicoCLexer.ID: colour = ColourManager.getColour(SWT.COLOR_BLUE) ;break;// ident
			case PicoCLexer.COMMENT: colour = ColourManager.getColour(SWT.COLOR_GRAY) ;break;// comment
			case PicoCLexer.STRING: colour = ColourManager.getColour(SWT.COLOR_DARK_GREEN) ;break;// string
			case PicoCLexer.KEYWORD: colour = ColourManager.getColour(SWT.COLOR_DARK_BLUE) ;break;// keyword
			default : colour = ColourManager.getColour(SWT.COLOR_BLACK);
		}
		return new StyleRange(start, length, colour, ColourManager.getColour(SWT.COLOR_WHITE));
	}
}
