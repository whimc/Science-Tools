package edu.whimc.sciencetools.javascript;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JSContext {

    public static final double DEFAULT = 1.0;

    private final Location location;
    private final double toConvert;

    private JSContext(Location location, double toConvert) {
        this.location = location;
        this.toConvert = toConvert;
    }

    public static JSContext create() {
        return create(DEFAULT);
    }

    public static JSContext create(CommandSender executor) {
        return create(executor, DEFAULT);
    }

    public static JSContext create(CommandSender executor, double toConvert) {
        if (executor instanceof Player) {
            return create(((Player) executor).getLocation(), toConvert);
        } else {
            return create(toConvert);
        }
    }

    public static JSContext create(Location location) {
        return new JSContext(location, DEFAULT);
    }

    public static JSContext create(double toConvert) {
        return create(Bukkit.getWorlds().get(0).getSpawnLocation(), toConvert);
    }

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
