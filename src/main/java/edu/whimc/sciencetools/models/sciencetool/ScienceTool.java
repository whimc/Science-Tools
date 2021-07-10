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

public class ScienceTool {

    protected ToolType type;

    protected String defaultMeasurement;

    protected Map<World, String> worldMeasurements;
    protected Map<String, String> regionMeasurements;
    protected Set<World> disabledWorlds;

    public ScienceTool(ToolType type, String defaultMeasurement,
                       Map<World, String> worldMeasurements,
                       Map<String, String> regionMeasurements,
                       Set<World> disabledWorlds) {
        this.type = type;
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
            String measurement = this.regionMeasurements.getOrDefault(region, null);
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
            Utils.msg(player, "&cWe don't know how to measure " + this.type.toString().toLowerCase() + " here!");
            return;
        }

        Utils.msg(player, getMeasurement(player.getLocation()));
    }

    /**
     * Get the type of this science tool.
     *
     * @return The science tool's type.
     */
    public ToolType getType() {
        return this.type;
    }

}
