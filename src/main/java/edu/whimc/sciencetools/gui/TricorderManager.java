package edu.whimc.sciencetools.gui;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Creates and locks the glowing Tricorder item in hotbar slot 9.
 */
public class TricorderManager implements Listener {

    /** Player hotbar slot 9 (0-based index). */
    public static final int HOTBAR_SLOT = 8;
    private static final String DEFAULT_NAME = "Tricorder";

    /**
     * How the Tricorder is given to players.
     */
    public enum Mode {
        OFF,
        LOCKED,
        UNLOCKED
    }

    private final Map<UUID, Mode> playerModes = new HashMap<>();

    /**
     * Builds a glowing Observer named Tricorder.
     *
     * @return The Tricorder item.
     */
    public ItemStack createItem() {
        ItemStack item = new ItemStack(triggerMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Utils.colored("&b" + itemName()));
            meta.setLore(Arrays.asList(
                    Utils.colored("&7Right-click to open science tools"),
                    Utils.colored("&8Tricorder")));
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Whether the stack is the named Tricorder item.
     *
     * @param item The item to check.
     * @return True if this is a Tricorder.
     */
    public boolean isTricorder(ItemStack item) {
        if (item == null || item.getType() != triggerMaterial() || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
        return ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase(itemName());
    }

    /**
     * The configured material used for the Tricorder.
     *
     * @return The trigger material, or Observer if the name is invalid.
     */
    public Material triggerMaterial() {
        String name = ScienceTools.getInstance().getConfig().getString("gui.trigger-item", "OBSERVER");
        Material material = Material.matchMaterial(name == null ? "OBSERVER" : name);
        return material == null ? Material.OBSERVER : material;
    }

    /**
     * Whether the Tricorder is forced into slot 9 for everyone.
     *
     * @return True if the global mode is locked.
     */
    public boolean isLocked() {
        return getGlobalMode() == Mode.LOCKED;
    }

    /**
     * The persisted Tricorder mode for new players.
     *
     * @return The global mode.
     */
    public Mode getGlobalMode() {
        String raw = ScienceTools.getInstance().getConfig().getString("gui.tricorder-mode");
        if (raw != null && !raw.isEmpty()) {
            try {
                return Mode.valueOf(raw.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                // Fall back to the older locked-item flag.
            }
        }
        return ScienceTools.getInstance().getConfig().getBoolean("gui.locked-item", false)
                ? Mode.LOCKED : Mode.OFF;
    }

    /**
     * Persist a global Tricorder mode and apply it to everyone online.
     *
     * @param mode The mode to use for all players.
     */
    public void setGlobalMode(Mode mode) {
        ScienceTools plugin = ScienceTools.getInstance();
        plugin.getConfig().set("gui.tricorder-mode", mode.name().toLowerCase(Locale.ROOT));
        plugin.getConfig().set("gui.locked-item", mode == Mode.LOCKED);
        plugin.saveConfig();
        this.playerModes.clear();
        syncOnlinePlayers();
    }

    /**
     * Persist the locked-item flag and apply or remove Tricorders for everyone online.
     *
     * @param locked Whether the Tricorder should be forced into slot 9.
     */
    public void setLocked(boolean locked) {
        setGlobalMode(locked ? Mode.LOCKED : Mode.OFF);
    }

    /**
     * Apply a session-only mode to one player.
     *
     * @param player The player to update.
     * @param mode   The mode for this player.
     */
    public void setPlayerMode(Player player, Mode mode) {
        this.playerModes.put(player.getUniqueId(), mode);
        applyMode(player);
    }

    /**
     * Force a Tricorder into this player's slot 9 for the rest of the session.
     *
     * @param player The player to update.
     */
    public void force(Player player) {
        setPlayerMode(player, Mode.LOCKED);
    }

    /**
     * Stop forcing a Tricorder for one player and remove it unless the global lock is on.
     *
     * @param player The player to update.
     */
    public void unforce(Player player) {
        setPlayerMode(player, Mode.OFF);
    }

    /**
     * Puts a Tricorder in hotbar slot 9 and removes extras.
     *
     * @param player The player to update.
     */
    public void apply(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack current = inventory.getItem(HOTBAR_SLOT);
        if (!isTricorder(current)) {
            ItemStack existing = findTricorder(inventory);
            if (existing != null) {
                inventory.setItem(HOTBAR_SLOT, existing);
            } else {
                if (current != null && current.getType() != Material.AIR) {
                    relocate(inventory, current);
                }
                inventory.setItem(HOTBAR_SLOT, createItem());
            }
        }
        removeExtraTricorders(inventory);
        player.updateInventory();
    }

    /**
     * Gives a Tricorder without forcing it into slot 9.
     *
     * @param player The player to update.
     */
    public void ensureHas(Player player) {
        PlayerInventory inventory = player.getInventory();
        boolean found = false;
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (isTricorder(inventory.getItem(slot))) {
                inventory.setItem(slot, createItem());
                found = true;
            }
        }
        if (isTricorder(inventory.getItemInOffHand())) {
            inventory.setItemInOffHand(createItem());
            found = true;
        }
        if (!found) {
            ItemStack current = inventory.getItem(HOTBAR_SLOT);
            if (current == null || current.getType() == Material.AIR) {
                inventory.setItem(HOTBAR_SLOT, createItem());
            } else {
                relocate(inventory, createItem());
            }
        }
        player.updateInventory();
    }

    /**
     * Applies this player's effective Tricorder mode.
     *
     * @param player The player to update.
     */
    public void applyMode(Player player) {
        Mode mode = effectiveMode(player);
        if (mode == Mode.LOCKED) {
            apply(player);
        } else if (mode == Mode.UNLOCKED) {
            ensureHas(player);
        } else {
            remove(player);
        }
    }

    /**
     * Removes every Tricorder from the player.
     *
     * @param player The player to update.
     */
    public void remove(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (isTricorder(inventory.getItem(slot))) {
                inventory.setItem(slot, null);
            }
        }
        if (isTricorder(inventory.getItemInOffHand())) {
            inventory.setItemInOffHand(null);
        }
        player.updateInventory();
    }

    /**
     * Gives or removes Tricorders for every online player based on the current lock.
     */
    public void syncOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyMode(player);
        }
    }

    /**
     * Whether this player should keep a locked Tricorder in slot 9.
     *
     * @param player The player to check.
     * @return True if the item should stay locked.
     */
    public boolean shouldLock(Player player) {
        return effectiveMode(player) == Mode.LOCKED;
    }

    /**
     * Whether this player should have a Tricorder at all.
     *
     * @param player The player to check.
     * @return True if the item should be given or kept.
     */
    public boolean shouldHave(Player player) {
        return effectiveMode(player) != Mode.OFF;
    }

    /**
     * The mode that currently applies to this player.
     *
     * @param player The player to check.
     * @return The effective mode.
     */
    public Mode effectiveMode(Player player) {
        Mode override = this.playerModes.get(player.getUniqueId());
        return override == null ? getGlobalMode() : override;
    }

    /**
     * Gives joining players a locked Tricorder, or removes leftovers when it is disabled.
     *
     * @param event The join event.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyMode(event.getPlayer());
    }

    /**
     * Restores the Tricorder after respawn when it is locked.
     *
     * @param event The respawn event.
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(ScienceTools.getInstance(), () -> applyMode(player));
    }

    /**
     * Prevents dropping the Tricorder.
     *
     * @param event The drop event.
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!isTricorder(event.getItemDrop().getItemStack())) {
            return;
        }
        if (!shouldLock(event.getPlayer())) {
            return;
        }
        event.setCancelled(true);
        apply(event.getPlayer());
    }

    /**
     * Prevents picking up extra Tricorders.
     *
     * @param event The pickup event.
     */
    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!isTricorder(event.getItem().getItemStack())) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!shouldLock(player)) {
            return;
        }
        event.setCancelled(true);
        event.getItem().remove();
        apply(player);
    }

    /**
     * Keeps the Tricorder out of death drops.
     *
     * @param event The death event.
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (shouldLock(event.getEntity())) {
            event.getDrops().removeIf(this::isTricorder);
        }
    }

    /**
     * Prevents swapping the Tricorder into the offhand.
     *
     * @param event The swap event.
     */
    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        if (!shouldLock(event.getPlayer())) {
            return;
        }
        if (isTricorder(event.getMainHandItem()) || isTricorder(event.getOffHandItem())) {
            event.setCancelled(true);
            apply(event.getPlayer());
        }
    }

    /**
     * Prevents moving the Tricorder out of hotbar slot 9.
     *
     * @param event The inventory click event.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getInventory().getHolder() instanceof ScienceToolGui.Holder) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!shouldLock(player)) {
            return;
        }

        boolean touchingSlot = event.getClickedInventory() != null
                && event.getClickedInventory().equals(player.getInventory())
                && event.getSlot() == HOTBAR_SLOT;
        boolean touchingItem = isTricorder(event.getCurrentItem()) || isTricorder(event.getCursor());
        boolean hotbarSwap = event.getHotbarButton() == HOTBAR_SLOT;

        if (touchingSlot || touchingItem || hotbarSwap) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(ScienceTools.getInstance(), () -> apply(player));
        }
    }

    /**
     * Prevents dragging the Tricorder out of hotbar slot 9.
     *
     * @param event The inventory drag event.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getInventory().getHolder() instanceof ScienceToolGui.Holder) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!shouldLock(player)) {
            return;
        }
        if (event.getInventorySlots().contains(HOTBAR_SLOT) || isTricorder(event.getOldCursor())) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(ScienceTools.getInstance(), () -> apply(player));
        }
    }

    private String itemName() {
        return ScienceTools.getInstance().getConfig().getString("gui.item-name", DEFAULT_NAME);
    }

    private ItemStack findTricorder(PlayerInventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (slot == HOTBAR_SLOT) {
                continue;
            }
            ItemStack item = inventory.getItem(slot);
            if (isTricorder(item)) {
                inventory.setItem(slot, null);
                return item;
            }
        }
        if (isTricorder(inventory.getItemInOffHand())) {
            ItemStack item = inventory.getItemInOffHand();
            inventory.setItemInOffHand(null);
            return item;
        }
        return null;
    }

    private void removeExtraTricorders(PlayerInventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (slot != HOTBAR_SLOT && isTricorder(inventory.getItem(slot))) {
                inventory.setItem(slot, null);
            }
        }
        if (isTricorder(inventory.getItemInOffHand())) {
            inventory.setItemInOffHand(null);
        }
    }

    private void relocate(PlayerInventory inventory, ItemStack item) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (slot == HOTBAR_SLOT) {
                continue;
            }
            ItemStack existing = inventory.getItem(slot);
            if (existing == null || existing.getType() == Material.AIR) {
                inventory.setItem(slot, item);
                return;
            }
        }
        Player holder = inventory.getHolder() instanceof Player ? (Player) inventory.getHolder() : null;
        if (holder != null) {
            holder.getWorld().dropItemNaturally(holder.getLocation(), item);
        }
    }
}
