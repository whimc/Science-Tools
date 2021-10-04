package edu.whimc.sciencetools.javascript;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A JavaScript function's context. Holds information used during the execution of an expression.
 */
public class JSContext {

    public static final double DEFAULT = 1.0;

    private final Location location;
    private final double toConvert;

    /**
     * Constructs a JSContext.
     *
     * @param location  The location to evaluate the expression.
     * @param toConvert The Double value to convert.
     */
    private JSContext(Location location, double toConvert) {
        this.location = location;
        this.toConvert = toConvert;
    }

    /**
     * Creates a default JSContext.
     *
     * @return The new JSContext.
     */
    public static JSContext create() {
        return create(DEFAULT);
    }

    /**
     * Creates a default JSContext at the executor's location.
     *
     * @param executor The command's sender.
     * @return The new JSContext.
     */
    public static JSContext create(CommandSender executor) {
        return create(executor, DEFAULT);
    }

    /**
     * Creates a JSContext at the executor's location if it is the player.
     * If the sender is not a player, the first world's spawn point is used as the location.
     *
     * @param executor  The command's sender.
     * @param toConvert The Double value to convert.
     * @return The new JSContext.
     */
    public static JSContext create(CommandSender executor, double toConvert) {
        if (executor instanceof Player) {
            return create(((Player) executor).getLocation(), toConvert);
        } else {
            return create(toConvert);
        }
    }

    /**
     * Creates a default JSContext at a specified location.
     *
     * @param location The location to evaluate the expression.
     * @return The new JSContext.
     */
    public static JSContext create(Location location) {
        return new JSContext(location, DEFAULT);
    }

    /**
     * Creates a JSContext at the overworld's spawn location.
     *
     * @param toConvert The Double value to convert.
     * @return The new JSContext.
     */
    public static JSContext create(double toConvert) {
        return create(Bukkit.getWorlds().get(0).getSpawnLocation(), toConvert);
    }

    /**
     * Creates a new JSContext at the specified location.
     *
     * @param location  The location to evaluate the expression.
     * @param toConvert The Double value to convert.
     * @return The new JSContext.
     */
    public static JSContext create(Location location, double toConvert) {
        return new JSContext(location, toConvert);
    }

    public double getToConvert() {
        return this.toConvert;
    }

    public Location getLocation() {
        return this.location;
    }
}
