package edu.whimc.sciencetools.commands.subcommands;

import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;

public abstract class AbstractSubCommand {

	protected ScienceTools plugin;
	
	public AbstractSubCommand(ScienceTools plugin) {
		this.plugin = plugin;
	}
	
	public AbstractSubCommand() { }
	
	public abstract boolean execute(CommandSender sender, String[] args);
	
}
