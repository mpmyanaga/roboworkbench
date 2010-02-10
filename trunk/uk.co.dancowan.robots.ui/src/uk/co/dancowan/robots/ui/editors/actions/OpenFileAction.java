package uk.co.dancowan.robots.ui.editors.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.utils.FileUtils;

public class OpenFileAction extends Action
{
	/**
	 * C'tor, sets action's look and feel.
	 */
	public OpenFileAction()
	{
		setToolTipText("Open a file");
		setImageDescriptor(Activator.getImageDescriptor("icons/action_open.gif"));
		setDisabledImageDescriptor(Activator.getImageDescriptor("icons/action_open_dis.gif"));
		setText("Open");
	}

	/**
	 * Opens a file browser and then attempts to open the selected file.
	 */
	public void run()
	{
		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);

		File file = new File(fd.open());
		if (file != null)
		{
			FileUtils.openFile(file);
		}
	}
}
