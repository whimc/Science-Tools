package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a template for all SubCommands.
 */
public abstract class AbstractSubCommand {

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
     * @param command      The name of the command.
     * @param args         The arguments required.
     * @param optionalArgs Any optional arguments.
     * @param description  A description of what the command does.
     * @param permission   The required permission level to use the command.
     */
    public AbstractSubCommand(String command, List<String> args, List<String> optionalArgs, String description,
                              Permission permission) {
        this.command = command;
        this.args = args == null ? Arrays.asList() : args;
        this.optionalArgs = optionalArgs == null ? Arrays.asList() : optionalArgs;
        this.description = description;
        this.permission = permission;
    }

    /**
     * Runs when the subcommand was properly executed (with correct permissions and arguments).
     *
     * @param sender The command's sender.
     * @param args   The arguments passed.
     * @return Whether the command has been run or not.
     */
    protected abstract boolean commandRoutine(CommandSender sender, String[] args);

    /**
     * Handles tab completion for a subcommand.
     * Returns an empty list by default.
     *
     * @param sender The command's sender.
     * @param args   The arguments passed.
     * @return An empty List of Strings.
     */
    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        return Arrays.asList();
    }

    /**
     * Runs when command is not passed enough arguments.
     *
     * @param sender The command's sender.
     */
    protected void notEnoughArgs(CommandSender sender) {
        return;
    }

    /**
     * The full description of the SubCommand.
     */
    public @NotNull String fullDescription() {
        return this + " &7- " + this.description;
    }

    /**
     * Whether the SubCommand has optional arguments.
     */
    public boolean hasOptionalArgs() {
        return this.optionalArgs.size() != 0;
    }

    /**
     * Checks if the SubCommand has been passed enough arguments.
     *
     * @param argsLen The number (as an integer) of passed in arguments.
     * @return Whether or not the SubCommand has been passed enough arguments.
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
     * @param ind The index of the argument.
     * @return The formatted argument.
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
     * @param ind The index of the optional argument.
     * @return The formatted optional argument.
     */
    public @NotNull String formattedOptionalArg(int ind) {
        // ensure the optional argument index is valid
        if (optionalArgs == null || ind < 0 || ind >= optionalArgs.size()) {
            return "";
        }

        return "&7[&b" + optionalArgs.get(ind) + "&7]";
    }

    /**
     * The subcommand's usage excluding optional arguments.
     */
    public @NotNull String getUsage() {
        return "&e/sciencetools " + command + " " + String.join(" ", IntStream
                .range(0, args.size())
                .mapToObj(this::formattedArg)
                .collect(Collectors.toList()));
    }

    /**
     * The subcommand's usage including optional arguments.
     */
    public @NotNull String getOptionalUsage() {
        return getUsage() + " " + String.join(" ", IntStream
                .range(0, optionalArgs.size())
                .mapToObj(this::formattedOptionalArg)
                .collect(Collectors.toList()));
    }

    /**
     * The SubCommand as a String.
     */
    @Override
    public @NotNull String toString() {
        return "&e&o/sciencetools " + command;
    }

    /**
     * The minimum number of arguments required to run the command.
     */
    public int getMinArgs() {
        return args.size();
    }

    /**
     * The maximum number of arguments that can be passed into the command.
     */
    public int getMaxArgs() {
        return getMinArgs() + optionalArgs.size();
    }

    /**
     * Wrapper function to {@link #commandRoutine(CommandSender, String[])}.
     * Checks permissions of the sender and for a valid number of arguments.
     * If these checks fail, the command routine is not run.
     *
     * @param sender The command's sender.
     * @param args   The passed arguments.
     * @return Whether the command has been executed.
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
     * Wrapper for {@link #tabRoutine(CommandSender, String[])}.
     * Returns an empty list if the sender does not have permission for the subcommand or if they
     * have surpassed the max number of arguments.
     *
     * @param sender The command's sender.
     * @param args   The passed arguments.
     * @return An empty List of Strings.
     */
    public List<String> executeTab(CommandSender sender, String[] args) {
        // check if user has sufficient permissions or if there are too many arguments passed
        if (!sender.hasPermission(this.permission.toString()) || args.length > getMaxArgs()) {
            return Arrays.asList();
        }

        return tabRoutine(sender, args);
    }

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
         * @param perm The name of the permission.
         */
        Permission(String perm) {
            this.perm = perm;
        }

        /**
         * The permission as a String in the form: "sciencetools.perm".
         */
        @Override
        public @NotNull String toString() {
            return "sciencetools." + perm;
        }
    }

}
