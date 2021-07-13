package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.javascript.JSContext;
import edu.whimc.sciencetools.javascript.JSNumericExpression;
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
    private int precision;
    private final List<Conversion> conversions;

    public NumericScienceTool(String toolKey,
                              String displayName,
                              String defaultMeasurement,
                              Map<World, String> worldMeasurements,
                              Map<World, Map<String, String>> regionMeasurements,
                              Set<World> disabledWorlds,
                              String unit,
                              int precision,
                              List<Conversion> conversions) {
        super(toolKey, displayName, defaultMeasurement, worldMeasurements, regionMeasurements, disabledWorlds);
        this.unit = unit;
        this.precision = precision;
        this.conversions = conversions;
    }

    @Override
    public void displayMeasurement(Player player) {
        if (super.disabledWorlds.contains(player.getWorld())) {
            Utils.msg(player, "&cWe don't know how to measure that here!");
            return;
        }

        double data = getData(player.getLocation());

        StringBuilder message = new StringBuilder("&aThe measured " + this.displayName
                + " is &f" + Utils.trimDecimals(data, this.precision) + this.unit + "&7");

        for (Conversion conversion : this.conversions) {
            String converted = Utils.trimDecimals(conversion.convert(data), this.precision);
            message.append(" (").append(converted).append(conversion.getUnit()).append(")");
        }

        Utils.msg(player, message.toString());
    }

    public double getData(Location loc) {
        JSNumericExpression expression = new JSNumericExpression(super.getMeasurement(loc));
        return expression.evaluate(JSContext.create(loc));
    }

    public String getMainUnit() {
        return this.unit;
    }

}
