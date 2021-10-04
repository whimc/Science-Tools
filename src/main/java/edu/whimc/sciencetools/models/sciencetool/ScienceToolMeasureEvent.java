package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.models.Measurement;
import java.sql.Timestamp;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A custom event to be fired whenever a player measures a science tool.
 */
public class ScienceToolMeasureEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Measurement measurement;

    /**
     * Construct the event.
     *
     * @param player The player that made the measurement.
     * @param scienceTool The tool that was used to make the measurement.
     * @param measurement The measurement that came from the tool.
     */
    public ScienceToolMeasureEvent(Player player, ScienceTool scienceTool, String measurement) {
        this.measurement =
                new Measurement(new Timestamp(System.currentTimeMillis()), player, player.getLocation(), scienceTool,
                        measurement);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Measurement getMeasurement() {
        return this.measurement;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
