package edu.whimc.sciencetools.utils;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import edu.whimc.sciencetools.utils.ToolManager.ToolType;

public class ScienceTool {

	private ToolManager manager;
	private ToolType type;
	
	private String defaultExpr;
	private String unit;
	
	private Map<String, String> worldExprs;
	
	private List<Conversion> conversions;
	
	public ScienceTool(ToolManager manager, ToolType type, String defaultExpr, String unit, Map<String, String> worldExprs, List<Conversion> conversions) {
		this.manager = manager;
		this.type = type;
		this.defaultExpr = defaultExpr;
		this.unit = unit;
		this.worldExprs = worldExprs;
		this.conversions = conversions;
		
		// TODO: Add methods to change values
	}
	
	public String fillIn(String expr, Player player) {
		
		String ph = type.getPlaceholder().toString();
		if (expr.contains(ph)) {
			Utils.msg(player, "&cPls don't use recursion. Replacing &7" + ph + " &cwith &f1");
			expr = expr.replace(ph, "1");
		}
		
		return manager.fillIn(expr, player);
	}
	
	public String getExpression(Player player) {
		String expr = worldExprs.getOrDefault(player.getWorld().getName(), null);
		if (expr == null) {
			expr = defaultExpr;
		}
		
		expr = fillIn(expr, player);
		
		return expr;
	}
	
	public double getData(Player player) {
		return Utils.executeExpression(player, getExpression(player));
	}
	
	public void displayData(Player player) {
		double val = getData(player);
		
		String message = "&aThe measured " + type.toString().toLowerCase() + " is &f" + Utils.trim2Deci(val) + unit + "&7";
		
		for (Conversion conv : conversions) {
			String converted = Utils.trim2Deci(conv.convert(player, val));
			message += " (" + converted + conv.getUnit() + ")";
		}
		
		Utils.msg(player, message);
		
	}
	
}
