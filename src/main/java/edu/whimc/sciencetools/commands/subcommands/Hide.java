package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.gui.GuiVisibility;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Hides a science tool in the Tricorder GUI for a world.
 * Command: /sciencetools hide
 */
public class Hide extends AbstractSubCommand {

    /**
     * Constructs the Hide command.
     */
    public Hide() {
        super("hide", Arrays.asList("tool"), Arrays.asList("world"),
                "Hide a tool in the GUI for a world", Permission.ADMIN);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Hides the tool in the player's world, a named world, or every world.
     */
    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        ScienceTool tool = ScienceTools.getInstance().getToolManager().resolveTool(args[0]);
        if (tool == null) {
            Utils.msg(sender, "&cThe tool \"&4" + args[0] + "&c\" does not exist!");
            return false;
        }

        String worldKey = worldKey(sender, args);
        if (worldKey == null) {
            Utils.msg(sender, "&cSpecify a world, or use &eall&c.");
            return false;
        }

        if (GuiVisibility.hide(tool.getToolKey(), worldKey)) {
            Utils.msg(sender, "&aHid " + tool.getToolKey() + " in the GUI for &f" + worldKey + "&a.");
        } else {
            Utils.msg(sender, "&e" + tool.getToolKey() + " is already hidden for &f" + worldKey + "&e.");
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Suggests tool names and worlds.
     */
    @Override
    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return ScienceTools.getInstance().getToolManager().toolTabComplete(args[0]);
        }
        if (args.length == 2) {
            return worldTab(args[1]);
        }
        return Arrays.asList();
    }

    static String worldKey(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            return args[1];
        }
        if (sender instanceof Player) {
            return ((Player) sender).getWorld().getName();
        }
        return null;
    }

    static List<String> worldTab(String hint) {
        String prefix = hint.toLowerCase(Locale.ROOT);
        List<String> names = new ArrayList<>();
        if (GuiVisibility.ALL_WORLDS.startsWith(prefix)) {
            names.add(GuiVisibility.ALL_WORLDS);
        }
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                names.add(world.getName());
            }
        }
        return names;
    }
}
