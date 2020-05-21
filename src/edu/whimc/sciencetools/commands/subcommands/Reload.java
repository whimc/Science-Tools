package edu.whimc.sciencetools.commands.subcommands;

import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;

public class Reload extends AbstractSubCommand {

	public Reload(ScienceTools plugin) {
		super(plugin);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Utils.setDebugReceiver(sender);
		plugin.reloadScienceTools();
		Utils.setDebugReceiver(null);
		Utils.msg(sender, "&aoScience Tools config reload!");
		return false;
	}

}
