/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (C) 2009  Dan Cowan
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
package uk.co.dancowan.robots.ui.views.commandConsole;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uk.co.dancowan.robots.ui.utils.TextUtils;

/**
 *
 * @author Dan Cowan
 * @ since version 1.0.0
 */
public class CommandLineShell
{
	public CommandLineShell(final CommandLine commandLine, final StyledText targetControl, final Point absLocation)
	{
		final Rectangle bounds = targetControl.getBounds();
		final Shell shell = new Shell(Display.getCurrent(), SWT.NO_TRIM);
		shell.setLayout(new FillLayout());
		shell.setBounds(new Rectangle(absLocation.x + 8, absLocation.y + bounds.height + 36, bounds.width - 19, 18));
		final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		text.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.keyCode == 27) // escape - exit without action
				{
					shell.close();
					shell.dispose();
				}
				else if (e.keyCode == 13) // enter - execute command
				{
					commandLine.setText(text.getText());
					commandLine.execute();
					targetControl.append(text.getText() + TextUtils.CR);
					shell.close();
					shell.dispose();
				}
				else if (e.keyCode == 16777217) // up cursor key - do history
				{
					String str = commandLine.last();
					if (str != null)
						text.setText(str);
				}
				else if (e.keyCode == 16777218) // up cursor key - do history
				{
					String str = commandLine.next();
					if (str != null)
						text.setText(str);
				}
			}
		});
		text.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				shell.close();
				shell.dispose();
			}
		});
		shell.open();
	}
}
