package edu.whimc.sciencetools.models.sciencetool;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A custom event to be fired whenever a player measures a science tool.
 */
public class ScienceToolMeasureEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final ScienceTool scienceTool;
    private final Player player;

    public ScienceToolMeasureEvent(ScienceTool scienceTool, Player player) {
        this.scienceTool = scienceTool;
        this.player = player;
    }

    public ScienceTool getScienceTool() {
        return this.scienceTool;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
