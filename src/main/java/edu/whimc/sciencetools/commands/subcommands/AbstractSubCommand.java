package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand.SubCommand;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractSubCommand {

    protected ScienceTools plugin;
    protected SubCommand subCmd;

    public AbstractSubCommand(ScienceTools plugin, SubCommand subCmd) {
        this.plugin = plugin;
        this.subCmd = subCmd;
    }

    protected abstract boolean commandRoutine(CommandSender sender, String[] args);

    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        return Arrays.asList();
    }

    protected void notEnoughArgs(CommandSender sender) {
        return;
    }

    public boolean executeCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(subCmd.getPermission().toString())) {
            Utils.msg(sender, "&cYou are missing the permission \"&4" + subCmd.getPermission() + "&c\" to use this command!");
            return false;
        }

        if (!subCmd.hasEnoughArguments(args.length - 1)) {
            List<String> missedCmdsList = new ArrayList<>();
            for (int ind = args.length - 1; ind < subCmd.getArgs().size(); ind++) {
                missedCmdsList.add(subCmd.formattedArg(ind));
            }
            for (int ind = Math.max(0, args.length - subCmd.getArgs().size() - 1); ind < subCmd.getOptionalArgs().size(); ind++) {
                missedCmdsList.add(subCmd.formattedOptionalArg(ind));
            }

            String missedCmds = String.join("&7, ", missedCmdsList);

            Utils.msg(sender, "&cMissing argument" + (missedCmdsList.size() > 1 ? "s" : "") + ": " + missedCmds);
            if (subCmd.hasOptionalArgs()) {
                Utils.msg(sender, "&cUsages:", "  " + subCmd.getUsage(), "  " + subCmd.getOptionalUsage());
            } else {
                Utils.msg(sender, "&cUsage: " + subCmd.getUsage());
            }
            notEnoughArgs(sender);

            return false;
        }

        return commandRoutine(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public List<String> executeTab(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.subCmd.getPermission().toString()) || args.length > this.subCmd.maxArgs()) {
            return Arrays.asList();
        }
        return tabRoutine(sender, args);
    }

}
