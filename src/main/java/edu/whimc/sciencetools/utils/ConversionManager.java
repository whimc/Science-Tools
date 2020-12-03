package edu.whimc.sciencetools.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

import edu.whimc.sciencetools.ScienceTools;

public class ConversionManager {

	private ScienceTools plugin;
	private Map<String, Conversion> conversions;
	
	private ConversionManager(ScienceTools plugin) {
		this.plugin = plugin;
		conversions = new HashMap<>();
	}
	
	public static ConversionManager loadConversions(ScienceTools plugin) {
		ConversionManager manager = new ConversionManager(plugin);

		Utils.log(plugin, ChatColor.YELLOW + "Loading Conversions from config");
		
		for (String convName : plugin.getConfig().getConfigurationSection("conversions").getKeys(false)) {
			Utils.log(plugin, ChatColor.AQUA + " - Loading " + ChatColor.WHITE + convName);
			String expr = plugin.getConfig().getString("conversions." + convName + ".expression");
			String unit = plugin.getConfig().getString("conversions." + convName + ".unit");

			Utils.log(plugin, ChatColor.AQUA + "   - Expression: \"" + ChatColor.WHITE + expr + ChatColor.AQUA + "\"");
			Utils.log(plugin, ChatColor.AQUA + "   - Unit: \"" + ChatColor.WHITE + unit + ChatColor.AQUA + "\"");
			
			Conversion conv = new Conversion(manager, convName, expr, unit);
			manager.conversions.put(convName.toLowerCase(), conv);
		}
		
		Utils.log(plugin, ChatColor.YELLOW + "Conversions loaded!");
		
		return manager;
	}
	
	public Conversion getConversion(String key) {
		return conversions.getOrDefault(key, null);
	}
	
	public void setExpression(Conversion conv, String expression) {
		setConfig(conv.getName() + ".expression", expression);
	}
	
	public void setUnit(Conversion conv, String unit) {
		setConfig(conv.getName() + ".unit", unit);
	}
	
	private void setConfig(String key, Object value) {
		plugin.getConfig().set("conversions." + key, value);
		plugin.saveConfig();
	}
	
}
