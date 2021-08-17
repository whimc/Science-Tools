package edu.whimc.sciencetools.models;

import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

public class Measurement {

    private final Timestamp timestamp;
    private final Player player;
    private final Location location;
    private final ScienceTool tool;
    private final String measurement;

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

    public void displayToUser(CommandSender sender) {
        String date = Utils.getDate(this.timestamp);
        String x = Utils.trimDecimals(this.location.getX(), 1);
        String y = Utils.trimDecimals(this.location.getY(), 1);
        String z = Utils.trimDecimals(this.location.getZ(), 1);

        // Send special chat component if displaying to Player
        if (sender instanceof Player) {
            Utils.sendComponent((Player) sender, "&8&l+ &f&l" + this.tool.getToolKey() + " &a" + this.measurement,
                    "Time: " + date,
                    "Location: " + x + ", " + y + ", " + z,
                    "Measurement: " + this.measurement,
                    "Tool: " + this.tool.getToolKey());
            return;
        }

        Utils.msg(sender, "&7[" + this.location.getWorld().getName() + " | " + x + ", " + y + ", " + z + "] " +
                "&f" + this.tool.getToolKey() + ": &a" + this.measurement);
    }
}
