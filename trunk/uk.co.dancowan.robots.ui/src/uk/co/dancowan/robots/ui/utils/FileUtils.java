package uk.co.dancowan.robots.ui.utils;

import java.io.File;

import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;

import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.editors.FileEditorInput;

public class FileUtils
{
	public static void openFile(File file)
	{
		boolean external = false;
		try
		{
			IWorkbench workbench = Activator.getDefault().getWorkbench();
			IEditorDescriptor editor = workbench.getEditorRegistry().getDefaultEditor(file.getName());
			
			// try an external editor
			if (editor == null)
			{
				external = true;
				editor = workbench.getEditorRegistry().findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
			}

			// try the Basic text editor
			if (editor == null)
			{
				external = true;
				editor = workbench.getEditorRegistry().getDefaultEditor(".txt");
			}

			if (editor != null)
			{
				String editorId = editor.getId();
				IEditorInput input = new FileEditorInput(file);
				workbench.getActiveWorkbenchWindow().getActivePage().openEditor(input, editorId);
			}
		}
		catch (PartInitException e)
		{
			String message = external ? "" : "No editor could be found to handle this file.";
			Activator.hanldeError("Editor Error", message);
		}
	}
}
