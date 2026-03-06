package seedu.GitSwole.command;

import seedu.GitSwole.assets.Exercise;
import seedu.GitSwole.assets.Workout;
import seedu.GitSwole.assets.WorkoutList;
import seedu.GitSwole.exceptions.GitSwoleException;
import seedu.GitSwole.ui.Ui;

/**
 * Represents a command that adds a new workout or exercise to the workout list.
 * <p>
 * Supported formats:
 * <ul>
 *   <li>{@code add w/WORKOUT} — creates a new workout session</li>
 *   <li>{@code add w/WORKOUT e/EXERCISE WEIGHT SETS REPS} — adds an exercise to an existing workout</li>
 * </ul>
 */
public class AddCommand extends Command {
	private String response;

	/**
	 * Constructs an AddCommand with the raw user input string.
	 *
	 * @param response The full command string entered by the user.
	 */
	public AddCommand(String response) {
		this.response = response;
	}

	/**
	 * Executes the add command by parsing the user input and either creating a new
	 * workout or adding an exercise to an existing one.
	 *
	 * @param workouts The current list of workouts.
	 * @param ui The user interface for displaying results.
	 * @throws GitSwoleException If required fields such as workout name or exercise details are missing or malformed.
	 */
	@Override
	public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
		// testing for adding workouts --> REMOVE LATER
		// Example expected format: "add w/WORKOUTNAME"
		if (!response.contains("w/")) {
			throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "add");
		}
		String workoutName = response.substring(response.indexOf("w/") + 2).trim();
		if (workoutName.isEmpty()) {
			throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "add");
		}
		Workout workoutToAdd = new Workout(workoutName);
		workouts.addWorkout(new Workout(workoutName));
		ui.showMessage("Workout '" + workoutName + "' added successfully!");
		ui.printWorkouts(workouts.getWorkouts());
	}
}
