package edu.whimc.sciencetools.commands.acf;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import edu.whimc.sciencetools.models.sciencetool.ScienceToolManager.ToolType;

@CommandAlias("%basecommand")
@Subcommand("tools")
@CommandPermission("%perm.admin")
public class ToolsCommand extends BaseCommand {

    @Subcommand("info")
    public void info(CommandSender sender, ToolType tool) {
        sender.sendMessage("Info about " + tool.name());
    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        sender.sendMessage(Stream
                .of(ToolType.values())
                .map(ToolType::name)
                .collect(Collectors.joining(", ")));
    }

    @Subcommand("conversions")
    public class ConversionCommands extends BaseCommand {

        @Subcommand("list")
        public void list(CommandSender sender) {}

        @Subcommand("add")
        public void add(CommandSender sender) {}

        @Subcommand("set")
        public void set(CommandSender sender) {}

        @Subcommand("remove")
        public void remove(CommandSender sender) {}

    }

    @Subcommand("worldexpressions")
    public class WorldExpressionCommands extends BaseCommand {

        @Subcommand("list")
        public void listWorldExpressions(CommandSender sender) {}

        @Subcommand("add")
        public void addWorldExpression(CommandSender sender) {}

        @Subcommand("set")
        public void set(CommandSender sender) {}

        @Subcommand("remove")
        public void remove(CommandSender sender) {}

    }

    @Subcommand("regionexpressions")
    public class RegionExpressionCommands extends BaseCommand {

        @Subcommand("list")
        public void list(CommandSender sender) {}

        @Subcommand("add")
        public void add(CommandSender sender) {}

        @Subcommand("set")
        public void set(CommandSender sender) {}

        @Subcommand("remove")
        public void remove(CommandSender sender) {}

    }

}
