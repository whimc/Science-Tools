package edu.whimc.sciencetools.models;

import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.javascript.JSEngine;
import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.javascript.JSVariable.ConversionVariable;
import edu.whimc.sciencetools.managers.ConversionManager;
import edu.whimc.sciencetools.utils.Utils;

public class Conversion {

	private ConversionManager manager;

	private String name, unit;
	private JSExpression expression;

	public Conversion(ConversionManager manager, String name, String unit, JSExpression expression) {
		this.manager = manager;
		this.name = name;
		this.unit = unit;
		this.expression = expression;
	}

	public double convert(CommandSender sender, ConversionVariable var) {
		String exp = expression.replace(JSEngine.Placeholder.VALUE.toString(), Double.toString(val));

		Double res = Utils.executeExpression(sender, exp);
		return res == null ? 0 : res;
	}

	public String getName() {
		return this.name;
	}

	public String getUnit() {
	    return this.unit;
	}

	public JSExpression getExpression() {
		return this.expression;
	}

	public void setExpression(CommandSender sender, JSExpression expression) {
		this.expression = expression;
		manager.saveToConfig(this);
	}

	public void setUnit(String unit) {
		this.unit = unit;
		manager.saveToConfig(this);
	}

	public void sendInfo(CommandSender sender) {
	    Utils.msg(sender,
	            getName() + ":",
	            "  Expression: " + getExpression(),
	            "  Unit: " + getUnit());
	}

}
