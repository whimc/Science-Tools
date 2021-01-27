package edu.whimc.sciencetools.javascript;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Location;

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
    private Function<JSPlaceholderContext, Double> action;

    private JSPlaceholder(String key, String usage, Function<JSPlaceholderContext, Double> action) {
        this.key = key;
        this.usage = usage;
        this.action = action;
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

    public static String prepareExpression(JSPlaceholderContext context, String expr) {
        return "";
    }

    public class JSPlaceholderContext {
        private Location location;
        private double toConvert;

        public JSPlaceholderContext(Location location) {
            this(location, 0);
        }

        public JSPlaceholderContext(double toConvert) {
            this(Bukkit.getWorlds().get(0).getSpawnLocation(), toConvert);
        }

        public JSPlaceholderContext(Location location, double toConvert) {
            this.location = location;
            this.toConvert = toConvert;
        }

        public double getToConvert() {
            return this.toConvert;
        }

        public Location getLocation() {
            return this.location;
        }
    }

}
