package edu.whimc.sciencetools.utils;

import edu.whimc.sciencetools.ScienceTools;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * All of the utility functions.
 */
public class Utils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d yyyy, h:mm a z");
    private static final String prefix = '[' + ScienceTools.getInstance().getDescription().getName() + "] ";
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
     * @param str    The String to parse.
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
     * @param val       The value to be trimmed.
     * @param precision The desired level of precision.
     * @return A String decimal trimmed to the desired level of precision.
     */
    public static String trimDecimals(double val, int precision) {
        return String.format("%,." + precision + "f", val);
    }

    /**
     * Set a user to receive debug messages.
     * This is a pretty weird way of doing this and somewhat unstable but that's ok.
     *
     * @param sender The user to receive debug messages.
     */
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
        Bukkit.getConsoleSender().sendMessage(prefix + colored(message));
    }

    /**
     * Sends colored messages to the sender.
     *
     * @param sender The command's sender.
     * @param strs   The Strings to be sent.
     */
    public static void msg(CommandSender sender, String... strs) {
        for (String str : strs) {
            sender.sendMessage(colored(str));
        }
    }

    /**
     * Translates a string using '&' into a string that uses the internal ChatColor.COLOR_CODE color code character.
     * The alternate color code character will only be replaced if it is immediately followed by 0-9, A-F, a-f, K-O,
     * k-o, R or r.
     *
     * @param str The String to translate.
     * @return The translated String.
     */
    public static String colored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * Get a formatted version of a timestamp.
     *
     * @param timestamp The time to format.
     * @return A formatted version of the given timestamp.
     */
    public static String getDate(Timestamp timestamp) {
        return DATE_FORMAT.format(new Date(timestamp.getTime()));
    }

    private static TextComponent createComponent(String text, String... hoverText) {
        TextComponent message = new TextComponent(colored(text));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(colored(String.join("\n", hoverText))).create()));
        return message;
    }

    /**
     * Send a Spigot chat component to the player containing hover-text.
     *
     * @param player    The player to receive the message.
     * @param text      The text of the message.
     * @param hoverText The hover-text of the message.
     */
    public static void sendComponent(Player player, String text, String... hoverText) {
        player.spigot().sendMessage(createComponent(text, hoverText));
    }
}
