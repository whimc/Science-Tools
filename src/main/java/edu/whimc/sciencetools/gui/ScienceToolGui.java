package edu.whimc.sciencetools.gui;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.Measurement;
import edu.whimc.sciencetools.models.sciencetool.NumericScienceTool;
import edu.whimc.sciencetools.models.sciencetool.PlayerToolState;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Tricorder-activated chest GUI for running science tools at the player's location.
 */
public class ScienceToolGui implements Listener {

    private static final String PERMISSION = "sciencetools.user";
    private static final Material DEFAULT_ICON = Material.STONE;
    private static final int HISTORY_SLOT = 0;

    private static final Map<String, Material> DEFAULT_ITEMS = new HashMap<>();
    private static final Map<String, String> DEFAULT_LORE = new HashMap<>();

    static {
        DEFAULT_ITEMS.put("ALTITUDE", Material.LADDER);
        DEFAULT_ITEMS.put("AIRFLOW", materialOr("WIND_CHARGE", Material.WHITE_WOOL));
        DEFAULT_ITEMS.put("ATMOSPHERE", Material.GLASS);
        DEFAULT_ITEMS.put("GRAVITY", Material.ANVIL);
        DEFAULT_ITEMS.put("HUMIDITY", Material.SPONGE);
        DEFAULT_ITEMS.put("MAGNETIC_FIELD", Material.COMPASS);
        DEFAULT_ITEMS.put("PRESSURE", Material.PISTON);
        DEFAULT_ITEMS.put("RADIATION", Material.GLOWSTONE);
        DEFAULT_ITEMS.put("COSMICRAYS", Material.NETHER_STAR);
        DEFAULT_ITEMS.put("RADIUS", Material.ENDER_PEARL);
        DEFAULT_ITEMS.put("ROTATIONAL_PERIOD", Material.CLOCK);
        DEFAULT_ITEMS.put("TECTONIC", Material.MAGMA_BLOCK);
        DEFAULT_ITEMS.put("TEMPERATURE", Material.BLAZE_POWDER);
        DEFAULT_ITEMS.put("TIDES", Material.PRISMARINE);
        DEFAULT_ITEMS.put("TILT", Material.LEVER);
        DEFAULT_ITEMS.put("YEAR", Material.SUNFLOWER);
        DEFAULT_ITEMS.put("SCALE", materialOr("CARTOGRAPHY_TABLE", Material.MAP));

        DEFAULT_LORE.put("ALTITUDE", "How high up you are");
        DEFAULT_LORE.put("AIRFLOW", "How fast the wind is blowing");
        DEFAULT_LORE.put("ATMOSPHERE", "What gases are in the air");
        DEFAULT_LORE.put("GRAVITY", "How hard things get pulled down");
        DEFAULT_LORE.put("HUMIDITY", "How much water is in the air");
        DEFAULT_LORE.put("MAGNETIC_FIELD", "How strong magnets work here");
        DEFAULT_LORE.put("PRESSURE", "How hard the air is pushing");
        DEFAULT_LORE.put("RADIATION", "How much harmful energy is here");
        DEFAULT_LORE.put("COSMICRAYS", "Tiny space particles hitting here");
        DEFAULT_LORE.put("RADIUS", "How big this world is");
        DEFAULT_LORE.put("ROTATIONAL_PERIOD", "How long a day lasts here");
        DEFAULT_LORE.put("TECTONIC", "How much the ground shakes");
        DEFAULT_LORE.put("TEMPERATURE", "How hot or cold it is");
        DEFAULT_LORE.put("TIDES", "How much water rises and falls");
        DEFAULT_LORE.put("TILT", "How tipped this world is");
        DEFAULT_LORE.put("YEAR", "How long a year lasts here");
        DEFAULT_LORE.put("SCALE", "How big or small things are here");
    }

    private final TricorderManager tricorderManager;

    /**
     * Constructs the GUI listener.
     *
     * @param tricorderManager Identifies the Tricorder used to open this GUI.
     */
    public ScienceToolGui(TricorderManager tricorderManager) {
        this.tricorderManager = tricorderManager;
    }

    /**
     * Open the science tools GUI for a player.
     *
     * @param player The player who will see the GUI.
     */
    public void open(Player player) {
        List<ScienceTool> tools = GuiVisibility.visibleTools(player);
        if (tools.isEmpty()) {
            Utils.msg(player, "&cNo science tools are available here.");
            return;
        }

        List<Integer> slots = spacedSlots(tools.size());
        int size = Math.min(54, Math.max(9, ((slots.get(slots.size() - 1) / 9) + 1) * 9));
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        String title = Utils.colored(config.getString("gui.title", "Science Tools"));

        Holder holder = new Holder();
        Inventory inventory = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inventory);
        inventory.setItem(HISTORY_SLOT, historyBook(player));

        for (int i = 0; i < tools.size() && i < slots.size(); i++) {
            int slot = slots.get(i);
            ScienceTool tool = tools.get(i);
            inventory.setItem(slot, iconFor(tool, player));
            holder.toolsBySlot.put(slot, tool);
        }

        player.openInventory(inventory);
    }

    /**
     * Opens the GUI when a player right-clicks the configured trigger item.
     *
     * @param event The interact event.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!this.tricorderManager.isTricorder(item)) {
            return;
        }

        event.setCancelled(true);
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        if (!config.getBoolean("gui.enabled", true) || !player.hasPermission(PERMISSION)) {
            return;
        }
        open(player);
    }

    /**
     * Left-click measures. Shift-click cycles units. Items cannot be taken.
     *
     * @param event The inventory click event.
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Holder)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory top = event.getView().getTopInventory();
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= top.getSize()) {
            return;
        }

        Holder holder = (Holder) top.getHolder();
        Player player = (Player) event.getWhoClicked();
        if (slot == HISTORY_SLOT) {
            showHistory(player);
            return;
        }

        ScienceTool tool = holder.toolsBySlot.get(slot);
        if (tool == null) {
            return;
        }

        if (event.isShiftClick()) {
            cycleUnit(player, top, slot, tool);
            return;
        }

        if (!event.isLeftClick()) {
            return;
        }

        Bukkit.getScheduler().runTask(ScienceTools.getInstance(), () -> {
            player.closeInventory();
            tool.measure(player);
        });
    }

    private void cycleUnit(Player player, Inventory top, int slot, ScienceTool tool) {
        if (!(tool instanceof NumericScienceTool)
                || ((NumericScienceTool) tool).getConversions().isEmpty()) {
            Utils.msg(player, "&7This tool has only one unit.");
            return;
        }
        NumericScienceTool numeric = (NumericScienceTool) tool;
        String unit = ScienceTools.getInstance().getPlayerToolState().cycleUnit(player, numeric);
        top.setItem(slot, iconFor(numeric, player));
        player.updateInventory();
        Utils.msg(player, "&7Unit set to &b" + unit);
    }

    /**
     * Prevents dragging items out of the GUI.
     *
     * @param event The inventory drag event.
     */
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof Holder) {
            event.setCancelled(true);
        }
    }

    /**
     * Places tools in a 6-row chest with a gap between each icon.
     */
    private static List<Integer> spacedSlots(int toolCount) {
        List<Integer> slots = new ArrayList<>();
        int[] columns = {2, 4, 6};
        for (int row = 0; row < 6 && slots.size() < toolCount; row++) {
            for (int column : columns) {
                if (slots.size() >= toolCount) {
                    break;
                }
                slots.add(row * 9 + column);
            }
        }
        return slots;
    }

    private static Material materialOr(String name, Material fallback) {
        Material material = Material.matchMaterial(name);
        return material == null ? fallback : material;
    }

    private ItemStack iconFor(ScienceTool tool, Player player) {
        ItemStack item = new ItemStack(materialFor(tool));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Utils.colored("&b" + tool.getDisplayName()));
            List<String> lore = new ArrayList<>();
            lore.add(Utils.colored("&7" + loreFor(tool)));
            if (tool instanceof NumericScienceTool) {
                NumericScienceTool numeric = (NumericScienceTool) tool;
                PlayerToolState state = ScienceTools.getInstance().getPlayerToolState();
                lore.add(Utils.colored("&eUnit: " + state.unitLabel(player, numeric)));
                if (!numeric.getConversions().isEmpty()) {
                    lore.add(Utils.colored("&8Shift-click to change unit"));
                }
            }
            lore.add(Utils.colored("&8/" + tool.getToolKey().toLowerCase(Locale.ROOT)));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack historyBook(Player player) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Utils.colored("&bLast measurements"));
            List<String> lore = new ArrayList<>();
            List<Measurement> recent = ScienceTools.getInstance().getPlayerToolState().recent(player);
            if (recent.isEmpty()) {
                lore.add(Utils.colored("&7No measurements yet"));
            } else {
                for (Measurement measurement : recent) {
                    lore.add(Utils.colored("&7" + measurement.getTool().getDisplayName()
                            + ": &f" + measurement.getMeasurement()));
                }
            }
            lore.add(Utils.colored("&8Left-click to print in chat"));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void showHistory(Player player) {
        List<Measurement> recent = ScienceTools.getInstance().getPlayerToolState().recent(player);
        if (recent.isEmpty()) {
            Utils.msg(player, "&7No measurements yet.");
            return;
        }
        Utils.msg(player, "&bLast measurements:");
        for (Measurement measurement : recent) {
            measurement.displayToUser(player);
        }
    }

    private String loreFor(ScienceTool tool) {
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        ConfigurationSection lore = config.getConfigurationSection("gui.lore");
        String configured = null;
        if (lore != null) {
            configured = lore.getString(tool.getToolKey());
            if (configured == null) {
                configured = lore.getString(tool.getToolKey().toLowerCase(Locale.ROOT));
            }
        }
        if (configured != null && !configured.isEmpty()) {
            return configured;
        }
        return DEFAULT_LORE.getOrDefault(tool.getToolKey().toUpperCase(Locale.ROOT), "See this measurement");
    }

    private Material materialFor(ScienceTool tool) {
        FileConfiguration config = ScienceTools.getInstance().getConfig();
        ConfigurationSection items = config.getConfigurationSection("gui.items");
        String configured = null;
        if (items != null) {
            configured = items.getString(tool.getToolKey());
            if (configured == null) {
                configured = items.getString(tool.getToolKey().toLowerCase(Locale.ROOT));
            }
        }

        Material material = configured == null ? null : Material.matchMaterial(configured);
        if (material == null) {
            material = DEFAULT_ITEMS.getOrDefault(tool.getToolKey().toUpperCase(Locale.ROOT), DEFAULT_ICON);
        }
        return material;
    }

    /**
     * Marks an inventory as the science tools GUI and maps slots to tools.
     */
    static class Holder implements InventoryHolder {

        private final Map<Integer, ScienceTool> toolsBySlot = new HashMap<>();
        private Inventory inventory;

        private void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return this.inventory;
        }
    }
}
