package edu.whimc.sciencetools.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandCompletions.CommandCompletionHandler;
import co.aikar.commands.CommandConditions.ParameterCondition;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.models.Conversion;
import edu.whimc.sciencetools.utils.Utils;

public class ConversionManager {

	private ScienceTools plugin;
	private Map<String, Conversion> conversions;

	private ConversionManager(ScienceTools plugin) {
		this.plugin = plugin;
		conversions = new HashMap<>();
	}

	public static ConversionManager loadConversions(ScienceTools plugin) {
		ConversionManager manager = new ConversionManager(plugin);

		Utils.log(plugin, ChatColor.YELLOW + "Loading Conversions from config");

		for (String convName : plugin.getConfig().getConfigurationSection("conversions").getKeys(false)) {
			Utils.log(plugin, ChatColor.AQUA + " - Loading " + ChatColor.WHITE + convName);
			String expr = plugin.getConfig().getString("conversions." + convName + ".expression");
			String unit = plugin.getConfig().getString("conversions." + convName + ".unit");

			Utils.log(plugin, ChatColor.AQUA + "   - Expression: \"" + ChatColor.WHITE + expr + ChatColor.AQUA + "\"");
			Utils.log(plugin, ChatColor.AQUA + "   - Unit: \"" + ChatColor.WHITE + unit + ChatColor.AQUA + "\"");

			JSExpression jsExpr = new JSExpression(expr);
			if (!jsExpr.numerical()) {
			    Utils.log(plugin, ChatColor.RED + "   * Invalid expression! (Skipping this conversion)");
			    continue;
			}
			manager.loadConversion(convName, unit, new JSExpression(expr));
		}

		Utils.log(plugin, ChatColor.YELLOW + "Conversions loaded!");

		return manager;
	}

	private Conversion loadConversion(String name, String unit, JSExpression expr) {
	    Conversion conv = new Conversion(this, name, unit, expr);
	    conversions.put(name, conv);
	    return conv;
	}

	public Conversion createConversion(String name, String unit, JSExpression expr) {
	    Conversion conv = loadConversion(name, unit, expr);
	    saveToConfig(conv);
	    return conv;
	}

	public void removeConversion(Conversion conv) {
	    String name = conv.getName();
	    conversions.remove(name);
	    setConfig(name, null);
    }

	public Conversion getConversion(String key) {
		return conversions.getOrDefault(key, null);
	}

	public Collection<Conversion> getConversions() {
	    return conversions.values();
	}

	public void saveToConfig(Conversion conv) {
	    setConfig(conv.getName() + ".unit", conv.getUnit());
	    setConfig(conv.getName() + ".expression", conv.getExpression().toString());
	}

	private void setConfig(String key, Object value) {
		plugin.getConfig().set("conversions." + key, value);
		plugin.saveConfig();
	}

   public CommandCompletionHandler<BukkitCommandCompletionContext> getCommandCompletionHandler() {
        return c -> conversions.keySet();
    }

    public ContextResolver<Conversion, BukkitCommandExecutionContext> getContextResolver() {
        return c -> {
            String key = c.popFirstArg();
            Conversion conversion = getConversion(key);
            if (conversion == null) {
                throw new InvalidCommandArgument("Unknown conversion \"" + key + "\"", false);
            }
            return conversion;
        };
    }

    public ParameterCondition<String, BukkitCommandExecutionContext, BukkitCommandIssuer> getUniqueConditionHandler() {
        return (c, exec, value) -> {
            if (getConversion(value) != null) {
                throw new InvalidCommandArgument("\"" + value + "\" already exists!", false);
            }
        };
    }

}
