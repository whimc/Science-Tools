package edu.whimc.sciencetools.commands;

/**
 * A command error runtime exception.
 */
public class CommandError extends RuntimeException {

    private final String message;
    private final boolean sendUsage;

    /**
     * Constructs the CommandError.
     *
     * @param message the message for the error to display in-game
     * @param sendUsage whether or not to send the command usage
     */
    public CommandError(String message, boolean sendUsage) {
        this.message = message;
        this.sendUsage = sendUsage;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isSendUsage() {
        return this.sendUsage;
    }
}
