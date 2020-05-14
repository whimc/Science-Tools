package edu.whimc.sciencetools.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.subcommands.AbstractSubCommand;
import edu.whimc.sciencetools.commands.subcommands.JSInterpreter;
import edu.whimc.sciencetools.commands.subcommands.Reload;
import edu.whimc.sciencetools.commands.subcommands.conversions.ConversionsBase;
import edu.whimc.sciencetools.commands.subcommands.tools.ToolsBase;
import edu.whimc.sciencetools.utils.Utils;

public class BaseToolCommand implements CommandExecutor {

	public static enum SubCommand {
		JS("Run interpreted JavaScript"),
		TOOLS("Manage the science tools"),
		CONVERSIONS("Manage unit conversions"),
		RELOAD("Reload the plugin's config");
		
		private String description;
		
		private SubCommand(String usage) {
			this.description = usage;
		}
		
		public String fullUsage() {
			return "&e&o/sciencetools " + this.name().toLowerCase() + " &7- " + description;
		}
	}
	
	public static enum Permission {
		ADMIN("admin"),
		USER("user");
		
		private String perm;
		
		private Permission(String perm) {
			this.perm = perm;
		}
		
		@Override
		public String toString() {
			return "sciencetools." + perm;
		}
		
	}
	
	private Map<SubCommand, AbstractSubCommand> subCommands;
	
	public BaseToolCommand(ScienceTools plugin) {
		subCommands = new HashMap<>();
		subCommands.put(SubCommand.JS, new JSInterpreter(plugin));
		subCommands.put(SubCommand.TOOLS, new ToolsBase(plugin));
		subCommands.put(SubCommand.CONVERSIONS, new ConversionsBase(plugin));
		subCommands.put(SubCommand.RELOAD, new Reload());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!sender.hasPermission(Permission.ADMIN.toString())) {
			Utils.msg(sender, "&cYou are missing the permission \"&4" + Permission.ADMIN + "&c\" to use this command!");
		}
		
		if (args.length == 0) {
			sendSubCommands(sender);
			return false;
		}
		
		SubCommand subCmd = SubCommand.valueOf(args[0].toUpperCase());
		if (subCmd == null) {
			sendSubCommands(sender);
			return false;
		}
		
		return subCommands.get(subCmd).execute(sender, args);
	}
	
	
	public static void sendSubCommands(CommandSender sender) {
		for (SubCommand subCmd : SubCommand.values()) {
			Utils.msg(sender, subCmd.fullUsage());
		}
	}
	
}
