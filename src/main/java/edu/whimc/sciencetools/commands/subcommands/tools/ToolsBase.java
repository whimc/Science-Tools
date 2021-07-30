package edu.whimc.sciencetools.commands.subcommands.tools;

import edu.whimc.sciencetools.commands.subcommands.AbstractSubCommand;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

/**
 * The command to manage tools. This is not yet implemented.
 * Command: /sciencetools tool
 */
public class ToolsBase extends AbstractSubCommand {

    /**
     * Constructs a ToolsBase.
     */
    public ToolsBase() {
        super("tool", null, null, "Manage the science tools", Permission.ADMIN);
    }

    /**
     * {@inheritDoc}
     *
     * Allows the user to manage all science tools.
     */
    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        // TODO Implement this
        Utils.msg(sender, "This command is unimplemented!");
        return false;
    }

}
