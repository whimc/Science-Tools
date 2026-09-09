package edu.whimc.sciencetools.commands;

import edu.whimc.sciencetools.commands.subcommands.AbstractSubCommand;
import edu.whimc.sciencetools.gui.TricorderManager;
import edu.whimc.sciencetools.gui.TricorderManager.Mode;
import edu.whimc.sciencetools.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for {@code /tricorder on|off|unlock [player]}.
 */
public class TricorderCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION = AbstractSubCommand.Permission.ADMIN.toString();

    private final TricorderManager manager;

    /**
     * Constructs the Tricorder command.
     *
     * @param manager The manager that applies and removes Tricorders.
     */
    public TricorderCommand(TricorderManager manager) {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Enables, unlocks, or disables the Tricorder for everyone or one player.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            Utils.msg(sender, "&cYou are missing the permission \"&4" + PERMISSION + "&c\" to use this command!");
            return true;
        }

        Mode mode = parseMode(args.length == 0 ? "" : args[0]);
        if (mode == null) {
            Utils.msg(sender, "&cUsage: &e/tricorder <on|off|unlock> [player]");
            return true;
        }

        if (args.length == 1) {
            this.manager.setGlobalMode(mode);
            Utils.msg(sender, globalMessage(mode));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Utils.msg(sender, "&cPlayer \"&4" + args[1] + "&c\" is not online!");
            return true;
        }
        this.manager.setPlayerMode(target, mode);
        Utils.msg(sender, playerMessage(mode, target.getName()));
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Suggests {@code on}/{@code off}/{@code unlock} and online player names.
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                                      @NotNull String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            return Arrays.asList();
        }
        if (args.length == 1) {
            String hint = args[0].toLowerCase(Locale.ROOT);
            return Arrays.asList("on", "off", "unlock").stream()
                    .filter(option -> option.startsWith(hint))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String hint = args[1].toLowerCase(Locale.ROOT);
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase(Locale.ROOT).startsWith(hint)) {
                    names.add(player.getName());
                }
            }
            return names;
        }
        return Arrays.asList();
    }

    private Mode parseMode(String arg) {
        if (arg.equalsIgnoreCase("on")) {
            return Mode.LOCKED;
        }
        if (arg.equalsIgnoreCase("off")) {
            return Mode.OFF;
        }
        if (arg.equalsIgnoreCase("unlock")) {
            return Mode.UNLOCKED;
        }
        return null;
    }

    private String globalMessage(Mode mode) {
        if (mode == Mode.LOCKED) {
            return "&aTricorder enabled. It is locked to hotbar slot 9 for all players.";
        }
        if (mode == Mode.UNLOCKED) {
            return "&aTricorder unlocked. Players can move or drop it.";
        }
        return "&cTricorder disabled and removed from all online players.";
    }

    private String playerMessage(Mode mode, String name) {
        if (mode == Mode.LOCKED) {
            return "&aGave " + name + " a locked Tricorder.";
        }
        if (mode == Mode.UNLOCKED) {
            return "&aGave " + name + " an unlocked Tricorder.";
        }
        return "&cRemoved the Tricorder from " + name + ".";
    }
}
