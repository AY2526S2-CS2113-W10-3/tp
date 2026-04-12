package seedu.gitswole.command;

import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.ui.Ui;
import seedu.gitswole.parser.Parser;
import seedu.gitswole.storage.HistoryStorage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents a command that lists logged workout history.
 * <p>
 * Supported formats:
 * <ul>
 *   <li>{@code loglist} — lists all logged entries sorted by date</li>
 *   <li>{@code loglist w/WORKOUT_NAME} — lists all logged entries for a specific workout</li>
 *   <li>{@code loglist d/DATE} — lists all logged entries for a specific date (dd-MM-yyyy)</li>
 * </ul>
 */
public class LogListCommand extends Command {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private String response;
    private HistoryStorage historyStorage;

    /**
    * Constructs a LogListCommand with the raw user input string.
    * <p>
    * This constructor is used by the production application and initializes a real {@link HistoryStorage}.
    *
    * @param response The full command string entered by the user.
    */
    public LogListCommand(String response) {
        this(response, new HistoryStorage());
    }

    /**
    * Constructs a LogListCommand with a specific {@link HistoryStorage} instance.
    * <p>
    * This constructor supports dependency injection, allowing for the use of mock or stub
    * storage objects during testing.
    *
    * @param response       The full command string entered by the user.
    * @param historyStorage The {@link HistoryStorage} instance to use for reading history logs.
    */
    public LogListCommand(String response, HistoryStorage historyStorage) {
        assert response != null : "Response cannot be null";
        assert historyStorage != null : "HistoryStorage cannot be null";
        this.response = response;
        this.historyStorage = historyStorage;
    }

    /**
    * Executes the loglist command by determining the filter type and displaying results.
    *
    * @param workouts The current list of workouts (unused but required by interface).
    * @param ui       The user interface for displaying results.
    * @throws GitSwoleException If an error occurs reading the history file.
    */
    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        assert workouts != null : "WorkoutList must not be null";
        assert ui != null : "Ui must not be null";

        Parser.validateNoUnknownFlags(response, "w/", "d/");

        try {
            String[] parts = response.trim().split(" ");
            boolean hasArguments = parts.length > 1;
            boolean hasValidFlag = response.contains(" w/") || response.contains(" d/");

            if (response.contains(" w/")) {
                handleLogListByWorkout(ui);
            } else if (response.contains(" d/")) {
                handleLogListByDate(ui);
            } else if (hasArguments && !hasValidFlag) {
                LOGGER.log(Level.WARNING, "loglist command has unrecognised arguments: {0}", response);
                throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
                        "Command has unrecognised arguments");
            } else {
                handleLogListAll(ui);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to read history file: " + e.getMessage());
            throw new GitSwoleException(GitSwoleException.ErrorType.DEFAULT,
            "Could not read history log: " + e.getMessage());
        }
    }

    /**
    * Lists all logged entries for a specific workout name.
    */
    private void handleLogListByWorkout(Ui ui) throws IOException, GitSwoleException {
        String workoutName = Parser.parseValue(response, "w/");
        if (workoutName == null || workoutName.isEmpty()) {
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
            "invalid command. format: loglist w/WORKOUT_NAME");
        }

        List<String> entries = historyStorage.getEntriesByWorkout(workoutName);
        if (entries.isEmpty()) {
            ui.showMessage("No logged sessions found for workout: " + workoutName);
            ui.showLine();
            return;
        }

        ui.showMessage("=== LOG HISTORY FOR: " + workoutName.toUpperCase() + " ===");
        for (String line : entries) {
            ui.showMessage(line);
        }
        ui.showLine();
    }

    /**
    * Lists all logged entries for a specific date.
    */
    private void handleLogListByDate(Ui ui) throws IOException, GitSwoleException {
        String date = Parser.parseValue(response, "d/");
        if (date == null || date.isEmpty()) {
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
                    "invalid command. format: loglist d/DATE");
        }

        try {
            LocalDate.parse(date, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
                    "Invalid date format. Expected dd-MM-yyyy (e.g. 10-04-2026)");
        }

        List<String> entries = historyStorage.getEntriesByDate(date);
        if (entries.isEmpty()) {
            ui.showMessage("No logged sessions found for this date: " + date);
            ui.showLine();
            return;
        }

        ui.showMessage("=== LOG HISTORY FOR: " + date + " ===");
        for (String line : entries) {
            ui.showMessage(line);
        }
        ui.showLine();
    }

    /**
    * Lists all logged entries across all dates.
    */
    private void handleLogListAll(Ui ui) throws IOException {
        List<String> entries = historyStorage.getAllEntries();
        if (entries.isEmpty()) {
            ui.showMessage("No workout sessions have been logged yet.");
            ui.showLine();
            return;
        }

        ui.showMessage("=== COMPLETE LOG HISTORY ===");
        for (String line : entries) {
            ui.showMessage(line);
        }
        ui.showLine();
    }
}
