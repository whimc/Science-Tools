package edu.whimc.sciencetools.models;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import edu.whimc.sciencetools.managers.ScienceToolManager;
import edu.whimc.sciencetools.managers.ScienceToolManager.ToolType;
import edu.whimc.sciencetools.utils.Utils;

public class ScienceTool {

	private ScienceToolManager manager;
	private ToolType type;

	private String defaultExpr;
	private String unit;

	private Map<String, String> worldExprs;
	private Map<String, String> regionExprs;

	private List<Conversion> conversions;
	private List<String> disabledWorlds;

	public ScienceTool(ScienceToolManager manager, ToolType type, String defaultExpr, String unit,
			Map<String, String> worldExprs, Map<String, String> regionExprs,
			List<Conversion> conversions, List<String> disabledWorlds) {
		this.manager = manager;
		this.type = type;
		this.defaultExpr = defaultExpr;
		this.unit = unit;
		this.worldExprs = worldExprs;
		this.regionExprs = regionExprs;
		this.conversions = conversions;
		this.disabledWorlds = disabledWorlds;

		// TODO: Add methods to change values
	}

	public String fillIn(CommandSender sender, String expr, Location loc) {

		String ph = type.getPlaceholder().toString();
		if (expr.contains(ph)) {
			Utils.msg(sender, "&cPls don't use recursion. Replacing &7" + ph + " &cwith &f1");
			expr = expr.replace(ph, "1");
		}

		return manager.fillIn(sender, expr, loc);
	}

	String getRegionExpression(Location loc) {
		if (!Utils.worldGuardEnabled()) {
			return null;
		}

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		if (container == null) {
			return null;
		}

		RegionManager regionManager = container.get(BukkitAdapter.adapt(loc.getWorld()));
		if (regionManager == null) {
			return null;
		}

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

	String getWorldExpression(Location loc) {
		return worldExprs.getOrDefault(loc.getWorld().getName(), null);
	}

	public String getExpression(CommandSender sender, Location loc) {

		String expr = getRegionExpression(loc);

		if (expr == null) {
			expr = getWorldExpression(loc);
		}

		if (expr == null) {
			expr = defaultExpr;
		}

		expr = fillIn(sender, expr, loc);

		return expr;
	}

	public double getData(CommandSender sender, Location loc) {
	    return Utils.executeExpression(sender, getExpression(sender, loc));
	}

	public String getMainUnit() {
		return unit;
	}

	public void displayData(Player player) {
		if (disabledWorlds.contains(player.getWorld().getName())) {
			Utils.msg(player, "&cWe don't know how to measure " + type.toString().toLowerCase() + " here!");
			return;
		}

		double val = getData(player, player.getLocation());

		String message = "&aThe measured " + type.toString().toLowerCase() + " is &f" + Utils.trim2Deci(val) + unit + "&7";

		for (Conversion conv : conversions) {
			String converted = Utils.trim2Deci(conv.convert(player, val));
			message += " (" + converted + conv.getUnit() + ")";
		}

		Utils.msg(player, message);

	}

}
