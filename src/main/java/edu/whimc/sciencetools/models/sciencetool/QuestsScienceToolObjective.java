package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quests;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.Map;

public class QuestsScienceToolObjective extends CustomObjective {

    private static final String KEY = "Science Tool";

    private Quests quests;

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

        // Increment the count for this objective on all quests for the player
        for (Quest quest : this.quests.getQuester(event.getPlayer().getUniqueId()).getCurrentQuests().keySet()) {
            Map<String, Object> data = getDataForPlayer(event.getPlayer(), this, quest);
            String toolName = (String) data.get(KEY);

            Utils.log("objective tool is " + toolName);
            Utils.log("event tool is " + event.getScienceTool().getToolKey());

            // Make sure the measured tool was the one tracked by this objective
            if (!event.getScienceTool().getToolKey().equalsIgnoreCase(toolName)) {
                continue;
            }

            super.incrementObjective(event.getPlayer(), this, 1, quest);
        }
    }
}
