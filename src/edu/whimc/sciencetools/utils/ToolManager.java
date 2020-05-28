package edu.whimc.sciencetools.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sun.xml.internal.ws.util.StringUtils;

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
		
		@Override
		public String toString() {
			return StringUtils.capitalize(this.name().toLowerCase());
		}
		
		public static ToolType match(String str) {
			for (ToolType type : ToolType.values()) {
				if (type.name().equalsIgnoreCase(str)) {
					return type;
				}
			}
			return null;
		}
	}
	
	private Map<ToolType, ScienceTool> tools;
	
	private ToolManager() {
		this.tools = new HashMap<>();
	}
	
	public static ToolManager loadTools(ScienceTools plugin, ConversionManager convManager) {
		ToolManager manager = new ToolManager();
		
		Utils.log(plugin, ChatColor.YELLOW + "Loading Science Tools from config");
		
		for (ToolType type : ToolType.values()) {
			String name = type.name();
			if (!plugin.getConfig().contains("tools." + name)) {
				Utils.log(plugin, ChatColor.RED + " - No tool entry found for " + name + "!");
				continue;
			}
			
			Utils.log(plugin, ChatColor.AQUA + " - Loading " + ChatColor.WHITE + name);
			String defaultExpr = plugin.getConfig().getString("tools." + name + ".default-expression");
			String unit = plugin.getConfig().getString("tools." + name + ".unit");
			
			Utils.log(plugin, ChatColor.AQUA + "   - Default Expression: \"" + ChatColor.WHITE + defaultExpr + ChatColor.AQUA + "\"");
			Utils.log(plugin, ChatColor.AQUA + "   - Unit: \"" + ChatColor.WHITE + unit + ChatColor.AQUA + "\"");
			
			List<Conversion> convs = new ArrayList<>();
			if (plugin.getConfig().contains("tools." + name + ".conversions")) {
				Utils.log(plugin, ChatColor.AQUA + "     - Loading conversions");
				for (String convName : plugin.getConfig().getStringList("tools." + name + ".conversions")) {
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
			if (plugin.getConfig().contains("tools." + name + ".worlds")) {
				Utils.log(plugin, ChatColor.AQUA + "     - Loading world-specific expressions");
				for (String world : plugin.getConfig().getConfigurationSection("tools." + name + ".worlds").getKeys(false)) {
					String worldExpr = plugin.getConfig().getString("tools." + name + ".worlds." + world);
					
					worldExprs.put(world, worldExpr);
					Utils.log(plugin, ChatColor.AQUA + "       - " + ChatColor.WHITE + world + " \"" + worldExpr + "\"");
				}
			}
			
			Map<String, String> regionExprs = new HashMap<>();
			if (plugin.getConfig().contains("tools." + name + ".regions")) {
				Utils.log(plugin, ChatColor.AQUA + "     - Loading region-specific expressions");
				for (String region : plugin.getConfig().getConfigurationSection("tools." + name + ".regions").getKeys(false)) {
					String regionExpr = plugin.getConfig().getString("tools." + name + ".regions." + region);
					
					regionExprs.put(region, regionExpr);
					Utils.log(plugin, ChatColor.AQUA + "       - " + ChatColor.WHITE + region + " \"" + regionExpr + "\"");
				}
			}
			
			ScienceTool tool = new ScienceTool(manager, type, defaultExpr, unit, worldExprs, regionExprs, convs);
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
	
	public Set<ToolType> getLoadedTools() {
		return tools.keySet();
	}
	
}
