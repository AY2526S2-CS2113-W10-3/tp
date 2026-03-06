package seedu.GitSwole.command;

import seedu.GitSwole.assets.WorkoutList;
import seedu.GitSwole.exceptions.GitSwoleException;
import seedu.GitSwole.ui.Ui;

/**
 * Represents a command that deletes a workout or an exercise from the workout list.
 * <p>
 * Supported formats:
 * <ul>
 *   <li>{@code delete w/WORKOUT} — removes the specified workout</li>
 *   <li>{@code delete e/EXERCISE w/WORKOUT} — removes the specified exercise from a workout</li>
 * </ul>
 */
public class DeleteCommand extends Command{
    private String arguments;

    /**
     * Constructs a DeleteCommand with the raw user input string.
     *
     * @param arguments The full command string entered by the user.
     */
    public DeleteCommand(String arguments){
        this.arguments = arguments;
    }

    /**
     * Executes the delete command by determining whether to remove a workout or an exercise,
     * based on the flags present in the input.
     *
     * @param workouts The current list of workouts.
     * @param ui       The user interface for displaying results or error messages.
     * @throws GitSwoleException If the input format is invalid or required fields are missing.
     */
    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        // Check if the user is trying to delete an exercise (contains "e/")
        if (arguments.contains("e/")) {
            deleteExercise(arguments);
        }
        // Check if the user is trying to delete a workout (contains "w/")
        else if (arguments.contains("w/")) {
            deleteWorkout(arguments);
        }
        // Handle invalid formats
        else {
            System.out.println("Invalid delete format!");
            System.out.println("Use: delete w/WORKOUT  OR  delete e/EXERCISE w/WORKOUT");
        }
    }

    /**
     * Parses the input and deletes the specified workout from the workout list.
     *
     * @param arguments The raw argument string containing the {@code w/} prefix.
     */
    private static void deleteWorkout(String arguments) {
        // Extract the workout name by removing the "w/" prefix
        String workoutName = arguments.replace("w/", "").trim();

        if (workoutName.isEmpty()) {
            System.out.println("Please specify the workout name. Example: delete w/push");
            return;
        }

        String formattedName = workoutName.substring(0, 1).toUpperCase() + workoutName.substring(1);

        System.out.println("Successfully deleted a " + formattedName + " Session!");
    }

    /**
     * Parses the input and deletes the specified exercise from the target workout.
     *
     * @param arguments The raw argument string containing both {@code e/} and {@code w/} prefixes.
     */
    private static void deleteExercise(String arguments) {
        int eIndex = arguments.indexOf("e/");
        int wIndex = arguments.indexOf("w/");

        // Ensure both prefixes exist and "e/" comes before "w/"
        if (eIndex == -1 || wIndex == -1 || eIndex > wIndex) {
            System.out.println("Invalid format! Please use: delete e/EXERCISE w/WORKOUT");
            return;
        }

        // Extract the exercise name between "e/" and "w/"
        String exerciseName = arguments.substring(eIndex + 2, wIndex).trim();

        // Extract the workout name after "w/"
        String remainingArgs = arguments.substring(wIndex + 2).trim();


        String workoutName = remainingArgs.split(" ")[0];

        if (exerciseName.isEmpty() || workoutName.isEmpty()) {
            System.out.println("Exercise or Workout name cannot be empty.");
            return;
        }

        System.out.println("Your exercise has been successfully deleted!");
    }

}
