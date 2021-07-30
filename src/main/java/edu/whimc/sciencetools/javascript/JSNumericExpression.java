package edu.whimc.sciencetools.javascript;

/**
 * A numeric JavaScript expression.
 * This differs from {@link edu.whimc.sciencetools.javascript.JSExpression} by requiring the result be a number.
 */
public class JSNumericExpression extends JSExpression {

    /**
     * Constructs a JSNumericExpression.
     *
     * @param expr The JavaScript expression.
     */
    public JSNumericExpression(String expr) {
        super(expr);
    }

    /**
     * Checks if the JavaScript expression is a valid Double.
     *
     * @return Whether the expression is a valid Double or not.
     */
    @Override
    public boolean valid() {
        return evaluate(JSContext.create()) != null;
    }

    /**
     * Evaluates the JavaScript expression in the given JSContext without throwing a CommandError.
     *
     * @param ctx The JavaScript context.
     * @return The resulting Double value.
     */
    public Double evaluate(JSContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), false);
    }

    /**
     * Evaluates the JavaScript expression in the given JSContext.
     *
     * @param ctx The JavaScript context.
     * @return The resulting Double value.
     */
    private Double evaluateWithArgumentCheck(JSContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), true);
    }

}
