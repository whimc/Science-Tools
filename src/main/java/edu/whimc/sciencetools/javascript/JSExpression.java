package edu.whimc.sciencetools.javascript;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;

public class JSExpression {

    public static ContextResolver<JSExpression, BukkitCommandExecutionContext> getContextResolver() {
        // TODO make this better
        return c -> {
            JSExpression expr = new JSExpression(c.joinArgs());
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
        // TODO implement this
        return false;
    }

    private String getPreparedExpression() {
        return null;
    }

    private String getPreparedExpression(CommandSender executor) {
        return null;
    }

    public Object run() {
        return JSEngine.run(getPreparedExpression());
    }

    public Object runWithContext(CommandSender executor) {
        return JSEngine.runWithContext(executor, getPreparedExpression());
    }

    public Double evaluate(JSVariable... vars) {
        return execute(Bukkit.getConsoleSender(), vars);
    }


    public Double execute(CommandSender executor, JSVariable... vars) {
        // TODO implement this
        return null;
    }

    public Double execute(double defaultVal, JSVariable... vars) {
        String subbed = JSVariable.substituteVariables(this.expr, vars);

        return JSEngine.eval(subbed, defaultVal);
    }

    @Override
    public String toString() {
        return this.expr;
    }

}
