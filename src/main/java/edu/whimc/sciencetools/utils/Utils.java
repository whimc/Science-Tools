package edu.whimc.sciencetools.utils;

import edu.whimc.sciencetools.ScienceTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * All of the utility functions.
 */
public class Utils {

    private static CommandSender debugReceiver = null;

    /**
     * Parses a String and returns a Double.
     *
     * @param str The String to parse.
     * @return The Double parsed from the String.
     */
    public static Double parseDouble(String str) {
        try {
            return Double.valueOf(str.replace(",", ""));
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    /**
     * Parses a String and returns a Double. Throws a chat error if String is not a valid number.
     *
     * @param sender The command's sender.
     * @param str The String to parse.
     * @return The Double parsed from the String.
     */
    public static Double parseDoubleWithError(CommandSender sender, String str) {
        Double res = parseDouble(str);
        if (res == null) {
            Utils.msg(sender, "&4" + str + " &cis not a valid number!");
            return null;
        }
        return res;
    }

    /**
     * Checks if the WorldGuard plugin is enabled.
     *
     * @return Whether WorldGuard is enabled.
     */
    public static boolean worldGuardEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
    }

    /**
     * Trims decimal to desired level of precision.
     *
     * @param val The value to be trimmed.
     * @param precision The desired level of precision.
     * @return A String decimal trimmed to the desired level of precision.
     */
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

    /**
     * Logs the message.
     *
     * @param message The message to log.
     */
    public static void log(String message) {
        // Make tabs more consistent
        message = message.replace("\t", "    ");
        if (debugReceiver != null) {
            Utils.msg(debugReceiver, message);
        }
        ScienceTools.getInstance().getLogger().info(colored(message));
    }

    /**
     * Sends colored messages to the sender.
     *
     * @param sender The command's sender.
     * @param strs The Strings to be sent.
     */
    public static void msg(CommandSender sender, String... strs) {
        for (String str : strs) {
            sender.sendMessage(colored(str));
        }
    }

    /**
     * Translates a string using '&' into a string that uses the internal ChatColor.COLOR_CODE color code character.
     * The alternate color code character will only be replaced if it is immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
     *
     * @param str The String to translate.
     * @return The translated String.
     */
    public static String colored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
