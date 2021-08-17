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
     * @param expr The JavaScript expression.
     */
    public JSExpression(String expr) {
        this.expr = expr;
    }

    public void setExpression(String newExpr) {
        this.expr = newExpr;
    }

    /**
     * @return The JavaScript expression.
     */
    public String getExpression() {
        return this.expr;
    }

    /**
     * Checks if the JavaScript expression is valid.
     *
     * @return Whether or not the JavaScript expression is valid.
     */
    public boolean valid() {
        return run(JSContext.create(), false) != null;
    }

    /**
     * Runs the JavaScript engine.
     *
     * @param ctx               The JavaScript context.
     * @param throwCommandError Whether or not to throw a command error.
     * @return The value returned from the execution of the script.
     */
    public Object run(JSContext ctx, boolean throwCommandError) {
        return JSEngine.run(getPreparedExpression(ctx), throwCommandError);
    }

    /**
     * Gets the JavaScript expression with placeholders swapped out for their replacements.
     *
     * @param ctx The JavaScript context.
     * @return The JavaScript expression with placeholders swapped out for their replacements.
     */
    protected String getPreparedExpression(JSContext ctx) {
        return JSPlaceholder.prepareExpression(ctx, this.expr);
    }

    /**
     * @return The JSExpression as a String.
     */
    @Override
    public String toString() {
        return this.expr;
    }

}
