package seedu.gitswole.exceptions;

//@@author vet3whale
/**
 * Represents a custom exception for errors encountered during GitSwole command execution.
 * Each exception carries an {@link ErrorType} and the command string that caused it,
 * and provides a human-readable message via {@link #getMessage()}.
 */
public class GitSwoleException extends Exception {
    /**
     * Defines the categories of errors that can occur in the application.
     */
    public enum ErrorType {
        INCOMPLETE_COMMAND,
        UNKNOWN_COMMAND,
        IDX_OUTOFBOUNDS,
        NOT_FOUND,
        NEG_INPUT,
        DUPLICATE,
        DEFAULT
    }

    private ErrorType type;
    private String command;

    /**
     * Constructs a GitSwoleException with the specified error type and associated command.
     *
     * @param type    The category of error that occurred.
     * @param command The command string that triggered the exception.
     */
    public GitSwoleException(ErrorType type, String command) {
        super(buildMessage(type, command));
        this.type = type;
        this.command = command;
    }

    /**
     * Builds a human-readable error message based on the error type and command.
     *
     * @param type    The category of error.
     * @param command The command string involved.
     * @return A formatted error message string.
     */
    private static String buildMessage(ErrorType type, String command) {
        switch (type) {
        case INCOMPLETE_COMMAND:
            return command + " ? Please complete your command!";
        case UNKNOWN_COMMAND:
            return "What's " + command + " ? Invalid command. Try again...";
        case IDX_OUTOFBOUNDS:
            return "Workout does not exist. Try again...";
        case NOT_FOUND:
            return "'" + command + "' not found. Please check your spelling.";
        case NEG_INPUT:
            return command;
        case DUPLICATE:
            return command;
        case DEFAULT:
            return command;
        default:
            return "Error! Try again...";
        }
    }

    /**
     * Returns the error type associated with this exception.
     *
     * @return The {@link ErrorType} of this exception.
     */
    public ErrorType getType() {
        return type;
    }

    /**
     * Returns the command string that triggered this exception.
     *
     * @return The command string.
     */
    public String getCommand() {
        return command;
    }
}
//@@author
