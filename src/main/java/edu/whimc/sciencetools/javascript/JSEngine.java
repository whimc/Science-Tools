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
            for (Placeholder ph : Placeholder.values()) {
                if (!ph.definition.isEmpty()) {
                    engine.eval(ph.getDefinition());
                }
            }
        } catch (ScriptException e) {}
    }

    public static enum Placeholder {
        // Values
        VALUE("{VAL}", "Value to convert"),
        X_POS("{X}", "Current X value"),
        Y_POS("{Y}", "Current Y value"),
        Z_POS("{Z}", "Current Z value"),
        ALTITUDE("{ALTITUDE}", "Value from /altitude"),
        OXYGEN("{OXYGEN}", "Value from /oxygen"),
        PRESSURE("{PRESSURE}", "Value from /pressure"),
        RADIATION("{RADIATION}", "Value from /radiation"),
        TEMPERATURE("{TEMPERATURE}", "Value from /temperature"),
        WIND("{WIND}", "Value from /wind"),

        // Functions
        RAND(
            "rand(min, max)",
            "Random decimal between 'min' and 'max' (inclusive)",
            "function rand(min, max) { return Math.random() * (+max - +min) + +min }"
        ),
        RAND_INT(
            "randInt(min, max)",
            "Random integer between 'min' and 'max' (inclusive",
            "function randInt(min, max) { return Math.floor(rand(min, max + 1)) }"
        ),
        MIN(
            "min(a, b)",
            "The minimum between 'a' and 'b'",
            "function min(a, b) { return Math.min(a, b) }"
        ),
        MAX(
            "max(a, b)",
            "The maximum between 'a' and 'b'",
            "function max(a, b) { return Math.max(a, b) }"
        );

        public static final double DEFAULT = 1.0;

        private String key;
        private String usage;
        private String definition;

        private Placeholder(String key, String usage) {
            this.key = key;
            this.usage = usage;
            this.definition = "";
        }

        private Placeholder(String key, String usage, String definition) {
            this.key = key;
            this.usage = usage;
            this.definition = definition;

        }

        public String getKey() {
            return this.key;
        }

        public String getDefinition() {
            return this.definition;
        }

        @Override
        public String toString() {
            return this.key;
        }

        public String fullUsage() {
            return "&f\"&e&o" + this.key + "&f\" &7- " + this.usage;
        }
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
