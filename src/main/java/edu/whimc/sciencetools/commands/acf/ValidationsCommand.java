package edu.whimc.sciencetools.commands.acf;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("%basecommand")
@Subcommand("validations")
@CommandPermission("%perm.admin")
public class ValidationsCommand extends BaseCommand {

    @Default @CatchUnknown
    public void root(CommandSender sender, CommandHelp cmd) {
        sender.sendMessage("This command is unimplemented");
    }

}
