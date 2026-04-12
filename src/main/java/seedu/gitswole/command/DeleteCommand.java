package seedu.gitswole.command;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.ui.Ui;
import seedu.gitswole.parser.Parser;
import java.util.logging.Level;
/**
 * Represents a command that deletes a workout or an exercise from the workout list.
 * <p>
 * Supported formats:
 * <ul>
 * <li>{@code delete w/WORKOUT} — removes the specified workout</li>
 * <li>{@code delete e/EXERCISE w/WORKOUT} — removes the specified exercise from a workout</li>
 * </ul>
 */
public class DeleteCommand extends Command {
    private String arguments;

    /**
     * Constructs a DeleteCommand with the raw user input string.
     *
     * @param arguments The full command string entered by the user.
     */
    public DeleteCommand(String arguments) {
        assert arguments != null : "Arguments passed to DeleteCommand cannot be null";
        this.arguments = arguments;
    }

    /**
     * Executes the delete command by determining whether to remove a workout or an exercise,
     * based on the flags present in the input.
     *
     * @param workouts The current list of workouts.
     * @param ui       The user interface for displaying results or error messages.
     */
    @Override
    public void execute(WorkoutList workouts, Ui ui) {
        // Assert that the essential dependencies are initialized before proceeding
        assert workouts != null : "WorkoutList must be initialized before execution";
        assert ui != null : "Ui must be initialized before execution";

        // Check if the user is trying to delete an exercise (contains "e/")
        if (Parser.parseValue(arguments, "e/") != null) {
            deleteExercise(workouts, ui);
        } else if (Parser.parseValue(arguments, "w/") != null) {
            deleteWorkout(workouts, ui);
        } else { // Handle invalid formats
            LOGGER.log(Level.WARNING, "Invalid delete format received: {0}", arguments);
            ui.showMessage("Invalid delete format!");
            ui.showMessage("Use: delete w/WORKOUT  OR  delete e/EXERCISE w/WORKOUT");
        }
    }

    /**
     * Parses the input and deletes the specified workout from the workout list.
     *
     * @param workouts The current list of workouts.
     */
    private void deleteWorkout(WorkoutList workouts, Ui ui) {
        int wIndex = arguments.indexOf("w/");

        // This method is only called if arguments.contains("w/") was true, so wIndex MUST NOT be -1
        assert wIndex != -1 : "wIndex should not be -1 because execute() confirmed 'w/' exists";

        // Extract the workout name by taking everything after "w/"
        String workoutName = arguments.substring(wIndex + 2).trim();

        if (workoutName.isEmpty()) {
            LOGGER.log(Level.WARNING, "DeleteWorkout failed: Workout name is empty.");
            ui.showMessage("Please specify the workout name. Usage: delete w/WORKOUT");
            ui.showMessage("________________________________________________________________________________" +
                    "____________________");
            return;
        }

        boolean isDeleted = workouts.removeWorkout(workoutName);

        if (isDeleted) {
            String formattedName = workoutName.substring(0, 1).toUpperCase() + workoutName.substring(1);
            ui.showMessage("Successfully deleted the " + formattedName + " session!");
            ui.showMessage("________________________________________________________________________________" +
                    "____________________");
        } else {
            // Print out the warning gracefully instead of throwing an exception to satisfy the test
            ui.showMessage("'" + workoutName + "' not found. Please check your spelling.");
            ui.showMessage("________________________________________________________________________________" +
                    "____________________");
        }
    }

    /**
     * Parses the input and deletes the specified exercise from the target workout.
     *
     * @param workouts The current list of workouts.
     */
    private void deleteExercise(WorkoutList workouts, Ui ui) {
        int eIndex = arguments.indexOf("e/");
        int wIndex = arguments.indexOf("w/");

        // This method is only called if arguments.contains("e/") was true, so eIndex MUST NOT be -1
        assert eIndex != -1 : "eIndex should not be -1 because execute() confirmed 'e/' exists";

        // Ensure both prefixes exist and "e/" comes before "w/"
        if (eIndex == -1 || wIndex == -1 || eIndex > wIndex) {
            LOGGER.log(Level.WARNING, "DeleteExercise failed: Invalid flag order or missing flags.");
            // Print out the warning gracefully instead of throwing an exception
            ui.showMessage("Invalid format! Please use: delete e/EXERCISE w/WORKOUT");
            return;
        }

        // Extract the exercise name between "e/" and "w/"
        String exerciseName = arguments.substring(eIndex + 2, wIndex).trim();

        // Extract the workout name after "w/"
        String remainingArgs = arguments.substring(wIndex + 2).trim();
        String workoutName = remainingArgs;

        if (exerciseName.isEmpty() || workoutName.isEmpty()) {
            LOGGER.log(Level.WARNING, "DeleteExercise failed: Empty exercise ({0}) or workout ({1}) name.",
                new Object[]{exerciseName, workoutName});
            ui.showMessage("Exercise or Workout name cannot be empty. Usage: delete e/EXERCISE w/WORKOUT");
            ui.showMessage("________________________________________________________________________________" +
                    "____________________");
            return;
        }

        boolean isDeleted = workouts.removeExercise(workoutName, exerciseName);

        if (isDeleted) {
            ui.showMessage("Successfully deleted '" + exerciseName + "' from '" + workoutName + "'!");
            ui.showMessage("________________________________________________________________________________" +
                    "____________________");
        } else {
            ui.showMessage("'" + exerciseName + "' or workout '" + workoutName
                + "' not found. Please check your spelling.");
            ui.showMessage("________________________________________________________________________________" +
                    "____________________");
        }
    }
}
