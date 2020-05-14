package edu.whimc.sciencetools.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.utils.Utils;
import edu.whimc.sciencetools.utils.Utils.Placeholder;

public class JSInterpreter extends AbstractSubCommand {

	public JSInterpreter(ScienceTools plugin) {
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {

		if (args.length == 1) {
			Utils.msg(sender, "&o/sciencetools js <expr...>");
			Utils.msg(sender, "&7Custom syntax:");
			for (Placeholder ph : Placeholder.values()) {
				Utils.msg(sender, ph.fullUsage());
			}
			return false;
		}
		
		StringBuilder builder = new StringBuilder();
		for (int ind = 1; ind < args.length; ind++) {
			builder.append(args[ind]).append(" ");
		}
		
		String exp = builder.toString().trim();
		if (sender instanceof Player) {
			exp = plugin.getToolManager().fillIn(builder.toString().trim(), (Player) sender);
		}
		Object res = Utils.executeExpressionDebug(sender, exp, true);
		
		if (res == null) {
			return false;
		}
		
		Utils.msg(sender, res.toString());
		
		return true;
	}

}
