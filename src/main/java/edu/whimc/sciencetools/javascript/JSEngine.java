package edu.whimc.sciencetools.javascript;

import edu.whimc.sciencetools.commands.CommandError;

import javax.script.*;

/**
 * The JavaScript engine.
 */
public class JSEngine {

    private static final ScriptEngineManager engineManager = new ScriptEngineManager();
    private static final ScriptEngine engine = engineManager.getEngineByName("Nashorn");
    private static final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

    /* Set up the engine and remove some harmful bindings */
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
        } catch (ScriptException ignored) {
        }
    }

    /**
     * Runs the passed JavaScript code.
     *
     * @param code the JavaScript code to run
     * @param throwCommandError whether or not to throw a CommandError
     * @return the value returned from the execution of the script
     */
    protected static Object run(String code, boolean throwCommandError) {
        try {
            return engine.eval(code);
        } catch (ScriptException exc) {
            if (throwCommandError) {
                throw new CommandError("&eYour expression contains invalid syntax!\n" + exc.getMessage(), false);
            }
            return null;
        }
    }

    /**
     * Evaluates the passed JavaScript expression.
     *
     * @param expression the JavaScript expression to evaluate
     * @param throwCommandError whether or not to throw a CommandError
     * @return the resulting Double value
     */
    protected static Double evaluate(String expression, boolean throwCommandError) {
        Object res = run(expression, throwCommandError);

        if (res instanceof Number) {
            return ((Number) res).doubleValue();
        }

        if (!throwCommandError) {
            return null;
        }

        String type = "Unknown";
        if (res != null) {
            type = res.getClass().getSimpleName();
        }

        throw new CommandError("&eThe JavaScript expression must resolve to a number (Found type \"" + type + "\")", false);
    }

}
