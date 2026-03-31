package seedu.gitswole.command;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.parser.Parser;
import seedu.gitswole.ui.Ui;

import java.util.logging.Level;

/**
 * Represents a command that searches for a workout or an exercise by keyword.
 * <p>
 * Supported formats:
 * <ul>
 *   <li>{@code find w/KEYWORD} — searches for workouts whose names contain the keyword</li>
 *   <li>{@code find e/KEYWORD w/WORKOUT} — searches for exercises within the specified workout</li>
 * </ul>
 */
public class FindCommand extends Command {
    private String arguments;

    /**
     * Constructs a FindCommand with the raw user input string.
     *
     * @param arguments The full command string entered by the user.
     */
    public FindCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Executes the find command by determining whether to search for a workout or an exercise,
     * based on the flags present in the input.
     *
     * @param workouts The current list of workouts.
     * @param ui       The user interface for displaying results or error messages.
     * @throws GitSwoleException If the input format is invalid or required fields are missing.
     */
    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        LOGGER.log(Level.INFO, "Executing FindCommand with arguments: {0}", arguments);
        if (arguments.contains("e/")) {
            handleFindExercise(workouts, ui);
        } else {
            handleFindWorkout(workouts, ui);
        }
    }

    /**
     * Parses the input and searches for workouts whose names contain the given keyword.
     * Displays all matching workout names, or a "not found" message if none match.
     *
     * @param workouts The current list of workouts to search through.
     * @param ui       The user interface for displaying results.
     * @throws GitSwoleException If the {@code w/} keyword is missing or empty.
     */
    private void handleFindWorkout(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String keyword = Parser.parseValue(arguments, "w/");
        if (keyword == null || keyword.isEmpty()) {
            LOGGER.log(Level.WARNING, "FindWorkout failed: Missing 'w/' keyword.");
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "find w/WORKOUT");
        }

        boolean found = false;
        for (Workout workout : workouts.getWorkouts()) {
            if (workout.getWorkoutName().toLowerCase().contains(keyword.toLowerCase())) {
                ui.showMessage(String.format("%s | Exercises: %d",
                    workout.getWorkoutName(),
                    workout.getExerciseList().size()));
                found = true;
            }
        }
        if (!found) {
            LOGGER.log(Level.INFO, "No workout matches found for keyword: {0}", keyword);
            ui.showLine();
            ui.showMessage(" Workout Not Found :(");
        }
        ui.showLine();
    }

    /**
     * Parses the input and searches for exercises within the specified workout
     * whose names contain the given keyword.
     * Displays matching exercise details (name, weight, sets, reps),
     * or a "not found" message if none match.
     *
     * @param workouts The current list of workouts to search through.
     * @param ui       The user interface for displaying results.
     * @throws GitSwoleException If the {@code e/} or {@code w/} keyword is missing or empty,
     *                           or if the specified workout does not exist.
     */
    private void handleFindExercise(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String exerciseKeyword = Parser.parseValue(arguments, "e/");
        String workoutName = Parser.parseValue(arguments, "w/");

        if (exerciseKeyword == null || exerciseKeyword.isEmpty()
            || workoutName == null || workoutName.isEmpty()) {
            LOGGER.log(Level.WARNING, "FindExercise failed: Missing e/ or w/ flag.");
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
                "find e/EXERCISE w/WORKOUT");
        }

        Workout targetWorkout = workouts.getWorkoutByName(workoutName);
        if (targetWorkout == null) {
            LOGGER.log(Level.INFO, "Search aborted: Workout '{0}' does not exist.", workoutName);
            throw new GitSwoleException(GitSwoleException.ErrorType.IDX_OUTOFBOUNDS, workoutName);
        }

        boolean found = false;
        for (Exercise exercise : targetWorkout.getExerciseList()) {
            if (exercise.getExerciseName().toLowerCase().contains(exerciseKeyword.toLowerCase())) {
                ui.showMessage(String.format("%s | Weight: %dkg | Sets: %d | Reps: %d",
                    exercise.getExerciseName(),
                    exercise.getWeight(),
                    exercise.getSets(),
                    exercise.getReps()));
                found = true;
            }
        }
        if (!found) {
            LOGGER.log(Level.INFO, "No exercise matches found for '{0}' in '{1}'",
                new Object[]{exerciseKeyword, workoutName});
            ui.showMessage("Exercise Not Found :(");
        }
        ui.showLine();
    }
}
