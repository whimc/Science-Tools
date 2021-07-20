package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * The command for measuring the value of each ScienceTool.
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
     * Displays the measurement of the given tool in chat.
     *
     * @param sender the command's sender
     * @param args the arguments passed
     * @return whether or not the command has run
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
            Utils.msg(sender, "&cAvailable tools: &7" +
                    String.join(", ", manager.toolTabComplete("")));
            return false;
        }

        tool.displayMeasurement((Player) sender);
        return true;
    }

    /**
     * Auto-fills the list of valid tools.
     *
     * @param sender the command's sender
     * @param args the arguments passed
     * @return the list of tools that can be measured
     */
    @Override
    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return ScienceTools.getInstance().getToolManager().toolTabComplete(args[0]);
        }

        return null;
    }
}
