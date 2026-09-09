package edu.whimc.sciencetools.commands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager;
import edu.whimc.sciencetools.utils.Utils;
import java.lang.reflect.Field;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Rewrites unknown commands that look like misspelled science tool names.
 */
public class ToolCommandCorrector implements Listener {

    /**
     * If the player typed an unknown command that phonetically matches a tool, run that tool instead.
     *
     * @param event The command preprocess event.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String message = event.getMessage();
        if (message == null || message.length() < 2 || message.charAt(0) != '/') {
            return;
        }

        String body = message.substring(1).trim();
        if (body.isEmpty()) {
            return;
        }

        int space = body.indexOf(' ');
        String label = (space < 0 ? body : body.substring(0, space)).toLowerCase();
        String args = space < 0 ? "" : body.substring(space);

        if (getRegisteredCommand(label) != null) {
            return;
        }

        ScienceToolManager manager = ScienceTools.getInstance().getToolManager();
        if (manager == null) {
            return;
        }

        List<ScienceTool> matches = manager.resolveToolCandidates(label);
        if (matches.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.hasPermission("sciencetools.user")) {
            return;
        }

        if (matches.size() > 1) {
            event.setCancelled(true);
            Utils.sendDidYouMean(player, matches);
            return;
        }

        event.setMessage("/" + matches.get(0).getToolKey() + args);
    }

    private Command getRegisteredCommand(String label) {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer());
            return commandMap.getCommand(label);
        } catch (Exception exc) {
            return null;
        }
    }
}
