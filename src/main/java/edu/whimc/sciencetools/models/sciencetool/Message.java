package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Class to represent messages for science tools.
 */
public enum Message {

    MEASURE("measure-format"),

    NUMERIC_MEASURE("numeric-measure-format"),

    DISABLED_IN_WORLD("disabled-in-world"),
    ;

    private static final String TOOL_PLACEHOLDER = "{TOOL}";
    private static final String MEASUREMENT_PLACEHOLDER = "{MEASUREMENT}";
    private static final String UNIT_PLACEHOLDER = "{UNIT}";

    private String configPath;

    Message(String configPath) {
        this.configPath = configPath;
    }

    /**
     * Format the given message using the tool and player as context.
     *
     * @param tool The tool the message is about.
     * @param player The player the message is about.
     * @return The formatted message.
     */
    public String format(ScienceTool tool, Player player) {
        return format(tool, player, null, null);
    }

    /**
     * Format the message using a measurement that was already computed.
     *
     * @param tool        The tool the message is about.
     * @param player      The player the message is about.
     * @param measurement The already-evaluated measurement, or null to look it up.
     * @param unit        The unit to show for numeric tools, or null for the main unit.
     * @return The formatted message.
     */
    public String format(ScienceTool tool, Player player, String measurement, String unit) {
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        String fallback = config.getString("messages." + this.configPath);

        String message = config.getString("tools." + tool.getToolKey() + ".messages." + this.configPath);
        if (message == null) {
            message = fallback;
        }

        message = message.replace(TOOL_PLACEHOLDER, tool.displayName);

        if (message.contains(MEASUREMENT_PLACEHOLDER) || message.contains(UNIT_PLACEHOLDER)) {
            if (measurement == null) {
                if (tool instanceof NumericScienceTool) {
                    NumericScienceTool numTool = (NumericScienceTool) tool;
                    double data = numTool.getData(player.getLocation());
                    measurement = Utils.trimDecimals(data, numTool.getPrecision());
                    if (unit == null) {
                        unit = numTool.getMainUnit();
                    }
                } else {
                    measurement = tool.getMeasurement(player.getLocation());
                }
            } else if (unit == null && tool instanceof NumericScienceTool) {
                unit = ((NumericScienceTool) tool).getMainUnit();
            }

            if (measurement != null) {
                message = message.replace(MEASUREMENT_PLACEHOLDER, measurement);
            }
            message = message.replace(UNIT_PLACEHOLDER, unit == null ? "" : unit);
        }
        return message;
    }
}

