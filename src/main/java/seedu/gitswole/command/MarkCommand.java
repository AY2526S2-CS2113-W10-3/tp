package seedu.gitswole.command;

import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.ui.Ui;
import seedu.gitswole.parser.Parser;

import java.util.logging.Level;

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
        assert workouts != null : "WorkoutList must not be null";
        assert ui != null : "Ui must not be null";

        String[] parts = response.split(" ");
        assert parts.length > 0 : "Response must contain at least one word";
        assert parts[0].equalsIgnoreCase("mark") || parts[0].equalsIgnoreCase("unmark")
                : "First word must be mark or unmark";

        boolean isDone = parts[0].equalsIgnoreCase("mark");
        String workoutName = Parser.parseValue(response, "w/");

        if (workoutName == null || workoutName.isEmpty()) {
            LOGGER.log(Level.WARNING, "Mark/Unmark command missing w/ flag or workout name.");
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, parts[0]);
        }

        Workout target = workouts.getWorkoutByName(workoutName);
        if (target == null) {
            LOGGER.log(Level.WARNING, "Workout ''{0}'' not found.", workoutName);
            throw new GitSwoleException(GitSwoleException.ErrorType.NOT_FOUND, workoutName);
        }
        assert target != null : "Target workout must not be null after null check";

        if (isDone && target.getNumOfExercises() == 0) {
            LOGGER.log(Level.WARNING, "MarkCommand failed: Workout ''{0}'' has no exercises.", workoutName);
            throw new GitSwoleException(GitSwoleException.ErrorType.DEFAULT,
                    "Cannot mark \"" + target.getWorkoutName() + "\" as done because it has no exercises!");
        }

        target.markDone(isDone);
        LOGGER.log(Level.INFO, "Workout ''{0}'' marked as {1}",
                new Object[]{workoutName, isDone ? "done" : "not done"});

        if (isDone) {
            ui.showMessage("Successfully marked '" + target.getWorkoutName() + "' as done!");
        } else {
            ui.showMessage("Successfully unmarked '" + target.getWorkoutName() + "'!");
        }
        ui.showLine();
    }
}
