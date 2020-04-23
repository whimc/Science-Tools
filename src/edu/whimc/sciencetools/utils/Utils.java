package edu.whimc.sciencetools.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.ScienceTools;

public class Utils {

	private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	private static ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("Nashorn");
	
	static {
		try {
			scriptEngine.eval("function rand(min, max) { return Math.random() * (+max - +min) + +min }");
			scriptEngine.eval("function randInt(min, max) { return Math.floor(rand(min, max + 1)) }");
			scriptEngine.eval("function min(a, b) { return Math.min(a, b) }");
			scriptEngine.eval("function max(a, b) { return Math.max(a, b) }");
		} catch (ScriptException e) {}
	}
	
	public static enum Placeholder {
		VALUE("{VAL}"),
		X_POS("{X}"),
		Y_POS("{Y}"),
		Z_POS("{Z}"),
		ALTITUDE("{ALTITUDE}"),
		OXYGEN("{OXYGEN}"),
		PRESSURE("{PRESSURE}"),
		TEMPERATURE("{TEMPERATURE}"),
		WIND("{WIND}");
		
		private String key;
		
		private Placeholder(String key) {
			this.key = key;
		}
		
		@Override
		public String toString() {
			return key;
		}
	}
	
	public static String trim2Deci(double val) {
		return String.format("%,.2f", val);
	}
	
	private static CommandSender debugReceiver = null;
	public static void setDebugReceiver(CommandSender sender) {
		debugReceiver = sender;
	}
	
	public static void log(ScienceTools plugin, String message) {
		if (debugReceiver != null) {
			Utils.msg(debugReceiver, message);
		}
		plugin.getLogger().info(message);
		
	}
	
	public static Object executeExpressionDebug(CommandSender sender, String exp, boolean verbose) {
		Object res = null;
		try {
			res = scriptEngine.eval(exp);
		} catch (ScriptException e) {
			msg(sender, "&cYour expression contains invalid syntax!");
			
			if (verbose) {
				msg(sender, e.getMessage());
			}
			
			return null; 
		}
		
		return res;
	}
	
	public static Double executeExpression(CommandSender sender, String exp) {
		Object res = executeExpressionDebug(sender, exp, false);
		
		if (!(res instanceof Number)) {
			String type = "Unknown";
			if (res != null) {
				type = res.getClass().getSimpleName();
			}
			
			msg(sender, "&cExpected a number but found type &7&o" + type);
			return null;
		}
		
		return Double.valueOf(((Number) res).doubleValue());
	}
	
	public static void msg(CommandSender sender, String str) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
	}
}
