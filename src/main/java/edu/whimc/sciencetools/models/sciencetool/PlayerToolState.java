package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.Measurement;
import edu.whimc.sciencetools.models.conversion.Conversion;
import edu.whimc.sciencetools.utils.Utils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Per-player cooldowns, unit choices, and recent measurements.
 */
public class PlayerToolState {

    private static final int HISTORY_SIZE = 5;
    private static final int DEFAULT_COOLDOWN_SECONDS = 5;

    private final Map<UUID, Map<String, Long>> lastMeasure = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> unitIndex = new HashMap<>();
    private final Map<UUID, Deque<Measurement>> history = new HashMap<>();

    /**
     * Seconds left before this player can measure this tool again.
     *
     * @param player The player.
     * @param tool   The tool they want to use.
     * @return Remaining seconds, or 0 if they can measure now.
     */
    public int cooldownRemaining(Player player, ScienceTool tool) {
        int cooldown = cooldownSeconds();
        if (cooldown <= 0) {
            return 0;
        }
        Map<String, Long> byTool = this.lastMeasure.get(player.getUniqueId());
        if (byTool == null) {
            return 0;
        }
        Long last = byTool.get(tool.getToolKey().toLowerCase());
        if (last == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - last;
        long remaining = cooldown * 1000L - elapsed;
        return remaining <= 0 ? 0 : (int) Math.ceil(remaining / 1000.0);
    }

    /**
     * Records that this player just measured this tool.
     *
     * @param player The player.
     * @param tool   The tool that was measured.
     */
    public void markMeasured(Player player, ScienceTool tool) {
        this.lastMeasure
                .computeIfAbsent(player.getUniqueId(), unused -> new HashMap<>())
                .put(tool.getToolKey().toLowerCase(), System.currentTimeMillis());
    }

    /**
     * Plays a click sound and shows a short title with the measurement.
     *
     * @param player       The player.
     * @param tool         The tool that was measured.
     * @param measurement  The value shown to the player.
     */
    public void playSuccess(Player player, ScienceTool tool, String measurement) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.4F);
        String subtitle = ChatColor.stripColor(Utils.colored(measurement));
        if (subtitle.length() > 48) {
            subtitle = subtitle.substring(0, 45) + "...";
        }
        player.sendTitle(Utils.colored("&b" + tool.getDisplayName()), Utils.colored("&f" + subtitle), 5, 40, 10);
    }

    /**
     * Stores a measurement in the player's last-five history.
     *
     * @param measurement The measurement to remember.
     */
    public void remember(Measurement measurement) {
        Deque<Measurement> recent = this.history.computeIfAbsent(
                measurement.getPlayer().getUniqueId(), unused -> new ArrayDeque<>());
        recent.addFirst(measurement);
        while (recent.size() > HISTORY_SIZE) {
            recent.removeLast();
        }
    }

    /**
     * The player's most recent measurements, newest first.
     *
     * @param player The player.
     * @return Up to five measurements.
     */
    public List<Measurement> recent(Player player) {
        Deque<Measurement> recent = this.history.get(player.getUniqueId());
        return recent == null ? new ArrayList<>() : new ArrayList<>(recent);
    }

    /**
     * Cycles this player's display unit for a numeric tool.
     *
     * @param player The player.
     * @param tool   The numeric tool.
     * @return A short description of the new unit.
     */
    public String cycleUnit(Player player, NumericScienceTool tool) {
        int choices = tool.getConversions().size() + 1;
        int next = (selectedIndex(player, tool) + 1) % choices;
        this.unitIndex
                .computeIfAbsent(player.getUniqueId(), unused -> new HashMap<>())
                .put(tool.getToolKey().toLowerCase(), next);
        return unitLabel(player, tool);
    }

    /**
     * The conversion this player selected, or null for the tool's main unit.
     *
     * @param player The player.
     * @param tool   The numeric tool.
     * @return The selected conversion, or null.
     */
    public Conversion selectedConversion(Player player, NumericScienceTool tool) {
        int index = selectedIndex(player, tool);
        if (index <= 0 || index > tool.getConversions().size()) {
            return null;
        }
        return tool.getConversions().get(index - 1);
    }

    /**
     * A short label for the player's current unit.
     *
     * @param player The player.
     * @param tool   The numeric tool.
     * @return The unit text.
     */
    public String unitLabel(Player player, NumericScienceTool tool) {
        Conversion conversion = selectedConversion(player, tool);
        if (conversion == null) {
            String unit = tool.getMainUnit();
            return unit == null || unit.isEmpty() ? "default" : unit.trim();
        }
        String unit = conversion.getUnit();
        return unit == null || unit.isEmpty() ? conversion.getName() : unit.trim();
    }

    private int selectedIndex(Player player, NumericScienceTool tool) {
        Map<String, Integer> byTool = this.unitIndex.get(player.getUniqueId());
        if (byTool == null) {
            return 0;
        }
        Integer index = byTool.get(tool.getToolKey().toLowerCase());
        return index == null ? 0 : index;
    }

    private int cooldownSeconds() {
        return ScienceTools.getInstance().getConfig().getInt("gui.measure-cooldown-seconds", DEFAULT_COOLDOWN_SECONDS);
    }
}
