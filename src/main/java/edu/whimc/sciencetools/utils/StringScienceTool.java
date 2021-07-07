package edu.whimc.sciencetools.utils;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.ToolManager.ToolType;

/**
 * This class handles commands that use custom strings as config input.
 * Note: These do not work with the validation system.
 * Commands: /atmosphere, /radiation_EM
 * 
 * @author Emi Brown
 */
public class StringScienceTool {	
	private ScienceTools plugin;
	private ToolType type;
	
	private String defaultExpr;
	private Map<String, String> worldExprs;
	private Map<String, String> regionExprs;

	private List<String> disabledWorlds;
	
	/**
	 * Constructs the StringScienceTool.
	 * 
	 * @param plugin this plugin
	 * @param tool the current science tool
	 */
	public StringScienceTool(ScienceTools plugin, ScienceTool tool) {
		this.plugin = plugin;
		
		type = tool.getType();
		worldExprs = tool.getWorldExprs();
		regionExprs = tool.getRegionExprs();
		disabledWorlds = tool.getDisabledWorlds();
		
		defaultExpr = this.plugin.getConfig().getString("tools." + type.toString().toUpperCase() + ".default-expression");
	}
	
	/**
	 * Displays the current world's string value for StringScienceTool in the chat
	 * 
	 * @param player the player issuing the command
	 */
	public void displayData(Player player) {
		// set initial message to default expression & find player's world
		String currentWorldName = player.getWorld().getName();
		String messageBase = "";
		String messageDisabled = "";
		
		// change message based on tool type
		switch (type) {
			case ATMOSPHERE:
				messageBase = "&aThe atmospheric composition of " + currentWorldName + " is &f";
				messageDisabled = "&cWe don't know how to measure the atmosphere here!";
				break;
			case RADIATION_EM:
				messageBase = "&aThe electromagnetic radiation here is &f";
				messageDisabled = "&cWe don't know how to measure the electromagnetic radiation here!";
				break;
			default:
				break;
		}
		
		String message = messageBase + defaultExpr + "&7";
		
		// check if player is in a disabled world
		if (disabledWorlds.contains(currentWorldName)) {
			Utils.msg(player, messageDisabled);
			return;
		}
		
		// check what world player is in and set the message to the correct config value
		if (worldExprs.containsKey(currentWorldName)) {
			message = messageBase + worldExprs.get(currentWorldName) + "&7";
		}
		
		// check if current region has an expression
		if (getRegionExpression(player.getLocation()) != null) {
			message = messageBase + getRegionExpression(player.getLocation()) + "&7";
		}
		
		// send message to user
		Utils.msg(player, message);
		return;
	}
	
	/**
	 * Get the tool's value in the player's current region.
	 * 
	 * @param location the player's current location
	 * @return the expression for the given region
	 */
	String getRegionExpression(Location location) {
		// exit if WorldGuard is not enabled
		if (!Utils.worldGuardEnabled()) {
			return null;
		}

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		if (container == null) {
			return null;
		}

		RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));
		if (regionManager == null) {
			return null;
		}

		BlockVector3 bv = BlockVector3.at(location.getX(), location.getY(), location.getZ());
		List<String> regions = regionManager.getApplicableRegionsIDs(bv);

		for (String region : regions) {
			String expr = regionExprs.getOrDefault(region, null);
			if (expr != null) {
				return expr;
			}
		}

		return null;
	}
}
