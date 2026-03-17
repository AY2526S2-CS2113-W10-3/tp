package seedu.GitSwole.command;

import seedu.GitSwole.assets.Workout;
import seedu.GitSwole.assets.WorkoutList;
import seedu.GitSwole.exceptions.GitSwoleException;
import seedu.GitSwole.parser.Parser;
import seedu.GitSwole.ui.Ui;

/**
 * Represents a command that marks a workout session as completed.
 * <p>
 * Supported format:
 * <ul>
 *   <li>{@code mark WORKOUT_NAME} — marks the named workout as completed</li>
 * </ul>
 * The workout name may contain multiple words (e.g. {@code mark push day}).
 */
public class MarkCommand extends Command {
    private String response;

    /**
     * Constructs a MarkCommand with the raw user input string.
     *
     * @param response The full command string entered by the user.
     */
    public MarkCommand(String response) {
        this.response = response;
    }

    /**
     * Marks or unmarks the target workout and prints a confirmation message.
     * <p>
     * The command keyword ({@code mark} or {@code unmark}) is used to determine
     * the completion status to apply. The workout name is extracted as everything
     * after the command keyword.
     * <p>
     * Prints {@code [X] workoutName} if marking as done, or
     * {@code [ ] workoutName} if unmarking.
     *
     * @param workouts The current list of workouts.
     * @param ui       The user interface for displaying results.
     * @throws GitSwoleException If the specified workout does not exist in the list.
     */
    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String[] parts = response.split(" ");
        boolean isDone = parts[0].equalsIgnoreCase("mark");
        String workoutName = response.substring(parts[0].length()).trim();

        Workout target = workouts.getWorkoutByName(workoutName);
        if (target == null) {
            throw new GitSwoleException(GitSwoleException.ErrorType.IDX_OUTOFBOUNDS, workoutName);
        }

        target.markDone(isDone);

        if (isDone) {
            ui.showMessage("[X] " + target.getWorkoutName());
        } else {
            ui.showMessage("[ ] " + target.getWorkoutName());
        }
        ui.showLine();
    }
}
