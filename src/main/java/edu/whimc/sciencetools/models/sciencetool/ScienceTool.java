package edu.whimc.sciencetools.models.sciencetool;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Super class to represent a ScienceTool. The measurement is just a string that is not evaluated.
 */
public class ScienceTool {

    /* The key for this tool within the config. */
    protected String toolKey;
    /* Displayed when referencing the tool in game. */
    protected String displayName;
    /* Aliases for the command. */
    protected List<String> aliases;

    /* Default measurement to be used when no region or world measurement is found. */
    protected String defaultMeasurement;
    /* World-specific global measurements. */
    protected Map<World, String> worldMeasurements;
    /* Region-specific measurements. Each world has separate regions. */
    protected Map<World, Map<String, String>> regionMeasurements;

    /* Worlds where you cannot measure the science tool. */
    protected Set<World> disabledWorlds;

    /* The root command used to measure this tool */
    protected MeasureCommand command;

    /**
     * Constructs a ScienceTool.
     *
     * @param toolKey            The tool's key within the config.
     * @param displayName        The tool's in-game name.
     * @param aliases            Alternate names for the tool.
     * @param defaultMeasurement The measurement used when no region- or world-specific measurements are found.
     * @param worldMeasurements  All world-specific global measurements.
     * @param regionMeasurements All region-specific measurements.
     * @param disabledWorlds     All worlds where the tool cannot be measured.
     */
    public ScienceTool(String toolKey,
                       String displayName,
                       List<String> aliases,
                       String defaultMeasurement,
                       Map<World, String> worldMeasurements,
                       Map<World, Map<String, String>> regionMeasurements,
                       Set<World> disabledWorlds) {
        this.toolKey = toolKey;
        this.displayName = displayName;
        this.aliases = aliases;
        this.defaultMeasurement = defaultMeasurement;
        this.worldMeasurements = worldMeasurements;
        this.regionMeasurements = regionMeasurements;
        this.disabledWorlds = disabledWorlds;
        this.command = new MeasureCommand();
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
     * @return The measurement
     */
    public @Nullable String displayMeasurement(Player player) {
        if (this.disabledWorlds.contains(player.getWorld())) {
            Utils.msg(player, "&cWe don't know how to measure " + this.displayName + " here!");
            return null;
        }

        String measurement = getMeasurement(player.getLocation());
        Utils.msg(player, measurement);
        return measurement;
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

    public class MeasureCommand extends Command {

        private MeasureCommand() {
            super(ScienceTool.this.toolKey, "Measure the " + ScienceTool.this.displayName,
                    "", ScienceTool.this.aliases);

            if (!getCommandMap().register("WHIMC-ScienceTools", this)) {
                Utils.log("&c\t- Error registering /" + ScienceTool.this.toolKey);
            }
        }

        private CommandMap getCommandMap() {
            try {
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                bukkitCommandMap.setAccessible(true);
                return (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            } catch (Exception exc) {
                exc.printStackTrace();
                return null;
            }
        }

        private Map<String, Command> getKnownCommands() {
            try {
                final Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
                f.setAccessible(true);
                return (Map<String, Command>) f.get(getCommandMap());
            } catch (Exception exc) {
                exc.printStackTrace();
                return null;
            }
        }

        public void unregister() {
            if (!super.unregister(getCommandMap())) {
                Utils.log("&c\t- Error unregistering /" + ScienceTool.this.toolKey);
            }

            getKnownCommands().remove(super.getLabel());
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
            if (!(sender instanceof Player)) {
                Utils.msg(sender, "&cYou must be a player to use this command!");
                return false;
            }

            Player player = (Player) sender;
            String measurement = ScienceTool.this.displayMeasurement(player);

            if (measurement != null) {
                ScienceToolMeasureEvent event = new ScienceToolMeasureEvent(player, ScienceTool.this, measurement);
                Bukkit.getPluginManager().callEvent(event);
            }

            return true;
        }

        @NotNull
        @Override
        public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
            return Collections.emptyList();
        }
    }

}
