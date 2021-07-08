package edu.whimc.sciencetools.commands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ToolType;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.StringScienceTool;
import edu.whimc.sciencetools.utils.ScienceTool;
import edu.whimc.sciencetools.utils.ToolManager.ToolType;
import edu.whimc.sciencetools.utils.Utils;

/**
 * For commands like Altitude, Oxygen, Pressure, Temperature, Wind, etc...
 *
 * @author Jack
 */
public class GetData implements CommandExecutor {

    private final ScienceTools plugin;
    private final ToolType type;

    public GetData(ScienceTools plugin, ToolType type) {
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        ScienceTool tool = plugin.getToolManager().getTool(type);

        if (tool == null) {
            Utils.msg(sender, "&cThat data tool isn't loaded!");
            return false;
        }

        if (!(sender instanceof Player)) {
            Utils.msg(sender, "&cYou must be a player to use this command!");
            return false;
        }

        tool.displayData((Player) sender);

        return false;
    }

		if (!(sender instanceof Player)) {
			Utils.msg(sender, "&cYou must be a player to use this command!");
			return false;
		}

		// handle as a StringScienceTool if using /atmosphere or /radiation_em
		if (type == ToolType.ATMOSPHERE || type == ToolType.RADIATION_EM) {
			StringScienceTool atmosTool = new StringScienceTool(plugin, tool);
			atmosTool.displayData((Player) sender);
			return false;
		}

		tool.displayData((Player) sender);
		return false;
	}

}
