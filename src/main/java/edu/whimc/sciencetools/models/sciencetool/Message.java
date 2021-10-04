package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
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
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        String fallback = config.getString("messages." + this.configPath);

        String message = config.getString("tools." + tool.getToolKey() + ".messages." + this.configPath);
        if (message == null) {
            message = fallback;
        }

        message = message.replace(TOOL_PLACEHOLDER, tool.displayName);

        if (tool instanceof NumericScienceTool) {
            NumericScienceTool numTool = (NumericScienceTool) tool;
            double data = numTool.getData(player.getLocation());
            message = message.replace(MEASUREMENT_PLACEHOLDER, Utils.trimDecimals(data, numTool.getPrecision()));
            message = message.replace(UNIT_PLACEHOLDER, numTool.getMainUnit());
        } else {
            String measurement = tool.getMeasurement(player.getLocation());
            message = message.replace(MEASUREMENT_PLACEHOLDER, measurement);
        }

        return message;
    }
}

