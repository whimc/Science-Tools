package edu.whimc.sciencetools.javascript;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import edu.whimc.sciencetools.javascript.JSPlaceholder.JSPlaceholderContext;

public class JSExpression {

    public static ContextResolver<JSExpression, BukkitCommandExecutionContext> getContextResolver() {
        return c -> {
            JSExpression expr = new JSExpression(c.joinArgs());
            JSPlaceholderContext ctx = JSPlaceholderContext.create(c.getSender());
            if (c.hasFlag("any-type")) {
                expr.runWithArgumentCheck(ctx);
            } else {
                expr.evaluateWithArgumentCheck(ctx);
            }
            return expr;
        };
    }

    private String expr;

    public JSExpression(String expr) {
        this.expr = expr;
    }

    public void setExpression(String newExpr) {
        this.expr = newExpr;
    }

    public String getExpression() {
        return this.expr;
    }

    public boolean valid() {
        return run(JSPlaceholderContext.create()) != null;
    }

    public boolean numerical() {
        return evaluate(JSPlaceholderContext.create()) != null;
    }

    private String getPreparedExpression(JSPlaceholderContext ctx) {
        return JSPlaceholder.prepareExpression(ctx, this.expr);
    }

    public Object run(JSPlaceholderContext ctx) {
        return JSEngine.run(getPreparedExpression(ctx), false);
    }

    private Object runWithArgumentCheck(JSPlaceholderContext ctx) {
        return JSEngine.run(getPreparedExpression(ctx), true);
    }

    public Double evaluate(JSPlaceholderContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), false);
    }

    private Double evaluateWithArgumentCheck(JSPlaceholderContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), true);
    }

    @Override
    public String toString() {
        return this.expr;
    }

}
