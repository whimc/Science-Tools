package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolMeasureEvent;
import edu.whimc.sciencetools.utils.Utils;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The command for measuring the value of each ScienceTool.
 * Command: /sciencetools measure
 */
public class Measure extends AbstractSubCommand {

    /**
     * Constructs the Measure command.
     */
    public Measure() {
        super("measure", Arrays.asList("tool"), null,
                "Measure a science tool", Permission.USER);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Displays the measurement of the given tool to the sender.
     */
    @Override
    protected boolean commandRoutine(CommandSender sender, String[] args) {
        // ensure command sender is a player
        if (!(sender instanceof Player)) {
            Utils.msg(sender, "&cYou must be a player to use this command!");
            return false;
        }

        // set up tool
        ScienceToolManager manager = ScienceTools.getInstance().getToolManager();
        ScienceTool tool = manager.getTool(args[0]);

        // ensure tool is valid
        if (tool == null) {
            Utils.msg(sender, "&cThe tool \"&4" + args[0] + "&c\" does not exist!");
            Utils.msg(sender, "&cAvailable tools: &7"
                    +
                    String.join(", ", manager.toolTabComplete("")));
            return false;
        }

        Player player = (Player) sender;
        String measurement = tool.displayMeasurement(player);

        if (measurement != null) {
            ScienceToolMeasureEvent event = new ScienceToolMeasureEvent(player, tool, measurement);
            Bukkit.getPluginManager().callEvent(event);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Auto-completes the list of science tools.
     */
    @Override
    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return ScienceTools.getInstance().getToolManager().toolTabComplete(args[0]);
        }

        return null;
    }
}
