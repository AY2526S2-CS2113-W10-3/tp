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
		ADD, DELETE, EXIT, HELP, LIST, FIND, MARK
	}
	private static final Map<String, CommandType> COMMAND_MAP = new HashMap<>();
	static {
		COMMAND_MAP.put("add",    CommandType.ADD);
		COMMAND_MAP.put("delete", CommandType.DELETE);
		COMMAND_MAP.put("exit",   CommandType.EXIT);
		COMMAND_MAP.put("help",   CommandType.HELP);
		COMMAND_MAP.put("list",   CommandType.LIST);
		COMMAND_MAP.put("find",   CommandType.FIND);
		COMMAND_MAP.put("mark",	  CommandType.MARK);
		COMMAND_MAP.put("unmark", CommandType.MARK);
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
		case MARK:
			if (words.length < 2) {
				throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, command);
			}
			return new MarkCommand(response);
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
	 * Extracts the value associated with a flag in the user's input string.
	 * <p>
	 * Flags follow the format {@code /flagName value}, where the value spans
	 * from after the flag to the next flag (indicated by {@code " /"}) or the
	 * end of the input string. Values may contain multiple words.
	 * <p>
	 * Examples:
	 * <ul>
	 *   <li>{@code parseValue("add /w push day", "/w")} returns {@code "push day"}</li>
	 *   <li>{@code parseValue("add /e bench press /w push", "/e")} returns {@code "bench press"}</li>
	 *   <li>{@code parseValue("add /w push", "/e")} returns {@code null}</li>
	 * </ul>
	 *
	 * @param input  The full command string entered by the user.
	 * @param prefix The flag to search for (e.g. {@code "/w"}, {@code "/e"}, {@code "/wt"}).
	 * @return The trimmed value following the flag, or {@code null} if the flag is absent or has no value.
	 */
	 public static String parseValue(String input, String prefix) {
		 String searchToken = prefix + " ";
		 int start = -1;

		 if (input.startsWith(searchToken)) {
			 start = searchToken.length();
		 } else {
			 int index = input.indexOf(" " + searchToken);

			 if (index == -1) {
				 return null;
			 }

			 start = index + 1 + searchToken.length();
		 }

		 int end = input.indexOf(" /", start);
		 if (end == -1) {
			 end = input.length();
		 }

		 String value = input.substring(start, end).trim();
		 return value.isEmpty() ? null : value;
	 }

	/**
	 * Extracts an integer value associated with a flag in the user's input string,
	 * returning a default value if the flag is absent or its value is not a valid integer.
	 * <p>
	 * Delegates to {@link #parseValue(String, String)} to locate the flag value,
	 * then attempts to parse it as an integer.
	 * <p>
	 * Examples:
	 * <ul>
	 *   <li>{@code parseOptionalInt("add /e bench /w push /wt 40", "/wt", 0)} returns {@code 40}</li>
	 *   <li>{@code parseOptionalInt("add /e bench /w push", "/wt", 0)} returns {@code 0}</li>
	 *   <li>{@code parseOptionalInt("add /e bench /w push /wt heavy", "/wt", 0)} returns {@code 0}</li>
	 * </ul>
	 *
	 * @param input        The full command string entered by the user.
	 * @param prefix       The flag to search for (e.g. {@code "/wt"}, {@code "/s"}, {@code "/r"}).
	 * @param defaultValue The value to return if the flag is missing or its value cannot be parsed as an integer.
	 * @return The parsed integer value, or {@code defaultValue} if parsing fails or the flag is absent.
	 */
	 public static int parseOptionalInt(String input, String prefix, int defaultValue) {
		 String value = parseValue(input, prefix);

		 if (value == null) {
			 return defaultValue;
		 }

		 try {
			 return Integer.parseInt(value.trim());
		 } catch (NumberFormatException e) {
		 	return defaultValue;
		 }
	 }
}
