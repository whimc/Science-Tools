package edu.whimc.sciencetools.javascript;

/**
 * A JavaScript expression.
 */
public class JSExpression {
    /* The JavaScript expression */
    private String expr;

    /**
     * Constructs a JSExpression.
     *
     * @param expr the JavaScript expression
     */
    public JSExpression(String expr) {
        this.expr = expr;
    }

    public void setExpression(String newExpr) {
        this.expr = newExpr;
    }

    /**
     * @return the JavaScript expression
     */
    public String getExpression() {
        return this.expr;
    }

    /**
     * Checks if the JavaScript expression is valid.
     *
     * @return whether or not the JavaScript expression is valid
     */
    public boolean valid() {
        return run(JSContext.create(), false) != null;
    }

    /**
     * Runs the JavaScript engine.
     *
     * @param ctx the JavaScript context
     * @param throwCommandError whether or not to throw a command error
     * @return the value returned from the execution of the script
     */
    public Object run(JSContext ctx, boolean throwCommandError) {
        return JSEngine.run(getPreparedExpression(ctx), throwCommandError);
    }

    /**
     * Gets the JavaScript expression with placeholders swapped out for their replacements.
     *
     * @param ctx the JavaScript context
     * @return the JavaScript expression with placeholders swapped out for their replacements
     */
    protected String getPreparedExpression(JSContext ctx) {
        return JSPlaceholder.prepareExpression(ctx, this.expr);
    }

    /**
     * Converts the JSExpression into a String.
     *
     * @return the JSExpression as a String
     */
    @Override
    public String toString() {
        return this.expr;
    }

}
