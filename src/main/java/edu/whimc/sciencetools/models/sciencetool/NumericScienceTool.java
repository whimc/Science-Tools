package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.javascript.JSContext;
import edu.whimc.sciencetools.javascript.JSNumericalExpression;
import edu.whimc.sciencetools.models.conversion.Conversion;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NumericScienceTool extends ScienceTool {

    private final String unit;
    private final List<Conversion> conversions;

    public NumericScienceTool(ToolType type, String defaultMeasurement,
                              Map<World, String> worldMeasurements,
                              Map<String, String> regionMeasurements,
                              Set<World> disabledWorlds,
                              String unit,
                              List<Conversion> conversions) {
        super(type, defaultMeasurement, worldMeasurements, regionMeasurements, disabledWorlds);
        this.unit = unit;
        this.conversions = conversions;
    }

    public double getData(Location loc) {
        JSNumericalExpression expression = new JSNumericalExpression(super.getMeasurement(loc));
        return expression.evaluate(JSContext.create(loc));
    }

    @Override
    public void displayMeasurement(Player player) {
        if (super.disabledWorlds.contains(player.getWorld())) {
            Utils.msg(player, "&cWe don't know how to measure that here!");
            return;
        }

        double data = getData(player.getLocation());

        StringBuilder message = new StringBuilder("&aThe measured " + this.type.toString().toLowerCase()
                + " is &f" + Utils.trim2Deci(data) + this.unit + "&7");

        for (Conversion conversion : this.conversions) {
            String converted = Utils.trim2Deci(conversion.convert(data));
            message.append(" (").append(converted).append(conversion.getUnit()).append(")");
        }

        Utils.msg(player, message.toString());
    }

    public String getMainUnit() {
        return this.unit;
    }

}
