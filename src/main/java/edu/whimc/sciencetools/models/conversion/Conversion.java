package edu.whimc.sciencetools.models.conversion;

import edu.whimc.sciencetools.ScienceTools;
import edu.whimc.sciencetools.javascript.JSContext;
import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.javascript.JSNumericExpression;
import edu.whimc.sciencetools.utils.Utils;
import org.bukkit.command.CommandSender;

/**
 * A unit conversion.
 */
public class Conversion {
    /* The plugin's conversion manager */
    private final ConversionManager manager = ScienceTools.getInstance().getConversionManager();

    /* The name of the conversion */
    private final String name;
    /* The unit of measurement being converted to */
    private String unit;
    /* The expression to calculate the passed value in the new unit*/
    private JSNumericExpression expression;

    /**
     * Constructs a Conversion.
     *
     * @param name the name of the Conversion
     * @param unit the unit being converted to
     * @param expression the Conversion equation
     */
    protected Conversion(String name, String unit, JSNumericExpression expression) {
        this.name = name;
        this.unit = unit;
        this.expression = expression;
    }

    /**
     * Uses the Conversion's expression to calculate the passed Double into the new unit.
     *
     * @param val the Double value to convert
     * @return a Double in the new unit
     */
    public Double convert(double val) {
        return this.expression.evaluate(JSContext.create(val));
    }

    /**
     * @return the Conversion's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the unit being converted to
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * @return the Conversion equation
     */
    public JSExpression getExpression() {
        return this.expression;
    }

    /**
     * Sets a new JavaScript expression and saves it to the config.
     *
     * @param sender the command's sender
     * @param expression the new Conversion equation
     */
    public void setExpression(CommandSender sender, JSNumericExpression expression) {
        this.expression = expression;
        this.manager.saveToConfig(this);
    }

    /**
     * Sets a new unit and saves it to the config.
     *
     * @param unit the new unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
        this.manager.saveToConfig(this);
    }

    /**
     * Sends the user information about the conversion (name, expression, unit).
     *
     * @param sender the command's sender
     */
    public void sendInfo(CommandSender sender) {
        Utils.msg(sender,
                getName() + ":",
                "  Expression: " + getExpression(),
                "  Unit: " + getUnit());
    }

}
