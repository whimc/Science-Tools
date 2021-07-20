package edu.whimc.sciencetools.javascript;

import edu.whimc.sciencetools.models.sciencetool.NumericScienceTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * The JavaScript placeholders that can be used in the config.
 */
public class JSPlaceholder {

    /* The list of default placeholders */
    private static final List<JSPlaceholder> DEFAULT_PLACEHOLDERS = new ArrayList<>(Arrays.asList(
            new JSPlaceholder("{VAL}", "Value to convert", JSContext::getToConvert),
            new JSPlaceholder("{X}", "Current X value", ctx -> ctx.getLocation().getX()),
            new JSPlaceholder("{Y}", "Current Y value", ctx -> ctx.getLocation().getY()),
            new JSPlaceholder("{Z}", "Current Z value", ctx -> ctx.getLocation().getZ())
    ));

    private static final List<JSPlaceholder> placeholders = new ArrayList<>();

    /* The String used in the config */
    private final String key;
    /* A description of how the placeholder is used */
    private final String usage;
    /* The value that the placeholder is replaced with */
    private final Function<JSContext, Double> replacement;

    /**
     * Construct a JSPlaceholder.
     *
     * @param key the String used in the config
     * @param usage a description of how the placeholder is used
     * @param replacement the value that the placeholder is replaced with
     */
    private JSPlaceholder(String key, String usage, Function<JSContext, Double> replacement) {
        this.key = key;
        this.usage = usage;
        this.replacement = replacement;
    }

    /**
     * @return the String used in the config.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @return the key and a description of how the placeholder is used
     */
    public String getUsage() {
        return "&f\"&e&o" + this.key + "&f\" &7- " + this.usage;
    }

    /**
     * @param ctx the JavaScript context
     * @return the Double value that replaces the placeholder in the given context
     */
    public Double getReplacement(JSContext ctx) {
        return this.replacement.apply(ctx);
    }

    /**
     * Creates a placeholder for the passed NumericScienceTool.
     *
     * @param tool the NumericScienceTool to add a placeholder for
     */
    public static void registerCustomPlaceholder(NumericScienceTool tool) {
        String key = "{" + tool.getToolKey() + "}";
        String usage = "Value from " + tool.getDisplayName();
        Function<JSContext, Double> replacement = ctx -> tool.getData(ctx.getLocation());

        JSPlaceholder.placeholders.add(new JSPlaceholder(key, usage, replacement));
    }

    /**
     * Clears all of the custom placeholders.
     */
    public static void unregisterCustomPlaceholders() {
        JSPlaceholder.placeholders.clear();
    }

    /**
     * @return the list of all placeholders
     */
    public static List<JSPlaceholder> getPlaceholders() {
        List<JSPlaceholder> result = new ArrayList<>(DEFAULT_PLACEHOLDERS);
        result.addAll(JSPlaceholder.placeholders);
        return Collections.unmodifiableList(result);
    }

    /**
     * Swaps out the placeholders in the given expression with their replacement values in the given context.
     *
     * @param ctx the JavaScript context
     * @param expr the JavaScript expression
     * @return the JavaScript expression with placeholders swapped out for their replacements
     */
    public static String prepareExpression(JSContext ctx, String expr) {
        for (JSPlaceholder ph : getPlaceholders()) {
            if (expr.contains(ph.getKey())) {
                expr = expr.replace(ph.getKey(), String.valueOf(ph.getReplacement(ctx)));
            }
        }
        return expr;
    }

}
