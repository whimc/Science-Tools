package edu.whimc.sciencetools.javascript;

import edu.whimc.sciencetools.models.sciencetool.NumericScienceTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class JSPlaceholder {

    private static final List<JSPlaceholder> placeholders = new ArrayList<>(Arrays.asList(
            new JSPlaceholder("{VAL}", "Value to convert", JSContext::getToConvert),
            new JSPlaceholder("{X}", "Current X value", ctx -> ctx.getLocation().getX()),
            new JSPlaceholder("{Y}", "Current Y value", ctx -> ctx.getLocation().getY()),
            new JSPlaceholder("{Z}", "Current Z value", ctx -> ctx.getLocation().getZ())
    ));

    private final String key;
    private final String usage;
    private final Function<JSContext, Double> replacement;

    private JSPlaceholder(String key, String usage, Function<JSContext, Double> replacement) {
        this.key = key;
        this.usage = usage;
        this.replacement = replacement;
    }

    public String getKey() {
        return this.key;
    }

    public String getUsage() {
        return "&f\"&e&o" + this.key + "&f\" &7- " + this.usage;
    }

    public Double getReplacement(JSContext ctx) {
        return this.replacement.apply(ctx);
    }

    public static String prepareExpression(JSContext ctx, String expr) {
        for (JSPlaceholder ph : JSPlaceholder.placeholders) {
            if (expr.contains(ph.getKey())) {
                expr = expr.replace(ph.getKey(), String.valueOf(ph.getReplacement(ctx)));
            }
        }
        return expr;
    }

    public static void registerPlaceholder(NumericScienceTool tool) {
        String key = "{" + tool.getToolKey() + "}";
        String usage = "Value from " + tool.getDisplayName();
        Function<JSContext, Double> replacement = ctx -> tool.getData(ctx.getLocation());

        JSPlaceholder.placeholders.add(new JSPlaceholder(key, usage, replacement));
    }

    public static List<JSPlaceholder> getPlaceholders() {
        return Collections.unmodifiableList(JSPlaceholder.placeholders);
    }

}
