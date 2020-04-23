package edu.whimc.sciencetools.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils.Placeholder;

public class ToolManager {
	
	public static enum ToolType {
		ALTITUDE(Placeholder.ALTITUDE),
		OXYGEN(Placeholder.OXYGEN),
		PRESSURE(Placeholder.PRESSURE),
		TEMPERATURE(Placeholder.TEMPERATURE),
		WIND(Placeholder.WIND);
		
		private Placeholder placeholder;
		
		private ToolType(Placeholder placeholder) {
			this.placeholder = placeholder;
		}
		
		public Placeholder getPlaceholder() {
			return placeholder;
		}
	}
	
	private ScienceTools plugin;
	private Map<ToolType, ScienceTool> tools;
	
	private ToolManager(ScienceTools plugin) {
		this.plugin = plugin;
		this.tools = new HashMap<>();
	}
	
	public static ToolManager loadTools(ScienceTools plugin, ConversionManager convManager) {
		ToolManager manager = new ToolManager(plugin);
		
		Utils.log(plugin, ChatColor.YELLOW + "Loading Science Tools from config");
		
		for (ToolType type : ToolType.values()) {
			if (!plugin.getConfig().contains("tools." + type)) {
				Utils.log(plugin, ChatColor.RED + " - No tool entry found for " + type + "!");
				continue;
			}
			
			Utils.log(plugin, ChatColor.AQUA + " - Loading " + ChatColor.WHITE + type);
			String defaultExpr = plugin.getConfig().getString("tools." + type + ".default-expression");
			String unit = plugin.getConfig().getString("tools." + type + ".unit");
			
			Utils.log(plugin, ChatColor.AQUA + "   - Default Expression: \"" + ChatColor.WHITE + defaultExpr + ChatColor.AQUA + "\"");
			Utils.log(plugin, ChatColor.AQUA + "   - Unit: \"" + ChatColor.WHITE + unit + ChatColor.AQUA + "\"");
			
			List<Conversion> convs = new ArrayList<>();
			if (plugin.getConfig().contains("tools." + type + ".conversions")) {
				Utils.log(plugin, ChatColor.AQUA + "     - Loading conversions");
				for (String convName : plugin.getConfig().getStringList("tools." + type + ".conversions")) {
					Conversion convToAdd = convManager.getConversion(convName); 
					
					if (convToAdd == null) {
						Utils.log(plugin, ChatColor.AQUA + "       - " + ChatColor.RED + convName + " is not a valid conversion!");
						continue;
					} 
					
					convs.add(convToAdd);
					Utils.log(plugin, ChatColor.AQUA + "       - " + ChatColor.WHITE + convName);
				}
			}
			
			Map<String, String> worldExprs = new HashMap<>();
			if (plugin.getConfig().contains("tools." + type + ".worlds")) {
				Utils.log(plugin, ChatColor.AQUA + "     - Loading world-specific expressions");
				for (String world : plugin.getConfig().getConfigurationSection("tools." + type + ".worlds").getKeys(false)) {
					String worldExpr = plugin.getConfig().getString("tools." + type + ".worlds." + world);
					
					worldExprs.put(world, worldExpr);
					Utils.log(plugin, ChatColor.AQUA + "       - " + ChatColor.WHITE + world + " \"" + worldExpr + "\"");
				}
			}
			
			ScienceTool tool = new ScienceTool(manager, type, defaultExpr, unit, worldExprs, convs);
			manager.tools.put(type, tool);
		}
		
		Utils.log(plugin, ChatColor.YELLOW + "Science tools loaded!");
		
		return manager;
	}
	
	public String fillIn(String expr, Player player) {
		// Replace position placeholders
		expr = expr.replace("{X}", Double.toString(player.getLocation().getX()));
		expr = expr.replace("{Y}", Double.toString(player.getLocation().getY()));
		expr = expr.replace("{Z}", Double.toString(player.getLocation().getZ()));
		
		// Replace tool placeholders
		for (ToolType curType : ToolType.values()) {
			Placeholder ph = curType.getPlaceholder();
			if (!expr.contains(ph.toString())) {
				continue;
			}
			
			ScienceTool targetTool = getTool(curType);
			if (targetTool == null) {
				Utils.msg(player, "&f" + curType + " &cis not loaded. Replacing with &f1");
				expr = expr.replace(ph.toString(), "1");
				continue;
			}
			
			double val = Utils.executeExpression(player, targetTool.getExpression(player));
			expr = expr.replace(ph.toString(), Double.toString(val));
		}
		
		return expr;
	}

	public ScienceTool getTool(ToolType type) {
		return tools.getOrDefault(type, null);
	}
	
}
