package edu.whimc.sciencetools.javascript;

public class JSExpression {

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
        return run(JSContext.create(), false) != null;
    }

    public Object run(JSContext ctx, boolean throwCommandError) {
        return JSEngine.run(getPreparedExpression(ctx), throwCommandError);
    }

    protected String getPreparedExpression(JSContext ctx) {
        return JSPlaceholder.prepareExpression(ctx, this.expr);
    }

    @Override
    public String toString() {
        return this.expr;
    }

}
