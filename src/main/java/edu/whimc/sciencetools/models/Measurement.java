package edu.whimc.sciencetools.models;

import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import org.bukkit.entity.Player;

public class Measurement {

    private final Player player;
    private final ScienceTool tool;
    private final String measurement;

    public Measurement(Player player, ScienceTool tool, String measurement) {
        this.player = player;
        this.tool = tool;
        this.measurement = measurement;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ScienceTool getTool() {
        return this.tool;
    }

    public String getMeasurement() {
        return this.measurement;
    }
}
