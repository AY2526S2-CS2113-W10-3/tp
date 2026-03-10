package seedu.GitSwole.parser;

import seedu.GitSwole.assets.WorkoutList;
import seedu.GitSwole.command.*;
import seedu.GitSwole.exceptions.GitSwoleException;
import seedu.GitSwole.ui.Ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses raw user input and maps it to the appropriate {@link Command} object.
 */
public class Parser {
	enum CommandType {
		ADD, DELETE, EXIT, HELP, LIST, FIND
	}
	private static final Map<String, CommandType> COMMAND_MAP = new HashMap<>();
	static {
		COMMAND_MAP.put("add",    CommandType.ADD);
		COMMAND_MAP.put("delete", CommandType.DELETE);
		COMMAND_MAP.put("exit",   CommandType.EXIT);
		COMMAND_MAP.put("help",   CommandType.HELP);
		COMMAND_MAP.put("list",   CommandType.LIST);
		COMMAND_MAP.put("find",   CommandType.FIND);
	}
	private Ui ui;

	/**
	 * Constructs a Parser and initializes its user interface component.
	 */
	public Parser() {
		ui = new Ui();
	}

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
		String command = words[0];
		CommandType cmdType = parseCommand(command);

		switch (cmdType) {
		case ADD:
			if (words.length < 2) {
				throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
			}
			return new AddCommand(response);
		case DELETE:
			if (words.length < 2) {
				throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
			}
			return new DeleteCommand(response);
		case FIND:
			if (words.length < 2) {
				throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
			}
			return new FindCommand(response);
		case LIST:
			return new ListCommand(response);
		case HELP:
			return new HelpCommand();
		case EXIT:
			return new ExitCommand();
		default:
			throw new GitSwoleException(GitSwoleException.ErrorType.UNKNOWN_COMMAND, command);
		}
	}

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

	/**
	 * Helper method to extract a string value following a specific prefix.
	 *
	 * @param input The full command string.
	 * @param prefix The prefix to look for (e.g., "w/").
	 * @return The value associated with the prefix, or null if prefix not found.
	 */
	 public static String parseValue(String input, String prefix) {
		 if (!input.contains(prefix)) {
			 return null;
		 }

		 int start = input.indexOf(prefix) + prefix.length();
		 int end = input.indexOf(" ", start);
		 if (end == -1) {
			 end = input.length();
		 }
		 return input.substring(start, end).trim();
	 }

	/**
	 * Helper method to parse an integer following a prefix, returning a default if missing.
	 *
	 * @param input The full command string.
	 * @param prefix The prefix to look for (e.g., "wt/").
	 * @param defaultValue The value to return if parsing fails or prefix is missing.
	 * @return The parsed integer or default value.
	 */
	 public static int parseOptionalInt(String input, String prefix, int defaultValue) {
		 String value = parseValue(input, prefix);
		 try {
			 return (value != null) ? Integer.parseInt(value) : defaultValue;
		 } catch (NumberFormatException e) {
		 	return defaultValue;
		 }
	 }
}
