package edu.whimc.sciencetools.javascript;

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

    private final String key;
    private final String usage;
    private final String definition;

    JSFunction(String key, String usage, String definition) {
        this.key = key;
        this.usage = usage;
        this.definition = definition;

    }

    public String getKey() {
        return this.key;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getDefinition() {
        return this.definition == null ? "" : this.definition;
    }

    public String fullUsage() {
        return "&f\"&e&o" + this.key + "&f\" &7- " + this.usage;
    }
}
