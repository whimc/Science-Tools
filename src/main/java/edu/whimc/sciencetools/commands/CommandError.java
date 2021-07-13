package edu.whimc.sciencetools.commands;

public class CommandError extends RuntimeException {

    private final String message;
    private final boolean sendUsage;

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
