package uk.co.dancowan.robots.hal.core.commands;

import java.util.ArrayList;
import java.util.List;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.CommandQ;

public class CommandChain extends AbstractCommand
{
	private List<Command> mCommands;

	public CommandChain()
	{
		super(true);
		mCommands = new ArrayList<Command>();
	}

	public void clearCommands()
	{
		mCommands.clear();
	}

	public void addCommand(Command cmd)
	{
		mCommands.add(cmd);
	}

	public void removeCommand(Command cmd)
	{
		mCommands.remove(cmd);
	}

	public void execute(CommandQ cmdQ)
	{
		for (Command cmd : mCommands)
		{
			cmd.execute(cmdQ);
			new PauseQCmd(100).execute(cmdQ);
		}
	}

	@Override
	protected String getCommandString()
	{
		return "";
	}
}
