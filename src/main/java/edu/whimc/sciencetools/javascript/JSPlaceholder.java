package edu.whimc.sciencetools.javascript;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.CommandError;
import edu.whimc.sciencetools.models.sciencetool.NumericScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ToolType;

import java.util.function.Function;

public enum JSPlaceholder {

    VALUE("{VAL}", "Value to convert", JSContext::getToConvert),
    X_POS("{X}", "Current X value", c -> c.getLocation().getX()),
    Y_POS("{Y}", "Current Y value", c -> c.getLocation().getY()),
    Z_POS("{Z}", "Current Z value", c -> c.getLocation().getZ()),
    ALTITUDE("{ALTITUDE}", "Value from /altitude", c -> getToolValue(c, ToolType.ALTITUDE)),
    OXYGEN("{OXYGEN}", "Value from /oxygen", c -> getToolValue(c, ToolType.OXYGEN)),
    PRESSURE("{PRESSURE}", "Value from /pressure", c -> getToolValue(c, ToolType.PRESSURE)),
    RADIATION("{RADIATION}", "Value from /radiation", c -> getToolValue(c, ToolType.RADIATION)),
    TEMPERATURE("{TEMPERATURE}", "Value from /temperature", c -> getToolValue(c, ToolType.TEMPERATURE)),
    WIND("{WIND}", "Value from /wind", c -> getToolValue(c, ToolType.WIND)),
    ;

    private final String key;
    private final String usage;
    private final Function<JSContext, Double> replacement;

    JSPlaceholder(String key, String usage, Function<JSContext, Double> replacement) {
        this.key = key;
        this.usage = usage;
        this.replacement = replacement;
    }

    public String getKey() {
        return this.key;
    }

    public String getUsage() {
        return this.usage;
    }

    public String fullUsage() {
        return "&f\"&e&o" + this.key + "&f\" &7- " + this.usage;
    }

    public Double getReplacement(JSContext ctx) {
        return this.replacement.apply(ctx);
    }

    public static String prepareExpression(JSContext ctx, String expr) {
        for (JSPlaceholder ph : JSPlaceholder.values()) {
            if (expr.contains(ph.getKey())) {
                expr = expr.replace(ph.getKey(), String.valueOf(ph.getReplacement(ctx)));
            }
        }
        return expr;
    }

    private static double getToolValue(JSContext ctx, ToolType toolType) {
        ScienceTool tool = ScienceTools.getInstance().getToolManager().getTool(toolType);
        if (tool == null) {
            throw new CommandError("&c" + toolType.name() + " is not loaded.", false);
        }
        if (!(tool instanceof NumericScienceTool)) {
            throw new CommandError("&c" + toolType.toString() + " is not a numeric science tool!", false);
        }

        return ((NumericScienceTool) tool).getData(ctx.getLocation());
    }

}
