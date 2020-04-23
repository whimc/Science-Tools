package edu.whimc.sciencetools.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.ScienceTool;
import edu.whimc.sciencetools.utils.ToolManager.ToolType;
import edu.whimc.sciencetools.utils.Utils;

/**
 *
 * Altitude, Oxygen, Pressure, Temperature, Wind
 * 
 * @author Jack
 *
 */
public class GetData implements CommandExecutor {

	private ScienceTools plugin;
	private ToolType type;
	
	public GetData(ScienceTools plugin, ToolType type) {
		this.plugin = plugin;
		this.type = type;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		// TODO: Add permissions
		
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
	
}
