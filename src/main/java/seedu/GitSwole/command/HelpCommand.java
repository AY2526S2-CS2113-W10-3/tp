package seedu.GitSwole.command;

import seedu.GitSwole.assets.WorkoutList;
import seedu.GitSwole.exceptions.GitSwoleException;
import seedu.GitSwole.ui.Ui;

/**
 * Represents a command that displays a summary of all available GitSwole commands.
 */
public class HelpCommand extends Command{
	/**
	 * Constructs a HelpCommand.
	 */
	public HelpCommand() {}

	/**
	 * Executes the help command by printing a formatted command summary table.
	 *
	 * @param workouts The current list of workouts (unused).
	 * @param ui       The user interface used to display the help message.
	 * @throws GitSwoleException Not thrown by this command.
	 */
	@Override
	public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException{
		printHelpMessage(ui);
	}

	/**
	 * Prints a formatted table listing all supported commands, their formats, and examples.
	 *
	 * @param ui The user interface used to determine console width for formatting.
	 */
	public void printHelpMessage(Ui ui) {
		int consoleWidth = ui.getDashes();
		String format = "%-20s | %-60s | %-30s%n";

		// Header
		System.out.println("=".repeat(consoleWidth));
		String title = "GITSWOLE COMMAND SUMMARY";
		int padding = (consoleWidth - title.length()) / 2;
		System.out.println(" ".repeat(Math.max(0, padding)) + title);
		System.out.println("=".repeat(consoleWidth));

		// Table Columns
		System.out.printf(format, "ACTION", "FORMAT", "EXAMPLE");
		System.out.println("-".repeat(consoleWidth));

		// Commands
		System.out.printf(format, "Add Workout", "add w/WORKOUT", "add w/push");
		System.out.printf(format, "Add Exercise", "add e/EXERCISE w/WORKOUT [wt/WEIGHT] [s/SETS] [r/REPS]", "add e/benchpress w/push wt/40 s/3 r/8");
		System.out.printf(format, "Delete Workout", "delete w/WORKOUT", "delete w/push");
		System.out.printf(format, "Delete Exercise", "delete e/EXERCISE w/WORKOUT", "delete e/benchpress w/push");
		System.out.printf(format, "List Workouts", "list", "list");
		System.out.printf(format, "List Exercises 1", "list w/workout", "list w/pull");
		System.out.printf(format, "List Exercises 2", "list all", "list all");
		System.out.printf(format, "Find Workout", "find w/WORKOUT", "find w/push");
		System.out.printf(format, "Find Exercise", "find e/EXERCISE w/WORKOUT", "find e/benchpress w/push");
		System.out.printf(format, "Help", "help", "help");
		System.out.printf(format, "Exit", "exit", "exit");

		// Footer
		System.out.println("=".repeat(consoleWidth));
		System.out.println("For the full user guide, visit: https://xxxxxxxxx.com");
	}
}
