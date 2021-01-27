package edu.whimc.sciencetools.javascript;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.command.CommandSender;

import co.aikar.commands.InvalidCommandArgument;

public class JSEngine {

    private static final ScriptEngineManager engineManager = new ScriptEngineManager();
    private static final ScriptEngine engine = engineManager.getEngineByName("Nashorn");
    private static final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

    /**
     * Set up the engine and remove some harmful bindings
     */
    static {
        bindings.remove("print");
        bindings.remove("load");
        bindings.remove("loadWithNewGlobal");
        bindings.remove("exit");
        bindings.remove("java");
        bindings.remove("quit");
        try {
            for (JSFunction func : JSFunction.values()) {
                engine.eval(func.getDefinition());
            }
        } catch (ScriptException e) {}
    }

    protected static Object run(String code) {
        try {
            return engine.eval(code);
        } catch (ScriptException e) {
            return null;
        }
    }

    protected static Object runWithContext(CommandSender executor, String code) {
        try {
            return engine.eval(code);
        } catch (ScriptException e) {
            String error = "Your expression contains invalid syntax!\n";
            error += e.getMessage();

            throw new InvalidCommandArgument(error, false);
        }
    }

    protected static Double evaluate(String expression) {
        Object res = run(expression);
        if (!(res instanceof Number)) {
            return Double.valueOf(0);
        }

        return Double.valueOf(((Number) res).doubleValue());
    }

    protected static Double evaluateWithContext(CommandSender executor, String expression) {
        Object res = runWithContext(executor, expression);
        if (!(res instanceof Number)) {
            String type = "Unknown";
            if (res != null) {
                type = res.getClass().getSimpleName();
            }

            throw new InvalidCommandArgument("Expected a number but found type " + type, false);
        }

        return Double.valueOf(((Number) res).doubleValue());
    }

}
