package edu.whimc.sciencetools.models.conversion;

import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.javascript.JSNumericalExpression;
import edu.whimc.sciencetools.javascript.JSPlaceholder.JSPlaceholderContext;
import edu.whimc.sciencetools.utils.Utils;

public class Conversion {

	private ConversionManager manager;

	private String name, unit;
	private JSNumericalExpression expression;

	public Conversion(ConversionManager manager, String name, String unit, JSNumericalExpression expression) {
		this.manager = manager;
		this.name = name;
		this.unit = unit;
		this.expression = expression;
	}

	public Double convert(double val) {
		return expression.evaluate(JSPlaceholderContext.create(val));
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

	public void setExpression(CommandSender sender, JSNumericalExpression expression) {
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
