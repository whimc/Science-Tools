package edu.whimc.sciencetools.commands.subcommands;

import edu.whimc.sciencetools.javascript.JSContext;
import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.javascript.JSFunction;
import edu.whimc.sciencetools.javascript.JSPlaceholder;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * This class handles the interpretation of JavaScript expressions.
 * Command: /sciencetools js
 */
public class JSInterpreter extends AbstractSubCommand {

    /**
     * Constructs the custom JavaScript command.
     */
    public JSInterpreter() {
        super("js", Arrays.asList("expr..."), null,
                "Run interpreted JavaScript", Permission.ADMIN);
    }

    /**
     * Shows user custom JS syntax when not enough arguments are provided.
     *
     * @param sender The command's sender.
     */
    @Override
    protected void notEnoughArgs(CommandSender sender) {
        Utils.msg(sender, "&7Custom syntax:");

        // loop through all JS functions and display full usages
        for (JSFunction function : JSFunction.values()) {
            Utils.msg(sender, " " + function.fullUsage());
        }

        // loop through all JS placeholders and display full usages
        for (JSPlaceholder placeholder : JSPlaceholder.getPlaceholders()) {
            Utils.msg(sender, " " + placeholder.getUsage());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Runs interpreted JavaScript.
     */
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
