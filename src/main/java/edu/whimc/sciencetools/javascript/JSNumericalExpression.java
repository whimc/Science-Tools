package edu.whimc.sciencetools.javascript;

public class JSNumericalExpression extends JSExpression {

    public JSNumericalExpression(String expr) {
        super(expr);
    }

    @Override
    public boolean valid() {
        return evaluate(JSContext.create()) != null;
    }

    public Double evaluate(JSContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), false);
    }

    private Double evaluateWithArgumentCheck(JSContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), true);
    }

}
