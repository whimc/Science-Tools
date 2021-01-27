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
        return evaluate() != null;
    }

    private String getPreparedExpression(CommandSender executor) {
        return null;
    }

    public Object run() {
        return runWithContext(Bukkit.getConsoleSender());
    }

    public Object runWithContext(CommandSender executor) {
        return JSEngine.runWithContext(executor, getPreparedExpression(executor));
    }

    public Double evaluate() {
        return evaluateWithContext(Bukkit.getConsoleSender());
    }

    public Double evaluateWithContext(CommandSender executor) {
        return JSEngine.evaluateWithContext(executor, getPreparedExpression(executor));
    }

    @Override
    public String toString() {
        return this.expr;
    }

}
