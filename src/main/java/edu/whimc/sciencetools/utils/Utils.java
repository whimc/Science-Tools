package edu.whimc.sciencetools.utils;

import edu.whimc.sciencetools.ScienceTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class Utils {

    private static CommandSender debugReceiver = null;

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

    public static String trimDecimals(double val, int precision) {
        return String.format("%,." + precision + "f", val);
    }

    public static void setDebugReceiver(CommandSender sender) {
        // Avoid duplicate debug messages in console
        if (sender instanceof ConsoleCommandSender) {
            return;
        }
        debugReceiver = sender;
    }

    public static void log(String message) {
        // Make tabs more consistent
        message = message.replace("\t", "    ");
        if (debugReceiver != null) {
            Utils.msg(debugReceiver, message);
        }
        ScienceTools.getInstance().getLogger().info(colored(message));
    }

    public static void msg(CommandSender sender, String... strs) {
        for (String str : strs) {
            sender.sendMessage(colored(str));
        }
    }

    public static String colored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
