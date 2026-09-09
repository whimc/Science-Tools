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
    private final Map<UUID, String[]> titleParts = new HashMap<>();

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
     * Remembers the value and unit from a measurement so the title can show them apart.
     *
     * @param player The player.
     * @param value  The numeric or text reading.
     * @param unit   The unit, or empty when the tool has none.
     */
    public void prepareTitle(Player player, String value, String unit) {
        this.titleParts.put(player.getUniqueId(), new String[]{
                value == null ? "" : value,
                unit == null ? "" : unit
        });
    }

    /**
     * Shows the tool name on the title line and the reading on the subtitle line.
     * Newlines inside either line are ignored by the client, so these must be
     * two separate title slots.
     *
     * @param player The player.
     * @param tool   The tool that was measured.
     */
    public void playSuccess(Player player, ScienceTool tool) {
        if (!showOnScreen()) {
            return;
        }
        String[] parts = this.titleParts.remove(player.getUniqueId());
        String value = parts == null ? "" : parts[0];
        String unit = parts == null ? "" : parts[1];
        String type = ChatColor.stripColor(Utils.colored(tool.getDisplayName())).trim();
        if (type.isEmpty()) {
            type = tool.getToolKey();
        }
        String reading = ChatColor.stripColor(Utils.colored(value == null ? "" : value)).trim();
        String unitText = ChatColor.stripColor(Utils.colored(unit == null ? "" : unit)).trim();
        String amount = reading;
        if (!unitText.isEmpty()) {
            amount = reading.isEmpty() ? unitText : reading + " " + unitText;
        }

        player.sendTitle(Utils.colored("&f" + escapeTitle(type)),
                Utils.colored("&b" + escapeTitle(amount)), 5, 50, 10);
    }

    private static String escapeTitle(String text) {
        return text.replace("%", "%%");
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

    /**
     * Whether measurements should show as an on-screen title.
     *
     * @return True if titles are enabled (default true).
     */
    public boolean showOnScreen() {
        return ScienceTools.getInstance().getConfig().getBoolean("gui.show-on-screen", true);
    }

    /**
     * Whether measurements should print in chat.
     *
     * @return True if chat lines are enabled (default false).
     */
    public boolean showInChat() {
        return ScienceTools.getInstance().getConfig().getBoolean("gui.show-in-chat", false);
    }

    private int cooldownSeconds() {
        return ScienceTools.getInstance().getConfig().getInt("gui.measure-cooldown-seconds", DEFAULT_COOLDOWN_SECONDS);
    }
}
