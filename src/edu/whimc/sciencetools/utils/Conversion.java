package edu.whimc.sciencetools.utils;

import org.bukkit.command.CommandSender;

import edu.whimc.sciencetools.utils.Utils.Placeholder;

public class Conversion {

	private ConversionManager manager;

	private String name;
	private String expression;
	private String unit;
	
	public Conversion(ConversionManager manager, String name, String expression, String unit) {
		this.manager = manager;
		this.name = name;
		this.expression = expression;
		this.unit = unit;
	}
	
	public double convert(CommandSender sender, double val) {
		String exp = expression.replace(Placeholder.VALUE.toString(), Double.toString(val));
		
		Double res = Utils.executeExpression(sender, exp);
		return res == null ? 0 : res;
	}

	public String getName() {
		return name;
	}
	
	public String getExpression() {
		return expression;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public void setExpression(CommandSender sender, String expression) {
		this.expression = expression;
		manager.setExpression(this, expression);
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
		manager.setUnit(this, unit);
	}
	
}
