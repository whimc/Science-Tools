package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Measure extends AbstractSubCommand {

    public Measure() {
        super("measure", Arrays.asList("tool"), null,
                "Measure a science tool", Permission.USER);
    }

    @Override
    protected boolean commandRoutine(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Utils.msg(sender, "&cYou must be a player to use this command!");
            return false;
        }

        ScienceToolManager manager = ScienceTools.getInstance().getToolManager();
        ScienceTool tool = manager.getTool(args[0]);

        if (tool == null) {
            Utils.msg(sender, "&cThe tool \"&4" + args[0] + "&c\" does not exist!");
            Utils.msg(sender, "&cAvailable tools: &7" +
                    String.join(", ", manager.toolTabComplete("")));
            return false;
        }

        tool.displayMeasurement((Player) sender);
        return true;
    }

    @Override
    protected List<String> tabRoutine(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return ScienceTools.getInstance().getToolManager().toolTabComplete(args[0]);
        }

        return null;
    }
}
