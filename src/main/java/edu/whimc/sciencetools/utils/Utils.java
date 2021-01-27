package edu.whimc.sciencetools.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;

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

	public static void msg(CommandSender sender, String... strs) {
		for (String str : strs) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
		}
	}
}
