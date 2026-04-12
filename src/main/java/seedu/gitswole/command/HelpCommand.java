package seedu.gitswole.command;

import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.ui.Ui;

//@@author vet3whale
/**
 * Represents a command that displays a summary of all available GitSwole commands.
 */
public class HelpCommand extends Command {
    /**
     * Constructs a HelpCommand.
     */
    public HelpCommand() {
    }

    /**
     * Executes the help command by printing a formatted command summary table.
     *
     * @param workouts The current list of workouts (unused).
     * @param ui       The user interface used to display the help message.
     * @throws GitSwoleException Not thrown by this command.
     */
    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        printHelpMessage(ui);
    }

    /**
     * Prints a formatted table listing all supported commands, their formats, and examples.
     *
     * @param ui The user interface used to determine console width for formatting.
     */
    public void printHelpMessage(Ui ui) {
        int consoleWidth = ui.getDashes();
        String format = "%-21s | %-60s | %-30s";

        // Header
        ui.showMessage("=".repeat(consoleWidth));
        String title = "GITSWOLE COMMAND SUMMARY";
        int padding = (consoleWidth - title.length()) / 2;
        ui.showMessage(" ".repeat(Math.max(0, padding)) + title);
        ui.showMessage("=".repeat(consoleWidth));

        // Table Columns
        ui.showMessage(String.format(format, "ACTION", "FORMAT", "EXAMPLE"));
        ui.showMessage("-".repeat(consoleWidth));

        // Commands
        String[][] commands = {
            {"Add Workout", "add w/WORKOUT_NAME", "add w/push"},
            {"Add Exercise", "add e/EXERCISE_NAME w/WORKOUT_NAME", "add e/benchpress w/push"},
            {"", "    [wt/WEIGHT] [s/SETS] [r/REPS]", "    wt/40 s/3 r/8"},
            {"Delete Workout", "delete w/WORKOUT", "delete w/push"},
            {"Delete Exercise", "delete e/EXERCISE w/WORKOUT", "delete e/benchpress w/push"},
            {"Edit Workout", "edit w/WORKOUT_NAME", "edit w/push"},
            {"Edit Exercise", "edit w/WORKOUT_NAME e/EXERCISE_NAME", "edit w/push e/benchpress"},
            {"List Workouts", "list", "list"},
            {"List Exercises", "list w/WORKOUT", "list w/pull"},
            {"List ALL Exercises", "list all", "list all"},
            {"Log Session", "log w/WORKOUT", "log w/push"},
            {"Log Performance", "log e/EXERCISE w/WORKOUT wt/W s/S r/R", "log e/bench w/push wt/60 s/3 r/8"},
            {"Find Workout/Exercise", "find KEYWORD", "find push"},
            {"Mark Workout" , "mark w/WORKOUT_NAME", "mark w/push"},
            {"Unmark Workout", "unmark w/WORKOUT_NAME", "unmark w/push"},
            {"List Log (All)", "loglist", "loglist"},
            {"List Log (Workout)", "loglist w/WORKOUT_NAME", "loglist w/push"},
            {"List Log (Date)", "loglist d/DATE", "loglist d/24-03-2026"},
            {"Help", "help", "help"},
            {"Exit", "exit", "exit"}
        };

        for (String[] row : commands) {
            ui.showMessage(String.format(format, row[0], row[1], row[2]));
        }

        // Footer
        ui.showMessage("=".repeat(consoleWidth));
        ui.showMessage("For the full user guide, visit: https://ay2526s2-cs2113-w10-3.github.io/tp/UserGuide.html");
    }
}
//@@author
