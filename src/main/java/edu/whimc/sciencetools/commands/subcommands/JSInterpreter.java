package edu.whimc.sciencetools.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.commands.BaseToolCommand.SubCommand;
import edu.whimc.sciencetools.javascript.JSFunction;
import edu.whimc.sciencetools.javascript.JSPlaceholder;
import edu.whimc.sciencetools.utils.Utils;

public class JSInterpreter extends AbstractSubCommand {

    public JSInterpreter(ScienceTools plugin, SubCommand subCmd) {
        super(plugin, subCmd);
    }

    @Override
    protected void notEnoughArgs(CommandSender sender) {
        Utils.msg(sender, "&7Custom syntax:");
//        for (JSEngine.JSPlaceholder ph : JSEngine.Placeholder.values()) {
//            Utils.msg(sender, " " + ph.fullUsage());
//        }
        for (JSFunction function : JSFunction.values()) {
            Utils.msg(sender, " " + function.fullUsage());
        }

        for (JSPlaceholder placeholder : JSPlaceholder.values()) {
            Utils.msg(sender, " " + placeholder.fullUsage());
        }
    }

    @Override
    public boolean commandRoutine(CommandSender sender, String[] args) {

        StringBuilder builder = new StringBuilder();
        for (int ind = 0; ind < args.length; ind++) {
            builder.append(args[ind]).append(" ");
        }

        String exp = builder.toString().trim();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            exp = this.plugin.getToolManager().fillIn(player, exp, player.getLocation());
        }
//		Object res = Utils.executeExpressionDebug(sender, exp, true);
        Object res = null;

        if (res == null) {
            return false;
        }

        Utils.msg(sender, res.toString());

        return true;
    }

}
