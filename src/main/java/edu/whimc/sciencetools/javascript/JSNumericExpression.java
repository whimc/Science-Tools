package edu.whimc.sciencetools.javascript;

/**
 * A numeric JavaScript expression.
 */
public class JSNumericExpression extends JSExpression {

    /**
     * Constructs a JSNumericExpression.
     *
     * @param expr the JavaScript expression
     */
    public JSNumericExpression(String expr) {
        super(expr);
    }

    /**
     * Checks if the JavaScript expression is a valid Double.
     *
     * @return whether the expression is a valid Double or not
     */
    @Override
    public boolean valid() {
        return evaluate(JSContext.create()) != null;
    }

    /**
     * Evaluates the JavaScript expression in the given JSContext without throwing a CommandError.
     *
     * @param ctx the JavaScript context
     * @return the resulting Double value
     */
    public Double evaluate(JSContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), false);
    }

    /**
     * Evaluates the JavaScript expression in the given JSContext.
     *
     * @param ctx the JavaScript context
     * @return the resulting Double value
     */
    private Double evaluateWithArgumentCheck(JSContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), true);
    }

}
