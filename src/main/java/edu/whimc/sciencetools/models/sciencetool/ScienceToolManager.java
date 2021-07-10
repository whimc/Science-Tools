package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSNumericalExpression;
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
    private final Map<ToolType, ScienceTool> tools;

    public ScienceToolManager(ConversionManager conversionManager) {
        this.tools = new HashMap<>();
        loadTools(conversionManager);
    }

    public void loadTools(@NotNull ConversionManager conversionManager) {
        FileConfiguration config = ScienceTools.getInstance().getConfig();

        Utils.log("&eLoading Science Tools from config");

        for (ToolType type : ToolType.values()) {
            String name = type.name();
            if (!config.isSet("tools." + name)) {
                Utils.log("&e - No tool entry found for " + name + "!");
                continue;
            }

            Utils.log("&b - Loading &f" + name);
            ConfigurationSection section = config.getConfigurationSection("tools." + name);

            // Ensure there is a default measurement
            if (!section.isSet("default")) {
                Utils.log("&c * `" + section.getCurrentPath() + ".default` does not exist. Skipping!");
                continue;
            }

            String defaultMeasurement = section.getString("default");
            Utils.log("&b\t- Default measurement: \"&f" + defaultMeasurement + "&b\"");

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
                ScienceTool tool = new ScienceTool(type, defaultMeasurement, worldMeasurements, regionMeasurements, disabledWorlds);
                this.tools.put(type, tool);
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

            NumericScienceTool tool = new NumericScienceTool(type, defaultMeasurement, worldMeasurements,
                    regionMeasurements, disabledWorlds, unit, conversions);
            this.tools.put(type, tool);
        }

        Utils.log("&eScience tools loaded!");
    }

    public ScienceTool getTool(ToolType type) {
        return this.tools.getOrDefault(type, null);
    }

    public Set<ToolType> getLoadedTools() {
        return this.tools.keySet();
    }

    public List<String> toolTabComplete(String hint) {
        return getLoadedTools().stream()
                .map(ToolType::name)
                .filter(tool -> tool.toLowerCase().startsWith(hint.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<String> numericToolTabComplete(String hint) {
        return this.tools.values().stream()
                .filter(tool -> tool instanceof  NumericScienceTool)
                .map(tool -> tool.getType().name())
                .filter(tool -> tool.toLowerCase().startsWith(hint.toLowerCase()))
                .collect(Collectors.toList());
    }
}
