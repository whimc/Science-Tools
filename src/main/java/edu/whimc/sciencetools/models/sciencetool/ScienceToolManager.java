package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSContext;
import edu.whimc.sciencetools.javascript.JSNumericalExpression;
import edu.whimc.sciencetools.javascript.JSPlaceholder;
import edu.whimc.sciencetools.models.conversion.Conversion;
import edu.whimc.sciencetools.models.conversion.ConversionManager;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ScienceToolManager {

    private final Map<ToolType, ScienceTool> tools;
    private final ScienceTools plugin;

    public ScienceToolManager(ScienceTools plugin, ConversionManager conversionManager) {
        this.tools = new HashMap<>();
        this.plugin = plugin;
        loadTools(conversionManager);
    }

    public void loadTools(@NotNull ConversionManager conversionManager) {
        Utils.log("&eLoading Science Tools from config");

        for (ToolType type : ToolType.values()) {
            String name = type.name();
            if (!plugin.getConfig().contains("tools." + name)) {
                Utils.log("&e - No tool entry found for " + name + "!");
                continue;
            }

            Utils.log("&b - Loading &f" + name);
            JSNumericalExpression defaultExpr = new JSNumericalExpression(
                    plugin.getConfig().getString("tools." + name + ".default-expression"));
            if (!defaultExpr.valid()) {
                Utils.log("&e * Invalid default expression. Skipping!");
                continue;
            }
            String unit = plugin.getConfig().getString("tools." + name + ".unit");

            Utils.log("&b   - Default Expression: \"&f" + defaultExpr + "&b\"");
            Utils.log("&b   - Unit: \"&f" + unit + "&b\"");

            List<Conversion> convs = new ArrayList<>();
            if (plugin.getConfig().contains("tools." + name + ".conversions")) {
                Utils.log("&b     - Loading conversions");
                for (String convName : plugin.getConfig().getStringList("tools." + name + ".conversions")) {
                    Conversion convToAdd = conversionManager.getConversion(convName);

                    if (convToAdd == null) {
                        Utils.log("&b       - &e" + convName + " is not a valid conversion!");
                        continue;
                    }

                    convs.add(convToAdd);
                    Utils.log("&b       - &f" + convName);
                }
            }

            Map<String, JSNumericalExpression> worldExprs = new HashMap<>();
            if (plugin.getConfig().contains("tools." + name + ".worlds")) {
                Utils.log("&b     - Loading world-specific expressions");
                for (String world : plugin.getConfig().getConfigurationSection("tools." + name + ".worlds")
                        .getKeys(false)) {
                    String worldExpr = plugin.getConfig().getString("tools." + name + ".worlds." + world);

                    JSNumericalExpression jsExpr = new JSNumericalExpression(worldExpr);
                    if (!jsExpr.valid()) {
                        Utils.log("&b       - " + world + " \"" + worldExpr + "\" (Invalid expression)");
                        continue;
                    }
                    worldExprs.put(world, jsExpr);
                    Utils.log("&b       - &f" + world + " \"" + worldExpr + "\"");
                }
            }

            Map<String, JSNumericalExpression> regionExprs = new HashMap<>();
            if (plugin.getConfig().contains("tools." + name + ".regions")) {
                Utils.log("&b     - Loading region-specific expressions");
                for (String region : plugin.getConfig().getConfigurationSection("tools." + name + ".regions")
                        .getKeys(false)) {
                    String regionExpr = plugin.getConfig().getString("tools." + name + ".regions." + region);

                    JSNumericalExpression jsExpr = new JSNumericalExpression(regionExpr);
                    if (!jsExpr.valid()) {
                        Utils.log("&e       - " + region + " \"" + regionExpr + "\" (Invalid expression)");
                        continue;
                    }
                    regionExprs.put(region, jsExpr);
                    Utils.log("&b       - &f" + region + " \"" + regionExpr + "\"");
                }
            }

            List<String> disabledWorlds = new ArrayList<>();
            if (plugin.getConfig().contains("tools." + name + ".disabled-worlds")) {
                Utils.log("&b     - Loading disabled worlds");
                for (String world : plugin.getConfig().getStringList("tools." + name + ".disabled-worlds")) {
                    disabledWorlds.add(world);
                    Utils.log("&b       - &f" + world);
                }
            }

            ScienceTool tool = new ScienceTool(this, type, defaultExpr, unit, worldExprs, regionExprs, convs,
                    disabledWorlds);
            this.tools.put(type, tool);
        }

        Utils.log("&eScience tools loaded!");
    }

    public String fillIn(CommandSender sender, String expr, Location loc) {
        return JSPlaceholder.prepareExpression(JSContext.create(loc), expr);
    }

    public ScienceTool getTool(ToolType type) {
        return this.tools.getOrDefault(type, null);
    }

    public Set<ToolType> getLoadedTools() {
        return this.tools.keySet();
    }

    public String getMainUnit(ToolType type) {
        ScienceTool tool = getTool(type);
        return tool == null ? "" : tool.getMainUnit();
    }

    public ScienceTools getPlugin() {
        return this.plugin;
    }

    public List<String> toolTabComplete(String hint) {
        return getLoadedTools().stream()
                .map(ToolType::name)
                .filter(v -> v.toLowerCase().startsWith(hint.toLowerCase()))
                .collect(Collectors.toList());
    }
}
