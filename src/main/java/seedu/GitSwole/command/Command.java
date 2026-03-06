package seedu.GitSwole.command;

import seedu.GitSwole.assets.WorkoutList;
import seedu.GitSwole.exceptions.GitSwoleException;
import seedu.GitSwole.ui.Ui;

/**
 * Represents an abstract command that can be executed within the GitSwole application.
 * All concrete command types must extend this class and implement {@link #execute}.
 */
public abstract class Command {
	protected boolean isExit = false;

	/**
	 * Executes the command, performing the appropriate action on the workout list
	 * and updating the user interface accordingly.
	 *
	 * @param workouts The current list of workouts to operate on.
	 * @param ui The user interface used to display results and messages.
	 * @throws GitSwoleException If the command cannot be completed due to invalid input or missing data.
	 */
	public abstract void execute(WorkoutList workouts, Ui ui) throws GitSwoleException;

	/**
	 * Returns whether this command signals the application to terminate.
	 *
	 * @return {@code true} if the application should exit; {@code false} otherwise.
	 */
	public boolean isExit(){
		return isExit;
	}
}
