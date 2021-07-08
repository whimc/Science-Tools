package edu.whimc.sciencetools.models.sciencetool;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import edu.whimc.sciencetools.javascript.JSContext;
import edu.whimc.sciencetools.javascript.JSNumericalExpression;
import edu.whimc.sciencetools.models.conversion.Conversion;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ScienceTool {

    private final ScienceToolManager manager;
    private final ToolType type;

    private final JSNumericalExpression defaultExpr;
    private final String unit;

    private final Map<String, JSNumericalExpression> worldExprs;
    private final Map<String, JSNumericalExpression> regionExprs;

    private final List<Conversion> conversions;
    private final List<String> disabledWorlds;

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
            JSNumericalExpression expr = this.regionExprs.getOrDefault(region, null);
            if (expr != null) {
                return expr;
            }
        }

        return null;
    }

    private JSNumericalExpression getWorldExpression(Location loc) {
        return this.worldExprs.getOrDefault(loc.getWorld().getName(), null);
    }

    public JSNumericalExpression getExpression(Location loc) {

        JSNumericalExpression expr = getRegionExpression(loc);

        if (expr == null) {
            expr = getWorldExpression(loc);
        }

        if (expr == null) {
            expr = this.defaultExpr;
        }

        return expr;
    }

    public double getData(Location loc) {
        JSNumericalExpression expr = getExpression(loc);
        Double val = expr.evaluate(JSContext.create(loc));
        return val == null ? 0 : val;
    }

    public String getMainUnit() {
        return this.unit;
    }

    public void displayData(Player player) {
        if (this.disabledWorlds.contains(player.getWorld().getName())) {
            Utils.msg(player, "&cWe don't know how to measure " + this.type.toString().toLowerCase() + " here!");
            return;
        }

        double val = getData(player.getLocation());

        String message = "&aThe measured " + this.type.toString().toLowerCase() + " is &f" + Utils.trim2Deci(val)
                + this.unit + "&7";

        for (Conversion conv : this.conversions) {
            String converted = Utils.trim2Deci(conv.convert(val));
            message += " (" + converted + conv.getUnit() + ")";
        }

        Utils.msg(player, message);

    }

}
