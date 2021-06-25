package edu.whimc.sciencetools.utils;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import edu.whimc.sciencetools.ScienceTools;

/**
 * This class handles the /atmosphere command.
 * 
 * @author Emi Brown
 */
public class AtmosphereTool {
	private static final String PATH = "tools.ATMOSPHERE.";
	
	private ScienceTools plugin;
	
	private String defaultExpr;

	private Map<String, String> worldExprs;
	private List<String> disabledWorlds;
	
	/**
	 * Constructs AtmosphereTool.
	 * 
	 * @param plugin this plugin
	 * @param tool the current science tool
	 */
	public AtmosphereTool(ScienceTools plugin, ScienceTool tool) {
		this.plugin = plugin;
		defaultExpr = this.plugin.getConfig().getString(PATH + "default-expression");
		worldExprs = tool.getWorldExprs();
		disabledWorlds = tool.getDisabledWorlds();
	}
	
	/**
	 * Displays the current world's string value for /atmosphere in the chat
	 * 
	 * @param player the player issuing the command
	 */
	public void displayData(Player player) {
		// set initial message to default expression & find player's world
		String currentWorldName = player.getWorld().getName();
		String messageBase = "&aThe atmospheric composition of " + currentWorldName + " is &f ";
		String message = messageBase + defaultExpr + "&7";
		
		// check if player is in a disabled world
		if (disabledWorlds.contains(currentWorldName)) {
			Utils.msg(player, "&cWe don't know how to measure the atmosphere here!");
			return;
		}
		
		// check what world player is in and set the message to the correct config value
		if (worldExprs.containsKey(currentWorldName)) {
			message = messageBase + worldExprs.get(currentWorldName) + "&7";
		}
		
		// send message to user
		Utils.msg(player, message);
		return;
	}
}
