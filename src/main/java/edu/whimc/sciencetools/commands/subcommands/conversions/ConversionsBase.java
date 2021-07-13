package edu.whimc.sciencetools.commands.subcommands.conversions;

import edu.whimc.sciencetools.commands.subcommands.AbstractSubCommand;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

public class ConversionsBase extends AbstractSubCommand {

    public ConversionsBase() {
        super("conversion", null, null, "Manage unit conversions", Permission.ADMIN);
    }

    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        Utils.msg(sender, "This command is unimplemented!");
        return false;
    }

}
