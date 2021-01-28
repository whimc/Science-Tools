package edu.whimc.sciencetools.javascript;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum JSPlaceholder {

    VALUE("{VAL}", "Value to convert", JSPlaceholderContext::getToConvert),
    X_POS("{X}", "Current X value", c -> c.getLocation().getX()),
    Y_POS("{Y}", "Current Y value", c -> c.getLocation().getY()),
    Z_POS("{Z}", "Current Z value", c -> c.getLocation().getZ()),
    ALTITUDE("{ALTITUDE}", "Value from /altitude", c -> 0.0),
    OXYGEN("{OXYGEN}", "Value from /oxygen", c -> 0.0),
    PRESSURE("{PRESSURE}", "Value from /pressure", c -> 0.0),
    RADIATION("{RADIATION}", "Value from /radiation", c -> 0.0),
    TEMPERATURE("{TEMPERATURE}", "Value from /temperature", c -> 0.0),
    WIND("{WIND}", "Value from /wind", c -> 0.0),
    ;

    public static final double DEFAULT = 1.0;

    private String key;
    private String usage;
    private Function<JSPlaceholderContext, Double> replacement;

    private JSPlaceholder(String key, String usage, Function<JSPlaceholderContext, Double> replacement) {
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

    public Double getReplacement(JSPlaceholderContext ctx) {
        return this.replacement.apply(ctx);
    }

    public static String prepareExpression(JSPlaceholderContext ctx, String expr) {
        for (JSPlaceholder ph : JSPlaceholder.values()) {
            expr = expr.replace(ph.getKey(), String.valueOf(ph.getReplacement(ctx)));
        }
        return expr;
    }

    public static class JSPlaceholderContext {
        private Location location;
        private double toConvert;

        private JSPlaceholderContext(Location location, double toConvert) {
            this.location = location;
            this.toConvert = toConvert;
        }

        public static JSPlaceholderContext create() {
            return create(DEFAULT);
        }

        public static JSPlaceholderContext create(CommandSender executor) {
            return create(executor, DEFAULT);
        }

        public static JSPlaceholderContext create(CommandSender executor, double toConvert) {
            if (executor instanceof Player) {
                return create(((Player) executor).getLocation(), toConvert);
            } else {
                return create(toConvert);
            }
        }

        public static JSPlaceholderContext create(Location location) {
            return new JSPlaceholderContext(location, DEFAULT);
        }

        public static JSPlaceholderContext create(double toConvert) {
            return create(Bukkit.getWorlds().get(0).getSpawnLocation(), toConvert);
        }

        public static JSPlaceholderContext create(Location location, double toConvert) {
            return new JSPlaceholderContext(location, toConvert);
        }

        public double getToConvert() {
            return this.toConvert;
        }

        public Location getLocation() {
            return this.location;
        }
    }

}
