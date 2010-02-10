package uk.co.dancowan.robots.ui.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;

public class SaveAsEditorAction extends Action
{
	private IEditorPart mEditor;

	public void setEditor(IEditorPart newEditor)
	{
		mEditor = newEditor;
	}

	public void run()
	{
		mEditor.doSaveAs();
	}
}
