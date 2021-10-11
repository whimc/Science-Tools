package edu.whimc.sciencetools.models;

import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.utils.Utils;
import java.sql.Timestamp;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Stores information relevant to the event of a player making a measurement.
 */
public class Measurement {

    private final Timestamp timestamp;
    private final Player player;
    private final Location location;
    private final ScienceTool tool;
    private final String measurement;

    /**
     * Construct a measurement.
     *
     * @param timestamp The time the measurement was taken.
     * @param player The player that took the measurement.
     * @param location The location of the measurement.
     * @param tool The tool used.
     * @param measurement The measurement displayed to the player.
     */
    public Measurement(Timestamp timestamp, Player player, Location location, ScienceTool tool, String measurement) {
        this.timestamp = timestamp;
        this.player = player;
        this.location = location;
        this.tool = tool;
        this.measurement = measurement;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return this.location;
    }

    public ScienceTool getTool() {
        return this.tool;
    }

    public String getMeasurement() {
        return this.measurement;
    }

    /**
     * Display the measurement to a user.
     *
     * @param sender The user to send the measurement to.
     */
    public void displayToUser(CommandSender sender) {
        String date = Utils.getDate(this.timestamp);
        String x = Utils.trimDecimals(this.location.getX(), 1);
        String y = Utils.trimDecimals(this.location.getY(), 1);
        String z = Utils.trimDecimals(this.location.getZ(), 1);

        // Send special chat component if displaying to Player
        if (sender instanceof Player) {
            Utils.sendComponent((Player) sender, "&8&l+ &f&l" + this.tool.getToolKey() + " &e" + this.measurement,
                    "&7Time: &e" + date,
                    "&7Location: &e" + this.location.getWorld().getName() + "&7, &e" + x + "&7, &e" + y + "&7, &e" + z,
                    "&7Measurement: &e" + this.measurement,
                    "&7Tool: &e" + this.tool.getToolKey());
            return;
        }

        Utils.msg(sender, "&7[" + this.location.getWorld().getName() + " | " + x + ", " + y + ", " + z + "] "
                + "&f" + this.tool.getToolKey() + ": &e" + this.measurement);
    }
}
