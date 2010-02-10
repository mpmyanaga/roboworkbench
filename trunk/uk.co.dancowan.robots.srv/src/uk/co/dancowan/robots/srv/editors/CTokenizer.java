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

import uk.co.dancowan.robots.ui.utils.TextUtils;

public class CTokenizer
{
	public static final int WORD = 1;
	public static final int KEYWORD = 2;
	public static final int SYMBOL = 4;
	public static final int SPACE = 8;
	public static final int CR = 16;
	public static final int COMMENT = 32;
	public static final int TAB = 64;

	private static String[] KEYWORDS = new String[]
	{
		"for",
		"if",
		"else",
		"while",
		"do",
		"return",
		"printf",
		"struct",
		"switch",
		"case",
		"char",
		"int",
		"void",
		"unsigned",
		"#define",
		"#include",
		"#ifdef",
		"#endif"
	};

	private static String[] SYMBOLS = new String[]
  	{
  		"{",
  		"}",
  		"(",
  		")",
  		"[",
  		"]",
  		"=",
  		"+",
  		"-",
  		"*",
  		">",
  		"<",
  		"\\",
  		";",
  		",",
  		":",
  		"/",
  		"\""
  	};

	private final List<String> mKeywords;
	private final List<String> mSymbols;

	public CTokenizer()
	{
		mKeywords = new ArrayList<String>();
		mSymbols = new ArrayList<String>();

		init(mKeywords, KEYWORDS);
		init(mSymbols, SYMBOLS);
	}

	public List<Token> tokenize(String input)
	{
		List<Token> tokens = new ArrayList<Token>();
		StringBuilder sb = new StringBuilder();
		int mark = 0;
		for (int i = 0; i < input.length(); i ++)
		{
			String c = input.substring(i, i+1);
			if (mSymbols.contains(c))
			{
				if (sb.length() > 0)
				{
					String str = sb.toString();
					tokens.add(new Token(str, mark, getType(str)));
					sb = new StringBuilder();
				}
				tokens.add(new Token(c, i, SYMBOL));
				mark = i + 1;
			}
			else if (input.charAt(i) == ' ' || input.charAt(i) == '	')
			{
				if (sb.length() > 0)
				{
					String str = sb.toString();
					tokens.add(new Token(str, mark, getType(str)));	
					sb = new StringBuilder();
				}
				tokens.add(new Token(c, i, SPACE));
				mark = i + 1;
			}
			else
			{
				sb.append(c);
			}
			if (sb.toString().equals(TextUtils.CR))
			{
				String str = sb.toString();
				tokens.add(new Token(str, mark, getType(str)));	
				sb = new StringBuilder();
				mark = i + 1;
			}
		}
		return tokens;
	}

	private int getType(String token)
	{
		if (mKeywords.contains(token))
			return KEYWORD;
		else if (mSymbols.contains(token))
			return SYMBOL;
		else if (TextUtils.CR.equals(token))
			return CR;
		else
			return WORD;
	}

	private void init(List<String> list, String[] array)
	{
		for (String str : array)
		{
			list.add(str);
		}
	}
}
