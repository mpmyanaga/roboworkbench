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

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;

import uk.co.dancowan.robots.srv.editors.output.PicoCLexer;

public class LexerThread extends Thread
{
	private final Model mModel;
	private final String mInput;

	public LexerThread(Model model, String input)
	{
		mInput = input;
		mModel = model;

		setName("picoC Lexer Thread");
	}

	@Override
	public void run()
	{
		PicoCLexer lex = new PicoCLexer(new ANTLRStringStream(mInput));
		if (isInterrupted()) return;
		CommonTokenStream tokens = new CommonTokenStream(lex);
		if (isInterrupted()) return;
		@SuppressWarnings("unchecked")
		List<CommonToken> tokenList = tokens.getTokens();
		if (isInterrupted()) return;
		mModel.createStyles(tokenList);
	}
}
