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
     * @param name       The name of the Conversion.
     * @param unit       The unit being converted to.
     * @param expression The Conversion equation.
     */
    protected Conversion(String name, String unit, JSNumericExpression expression) {
        this.name = name;
        this.unit = unit;
        this.expression = expression;
    }

    /**
     * Uses the Conversion's expression to calculate the passed Double into the new unit.
     *
     * @param val The Double value to convert.
     * @return A Double in the new unit.
     */
    public Double convert(double val) {
        return this.expression.evaluate(JSContext.create(val));
    }

    /**
     * The Conversion's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * The unit being converted to.
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * Sets a new unit and saves it to the config.
     *
     * @param unit The new unit.
     */
    public void setUnit(String unit) {
        this.unit = unit;
        this.manager.saveToConfig(this);
    }

    /**
     * The Conversion equation.
     */
    public JSExpression getExpression() {
        return this.expression;
    }

    /**
     * Sets a new JavaScript expression and saves it to the config.
     *
     * @param sender     The command's sender.
     * @param expression The new Conversion equation.
     */
    public void setExpression(CommandSender sender, JSNumericExpression expression) {
        this.expression = expression;
        this.manager.saveToConfig(this);
    }

    /**
     * Sends the user information about the conversion (name, expression, unit).
     *
     * @param sender The command's sender.
     */
    public void sendInfo(CommandSender sender) {
        Utils.msg(sender,
                getName() + ":",
                "  Expression: " + getExpression(),
                "  Unit: " + getUnit());
    }

}
