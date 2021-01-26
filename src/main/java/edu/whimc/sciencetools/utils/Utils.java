package edu.whimc.sciencetools.utils;

import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSEngine;

public class Utils {



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
		    res = JSEngine.eval(exp);
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
