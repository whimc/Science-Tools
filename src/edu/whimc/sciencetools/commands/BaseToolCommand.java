package edu.whimc.sciencetools.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;

public class BaseToolCommand implements CommandExecutor {

	private static enum SubCommand {
		JS("js - Run interpreted JavaScript"),
		TOOLS("tools - Manage the science tools"),
		CONVERSIONS("conversions - Manage unit conversions"),
		RELOAD("reload - Reload the plugin");
		
		private String usage;
		
		private SubCommand(String usage) {
			this.usage = usage;
		}
	}
	
	private ScienceTools plugin;
	
	public BaseToolCommand(ScienceTools plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length == 0) {
			sendSubCommands(sender);
			return false;
		}
		
		SubCommand subCmd = SubCommand.valueOf(args[0].toUpperCase());
		if (subCmd == null) {
			sendSubCommands(sender);
			return false;
		}
		
		if (subCmd == SubCommand.JS) {
			if (args.length == 1) {
				Utils.msg(sender, "/sciencetools js [expr]");
				Utils.msg(sender, "Custom syntax:");
				Utils.msg(sender, " {X}, {Y}, {Z}, rand(min, max), randInt(min, max), min(a, b), max(a, b)");
				return false;
			}
			
			StringBuilder builder = new StringBuilder();
			for (int ind = 1; ind < args.length; ind++) {
				builder.append(args[ind]).append(" ");
			}
			
			String exp = builder.toString().trim();
			if (sender instanceof Player) {
				exp = plugin.getToolManager().fillIn(builder.toString().trim(), (Player) sender);
			}
			Object res = Utils.executeExpressionDebug(sender, exp, true);
			
			if (res == null) {
				return false;
			}
			
			Utils.msg(sender, res.toString());
			
			return true;
		}
		
		if (subCmd == SubCommand.RELOAD) {
			Utils.setDebugReceiver(sender);
			plugin.reloadScienceTools();
			Utils.setDebugReceiver(null);
			Utils.msg(sender, "&aReloaded config!");
		}
		
		return false;
	}
	
	
	private void sendSubCommands(CommandSender sender) {
		for (SubCommand subCmd : SubCommand.values()) {
			Utils.msg(sender, "/sciencetools " + subCmd.usage);
		}
	}
	
}
