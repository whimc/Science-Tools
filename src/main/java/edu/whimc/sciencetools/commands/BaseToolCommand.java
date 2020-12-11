package edu.whimc.sciencetools.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.subcommands.AbstractSubCommand;
import edu.whimc.sciencetools.commands.subcommands.JSInterpreter;
import edu.whimc.sciencetools.commands.subcommands.Reload;
import edu.whimc.sciencetools.commands.subcommands.Validate;
import edu.whimc.sciencetools.commands.subcommands.conversions.ConversionsBase;
import edu.whimc.sciencetools.commands.subcommands.tools.ToolsBase;
import edu.whimc.sciencetools.utils.Utils;

// Plugin management commands, see sciencetools.utils for actual commands players use to make observations

public class BaseToolCommand implements CommandExecutor {

	public static enum SubCommand {
		JS("js", Arrays.asList("expr..."), "Run interpreted JavaScript", Permission.ADMIN),
		TOOL("tool", null, "Manage the science tools", Permission.ADMIN),
		CONVERSION("conversion", null, "Manage unit conversions", Permission.ADMIN),
		VALIDATE("validate", Arrays.asList("tool", "player"), "Have a user validate a tool", Permission.ADMIN),
		RELOAD("reload", null, "Reload the plugin's config", Permission.ADMIN);
		
		private String command;
		private List<String> args;
		private String description;
		private Permission perm;
		
		private SubCommand(String command, List<String> args, String description, Permission perm) {
			this.command = command;
			this.args = args;
			this.description = description;
			this.perm = perm;
		}
		
		public String fullDescription() {
			return this.toString() + " &7- " + description;
		}
		
		public List<String> getArgs() {
			return args;
		}
		
		public String formattedArg(int ind) {
			if (args == null || ind < 0 || ind >= args.size()) {
				return "";
			}
			
			return "&7<&b" + args.get(ind) + "&7>";
		}
		
		public String getUsage() {
			return "&e/sciencetools " + command + " " + String.join(" ", IntStream
					.range(0, args.size())
					.mapToObj(ind -> formattedArg(ind))
					.collect(Collectors.toList()));
		}
		
		@Override
		public String toString() {
			return "&e&o/sciencetools " + command; 
		}
		
		public Permission getPermission() {
			return perm;
		}
		
		public int minArgs() {
			return 1 + (args == null ? 0 : args.size()); 
		}
		
		public static SubCommand match(String str) {
			for (SubCommand subCmd : SubCommand.values()) {
				if (subCmd.command.equalsIgnoreCase(str)) {
					return subCmd;
				}
			}
			return null;
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
		subCommands.put(SubCommand.JS, new JSInterpreter(plugin, SubCommand.JS));
		subCommands.put(SubCommand.TOOL, new ToolsBase(plugin, SubCommand.TOOL));
		subCommands.put(SubCommand.CONVERSION, new ConversionsBase(plugin, SubCommand.CONVERSION));
		subCommands.put(SubCommand.VALIDATE, new Validate(plugin, SubCommand.VALIDATE));
		subCommands.put(SubCommand.RELOAD, new Reload(plugin, SubCommand.RELOAD));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length == 0) {
			sendSubCommands(sender);
			return false;
		}
		
		SubCommand subCmd = SubCommand.match(args[0]);
		if (subCmd == null) {
			sendSubCommands(sender);
			return false;
		}
		
		return subCommands.get(subCmd).execute(sender, args);
	}
	
	
	private void sendSubCommands(CommandSender sender) {
		for (SubCommand subCmd : SubCommand.values()) {
			Utils.msg(sender, subCmd.fullDescription());
		}
	}
	
}
