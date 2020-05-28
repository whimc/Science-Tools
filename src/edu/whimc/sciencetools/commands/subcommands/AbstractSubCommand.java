package edu.whimc.sciencetools.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand.SubCommand;
import edu.whimc.sciencetools.utils.Utils;

public abstract class AbstractSubCommand {

	protected ScienceTools plugin;
	private SubCommand subCmd;
	
	public AbstractSubCommand(ScienceTools plugin, SubCommand subCmd) {
		this.plugin = plugin;
		this.subCmd = subCmd;
	}
	
	protected abstract boolean routine(CommandSender sender, String[] args);

	protected void notEnoughArgs(CommandSender sender) {
		return;
	}
	
	public boolean execute(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission(subCmd.getPermission().toString())) {
			Utils.msg(sender, "&cYou are missing the permission \"&4" + subCmd.getPermission() + "&c\" to use this command!");
			return false;
		}
		
		if (args.length < subCmd.minArgs()) {
			List<String> missedCmdsList = new ArrayList<>();
			for (int ind = args.length - 1; ind < subCmd.getArgs().size(); ind++) {
				missedCmdsList.add(subCmd.formattedArg(ind));
			}
			
			String missedCmds = String.join("&7, ", missedCmdsList);
			
			Utils.msg(sender, "&cMissing argument" + (missedCmdsList.size() > 1 ? "s" : "") + ": " + missedCmds);
			Utils.msg(sender, "&cUsage: " + subCmd.getUsage());
			notEnoughArgs(sender);
			
			return false;
		}
		
		return routine(sender, Arrays.copyOfRange(args, 1, args.length));
	}
	
}
