package edu.whimc.sciencetools.utils;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSNumericExpression;
import edu.whimc.sciencetools.javascript.JSPlaceholder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Logs config problems as warnings without skipping tools.
 */
public final class ConfigChecker {

    private ConfigChecker() {
    }

    /**
     * Walks the loaded config and prints warnings for likely mistakes.
     */
    public static void check() {
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        Utils.log("&eChecking science tool config...");
        int warnings = 0;
        warnings += checkPlaceholderCollisions();
        warnings += checkConversions(config);
        warnings += checkTools(config);
        if (warnings == 0) {
            Utils.log("&aConfig check finished with no warnings.");
        } else {
            Utils.log("&eConfig check finished with " + warnings + " warning"
                    + (warnings == 1 ? "" : "s") + ".");
        }
    }

    private static int checkPlaceholderCollisions() {
        int warnings = 0;
        List<String> keys = new ArrayList<>(JSPlaceholder.getAllPlaceholderKeys());
        for (int i = 0; i < keys.size(); i++) {
            String innerA = innerName(keys.get(i));
            for (int j = i + 1; j < keys.size(); j++) {
                String innerB = innerName(keys.get(j));
                if (innerA.isEmpty() || innerB.isEmpty() || innerA.equals(innerB)) {
                    continue;
                }
                if (innerA.startsWith(innerB) || innerB.startsWith(innerA)) {
                    Utils.log("&e  Warning: placeholders " + keys.get(i) + " and " + keys.get(j)
                            + " overlap; the longest match is used first.");
                    warnings++;
                }
            }
        }
        return warnings;
    }

    private static int checkConversions(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("conversions");
        if (section == null) {
            return 0;
        }
        int warnings = 0;
        for (String name : section.getKeys(false)) {
            String expr = section.getString(name + ".expression");
            warnings += warnExpression("conversions." + name + ".expression", expr);
        }
        return warnings;
    }

    private static int checkTools(FileConfiguration config) {
        ConfigurationSection tools = config.getConfigurationSection("tools");
        if (tools == null) {
            return 0;
        }
        int warnings = 0;
        for (String toolKey : tools.getKeys(false)) {
            ConfigurationSection tool = tools.getConfigurationSection(toolKey);
            if (tool == null) {
                continue;
            }
            warnings += warnExpression("tools." + toolKey + ".default-measurement",
                    tool.getString("default-measurement"));

            ConfigurationSection worlds = tool.getConfigurationSection("worlds");
            if (worlds == null) {
                continue;
            }
            for (String worldName : worlds.getKeys(false)) {
                if (Bukkit.getWorld(worldName) == null) {
                    Utils.log("&e  Warning: world \"" + worldName + "\" for " + toolKey
                            + " is not loaded yet (Multiverse worlds are used when they exist).");
                    warnings++;
                }
                ConfigurationSection world = worlds.getConfigurationSection(worldName);
                if (world == null) {
                    continue;
                }
                warnings += warnExpression("tools." + toolKey + ".worlds." + worldName + ".global-measurement",
                        world.getString("global-measurement"));
                ConfigurationSection regions = world.getConfigurationSection("regions");
                if (regions == null) {
                    continue;
                }
                for (String region : regions.getKeys(false)) {
                    warnings += warnExpression(
                            "tools." + toolKey + ".worlds." + worldName + ".regions." + region,
                            regions.getString(region));
                }
            }
        }
        return warnings;
    }

    private static int warnExpression(String path, String expr) {
        if (expr == null || expr.isEmpty()) {
            return 0;
        }
        int warnings = 0;
        if (!balancedParens(expr)) {
            Utils.log("&e  Warning: unbalanced parentheses in " + path);
            warnings++;
        }
        if (looksNumeric(expr) && !new JSNumericExpression(expr).valid()) {
            Utils.log("&e  Warning: expression may be invalid in " + path);
            warnings++;
        }
        return warnings;
    }

    private static boolean looksNumeric(String expr) {
        return expr.indexOf('{') >= 0 || expr.indexOf('+') >= 0 || expr.indexOf('*') >= 0
                || expr.indexOf('/') >= 0 || expr.indexOf('(') >= 0;
    }

    private static boolean balancedParens(String expr) {
        int depth = 0;
        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);
            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
                if (depth < 0) {
                    return false;
                }
            }
        }
        return depth == 0;
    }

    private static String innerName(String placeholder) {
        if (placeholder.length() >= 2 && placeholder.charAt(0) == '{'
                && placeholder.charAt(placeholder.length() - 1) == '}') {
            return placeholder.substring(1, placeholder.length() - 1);
        }
        return placeholder;
    }
}
