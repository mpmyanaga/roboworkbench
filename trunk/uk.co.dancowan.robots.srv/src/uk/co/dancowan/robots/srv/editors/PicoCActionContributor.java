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

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

import uk.co.dancowan.robots.srv.SRVActivator;
import uk.co.dancowan.robots.srv.hal.commands.picoc.BufferContentProvider;
import uk.co.dancowan.robots.srv.hal.commands.picoc.ClearFlashCmd;
import uk.co.dancowan.robots.srv.hal.commands.picoc.EscCmd;
import uk.co.dancowan.robots.srv.hal.commands.picoc.ReadFlashCmd;
import uk.co.dancowan.robots.srv.hal.commands.picoc.RunFlashCmd;
import uk.co.dancowan.robots.srv.hal.commands.picoc.SendCmd;
import uk.co.dancowan.robots.srv.hal.commands.picoc.ShowFlashCmd;
import uk.co.dancowan.robots.srv.hal.commands.picoc.WriteFlashCmd;
import uk.co.dancowan.robots.ui.editors.actions.CommandAction;
import uk.co.dancowan.robots.ui.editors.actions.CopyStyledTextAction;
import uk.co.dancowan.robots.ui.editors.actions.CutStyledTextAction;
import uk.co.dancowan.robots.ui.editors.actions.PasteStyledTextAction;
import uk.co.dancowan.robots.ui.editors.actions.SaveAsEditorAction;
import uk.co.dancowan.robots.ui.editors.actions.SaveEditorAction;

/**
 * ActionBarContributor attaches local file actions to global handlers and adds additional
 * local actions to the toolbar.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class PicoCActionContributor extends EditorActionBarContributor implements BufferContentProvider
{
	private static final String TRANSMIT_GROUP = "picoc.transmit";
	private static final String EXECUTE_GROUP = "picoc.execute";

	private PicoCEditor mEditor;

	private SaveEditorAction mSave;
	private SaveAsEditorAction mSaveAs;
	private CutStyledTextAction mCut;
	private CopyStyledTextAction mCopy;
	private PasteStyledTextAction mPaste;

	private CommandAction mSend;
	private CommandAction mRead;
	private CommandAction mWrite;
	private CommandAction mClear;
	private CommandAction mList;
	private CommandAction mRun;
	private CommandAction mStop;

	/**
	 * C'tor
	 */
	public PicoCActionContributor()
	{
		mSave = new SaveEditorAction();
		mSaveAs = new SaveAsEditorAction();
		mCut = new CutStyledTextAction();
		mCopy = new CopyStyledTextAction();
		mPaste = new PasteStyledTextAction();

		mSend = new CommandAction(new SendCmd(), "Send", "Send code to SRV flash buffer", SRVActivator.getImageDescriptor("icons/cmd_picoC-send.gif"));
		mRead = new CommandAction(new ReadFlashCmd(), "Read", "Read code from SRV flash sectors into buffer", SRVActivator.getImageDescriptor("icons/cmd_picoC-read.gif"));
		mWrite = new CommandAction(new WriteFlashCmd(), "Write", "Write code from buffer into SRV flash sectors", SRVActivator.getImageDescriptor("icons/cmd_picoC-write.gif"));
		mClear = new CommandAction(new ClearFlashCmd(), "Clear", "Clear the flash buffer", SRVActivator.getImageDescriptor("icons/cmd_clear.gif"));
		mList = new CommandAction(new ShowFlashCmd(), "List", "List flash buffer to terminal", SRVActivator.getImageDescriptor("icons/cmd_picoC-list.gif"));
		mRun = new CommandAction(new RunFlashCmd(), "Run", "Start picoC  interpreter", SRVActivator.getImageDescriptor("icons/cmd_picoC-run.png"));
		mStop = new CommandAction(new EscCmd(), "Stop", "Stop picoC interpreter", SRVActivator.getImageDescriptor("icons/cmd_stop.png"));
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActiveEditor(IEditorPart targetEditor)
	{
		mEditor = (PicoCEditor) targetEditor;
		updateActions();
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#init(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void init(IActionBars bars)
	{
		super.init(bars);

		bars.clearGlobalActionHandlers();
		bars.setGlobalActionHandler(ActionFactory.SAVE.getId(), mSave);
		bars.setGlobalActionHandler(ActionFactory.SAVE_AS.getId(), mSaveAs);
		bars.setGlobalActionHandler(ActionFactory.CUT.getId(), mCut);
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), mCopy);
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), mPaste);

		IToolBarManager manager = bars.getToolBarManager();
		manager.add(new GroupMarker(TRANSMIT_GROUP));
		manager.appendToGroup(TRANSMIT_GROUP, mSend);
		manager.appendToGroup(TRANSMIT_GROUP, mRead);
		manager.appendToGroup(TRANSMIT_GROUP, mWrite);
		manager.appendToGroup(TRANSMIT_GROUP, mClear);
		manager.appendToGroup(TRANSMIT_GROUP, mList);

		manager.add(new GroupMarker(EXECUTE_GROUP));
		manager.appendToGroup(EXECUTE_GROUP, mRun);
		manager.appendToGroup(EXECUTE_GROUP, mStop);

		bars.updateActionBars();
	}

	/**
	 * Implementation of the <code>BufferContentProvider</code> interface.
	 * 
	 * <p>Asks the associated editor for its styled text widget's content.</p>
	 * 
	 * @return String the content of the picoC editor
	 */
	public String getText()
	{
		return mEditor.getText().getText();
	}

	/*
	 * Set the new editor as the action's target
	 */
	private void updateActions()
	{
		SendCmd send = (SendCmd) mSend.getCommand();
		send.setProvider(this);

		mSave.setEditor(mEditor);
		mSaveAs.setEditor(mEditor);
		mCut.setEditor(mEditor);
		mCopy.setEditor(mEditor);
		mPaste.setEditor(mEditor);
	}
}
