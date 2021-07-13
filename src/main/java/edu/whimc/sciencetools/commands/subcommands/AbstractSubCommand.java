package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractSubCommand {

    public enum Permission {
        ADMIN("admin"),
        USER("user");

        private final String perm;

        Permission(String perm) {
            this.perm = perm;
        }

        @Override
        public @NotNull String toString() {
            return "sciencetools." + perm;
        }

    }

    private final String command;
    private final List<String> args;
    private final List<String> optionalArgs;
    private final String description;
    private final Permission permission;

    public AbstractSubCommand(String command, List<String> args, List<String> optionalArgs, String description, Permission permission) {
        this.command = command;
        this.args = args == null ? Arrays.asList() : args;
        this.optionalArgs = optionalArgs == null ? Arrays.asList() : optionalArgs;
        this.description = description;
        this.permission = permission;
    }

    protected abstract boolean commandRoutine(CommandSender sender, String[] args);

    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        return Arrays.asList();
    }

    protected void notEnoughArgs(CommandSender sender) {
        return;
    }

    public @NotNull String fullDescription() {
        return this + " &7- " + this.description;
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

    public @NotNull String formattedArg(int ind) {
        if (args == null || ind < 0 || ind >= args.size()) {
            return "";
        }

        return "&7<&b" + args.get(ind) + "&7>";
    }

    public @NotNull String formattedOptionalArg(int ind) {
        if (optionalArgs == null || ind < 0 || ind >= optionalArgs.size()) {
            return "";
        }

        return "&7[&b" + optionalArgs.get(ind) + "&7]";
    }

    public @NotNull String getUsage() {
        return "&e/sciencetools " + command + " " + String.join(" ", IntStream
                .range(0, args.size())
                .mapToObj(this::formattedArg)
                .collect(Collectors.toList()));
    }

    public @NotNull String getOptionalUsage() {
        return getUsage() + " " + String.join(" ", IntStream
                .range(0, optionalArgs.size())
                .mapToObj(this::formattedOptionalArg)
                .collect(Collectors.toList()));
    }

    @Override
    public @NotNull String toString() {
        return "&e&o/sciencetools " + command;
    }

    public int getMinArgs() {
        return args.size();
    }

    public int getMaxArgs() {
        return getMinArgs() + optionalArgs.size();
    }

    public boolean executeCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(this.permission.toString())) {
            Utils.msg(sender, "&cYou are missing the permission \"&4" + this.permission + "&c\" to use this command!");
            return false;
        }

        if (!hasEnoughArguments(args.length - 1)) {
            List<String> missedCmdsList = new ArrayList<>();
            for (int ind = args.length - 1; ind < this.args.size(); ind++) {
                missedCmdsList.add(formattedArg(ind));
            }
            for (int ind = Math.max(0, args.length - this.args.size() - 1); ind < this.optionalArgs.size(); ind++) {
                missedCmdsList.add(formattedOptionalArg(ind));
            }

            String missedCmds = String.join("&7, ", missedCmdsList);

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

    public List<String> executeTab(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permission.toString()) || args.length > getMaxArgs()) {
            return Arrays.asList();
        }
        return tabRoutine(sender, args);
    }

}
