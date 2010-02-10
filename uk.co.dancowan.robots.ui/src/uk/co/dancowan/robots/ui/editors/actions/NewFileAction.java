package uk.co.dancowan.robots.ui.editors.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.utils.FileUtils;

public class NewFileAction extends Action
{
	/**
	 * C'tor, sets action's look and feel.
	 */
	public NewFileAction()
	{
		setToolTipText("Create a new file");
		setImageDescriptor(Activator.getImageDescriptor("icons/action_new-file.gif"));
		setDisabledImageDescriptor(Activator.getImageDescriptor("icons/action_new-file_dis.gif"));
		setText("Open");
	}

	/**
	 * Opens a dialog and then attempts to create the selected file.
	 */
	public void run()
	{
		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
		fd.setText("Chose file location");

		File file = new File(fd.open());
		if (file != null)
		{
			FileUtils.openFile(file);
		}
	}
}
