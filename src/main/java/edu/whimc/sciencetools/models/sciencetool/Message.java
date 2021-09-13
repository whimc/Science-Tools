package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Class to represent messages for science tools
 */

public enum Message {

    MEASURE("measure-format"),

    NUMERIC_MEASURE("numeric-measure-format"),

    DISABLED_IN_WORLD("disabled-in-world"),
    ;

    private final String TOOL_PLACEHOLDER = "{TOOL}";
    private final String MEASUREMENT_PLACEHOLDER = "{MEASUREMENT}";
    private final String UNIT_PLACEHOLDER = "{UNIT}";

    private String configPath;

    private Message(String configPath) {
        this.configPath = configPath;
    }

    public String format(ScienceTool tool, Player player) {
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        String fallback = (String) config.get("messages." + this.configPath);
        ConfigurationSection section = config.getConfigurationSection("tools." + tool.getToolKey());
        String local = (String) section.get("messages." + this.configPath);
        // do all the tool checks here to see if the message is overridden
        // and to conditionally fill in information depending if 'tool' is a NumericScienceTool or not
        String message = "";
        if(local != null)
            message = local;
        else
            message = fallback;
        message = message.replace(TOOL_PLACEHOLDER,tool.displayName);
        if(tool instanceof NumericScienceTool) {
            NumericScienceTool nTool = (NumericScienceTool) tool;
            double data = nTool.getData(player.getLocation());
            message = message.replace(MEASUREMENT_PLACEHOLDER, Utils.trimDecimals(data, nTool.getPrecision()));
        }
        else {
            String measurement = tool.getMeasurement(player.getLocation());
            message = message.replace(MEASUREMENT_PLACEHOLDER, measurement);
        }
        if(tool instanceof NumericScienceTool) {
            NumericScienceTool nTool = (NumericScienceTool) tool;
            message = message.replace(UNIT_PLACEHOLDER, nTool.getMainUnit());
        }
        return message;
    }
}

