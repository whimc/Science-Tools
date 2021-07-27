package edu.whimc.sciencetools.models.sciencetool;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holds ScienceTool information.
 */
public class ScienceTool {

    /* The key for this tool within the config. */
    protected String toolKey;
    /* Displayed when referencing the tool in game. */
    protected String displayName;

    /* Default measurement to be used when no region or world measurement is found. */
    protected String defaultMeasurement;

    /* World-specific global measurements. */
    protected Map<World, String> worldMeasurements;
    /* Region-specific measurements. Each world has separate regions. */
    protected Map<World, Map<String, String>> regionMeasurements;
    /* Worlds where you cannot measure the science tool. */
    protected Set<World> disabledWorlds;

    /**
     * Constructs a ScienceTool.
     *
     * @param toolKey the tool's key within the config
     * @param displayName the tool's in-game name
     * @param defaultMeasurement the measurement used when no region- or world-specific measurements are found
     * @param worldMeasurements all world-specific global measurements
     * @param regionMeasurements all region-specific measurements
     * @param disabledWorlds all worlds where the tool cannot be measured
     */
    public ScienceTool(String toolKey,
                       String displayName,
                       String defaultMeasurement,
                       Map<World, String> worldMeasurements,
                       Map<World, Map<String, String>> regionMeasurements,
                       Set<World> disabledWorlds) {
        this.toolKey = toolKey;
        this.displayName = displayName;
        this.defaultMeasurement = defaultMeasurement;
        this.worldMeasurements = worldMeasurements;
        this.regionMeasurements = regionMeasurements;
        this.disabledWorlds = disabledWorlds;
    }

    /**
     * Get the measurement string for this tool based off the region the location is located in.
     *
     * @param loc The location to take the measurement.
     * @return The measurement string or null if none exists.
     */
    private @Nullable String getRegionMeasurement(Location loc) {
        if (!Utils.worldGuardEnabled()) {
            return null;
        }

        if (!this.regionMeasurements.containsKey(loc.getWorld())) {
            return null;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        if (container == null) {
            return null;
        }

        RegionManager regionManager = container.get(BukkitAdapter.adapt(loc.getWorld()));
        if (regionManager == null) {
            return null;
        }

        BlockVector3 bv = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        List<String> regions = regionManager.getApplicableRegionsIDs(bv);

        for (String region : regions) {
            String measurement = this.regionMeasurements.get(loc.getWorld()).getOrDefault(region, null);
            if (measurement != null) {
                return measurement;
            }
        }

        return null;
    }

    /**
     * Get the measurement string for this tool based off the world the location is located in.
     *
     * @param loc The location to take the measurement.
     * @return The measurement string or null if none exists.
     */
    private @Nullable String getWorldMeasurement(Location loc) {
        return this.worldMeasurements.getOrDefault(loc.getWorld(), null);
    }

    /**
     * Get the measurement string for this tool based off the given location.
     *
     * @param loc The location to take the measurement.
     * @return The measurement string.
     */
    protected String getMeasurement(Location loc) {
        String measurement = getRegionMeasurement(loc);

        if (measurement == null) {
            measurement = getWorldMeasurement(loc);
        }

        if (measurement == null) {
            measurement = this.defaultMeasurement;
        }

        return measurement;
    }

    /**
     * Display the measurement string to the player based off their current location.
     *
     * @param player The target player.
     */
    public void displayMeasurement(Player player) {
        if (this.disabledWorlds.contains(player.getWorld())) {
            Utils.msg(player, "&cWe don't know how to measure " + this.displayName + " here!");
            return;
        }

        Utils.msg(player, getMeasurement(player.getLocation()));
    }

    /**
     * @return The tool's key.
     */
    public String getToolKey() {
        return this.toolKey;
    }

    /**
     * @return The tool's display name.
     */
    public String getDisplayName() {
        return this.displayName;
    }

}
