package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.models.Measurement;
import edu.whimc.sciencetools.utils.Utils;
import java.util.Map;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quests;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

/**
 * A custom objective for the Quests plugin.
 * Adds an objective that requires the player to make X amount of measurements for a given science tool.
 * Requires the user to configure the name of the tool.
 */
public class QuestsScienceToolObjective extends CustomObjective {

    private static final String KEY = "Science Tool";

    private final Quests quests;

    /**
     * You should never have to create an instance of this class. Quests automatically does this.
     */
    public QuestsScienceToolObjective() {
        super.setName("Science Tool Objective");
        super.setAuthor("WHIMC");
        super.setCount(1);
        super.setDisplay("Measure %Science Tool% %count% time(s)");
        super.setCountPrompt("Enter the number of measurements the player must make with this tool:");
        super.addStringPrompt(KEY, "The name of the science tool", "");
        this.quests = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");
    }

    @EventHandler
    public void onMeasure(ScienceToolMeasureEvent event) {
        // Do nothing if quests is not installed
        if (this.quests == null) {
            Utils.log("quests not installed");
            return;
        }

        Measurement measurement = event.getMeasurement();

        // Increment the count for this objective on all quests for the player
        for (Quest quest : this.quests.getQuester(measurement.getPlayer().getUniqueId()).getCurrentQuests().keySet()) {
            Map<String, Object> data = getDataForPlayer(measurement.getPlayer(), this, quest);
            String toolName = (String) data.get(KEY);

            // Make sure the measured tool was the one tracked by this objective
            if (!measurement.getTool().getToolKey().equalsIgnoreCase(toolName)) {
                continue;
            }

            super.incrementObjective(measurement.getPlayer(), this, 1, quest);
        }
    }
}
