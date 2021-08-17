package edu.whimc.sciencetools.commands;

import edu.whimc.sciencetools.commands.subcommands.*;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main handler for the "/sciencetools" root command.
 */
public class ScienceToolCommand implements CommandExecutor, TabCompleter {

    private final Map<String, AbstractSubCommand> subCommands = new HashMap<>();

    /**
     * Constructs the ScienceTools command and adds all subcommands.
     */
    public ScienceToolCommand() {
        subCommands.put("history", new History());
        subCommands.put("js", new JSInterpreter());
        subCommands.put("measure", new Measure());
        subCommands.put("reload", new Reload());
        subCommands.put("validate", new Validate());
    }

    /**
     * {@inheritDoc}
     *
     * Runs the provided subcommand if it is valid.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // if no arguments provided, send user list of subcommands
        if (args.length == 0) {
            sendSubCommands(sender);
            return false;
        }

        // if argument invalid subcommand, send user list of subcommands
        AbstractSubCommand subCmd = subCommands.getOrDefault(args[0].toLowerCase(), null);
        if (subCmd == null) {
            sendSubCommands(sender);
            return false;
        }

        try {
            return subCmd.executeCommand(sender, args);
        } catch (CommandError error) {
            Utils.msg(sender, error.getMessage());
            if (error.isSendUsage()) {
                Utils.msg(sender, subCmd.getUsage());
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Handles auto-completion for the subcommand.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String @NotNull [] args) {
        String hint = args[0].toLowerCase();
        if (args.length <= 1) {
            return subCommands.keySet()
                    .stream()
                    .filter(v -> v.startsWith(hint))
                    .sorted()
                    .collect(Collectors.toList());
        }

        AbstractSubCommand subCmd = subCommands.getOrDefault(hint, null);
        if (subCmd == null) {
            return Arrays.asList();
        }

        return subCmd.executeTab(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    /**
     * Displays the full usage of each sub command to the sender.
     *
     * @param sender The command's sender.
     */
    private void sendSubCommands(CommandSender sender) {
        for (AbstractSubCommand subCommand : subCommands.values()) {
            Utils.msg(sender, subCommand.fullDescription());
        }
    }

}
