package edu.whimc.sciencetools;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Utils {

	private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	private static ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("Nashorn");
	
	private static DecimalFormat numberFormatTwo = new DecimalFormat("#.00");
	
	public static String trim2Deci(double val) {
		return numberFormatTwo.format(val);
	}
	
	public double randomDouble(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}
	
	public int randomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	public static Double executeExpression(CommandSender sender, String exp) {
		Object res = null;
		try {
			res = scriptEngine.eval(exp);
		} catch (ScriptException e) {
			msg(sender, "&cYour expression contains invalid syntax!");
			return null; 
		}
		
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
