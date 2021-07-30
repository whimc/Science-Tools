package edu.whimc.sciencetools.javascript;

/**
 * Manually defined JavaScript functions that can be used in the JS interpreter.
 */
public enum JSFunction {
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
    ),
    ;

    /* The String used in the config to call the function */
    private final String key;
    /* A description of how the function is used */
    private final String usage;
    /* The JavaScript definition of the function */
    private final String definition;

    /**
     * Constructs a JSFunction.
     *
     * @param key The String used in the config to call the function.
     * @param usage A description of how the function is used.
     * @param definition The JavaScript definition of the function.
     */
    JSFunction(String key, String usage, String definition) {
        this.key = key;
        this.usage = usage;
        this.definition = definition;

    }

    /**
     * @return The String used in the config to call the function.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @return A description of how the function is used.
     */
    public String getUsage() {
        return this.usage;
    }

    /**
     * @return The JavaScript definition of the function.
     */
    public String getDefinition() {
        return this.definition == null ? "" : this.definition;
    }

    /**
     * @return The function's key and description of how it is used.
     */
    public String fullUsage() {
        return "&f\"&e&o" + this.key + "&f\" &7- " + this.usage;
    }
}
