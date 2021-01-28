package edu.whimc.sciencetools.javascript;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import edu.whimc.sciencetools.javascript.JSPlaceholder.JSPlaceholderContext;

public class JSExpression {

    public static ContextResolver<JSExpression, BukkitCommandExecutionContext> getJSExprContextResolver() {
        return c -> {
            JSExpression expr = new JSExpression(c.joinArgs());
            expr.runWithArgumentCheck(JSPlaceholderContext.create(c.getSender()));
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

    public Object run(JSPlaceholderContext ctx) {
        return JSEngine.run(getPreparedExpression(ctx), false);
    }

    protected String getPreparedExpression(JSPlaceholderContext ctx) {
        return JSPlaceholder.prepareExpression(ctx, this.expr);
    }

    private Object runWithArgumentCheck(JSPlaceholderContext ctx) {
        return JSEngine.run(getPreparedExpression(ctx), true);
    }

    @Override
    public String toString() {
        return this.expr;
    }

}
