package edu.whimc.sciencetools.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;

public class Utils {

	private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	private static ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("Nashorn");

	static {
		try {
			scriptEngine.eval("function rand(min, max) { return Math.random() * (+max - +min) + +min }");
			scriptEngine.eval("function randInt(min, max) { return Math.floor(rand(min, max + 1)) }");
			scriptEngine.eval("function min(a, b) { return Math.min(a, b) }");
			scriptEngine.eval("function max(a, b) { return Math.max(a, b) }");
		} catch (ScriptException e) {}
	}

	public static enum Placeholder {
		VALUE("{VAL}", "Value to convert"),
		X_POS("{X}", "Current X value"),
		Y_POS("{Y}", "Current Y value"),
		Z_POS("{Z}", "Current Z value"),
		RAND("rand(min, max)", "Random decimal between 'min' and 'max' (inclusive)"),
		RAND_INT("randInt(min, max)", "Random integer between 'min' and 'max' (inclusive"),
		MIN("min(a, b)", "The minimum between 'a' and 'b'"),
		MAX("max(a, b)", "The maximum between 'a' and 'b'"),
		ALTITUDE("{ALTITUDE}", "Value from /altitude"),
		ATMOSPHERE("{ATMOSPHERE}", "Value from /atmosphere"),
		OXYGEN("{OXYGEN}", "Value from /oxygen"),
		PRESSURE("{PRESSURE}", "Value from /pressure"),
		RADIATION("{RADIATION}", "Value from /radiation"),
		RADIATION_EM("{RADIATION_EM}", "Value from /radiation_em"),
		RADIATION_PARTICLE("{RADIATION_PARTICLE}", "Value from /radiation_particle"),
		RADIATION_COSMICRAYS("{RADIATION_COSMICRAYS}", "Value from /radiation_cosmicrays"),
		TEMPERATURE("{TEMPERATURE}", "Value from /temperature"),
		WIND("{WIND}", "Value from /wind");

		private String key;
		private String usage;

		private Placeholder(String key, String usage) {
			this.key = key;
			this.usage = usage;
		}

		@Override
		public String toString() {
			return key;
		}

		public String fullUsage() {
			return "&f\"&e&o" + key + "&f\" &7- " + usage;
		}
	}

	public static Double parseDouble(String str) {
		try {
			return Double.valueOf(str.replace(",", ""));
		} catch (NumberFormatException exc) {
			return null;
		}
	}

	public static Double parseDoubleWithError(CommandSender sender, String str) {
	    Double res = parseDouble(str);
	    if (res == null) {
	        Utils.msg(sender, "&4" + str + " &cis not a valid number!");
	        return null;
	    }
	    return res;
	}

	public static boolean worldGuardEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
	}

	public static String trim2Deci(double val) {
		return String.format("%,.2f", val);
	}

	private static CommandSender debugReceiver = null;
	public static void setDebugReceiver(CommandSender sender) {
		debugReceiver = sender;
	}

	public static void log(ScienceTools plugin, String message) {
		if (debugReceiver != null) {
			Utils.msg(debugReceiver, message);
		}
		plugin.getLogger().info(message);

	}

	public static Object executeExpressionDebug(CommandSender sender, String exp, boolean verbose) {
		Object res = null;
		try {
			res = scriptEngine.eval(exp);
		} catch (ScriptException e) {
			msg(sender, "&cYour expression contains invalid syntax!");

			if (verbose) {
				msg(sender, e.getMessage());
			}

			return null;
		}

		return res;
	}

	public static Double executeExpression(CommandSender sender, String exp) {
		Object res = executeExpressionDebug(sender, exp, false);

		if (!(res instanceof Number)) {
			String type = "Unknown";
			if (res != null) {
				type = res.getClass().getSimpleName();
			}

			msg(sender, "&cExpected a number but found type &7&o" + type);
			return null;
		}

		return Double.valueOf(((Number) res).doubleValue());
	}

	public static void msg(CommandSender sender, String... strs) {
		for (String str : strs) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
		}
	}
}
