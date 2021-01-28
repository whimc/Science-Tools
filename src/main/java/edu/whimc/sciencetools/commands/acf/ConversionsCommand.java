package edu.whimc.sciencetools.commands.acf;

import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.managers.ConversionManager;
import edu.whimc.sciencetools.models.Conversion;
import edu.whimc.sciencetools.utils.Utils;

@CommandAlias("%basecommand")
@Subcommand("conversions")
@CommandPermission("%perm.admin")
public class ConversionsCommand extends BaseCommand {

    // TODO: Finish implementing this

    @Dependency
    private ConversionManager mgr;

    @Subcommand("info")
    @Description("Display information about a conversion")
    @CommandCompletion("@conversions")
    public void info(CommandSender sender, Conversion conversion) {
        conversion.sendInfo(sender);
    }

    @Subcommand("list")
    @Description("List all conversions")
    public void list(CommandSender sender) {
        Utils.msg(sender, mgr.getConversions()
                .stream()
                .map(Conversion::getName)
                .collect(Collectors.joining(", ")));
    }

    @Subcommand("create")
    @Description("Create a new conversion")
    @Syntax("<name> <unit> <expr...>")
    @CommandCompletion("@nothing @nothing @nothing")
    public void create(CommandSender sender, @Conditions("unique-conversion") @Single String name, @Single String unit, JSExpression expr) {
        mgr.createConversion(name, unit, expr);
        sender.sendMessage("Created new conversion \"" + name + "\"");
    }

    @Subcommand("remove")
    @Description("Remove a conversion")
    @CommandCompletion("@conversions")
    public void remove(CommandSender sender, Conversion conversion) {
        mgr.removeConversion(conversion);
        Utils.msg(sender, "Removed " + conversion.getName());
    }

    @Subcommand("setunit")
    @Description("Set the unit of a conversion")
    @CommandCompletion("@conversions @nothing")
    public void setUnit(CommandSender sender, Conversion conversion, @Single String newUnit) {
        conversion.setUnit(newUnit);
        Utils.msg(sender, "Unit changed to " + newUnit);
    }

    @Subcommand("setexpression")
    @Description("Set the expression of a conversion")
    @Syntax("<name> <expr...>")
    @CommandCompletion("@conversions @nothing")
    public void setExpression(CommandSender sender, Conversion conversion, JSExpression expression) {
        conversion.setExpression(sender, expression);
        Utils.msg(sender, "Expression changed to " + expression.getExpression());
    }

}
