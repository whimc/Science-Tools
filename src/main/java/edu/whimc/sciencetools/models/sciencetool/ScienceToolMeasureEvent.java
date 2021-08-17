package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.models.Measurement;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.sql.Timestamp;

/**
 * A custom event to be fired whenever a player measures a science tool.
 */
public class ScienceToolMeasureEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Measurement measurement;

    public ScienceToolMeasureEvent(Player player, ScienceTool scienceTool, String measurement) {
        this.measurement = new Measurement(new Timestamp(System.currentTimeMillis()), player, player.getLocation(), scienceTool, measurement);
    }

    public Measurement getMeasurement() {
        return this.measurement;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
