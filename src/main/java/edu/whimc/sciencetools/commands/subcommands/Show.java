package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.gui.GuiVisibility;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.utils.Utils;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * Shows a science tool in the Tricorder GUI for a world.
 * Command: /sciencetools show
 */
public class Show extends AbstractSubCommand {

    /**
     * Constructs the Show command.
     */
    public Show() {
        super("show", Arrays.asList("tool"), Arrays.asList("world"),
                "Show a tool in the GUI for a world", Permission.ADMIN);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Shows the tool in the player's world, a named world, or every world.
     */
    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        ScienceTool tool = ScienceTools.getInstance().getToolManager().resolveTool(args[0]);
        if (tool == null) {
            Utils.msg(sender, "&cThe tool \"&4" + args[0] + "&c\" does not exist!");
            return false;
        }

        String worldKey = Hide.worldKey(sender, args);
        if (worldKey == null) {
            Utils.msg(sender, "&cSpecify a world, or use &eall&c.");
            return false;
        }

        if (GuiVisibility.show(tool.getToolKey(), worldKey)) {
            Utils.msg(sender, "&aShowed " + tool.getToolKey() + " in the GUI for &f" + worldKey + "&a.");
        } else {
            Utils.msg(sender, "&e" + tool.getToolKey() + " was not hidden for &f" + worldKey + "&e.");
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
            return Hide.worldTab(args[1]);
        }
        return Arrays.asList();
    }
}
