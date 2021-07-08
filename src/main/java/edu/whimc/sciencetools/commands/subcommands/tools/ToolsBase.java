package edu.whimc.sciencetools.commands.subcommands.tools;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand.SubCommand;
import edu.whimc.sciencetools.commands.subcommands.AbstractSubCommand;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

public class ToolsBase extends AbstractSubCommand {

    public ToolsBase(ScienceTools plugin, SubCommand subCmd) {
        super(plugin, subCmd);
    }

    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        Utils.msg(sender, "This command is unimplemented!");
        return false;
    }

}
