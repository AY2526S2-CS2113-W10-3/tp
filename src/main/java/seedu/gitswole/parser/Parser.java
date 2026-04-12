package seedu.gitswole.parser;

import seedu.gitswole.assets.WorkoutList;

import seedu.gitswole.command.EditCommand;
import seedu.gitswole.command.AddCommand;
import seedu.gitswole.command.DeleteCommand;
import seedu.gitswole.command.HelpCommand;
import seedu.gitswole.command.ExitCommand;
import seedu.gitswole.command.FindCommand;
import seedu.gitswole.command.ListCommand;
import seedu.gitswole.command.LogCommand;
import seedu.gitswole.command.MarkCommand;
import seedu.gitswole.command.LogListCommand;
import seedu.gitswole.command.Command;

import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.ui.Ui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses raw user input and maps it to the appropriate {@link Command} object.
 */
public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    // @@author vet3whale
    enum CommandType {
        ADD, DELETE, EXIT, HELP, LIST, FIND, MARK, EDIT, LOG, LOGLIST
    }

    private static final Map<String, CommandType> COMMAND_MAP = new HashMap<>();

    static {
        COMMAND_MAP.put("add", CommandType.ADD);
        COMMAND_MAP.put("delete", CommandType.DELETE);
        COMMAND_MAP.put("exit", CommandType.EXIT);
        COMMAND_MAP.put("help", CommandType.HELP);
        COMMAND_MAP.put("list", CommandType.LIST);
        COMMAND_MAP.put("find", CommandType.FIND);
        COMMAND_MAP.put("mark", CommandType.MARK);
        COMMAND_MAP.put("unmark", CommandType.MARK);
        COMMAND_MAP.put("edit", CommandType.EDIT);
        COMMAND_MAP.put("log", CommandType.LOG);
        COMMAND_MAP.put("loglist", CommandType.LOGLIST);
    }
    private Ui ui;

    /**
     * Constructs a Parser and initializes its user interface component.
     */
    public Parser() {
        ui = new Ui();
    }
    // @@author

    /**
     * Reads a full user input string and returns the corresponding {@link Command}.
     *
     * @param response The full command string entered by the user.
     * @param workouts The current workout list, used by certain commands such as list or find.
     * @return The {@link Command} object corresponding to the user's input.
     * @throws GitSwoleException If the input is empty, incomplete, or an unrecognized command.
     */
    public Command readResponse(String response, WorkoutList workouts) throws GitSwoleException {
        String[] words = response.split(" ");
        if (words.length == 0 || words[0].isEmpty()) {
            logger.log(Level.WARNING, "Empty input received.");
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "command");
        }
        String command = words[0];
        CommandType cmdType = parseCommand(command);

        switch (cmdType) {
        case ADD:
            if (words.length < 2) {
                logger.log(Level.WARNING, "Add command missing arguments.");
                throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
            }
            return new AddCommand(response);
        case DELETE:
            if (words.length < 2) {
                logger.log(Level.WARNING, "Delete command missing index.");
                throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
            }
            return new DeleteCommand(response);
        case MARK:
            if (words.length < 2) {
                throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
            }
            return new MarkCommand(response);
        case FIND:
            if (words.length < 2) {
                logger.log(Level.WARNING, "Find command missing keyword.");
                throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
            }
            return new FindCommand(response.substring(command.length()).trim());
        case EDIT:
            if (words.length < 2) {
                logger.log(Level.WARNING, "Edit command missing keyword.");
                throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
            }
            return new EditCommand(response);
        case LOG:
            if (words.length < 2) {
                logger.log(Level.WARNING, "Log command missing workout name.");
                throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
            }
            return new LogCommand(response);
        case LIST:
            return new ListCommand(response);
        case LOGLIST:
            return new LogListCommand(response);
        case HELP:
            return new HelpCommand();
        case EXIT:
            return new ExitCommand();
        default:
            assert false : "Unhandled CommandType: " + cmdType;
            throw new GitSwoleException(GitSwoleException.ErrorType.UNKNOWN_COMMAND, command);
        }
    }

    // @@author vet3whale
    /**
     * Determines the {@link CommandType} from the first word of the user's input.
     *
     * @param input The raw input string to parse.
     * @return The {@link CommandType} corresponding to the given command word.
     * @throws GitSwoleException If the input is null, blank, or does not match any known command.
     */
    private CommandType parseCommand(String input) throws GitSwoleException {
        if (input == null || input.isBlank()) {
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "command");
        }
        String cmd = input.trim().split(" ")[0].toLowerCase();
        CommandType type = COMMAND_MAP.get(cmd);
        if (type == null) {
            throw new GitSwoleException(GitSwoleException.ErrorType.UNKNOWN_COMMAND, cmd);
        }
        return type;
    }
    // @@author

    /**
     * Extracts the value associated with a flag in the user's input string.
     * <p>
     * Flags follow the format {@code flagName/value}, where the value spans
     * from after the flag to the next flag (indicated by a space then letters and a slash)
     * or the end of the input string. Values may contain multiple words.
     *
     * @param input  The full command string entered by the user.
     * @param prefix The flag to search for (e.g. {@code "w/"}, {@code "e/"}, {@code "wt/"}).
     * @return The trimmed value following the flag, or {@code null} if the flag is absent or has no value.
     */
    public static String parseValue(String input, String prefix) {
        if (input == null || prefix == null || prefix.isBlank()) {
            return null;
        }

        int start = -1;
        String searchToken = " " + prefix;

        // 1. Locate the prefix
        int idx = input.indexOf(searchToken);
        if (idx != -1) {
            start = idx + searchToken.length();
        } else if (input.startsWith(prefix)) {
            start = prefix.length();
        }

        if (start == -1) {
            return null;
        }

        // 2. Find the next flag, but do not scan past remark/
        int end = input.length();

        // Find where remark/ starts (if it exists)
        int remarkIdx = input.indexOf(" remark/");
        if (remarkIdx == -1) {
            remarkIdx = input.startsWith("remark/") ? 0 : input.length();
        }

        // Limit search to before remark/
        int searchEnd = Math.min(remarkIdx, input.length());

        // Only search within the allowed region
        Matcher m = Pattern.compile(" [a-zA-Z]+/")
                .matcher(input.substring(0, searchEnd));

        if (m.find(start)) {
            end = m.start();
        }

        String value = input.substring(start, end).trim();
        return value.isEmpty() ? null : value;
    }

    /**
     * Extracts an integer value associated with a flag in the user's input string.
     *
     * @param input        The full command string entered by the user.
     * @param prefix       The flag to search for (e.g. {@code "wt/"}, {@code "s/"}, {@code "r/"}).
     * @param defaultValue The value to return if the flag is missing or its value cannot be parsed as an integer.
     * @return The parsed integer value, or {@code defaultValue} if parsing fails or the flag is absent.
     */
    public static int parseOptionalInt(String input, String prefix, int defaultValue) {
        assert input != null : "Input string must not be null";
        assert prefix != null : "Prefix must not be null";

        String value = parseValue(input, prefix);

        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid integer format for prefix {0}: {1}. Using default: {2}",
                new Object[]{prefix, value, defaultValue});
            return defaultValue;
        }
    }

    /**
     * Validates that a name does not contain reserved characters that conflict
     * with the storage file format.
     *
     * @param name       The name to validate.
     * @param fieldLabel A human-readable label for error messages (e.g. "Workout").
     * @throws GitSwoleException If the name contains a reserved character.
     */
    public static void validateName(String name, String fieldLabel) throws GitSwoleException {
        if (name != null && name.contains("|")) {
            throw new GitSwoleException(
                    GitSwoleException.ErrorType.NEG_INPUT,
                    fieldLabel + " name cannot contain the '|' character (reserved for storage)."
            );
        }
    }

    /**
     * Extracts the remark value from the input string.
     * Unlike parseValue, this treats remark/ as a terminal flag,
     * capturing everything after it to end-of-string.
     *
     * @param input The full command string entered by the user.
     * @return The trimmed remark value, or null if the remark/ flag is absent.
     */
    public static String parseRemark(String input) {
        if (input == null) {
            return null;
        }
        String flag = "remark/";
        int idx = input.indexOf(" " + flag);
        int start = -1;
        if (idx != -1) {
            start = idx + 1 + flag.length();
        } else if (input.startsWith(flag)) {
            start = flag.length();
        }
        if (start == -1 || start >= input.length()) {
            return null;
        }
        String value = input.substring(start).trim();
        return value.isEmpty() ? null : value;
    }
}
