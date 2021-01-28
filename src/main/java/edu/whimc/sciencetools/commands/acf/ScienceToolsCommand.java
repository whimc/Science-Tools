package edu.whimc.sciencetools.commands.acf;

import java.util.Comparator;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.javascript.JSPlaceholder.JSPlaceholderContext;

@CommandAlias("%basecommand")
@CommandPermission("%perm.admin")
@Description("Base command for WHIMC-ScienceTools")
public class ScienceToolsCommand extends BaseCommand {

    @Default @CatchUnknown
    public void root(CommandSender sender, CommandHelp cmd) {
        cmd.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        cmd.showHelp();
    }

    @Subcommand("reload")
    @Description("Reload the plugin configuration")
    public void reload(CommandSender sender) {
        // TODO implement this
        sender.sendMessage("plugin reloaded");
    }

    @Subcommand("js")
    @Description("Run interpreted JavaScript")
    @Syntax("<expr...>")
    public void jsInterpreter(CommandSender sender, @Flags("any-type") JSExpression expr) {
        Object result = expr.run(JSPlaceholderContext.create(sender));
        sender.sendMessage("Return value: " + result);
    }

}
