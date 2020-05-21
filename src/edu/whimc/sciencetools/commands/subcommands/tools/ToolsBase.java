package edu.whimc.sciencetools.commands.subcommands.tools;

import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand;
import edu.whimc.sciencetools.commands.subcommands.AbstractSubCommand;
import edu.whimc.sciencetools.utils.Utils;

public class ToolsBase extends AbstractSubCommand {

	public ToolsBase(ScienceTools plugin) {
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		
		// Command took form "/sciencetools tool"
		if (args.length <= 1) {
			sendUsage(sender);
			return false;
		}
		// TODO Auto-generated method stub
		return false;
	}
	
	private void sendUsage(CommandSender sender) {
		Utils.msg(sender, "&eTool Sub Commands: (&7/sciencetools tool <>)");
		Utils.msg(sender, BaseToolCommand.SubCommand.TOOL.toString());
		
	}

}
