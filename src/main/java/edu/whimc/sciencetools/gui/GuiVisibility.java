package edu.whimc.sciencetools.gui;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Per-world GUI visibility for science tools.
 */
public final class GuiVisibility {

    public static final String ALL_WORLDS = "all";

    private GuiVisibility() {
    }

    /**
     * Tools the player should see in the Tricorder GUI.
     *
     * @param player The player opening the GUI.
     * @return Visible tools, sorted by key.
     */
    public static List<ScienceTool> visibleTools(Player player) {
        String world = player.getWorld().getName();
        return ScienceTools.getInstance().getToolManager().getTools().stream()
                .filter(tool -> !isHidden(tool.getToolKey(), world))
                .sorted(Comparator.comparing(tool -> tool.getToolKey().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    /**
     * Whether a tool is hidden in this world, including the global {@code all} list.
     *
     * @param toolKey   The tool key.
     * @param worldName The world name.
     * @return True if the tool should be omitted from the GUI.
     */
    public static boolean isHidden(String toolKey, String worldName) {
        return containsTool(listFor(ALL_WORLDS), toolKey) || containsTool(listFor(worldName), toolKey);
    }

    /**
     * Hide a tool in a world (or {@code all} worlds) and save the config.
     *
     * @param toolKey  The tool key.
     * @param worldKey The world name, or {@code all}.
     * @return True if the tool was newly hidden.
     */
    public static boolean hide(String toolKey, String worldKey) {
        List<String> hidden = listFor(worldKey);
        if (containsTool(hidden, toolKey)) {
            return false;
        }
        hidden.add(toolKey.toUpperCase(Locale.ROOT));
        writeList(worldKey, hidden);
        return true;
    }

    /**
     * Show a tool in a world (or {@code all} worlds) and save the config.
     *
     * @param toolKey  The tool key.
     * @param worldKey The world name, or {@code all}.
     * @return True if the tool was newly shown.
     */
    public static boolean show(String toolKey, String worldKey) {
        List<String> hidden = listFor(worldKey);
        boolean removed = hidden.removeIf(name -> name.equalsIgnoreCase(toolKey));
        if (removed) {
            writeList(worldKey, hidden);
        }
        return removed;
    }

    /**
     * Tool keys hidden for a world, not including the global {@code all} list.
     *
     * @param worldKey The world name, or {@code all}.
     * @return Hidden tool keys.
     */
    public static List<String> hiddenKeys(String worldKey) {
        return listFor(worldKey);
    }

    private static List<String> listFor(String worldKey) {
        ConfigurationSection section = hiddenSection();
        if (section == null) {
            return new ArrayList<>();
        }
        String key = resolveKey(section, worldKey);
        if (key == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(section.getStringList(key));
    }

    private static void writeList(String worldKey, List<String> hidden) {
        ScienceTools plugin = ScienceTools.getInstance();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = hiddenSection();
        if (section == null) {
            section = config.createSection("gui.hidden");
        }
        String key = resolveKey(section, worldKey);
        if (key == null) {
            key = ALL_WORLDS.equalsIgnoreCase(worldKey) ? ALL_WORLDS : worldKey;
        }
        if (hidden.isEmpty()) {
            section.set(key, null);
        } else {
            section.set(key, hidden);
        }
        plugin.saveConfig();
    }

    private static ConfigurationSection hiddenSection() {
        return ScienceTools.getInstance().getConfig().getConfigurationSection("gui.hidden");
    }

    private static String resolveKey(ConfigurationSection section, String worldKey) {
        for (String key : section.getKeys(false)) {
            if (key.equalsIgnoreCase(worldKey)) {
                return key;
            }
        }
        return null;
    }

    private static boolean containsTool(List<String> hidden, String toolKey) {
        for (String name : hidden) {
            if (name.equalsIgnoreCase(toolKey)) {
                return true;
            }
        }
        return false;
    }
}
