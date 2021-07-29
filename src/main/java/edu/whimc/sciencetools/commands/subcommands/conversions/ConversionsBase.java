package edu.whimc.sciencetools.commands.subcommands.conversions;

import edu.whimc.sciencetools.commands.subcommands.AbstractSubCommand;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

/**
 * The command to manage unit conversions. This is not yet implemented.
 * Command: /sciencetools conversion
 */
public class ConversionsBase extends AbstractSubCommand {

    /**
     * Constructs a ConversionsBase.
     */
    public ConversionsBase() {
        super("conversion", null, null, "Manage unit conversions", Permission.ADMIN);
    }

    /**
     * {@inheritDoc}
     *
     * Allows the user to manage conversions.
     */
    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        // TODO Implement this
        Utils.msg(sender, "This command is unimplemented!");
        return false;
    }

}
