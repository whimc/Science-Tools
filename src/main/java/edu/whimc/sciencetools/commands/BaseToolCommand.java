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
import edu.whimc.sciencetools.utils.Utils;

// Plugin management commands, see sciencetools.utils for actual commands players use to make observations

public class BaseToolCommand implements CommandExecutor {

	public static enum SubCommand {
		JS("js", Arrays.asList("expr..."), null, "Run interpreted JavaScript", Permission.ADMIN),
		TOOL("tool", null, null, "Manage the science tools", Permission.ADMIN),
		CONVERSION("conversion", null, null, "Manage unit conversions", Permission.ADMIN),
		VALIDATE("validate", Arrays.asList("tool", "player"), Arrays.asList("world", "x", "y", "z"), "Have a user validate a tool", Permission.ADMIN),
		RELOAD("reload", null, null, "Reload the plugin's config", Permission.ADMIN);

		private String command;
		private List<String> args;
		private List<String> optionalArgs;
		private String description;
		private Permission perm;

		private SubCommand(String command, List<String> args, List<String> optionalArgs, String description, Permission perm) {
			this.command = command;
			this.args = args == null ? Arrays.asList() : args;
			this.optionalArgs = optionalArgs == null ? Arrays.asList() : optionalArgs;
			this.description = description;
			this.perm = perm;
		}

		public String fullDescription() {
			return this.toString() + " &7- " + this.description;
		}

		public List<String> getArgs() {
			return this.args;
		}

		public List<String> getOptionalArgs() {
		    return this.optionalArgs;
		}

		public boolean hasOptionalArgs() {
		    return this.optionalArgs.size() != 0;
		}

		public boolean hasEnoughArguments(int argsLen) {
		    if (argsLen == this.args.size()) {
		        return true;
		    }
		    return argsLen >= this.args.size() + this.optionalArgs.size();
		}

		public String formattedArg(int ind) {
			if (args == null || ind < 0 || ind >= args.size()) {
				return "";
			}

			return "&7<&b" + args.get(ind) + "&7>";
		}

		public String formattedOptionalArg(int ind) {
		    if (optionalArgs == null || ind < 0 || ind >= optionalArgs.size()) {
		        return "";
		    }

		    return "&7[&b" + optionalArgs.get(ind) + "&7]";
		}

		public String getUsage() {
		    return "&e/sciencetools " + command + " " + String.join(" ", IntStream
					.range(0, args.size())
					.mapToObj(this::formattedArg)
					.collect(Collectors.toList()));
		}

		public String getOptionalUsage() {
		    return getUsage() + " " + String.join(" ", IntStream
                    .range(0, optionalArgs.size())
                    .mapToObj(this::formattedOptionalArg)
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
			return args.size();
		}

		public int maxArgs() {
		    return minArgs() + optionalArgs.size();
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
//		subCommands.put(SubCommand.TOOL, new ToolsBase(plugin, SubCommand.TOOL));
//		subCommands.put(SubCommand.CONVERSION, new ConversionsBase(plugin, SubCommand.CONVERSION));
		subCommands.put(SubCommand.VALIDATE, new Validate(plugin, SubCommand.VALIDATE));
		subCommands.put(SubCommand.RELOAD, new Reload(plugin, SubCommand.RELOAD));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 0) {
			sendSubCommands(sender);
			return false;
		}

		AbstractSubCommand subCmd = subCommands.getOrDefault(SubCommand.match(args[0]), null);
		if (subCmd == null) {
			sendSubCommands(sender);
			return false;
		}

		return subCmd.executeCommand(sender, args);
	}


	private void sendSubCommands(CommandSender sender) {
		for (SubCommand subCmd : this.subCommands.keySet()) {
			Utils.msg(sender, subCmd.fullDescription());
		}
	}

}
