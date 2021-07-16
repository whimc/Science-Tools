package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is a template for all SubCommands.
 */
public abstract class AbstractSubCommand {

    /**
     * Permission levels that players can have.
     */
    public enum Permission {
        ADMIN("admin"),
        USER("user");

        private final String perm;

        /**
         * Constructs a permission level with the specified name.
         *
         * @param perm the name of the permission
         */
        Permission(String perm) {
            this.perm = perm;
        }

        /**
         * Converts the permission to a String in the form: "sciencetools.perm".
         *
         * @return the permission as a String
         */
        @Override
        public @NotNull String toString() {
            return "sciencetools." + perm;
        }
    }

    /* The command's name. */
    private final String command;
    /* The arguments required to run the command. */
    private final List<String> args;
    /* Any additional arguments for the command. */
    private final List<String> optionalArgs;
    /* A description of what the command does. */
    private final String description;
    /* The permission level required to use the command. */
    private final Permission permission;

    /**
     * Constructs a command with the given arguments.
     *
     * @param command the name of the command
     * @param args the arguments required
     * @param optionalArgs any optional arguments
     * @param description a description of what the command does
     * @param permission the required permission level to use the command
     */
    public AbstractSubCommand(String command, List<String> args, List<String> optionalArgs, String description, Permission permission) {
        this.command = command;
        this.args = args == null ? Arrays.asList() : args;
        this.optionalArgs = optionalArgs == null ? Arrays.asList() : optionalArgs;
        this.description = description;
        this.permission = permission;
    }

    /**
     * Runs when command is run.
     *
     * @param sender the command's sender
     * @param args the arguments passed
     * @return whether the command has been run or not
     */
    protected abstract boolean commandRoutine(CommandSender sender, String[] args);

    /**
     * Runs on tab press.
     *
     * @param sender the command's sender
     * @param args the arguments passed
     * @return an empty List of Strings
     */
    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        return Arrays.asList();
    }

    /**
     * Runs when command is not passed enough arguments.
     *
     * @param sender the command's sender
     */
    protected void notEnoughArgs(CommandSender sender) {
        return;
    }

    /**
     * Gets the full description of the SubCommand.
     *
     * @return the full description fo the SubCommand
     */
    public @NotNull String fullDescription() {
        return this + " &7- " + this.description;
    }

    /**
     * Checks if SubCommand has any optional arguments.
     *
     * @return whether or not the SubCommand has optional arguments
     */
    public boolean hasOptionalArgs() {
        return this.optionalArgs.size() != 0;
    }

    /**
     * Checks if the SubCommand has been passed enough arguments.
     *
     * @param argsLen the number (as an integer) of passed in arguments
     * @return whether or not the SubCommand has been passed enough arguments
     */
    public boolean hasEnoughArguments(int argsLen) {
        if (argsLen == this.args.size()) {
            return true;
        }
        return argsLen >= this.args.size() + this.optionalArgs.size();
    }

    /**
     * Formats the argument.
     *
     * @param ind the index of the argument
     * @return the formatted argument
     */
    public @NotNull String formattedArg(int ind) {
        // ensure argument index is valid
        if (args == null || ind < 0 || ind >= args.size()) {
            return "";
        }

        return "&7<&b" + args.get(ind) + "&7>";
    }

    /**
     * Formats the optional argument.
     *
     * @param ind the index of the optional argument
     * @return the formatted optional argument
     */
    public @NotNull String formattedOptionalArg(int ind) {
        // ensure the optional argument index is valid
        if (optionalArgs == null || ind < 0 || ind >= optionalArgs.size()) {
            return "";
        }

        return "&7[&b" + optionalArgs.get(ind) + "&7]";
    }

    /**
     * Gets the command's usage.
     *
     * @return a String specifying how the command is used.
     */
    public @NotNull String getUsage() {
        return "&e/sciencetools " + command + " " + String.join(" ", IntStream
                .range(0, args.size())
                .mapToObj(this::formattedArg)
                .collect(Collectors.toList()));
    }

    /**
     * Gets the command's optional usage.
     *
     * @return a String specifying how the command is used.
     */
    public @NotNull String getOptionalUsage() {
        return getUsage() + " " + String.join(" ", IntStream
                .range(0, optionalArgs.size())
                .mapToObj(this::formattedOptionalArg)
                .collect(Collectors.toList()));
    }

    /**
     * Converts the SubCommand into a String.
     *
     * @return the SubCommand as a String.
     */
    @Override
    public @NotNull String toString() {
        return "&e&o/sciencetools " + command;
    }

    /**
     * Gets the minimum amount of arguments required to run the command.
     *
     * @return the minimum number of arguments required to run the command
     */
    public int getMinArgs() {
        return args.size();
    }

    /**
     * Gets the maximum number of arguments that can be passed into the command.
     *
     * @return the maximum number of arguments that can be passed into the command
     */
    public int getMaxArgs() {
        return getMinArgs() + optionalArgs.size();
    }

    /**
     * Checks if the command has been executed.
     *
     * @param sender the command's sender
     * @param args the passed arguments
     * @return whether the command has been executed
     */
    public boolean executeCommand(CommandSender sender, String[] args) {
        // check if the user has sufficient permissions to run command
        if (!sender.hasPermission(this.permission.toString())) {
            Utils.msg(sender, "&cYou are missing the permission \"&4" + this.permission + "&c\" to use this command!");
            return false;
        }

        // check if command has enough arguments passed into it
        if (!hasEnoughArguments(args.length - 1)) {
            List<String> missedCmdsList = new ArrayList<>();

            // loop through and collect all the missing arguments
            for (int ind = args.length - 1; ind < this.args.size(); ind++) {
                missedCmdsList.add(formattedArg(ind));
            }
            for (int ind = Math.max(0, args.length - this.args.size() - 1); ind < this.optionalArgs.size(); ind++) {
                missedCmdsList.add(formattedOptionalArg(ind));
            }

            String missedCmds = String.join("&7, ", missedCmdsList);

            // notify user of what arguemnts they are missing
            Utils.msg(sender, "&cMissing argument" + (missedCmdsList.size() > 1 ? "s" : "") + ": " + missedCmds);
            if (hasOptionalArgs()) {
                Utils.msg(sender, "&cUsages:", "  " + getUsage(), "  " + getOptionalUsage());
            } else {
                Utils.msg(sender, "&cUsage: " + getUsage());
            }
            notEnoughArgs(sender);

            return false;
        }

        return commandRoutine(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    /**
     * Runs on tab press.
     *
     * @param sender the command's sender
     * @param args the passed arguments
     * @return an empty List of Strings
     */
    public List<String> executeTab(CommandSender sender, String[] args) {
        // check if user has sufficient permissions or if there are too many arguments passed
        if (!sender.hasPermission(this.permission.toString()) || args.length > getMaxArgs()) {
            return Arrays.asList();
        }

        return tabRoutine(sender, args);
    }

}
