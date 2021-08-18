package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSNumericExpression;
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

/**
 * Manages the ScienceTools.
 */
public class ScienceToolManager {

    /* Tools are identified by their lowercase name */
    private final Map<String, ScienceTool> tools;

    /**
     * Constructs a ScienceToolManager.
     *
     * @param conversionManager The ConversionManager.
     */
    public ScienceToolManager(ConversionManager conversionManager) {
        this.tools = new HashMap<>();
        loadTools(conversionManager);
    }

    /**
     * Loads the valid ScienceTools from the config.
     *
     * @param conversionManager The ConversionManager.
     */
    public void loadTools(@NotNull ConversionManager conversionManager) {
        FileConfiguration config = ScienceTools.getInstance().getConfig();

        Utils.log("&eLoading Science Tools from config");

        // Remove potentially pre-existing placeholders and root commands
        JSPlaceholder.unregisterCustomPlaceholders();
        this.tools.values().forEach(tool -> tool.command.unregister());

        this.tools.clear();

        for (String toolKey : config.getConfigurationSection("tools").getKeys(false)) {
            Utils.log("&b - &f" + toolKey);

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
            String defaultMeasurement = section.getString("default-measurement");
            if (defaultMeasurement == null) {
                Utils.log("&c * `" + section.getCurrentPath() + ".default-measurement` does not exist! Skipping.");
                continue;
            }
            Utils.log("&b\t- Default measurement: \"&f" + defaultMeasurement + "&b\"");

            String displayName = section.getString("display-name", toolKey);
            Utils.log("&b\t- Display name: \"&f" + displayName + "&b\"");

            // Load aliases
            Utils.log("&b\t- Aliases:");
            List<String> aliases = new ArrayList<String>();
            for (String alias : section.getStringList("aliases")) {
                // Ensure aliases do not contain spaces
                if (alias.contains(" ")) {
                    Utils.log("&c* Alias cannot contain whitespace! Skipping.");
                    continue;
                }
                aliases.add(alias);
                Utils.log("&b\t\t- \"&f" + alias + "&b\"");
            }

            // Load disabled worlds
            Set<World> disabledWorlds = new HashSet<>();
            if (section.isSet("disabled-worlds")) {
                Utils.log("&b\t- Disabled worlds");
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

            // Load world settings
            Map<World, String> worldMeasurements = new HashMap<>();
            Map<World, Map<String, String>> worldRegionMeasurements = new HashMap<>();

            if (section.isSet("worlds")) {
                Utils.log("&b\t- World settings");
                for (String worldName : section.getConfigurationSection("worlds").getKeys(false)) {
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        Utils.log("&c\t\t- Unknown world " + worldName);
                        continue;
                    }

                    Utils.log("&b\t\t- &f" + worldName);

                    // Global measurement
                    ConfigurationSection worldSection = section.getConfigurationSection("worlds." + worldName);
                    String globalMeasurement = worldSection.getString("global-measurement");
                    if (globalMeasurement != null) {
                        Utils.log("&b\t\t\t- Global measurement: \"&f" + globalMeasurement + "&b\"");
                        worldMeasurements.put(world, globalMeasurement);
                    }

                    // Region measurements
                    if (worldSection.isSet("regions")) {
                        Utils.log("&b\t\t\t- Regions");
                        Map<String, String> regionMeasurements = new HashMap<>();
                        for (String region : worldSection.getConfigurationSection("regions").getKeys(false)) {
                            String regionMeasurement = worldSection.getString("regions." + region);
                            Utils.log("&b\t\t\t\t- &f" + region + "&b: \"&f" + regionMeasurement + "&b\"");
                            regionMeasurements.put(region, regionMeasurement);
                        }
                        worldRegionMeasurements.put(world, regionMeasurements);
                    }

                }
            }

            // If the default measurement is not a valid numeric expression, parse as a string-based science tool
            JSNumericExpression defaultExpression = new JSNumericExpression(defaultMeasurement);
            if (!defaultExpression.valid()) {
                ScienceTool tool = new ScienceTool(toolKey, displayName, aliases, defaultMeasurement, worldMeasurements,
                        worldRegionMeasurements, disabledWorlds);
                this.tools.put(toolKey.toLowerCase(), tool);
                continue;
            }


            // Continue parsing as a numeric tool

            String unit = section.getString("unit", "");
            Utils.log("&b\t- Unit: \"&f" + unit + "&b\"");

            int precision = section.getInt("precision", 2);
            Utils.log("&b\t- Precision: &f" + precision + "&b decimal" + (precision == 1 ? "" : "s"));

            // Load conversions
            List<Conversion> conversions = new ArrayList<>();
            if (section.isSet("conversions")) {
                Utils.log("&b\t- Conversions");
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

            NumericScienceTool tool = new NumericScienceTool(toolKey, displayName, aliases, defaultMeasurement,
                    worldMeasurements, worldRegionMeasurements, disabledWorlds, unit, precision, conversions);
            this.tools.put(toolKey.toLowerCase(), tool);
            JSPlaceholder.registerCustomPlaceholder(tool);
        }

        Utils.log("&eScience tools loaded!");
    }

    /**
     * Gets the specified ScienceTool.
     *
     * @param key The ScienceTool's name.
     * @return The ScienceTool.
     */
    public ScienceTool getTool(String key) {
        return this.tools.getOrDefault(key.toLowerCase(), null);
    }

    /**
     * @return The Collection of ScienceTools.
     */
    public Collection<ScienceTool> getTools() {
        return this.tools.values();
    }

    /**
     * Gets a list of tool names that match the given hint.
     *
     * @param hint The starting characters to filter by.
     * @return The list of valid tool names.
     */
    public List<String> toolTabComplete(String hint) {
        return getTools().stream()
                .map(ScienceTool::getToolKey)
                .map(String::toLowerCase)
                .filter(key -> key.startsWith(hint.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of numeric tool names that match the given hint.
     *
     * @param hint The starting characters to filter by.
     * @return The list of valid tool names.
     */
    public List<String> numericToolTabComplete(String hint) {
        return getTools().stream()
                .filter(tool -> tool instanceof NumericScienceTool)
                .map(ScienceTool::getToolKey)
                .map(String::toLowerCase)
                .filter(key -> key.startsWith(hint.toLowerCase()))
                .collect(Collectors.toList());
    }
}
