package edu.whimc.sciencetools.commands.subcommands.tools;

import edu.whimc.sciencetools.commands.subcommands.AbstractSubCommand;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

public class ToolsBase extends AbstractSubCommand {

    public ToolsBase() {
        super("tool", null, null, "Manage the science tools", Permission.ADMIN);
    }

    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        // TODO Implement this
        Utils.msg(sender, "This command is unimplemented!");
        return false;
    }

}
