package edu.whimc.sciencetools.commands.subcommands;

import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand.SubCommand;
import edu.whimc.sciencetools.utils.Utils;

public class Reload extends AbstractSubCommand {

	public Reload(ScienceTools plugin, SubCommand subCmd) {
		super(plugin, subCmd);
	}

	@Override
	public boolean routine(CommandSender sender, String[] args) {
		Utils.setDebugReceiver(sender);
		plugin.reloadScienceTools();
		Utils.setDebugReceiver(null);
		Utils.msg(sender, "&a&oScience Tools config reload!");
		return false;
	}

}
