package uk.co.dancowan.robots.ui.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;

import uk.co.dancowan.robots.ui.editors.StyledTextSource;

public class CutStyledTextAction extends Action
{
	private StyledTextSource mSource;

	public void setEditor(IEditorPart newEditor)
	{
		if (newEditor instanceof StyledTextSource)
			mSource = (StyledTextSource) newEditor;
		else
			mSource = null;
	}

	public void run()
	{
		mSource.getText().cut();
	}
}
