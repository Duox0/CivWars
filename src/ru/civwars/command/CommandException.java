package ru.civwars.command;

public class CommandException extends Exception {

    private static final long serialVersionUID = 111111111111111L;
    private final Throwable cause;

    public CommandException() {
        this.cause = null;
    }

    public CommandException(String message) {
        super(message);
        this.cause = null;
    }

    public CommandException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public CommandException(Throwable throwable) {
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

}
