package seedu.gitswole.command;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.parser.Parser;
import seedu.gitswole.ui.Ui;

import java.util.logging.Level;

/**
 * Represents a command that adds a new workout or exercise to the workout list.
 * <p>
 * Supported formats:
 * <ul>
 * <li>{@code add w/WORKOUT_NAME} — creates a new workout session</li>
 * <li>{@code add e/EXERCISE_NAME w/WORKOUT_NAME [wt/WEIGHT] [s/SETS] [r/REPS]}
 * — adds an exercise to an existing workout</li>
 * </ul>
 * Flag values may contain multiple words. Optional flags default to {@code 0} if omitted.
 */
public class AddCommand extends Command {
    private String response;

    /**
     * Constructs an AddCommand with the raw user input string.
     *
     * @param response The full command string entered by the user.
     */
    public AddCommand(String response) {
        assert response != null : "AddCommand response should not be null";
        this.response = response;
    }

    /**
     * Executes the add command by determining whether to add a workout session
     * or an exercise, based on the presence of the {@code e/} flag in the input.
     *
     * @param workouts The current list of workouts.
     * @param ui       The user interface for displaying results.
     * @throws GitSwoleException If required flags are missing, their values are empty,
     *                           or the specified workout does not exist.
     */
    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        assert workouts != null : "WorkoutList should not be null";
        assert ui != null : "Ui should not be null";
        if (response.contains(" e/")) {
            handleAddExercise(workouts, ui);
        } else {
            handleAddWorkout(workouts, ui);
        }
    }

    /**
     * Processes the addition of a new workout session.
     * <p>
     * Parses the {@code w/} flag to extract the workout name, which may contain
     * multiple words (e.g. {@code add w/push day}). Throws an exception if the
     * flag is missing or its value is empty.
     *
     * @param workouts The {@link WorkoutList} to which the new session will be added.
     * @param ui       The {@link Ui} instance used to display success or error messages.
     * @throws GitSwoleException If the {@code w/} flag is absent or has no value.
     */
    private void handleAddWorkout(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String workoutName = Parser.parseValue(response, "w/");
        if (workoutName == null || workoutName.isEmpty()) {
            LOGGER.log(Level.WARNING, "AddWorkout failed: Missing 'w/' flag or value.");
            throw new GitSwoleException(
                GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
                "Missing name of workout. Usage: add w/WORKOUT_NAME"
            );
        }

        if (workouts.containsWorkout(workoutName)) {
            LOGGER.log(Level.WARNING, "AddWorkout failed: Duplicate workout name '{0}'.", workoutName);
            throw new GitSwoleException(
                GitSwoleException.ErrorType.DEFAULT,
                "A workout named '" + workoutName + "' already exists! Try a different name."
            );
        }

        workouts.addWorkout(new Workout(workoutName));
        ui.showMessage("Successfully added a " + workoutName + " session! Remember to add your exercises :)");
        ui.showLine();
    }

    /**
     * Processes the addition of an exercise to an existing workout session.
     * <p>
     * Parses the {@code e/} flag for the exercise name and the {@code w/} flag for
     * the target workout name — both may contain multiple words. Optional flags
     * {@code wt/}, {@code s/}, and {@code r/} specify weight, sets, and repetitions
     * respectively, and default to {@code 0} if omitted.
     * <p>
     * Example: {@code add e/bench press w/push day wt/40 s/3 r/8}
     *
     * @param workouts The {@link WorkoutList} used to locate the target workout session.
     * @param ui       The {@link Ui} instance used to display success or error messages.
     * @throws GitSwoleException If the {@code e/} or {@code w/} flag is missing or empty,
     *                           or if the specified workout does not exist in the list.
     */
    private void handleAddExercise(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String exerciseName = Parser.parseValue(response, "e/");
        String workoutName = Parser.parseValue(response, "w/");

        if (exerciseName == null || exerciseName.isEmpty()) {
            LOGGER.log(Level.WARNING, "AddExercise failed: Incomplete required flags. e: {0}, w: {1}",
                new Object[]{exerciseName, workoutName});
            throw new GitSwoleException(
                GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
                "Missing name of exercise. Usage: add e/EXERCISE_NAME w/WORKOUT_NAME"
            );
        }
        if (workoutName == null || workoutName.isEmpty()) {
            LOGGER.log(Level.WARNING, "AddExercise failed: Incomplete required flags. e: {0}, w: {1}",
                new Object[]{exerciseName, workoutName});
            throw new GitSwoleException(
                GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
                "Missing name of workout. Usage: add e/EXERCISE_NAME w/WORKOUT_NAME"
            );
        }

        Workout targetWorkout = workouts.getWorkoutByName(workoutName);
        if (targetWorkout == null) {
            LOGGER.log(Level.WARNING, "AddExercise failed: Target workout '{0}' not found.", workoutName);
            throw new GitSwoleException(GitSwoleException.ErrorType.IDX_OUTOFBOUNDS, workoutName);
        }

        int weight = Parser.parseOptionalInt(response, "wt/", 0);
        int sets = Parser.parseOptionalInt(response, "s/", 0);
        int reps = Parser.parseOptionalInt(response, "r/", 0);

        if (weight < 0) {
            throw new GitSwoleException(
                GitSwoleException.ErrorType.NEG_INPUT,
                "Weight cannot be negative. Usage: wt/WEIGHT (e.g. wt/40)"
            );
        }
        if (sets < 0) {
            throw new GitSwoleException(
                GitSwoleException.ErrorType.NEG_INPUT,
                "Sets cannot be negative. Usage: s/SETS (e.g. s/3)"
            );
        }
        if (reps < 0) {
            throw new GitSwoleException(
                GitSwoleException.ErrorType.NEG_INPUT,
                "Reps cannot be negative. Usage: r/REPS (e.g. r/8)"
            );
        }

        targetWorkout.addExercise(new Exercise(exerciseName, weight, sets, reps));
        ui.showMessage("Your exercise has been successfully added! Looking swole g");
        ui.showLine();
    }
}
