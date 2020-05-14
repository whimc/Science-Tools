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

import edu.whimc.sciencetools.utils.ToolManager.ToolType;

public class ScienceTool {

	private ToolManager manager;
	private ToolType type;
	
	private String defaultExpr;
	private String unit;
	
	private Map<String, String> worldExprs;
	private Map<String, String> regionExprs;
	
	private List<Conversion> conversions;
	
	public ScienceTool(ToolManager manager, ToolType type, String defaultExpr, String unit, 
			Map<String, String> worldExprs, Map<String, String> regionExprs, List<Conversion> conversions) {
		this.manager = manager;
		this.type = type;
		this.defaultExpr = defaultExpr;
		this.unit = unit;
		this.worldExprs = worldExprs;
		this.regionExprs = regionExprs;
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
	
	String getRegionExpression(Player player) {
		if (!Utils.worldGuardEnabled()) {
			return null;
		}
		
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		if (container == null) {
			return null;
		}
		
		RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
		if (regionManager == null) {
			return null;
		}
		
		Location loc = player.getLocation();
		BlockVector3 bv = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
		List<String> regions = regionManager.getApplicableRegionsIDs(bv);

		for (String region : regions) {
			String expr = regionExprs.getOrDefault(region, null);
			if (expr != null) {
				return expr;
			}
		}
		
		return null;
	}
	
	String getWorldExpression(Player player) {
		return worldExprs.getOrDefault(player.getWorld().getName(), null);
	}
	
	public String getExpression(Player player) {
		
		String expr = getRegionExpression(player);
		
		if (expr == null) {
			expr = getWorldExpression(player);
		}
		
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
