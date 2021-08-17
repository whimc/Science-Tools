package edu.whimc.sciencetools.commands;

/**
 * A runtime exception used to elevate errors during command execution.
 */
public class CommandError extends RuntimeException {

    private final String message;
    private final boolean sendUsage;

    /**
     * Constructs the CommandError.
     *
     * @param message   The message for the error to display in-game.
     * @param sendUsage Whether or not to send the command usage.
     */
    public CommandError(String message, boolean sendUsage) {
        this.message = message;
        this.sendUsage = sendUsage;
    }

    /**
     * @return The error message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return Whether or not the command usage should be sent.
     */
    public boolean isSendUsage() {
        return this.sendUsage;
    }
}
