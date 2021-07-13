package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

public class Reload extends AbstractSubCommand {

    public Reload() {
        super("reload", null, null, "Reload the plugin's config", Permission.ADMIN);
    }

    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        Utils.setDebugReceiver(sender);
        ScienceTools.getInstance().reloadScienceTools();
        Utils.setDebugReceiver(null);
        Utils.msg(sender, "&a&oScience Tools config reload!");
        return false;
    }

}
