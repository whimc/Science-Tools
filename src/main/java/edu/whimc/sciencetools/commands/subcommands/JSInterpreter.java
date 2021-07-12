package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand.SubCommand;
import edu.whimc.sciencetools.javascript.JSContext;
import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.javascript.JSFunction;
import edu.whimc.sciencetools.javascript.JSPlaceholder;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

public class JSInterpreter extends AbstractSubCommand {

    public JSInterpreter(ScienceTools plugin, SubCommand subCmd) {
        super(plugin, subCmd);
    }

    @Override
    protected void notEnoughArgs(CommandSender sender) {
        Utils.msg(sender, "&7Custom syntax:");

        for (JSFunction function : JSFunction.values()) {
            Utils.msg(sender, " " + function.fullUsage());
        }

        for (JSPlaceholder placeholder : JSPlaceholder.getPlaceholders()) {
            Utils.msg(sender, " " + placeholder.getUsage());
        }
    }

    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {
        StringBuilder builder = new StringBuilder();
        for (int ind = 0; ind < args.length; ind++) {
            builder.append(args[ind]).append(" ");
        }

        JSExpression expression = new JSExpression(builder.toString().trim());
        Utils.msg(sender, expression.run(JSContext.create(sender), true).toString());

        return true;
    }

}
