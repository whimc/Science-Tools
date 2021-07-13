package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSNumericalExpression;
import edu.whimc.sciencetools.javascript.JSPlaceholder;
import edu.whimc.sciencetools.models.conversion.Conversion;
import edu.whimc.sciencetools.models.conversion.ConversionManager;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ScienceToolManager {

    /* Tools are identified by their lowercase name */
    private final Map<String, ScienceTool> tools;

    public ScienceToolManager(ConversionManager conversionManager) {
        this.tools = new HashMap<>();
        loadTools(conversionManager);
    }

    public void loadTools(@NotNull ConversionManager conversionManager) {
        FileConfiguration config = ScienceTools.getInstance().getConfig();

        Utils.log("&eLoading Science Tools from config");

        for (String toolKey : config.getConfigurationSection("tools").getKeys(false)) {
            Utils.log("&b - Loading &f" + toolKey);

            if (toolKey.contains(" ")) {
                Utils.log("&c* Tool name cannot contain whitespace! Skipping.");
                continue;
            }

            // Prevent duplicate tool keys
            if (this.tools.containsKey(toolKey.toLowerCase())) {
                Utils.log("&c* A tool with the same key already exists! Skipping.");
                continue;
            }

            ConfigurationSection section = config.getConfigurationSection("tools." + toolKey);

            // Ensure there is a default measurement
            if (!section.isSet("default")) {
                Utils.log("&c * `" + section.getCurrentPath() + ".default` does not exist! Skipping.");
                continue;
            }

            String defaultMeasurement = section.getString("default");
            Utils.log("&b\t- Default measurement: \"&f" + defaultMeasurement + "&b\"");

            String displayName = section.getString("display-name", toolKey);
            Utils.log("&b\t- Display name: \"&f" + displayName + "&b\"");

            // Load disabled worlds
            Set<World> disabledWorlds = new HashSet<>();
            if (section.isSet("disabled-worlds")) {
                Utils.log("&b\t- Loading disabled worlds");
                for (String worldName : section.getStringList("disabled-worlds")) {
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        Utils.log("&c\t\t- Unknown world " + worldName);
                        continue;
                    }
                    Utils.log("&b\t\t- &f" + worldName);
                    disabledWorlds.add(world);
                }
            }

            // Load world-specific measurements
            Map<World, String> worldMeasurements = new HashMap<>();
            if (section.isSet("worlds")) {
                Utils.log("&b\t- Loading world-specific measurements");
                for (String worldName : section.getConfigurationSection("worlds").getKeys(false)) {
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        Utils.log("&c\t\t- Unknown world " + worldName);
                        continue;
                    }

                    String worldMeasurement = section.getString("worlds." + worldName);
                    Utils.log("&b\t\t- &f" + worldName + "&b: \"&f" + worldMeasurement + "&b\"");
                    worldMeasurements.put(world, worldMeasurement);
                }
            }

            // Load region-specific measurements
            Map<String, String> regionMeasurements = new HashMap<>();
            if (section.isSet("regions")) {
                Utils.log("&b\t- Loading region-specific measurements");
                for (String region : section.getConfigurationSection("regions").getKeys(false)) {
                    String regionMeasurement = section.getString("regions." + region);
                    Utils.log("&b\t\t- &f" + region + "&b: \"&f" + regionMeasurement + "&b\"");
                    regionMeasurements.put(region, regionMeasurement);
                }
            }

            // If the default measurement is not a valid numerical expression, parse as a string-based science tool
            JSNumericalExpression defaultExpression = new JSNumericalExpression(defaultMeasurement);
            if (!defaultExpression.valid()) {
                ScienceTool tool = new ScienceTool(toolKey, displayName, defaultMeasurement, worldMeasurements, regionMeasurements, disabledWorlds);
                this.tools.put(toolKey.toLowerCase(), tool);
                continue;
            }


            // Continue parsing as a numerical tool

            String unit = section.getString("unit", "");
            Utils.log("&b\t- Unit: \"&f" + unit + "&b\"");

            // Load conversions
            List<Conversion> conversions = new ArrayList<>();
            if (section.isSet("conversions")) {
                Utils.log("&b\t- Loading conversions");
                for (String conversionName : section.getStringList("conversions")) {
                    Conversion conversion = conversionManager.getConversion(conversionName);

                    if (conversion == null) {
                        Utils.log("&c\t\t- Unknown conversion " + conversionName);
                        continue;
                    }

                    Utils.log("&b\t\t- &f" + conversionName);
                    conversions.add(conversion);
                }
            }

            NumericScienceTool tool = new NumericScienceTool(toolKey, displayName, defaultMeasurement, worldMeasurements,
                    regionMeasurements, disabledWorlds, unit, conversions);
            this.tools.put(toolKey.toLowerCase(), tool);
            JSPlaceholder.registerPlaceholder(tool);
        }

        Utils.log("&eScience tools loaded!");
    }

    public ScienceTool getTool(String key) {
        return this.tools.getOrDefault(key.toLowerCase(), null);
    }

    public Collection<ScienceTool> getTools() {
        return this.tools.values();
    }

    public List<String> toolTabComplete(String hint) {
        return getTools().stream()
                .map(ScienceTool::getToolKey)
                .map(String::toLowerCase)
                .filter(key -> key.startsWith(hint.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<String> numericToolTabComplete(String hint) {
        return getTools().stream()
                .filter(tool -> tool instanceof NumericScienceTool)
                .map(ScienceTool::getToolKey)
                .map(String::toLowerCase)
                .filter(key -> key.startsWith(hint.toLowerCase()))
                .collect(Collectors.toList());
    }
}
