package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

/**
 * The command for reloading the plugin.
 * Command: /sciencetools reload
 */
public class Reload extends AbstractSubCommand {

    /**
     * Constructs the Reload command.
     */
    public Reload() {
        super("reload", null, null, "Reload the plugin's config", Permission.ADMIN);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reloads the plugin, refreshing values on the server to match changes in the config.
     */
    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        Utils.setDebugReceiver(sender);
        ScienceTools.getInstance().reloadScienceTools();
        Utils.setDebugReceiver(null);
        Utils.msg(sender, "&a&oScience Tools config reload!");
        return false;
    }

}
