package edu.whimc.sciencetools.javascript;

import edu.whimc.sciencetools.models.sciencetool.NumericScienceTool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.bukkit.World;

/**
 * The JavaScript placeholders that can be used in the config.
 */
public class JSPlaceholder {

    /* The list of default placeholders */
    private static final List<JSPlaceholder> DEFAULT_PLACEHOLDERS = new ArrayList<>(Arrays.asList(
            new JSPlaceholder("{VAL}", "Value to convert", JSContext::getToConvert),
            new JSPlaceholder("{X}", "Current X value", ctx -> ctx.getLocation().getX()),
            new JSPlaceholder("{Y}", "Current Y value", ctx -> ctx.getLocation().getY()),
            new JSPlaceholder("{Z}", "Current Z value", ctx -> ctx.getLocation().getZ()),
            new JSPlaceholder("{TIME_TICKS}", "The time of the world in ticks",
                    ctx -> (double) ctx.getLocation().getWorld().getTime()),
            new JSPlaceholder("{NIGHT}", "0 if day time, 1 if night time", ctx -> {
                World world = ctx.getLocation().getWorld();
                return world.getTime() >= 13000 ? 1.0 : 0.0;
            }),
            new JSPlaceholder("{WEATHER}", "0 if the weather is clear, 1 otherwise", ctx -> {
                World world = ctx.getLocation().getWorld();
                return world.hasStorm() || world.isThundering() ? 1.0 : 0.0;
            })
    ));

    private static final List<JSPlaceholder> placeholders = new ArrayList<>();
    /* Tool keys announced before those tools finish loading, so {TOOL} refs work in any order. */
    private static final Set<String> knownToolPlaceholderKeys = new HashSet<>();

    /* The String used in the config */
    private final String key;
    /* A description of how the placeholder is used */
    private final String usage;
    /* The value that the placeholder is replaced with */
    private final Function<JSContext, Double> replacement;

    /**
     * Construct a JSPlaceholder.
     *
     * @param key         The String used in the config.
     * @param usage       A description of how the placeholder is used.
     * @param replacement The value that the placeholder is replaced with.
     */
    private JSPlaceholder(String key, String usage, Function<JSContext, Double> replacement) {
        this.key = key;
        this.usage = usage;
        this.replacement = replacement;
    }

    /**
     * Creates a placeholder for the passed NumericScienceTool.
     *
     * @param tool The NumericScienceTool to add a placeholder for.
     */
    public static void registerCustomPlaceholder(NumericScienceTool tool) {
        String key = "{" + tool.getToolKey() + "}";
        String usage = "Value from " + tool.getDisplayName();
        Function<JSContext, Double> replacement = ctx -> tool.getData(ctx.getLocation());

        JSPlaceholder.placeholders.add(new JSPlaceholder(key, usage, replacement));
    }

    /**
     * Remember every configured tool key so expressions can reference a tool before it is loaded.
     * Unresolved keys are replaced with {@code 1} during load-time validation.
     *
     * @param toolKeys Tool keys from the config.
     */
    public static void setKnownToolKeys(Collection<String> toolKeys) {
        knownToolPlaceholderKeys.clear();
        for (String toolKey : toolKeys) {
            knownToolPlaceholderKeys.add("{" + toolKey + "}");
        }
    }

    public static void unregisterCustomPlaceholders() {
        JSPlaceholder.placeholders.clear();
        knownToolPlaceholderKeys.clear();
    }

    /**
     * The list of all placeholders (default + custom).
     */
    public static List<JSPlaceholder> getPlaceholders() {
        List<JSPlaceholder> result = new ArrayList<>(DEFAULT_PLACEHOLDERS);
        result.addAll(JSPlaceholder.placeholders);
        return Collections.unmodifiableList(result);
    }

    /**
     * Every placeholder key currently known, including tools that have not finished loading.
     *
     * @return Placeholder keys such as {@code {Y}} and {@code {YEAR}}.
     */
    public static Set<String> getAllPlaceholderKeys() {
        Set<String> keys = new HashSet<>();
        for (JSPlaceholder placeholder : getPlaceholders()) {
            keys.add(placeholder.getKey());
        }
        keys.addAll(knownToolPlaceholderKeys);
        return keys;
    }

    /**
     * Swaps out the placeholders in the given expression with their replacement values in the given context.
     *
     * @param ctx  The JavaScript context.
     * @param expr The JavaScript expression.
     * @return The JavaScript expression with placeholders swapped out for their replacements.
     */
    public static String prepareExpression(JSContext ctx, String expr) {
        Map<String, Function<JSContext, String>> replacements = new HashMap<>();
        for (JSPlaceholder ph : getPlaceholders()) {
            replacements.put(ph.getKey(), c -> String.valueOf(ph.getReplacement(c)));
        }
        for (String key : knownToolPlaceholderKeys) {
            replacements.putIfAbsent(key, c -> "1");
        }

        List<String> keys = new ArrayList<>(replacements.keySet());
        keys.sort(Comparator.comparingInt(String::length).reversed());
        for (String key : keys) {
            if (expr.contains(key)) {
                expr = expr.replace(key, replacements.get(key).apply(ctx));
            }
        }
        return expr;
    }

    /**
     * The String used in the config.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * The key and a description of how the placeholder is used.
     */
    public String getUsage() {
        return "&f\"&e&o" + this.key + "&f\" &7- " + this.usage;
    }

    /**
     * Compute the replacement with the given context.
     *
     * @param ctx The JavaScript context.
     * @return The Double value that replaces the placeholder in the given context.
     */
    public Double getReplacement(JSContext ctx) {
        return this.replacement.apply(ctx);
    }

}
