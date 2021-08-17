package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.models.Measurement;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A custom event to be fired whenever a player measures a science tool.
 */
public class ScienceToolMeasureEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Measurement measurement;

    public ScienceToolMeasureEvent(Player player, ScienceTool scienceTool, String measurement) {
        this.measurement = new Measurement(player, scienceTool, measurement);
    }

    public Measurement getMeasurement() {
        return this.measurement;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
