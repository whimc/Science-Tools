package edu.whimc.sciencetools.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSPlaceholder;
import edu.whimc.sciencetools.models.Conversion;
import edu.whimc.sciencetools.models.ScienceTool;
import edu.whimc.sciencetools.utils.Utils;

public class ScienceToolManager {

    public static enum ToolType {
        ALTITUDE(JSPlaceholder.ALTITUDE),
        OXYGEN(JSPlaceholder.OXYGEN),
        PRESSURE(JSPlaceholder.PRESSURE),
        RADIATION(JSPlaceholder.RADIATION),
        TEMPERATURE(JSPlaceholder.TEMPERATURE),
        WIND(JSPlaceholder.WIND),
        ;

        private JSPlaceholder placeholder;

        private ToolType(JSPlaceholder placeholder) {
            this.placeholder = placeholder;
        }

        public JSPlaceholder getPlaceholder() {
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
    private ScienceTools plugin;

    private ScienceToolManager(ScienceTools plugin) {
        this.tools = new HashMap<>();
        this.plugin = plugin;
    }

    public static ScienceToolManager loadTools(ScienceTools plugin, ConversionManager convManager) {
        ScienceToolManager manager = new ScienceToolManager(plugin);

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

            List<String> disabledWorlds = new ArrayList<>();
            if (plugin.getConfig().contains("tools." + name + ".disabled-worlds")) {
                Utils.log(plugin, ChatColor.AQUA + "     - Loading disabled worlds");
                for (String world : plugin.getConfig().getStringList("tools." + name + ".disabled-worlds")) {
                    disabledWorlds.add(world);
                    Utils.log(plugin, ChatColor.AQUA + "       - " + ChatColor.WHITE + world);
                }
            }

            ScienceTool tool = new ScienceTool(manager, type, defaultExpr, unit, worldExprs, regionExprs, convs, disabledWorlds);
            manager.tools.put(type, tool);
        }

        Utils.log(plugin, ChatColor.YELLOW + "Science tools loaded!");

        return manager;
    }

    public String fillIn(CommandSender sender, String expr, Location loc) {
        // Replace position placeholders
        expr = expr.replace("{X}", Double.toString(loc.getX()));
        expr = expr.replace("{Y}", Double.toString(loc.getY()));
        expr = expr.replace("{Z}", Double.toString(loc.getZ()));

        // Replace tool placeholders
        for (ToolType curType : ToolType.values()) {
            JSPlaceholder ph = curType.getPlaceholder();
            if (!expr.contains(ph.toString())) {
                continue;
            }

            ScienceTool targetTool = getTool(curType);
            if (targetTool == null) {
                Utils.msg(sender, "&f" + curType + " &cis not loaded. Replacing with &f1");
                expr = expr.replace(ph.toString(), "1");
                continue;
            }

            double val = Utils.executeExpression(Bukkit.getConsoleSender(), targetTool.getExpression(sender, loc));
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
                .sorted()
                .collect(Collectors.toList());
    }
}
