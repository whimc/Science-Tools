package edu.whimc.sciencetools.models.sciencetool;

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

import edu.whimc.sciencetools.javascript.JSNumericalExpression;
import edu.whimc.sciencetools.javascript.JSPlaceholder.JSPlaceholderContext;
import edu.whimc.sciencetools.models.conversion.Conversion;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager.ToolType;
import edu.whimc.sciencetools.utils.Utils;

public class ScienceTool {

	private ScienceToolManager manager;
	private ToolType type;

	private JSNumericalExpression defaultExpr;
	private String unit;

	private Map<String, JSNumericalExpression> worldExprs;
	private Map<String, JSNumericalExpression> regionExprs;

	private List<Conversion> conversions;
	private List<String> disabledWorlds;

	public ScienceTool(ScienceToolManager manager, ToolType type, JSNumericalExpression defaultExpr, String unit,
			Map<String, JSNumericalExpression> worldExprs, Map<String, JSNumericalExpression> regionExprs,
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

	private JSNumericalExpression getRegionExpression(Location loc) {
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
			JSNumericalExpression expr = regionExprs.getOrDefault(region, null);
			if (expr != null) {
				return expr;
			}
		}

		return null;
	}

	private JSNumericalExpression getWorldExpression(Location loc) {
		return worldExprs.getOrDefault(loc.getWorld().getName(), null);
	}

	public JSNumericalExpression getExpression(CommandSender sender, Location loc) {

	    JSNumericalExpression expr = getRegionExpression(loc);

		if (expr == null) {
			expr = getWorldExpression(loc);
		}

		if (expr == null) {
			expr = defaultExpr;
		}

		return expr;
	}

	public double getData(CommandSender sender, Location loc) {
	    JSNumericalExpression expr = getExpression(sender, loc);
	    Double val = expr.evaluate(JSPlaceholderContext.create(loc));
	    return val == null ? 0 : val;
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
			String converted = Utils.trim2Deci(conv.convert(val));
			message += " (" + converted + conv.getUnit() + ")";
		}

		Utils.msg(player, message);

	}

}
