package edu.whimc.sciencetools.models.conversion;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSContext;
import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.javascript.JSNumericExpression;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

public class Conversion {

    private final ConversionManager manager = ScienceTools.getInstance().getConversionManager();

    private final String name;
    private String unit;
    private JSNumericExpression expression;

    protected Conversion(String name, String unit, JSNumericExpression expression) {
        this.name = name;
        this.unit = unit;
        this.expression = expression;
    }

    public Double convert(double val) {
        return this.expression.evaluate(JSContext.create(val));
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

    public void setExpression(CommandSender sender, JSNumericExpression expression) {
        this.expression = expression;
        this.manager.saveToConfig(this);
    }

    public void setUnit(String unit) {
        this.unit = unit;
        this.manager.saveToConfig(this);
    }

    public void sendInfo(CommandSender sender) {
        Utils.msg(sender,
                getName() + ":",
                "  Expression: " + getExpression(),
                "  Unit: " + getUnit());
    }

}
