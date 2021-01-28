package edu.whimc.sciencetools.javascript;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import edu.whimc.sciencetools.javascript.JSPlaceholder.JSPlaceholderContext;

public class JSNumericalExpression extends JSExpression {

    public static ContextResolver<JSNumericalExpression, BukkitCommandExecutionContext> getJSNumExprContextResolver() {
        return c -> {
            JSNumericalExpression expr = new JSNumericalExpression(c.joinArgs());
            expr.evaluateWithArgumentCheck(JSPlaceholderContext.create(c.getSender()));
            return expr;
        };
    }


    public JSNumericalExpression(String expr) {
        super(expr);
    }

    @Override
    public boolean valid() {
        return evaluate(JSPlaceholderContext.create()) != null;
    }

    public Double evaluate(JSPlaceholderContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), false);
    }

    private Double evaluateWithArgumentCheck(JSPlaceholderContext ctx) {
        return JSEngine.evaluate(getPreparedExpression(ctx), true);
    }

}
