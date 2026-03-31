package seedu.gitswole.command;

import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.parser.Parser;
import seedu.gitswole.ui.Ui;

import java.util.ArrayList;

/**
 * Represents a command that lists workouts or exercises based on user input.
 * <p>
 * Supported formats:
 * <ul>
 *   <li>{@code list} — lists names of all workout sessions</li>
 *   <li>{@code list w/WORKOUT} — lists all exercises within a specific workout</li>
 *   <li>{@code list all} — lists all exercises across all workout sessions</li>
 * </ul>
 */
public class ListCommand extends Command {
    private String arguments;

    /**
     * Constructs a ListCommand with the raw user input string.
     *
     * @param arguments The full command string entered by the user.
     */
    public ListCommand(String arguments) {
        assert arguments != null : "Arguments passed to ListCommand cannot be null";
        this.arguments = arguments.trim().toLowerCase();
    }

    /**
     * Executes the list command by determining the scope of the search.
     *
     * @param workouts The current list of workouts.
     * @param ui       The user interface for displaying results.
     * @throws GitSwoleException If a specified workout cannot be found.
     */
    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        assert workouts != null : "WorkoutList must be initialized before execution";
        assert ui != null : "Ui must be initialized before execution";

        if (arguments.contains("w/")) {
            handleListWorkout(workouts, ui);
        } else if (arguments.endsWith("all")) {
            handleListAll(workouts, ui);
        } else {
            handleListSummary(workouts, ui);
        }
    }

    /**
     * Lists only the names of the workout sessions currently stored.
     *
     * @param workouts The WorkoutList to retrieve the names from.
     * @param ui       The instance to display the list.
     */
    private void handleListSummary(WorkoutList workouts, Ui ui) {
        ArrayList<Workout> workoutList = workouts.getWorkouts();
        assert workoutList != null : "Workout repository returned a null list";

        if (workoutList.isEmpty()) {
            ui.showMessage("Your workout list is currently empty :(");
            ui.showLine();
            return;
        }

        ui.showMessage("Your Workouts:");
        for (int i = 0; i < workoutList.size(); i++) {
            Workout w = workoutList.get(i);
            String status = w.isDone() ? "[X]" : "[ ]";
            ui.showMessage((i + 1) + ". " + status + w.getWorkoutName());
        }
        ui.showLine();
    }

    /**
     * Lists all exercises within a specific workout session identified by the "w/" prefix.
     *
     * @param workouts The WorkoutList to search.
     * @param ui       The instance to display results.
     * @throws GitSwoleException If the workout name is missing or not found.
     */
    private void handleListWorkout(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String workoutName = Parser.parseValue(arguments, "w/");
        if (workoutName == null || workoutName.isEmpty()) {
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "list w/workout");
        }
        
        assert !workoutName.isEmpty() : "Workout name should not be empty after validation check";

        Workout workout = workouts.getWorkoutByName(workoutName);
        if (workout == null) {
            throw new GitSwoleException(GitSwoleException.ErrorType.IDX_OUTOFBOUNDS, workoutName);
        }

        String status = workout.isDone() ? "[X]" : "[ ]";
        ui.showMessage(status + " " + workout.getWorkoutName().toUpperCase() + " Workout Exercises:");
        ui.printExercises(workout.getExerciseList());
        ui.showLine();
    }

    /**
     * Lists every exercise across every workout session stored in the application.
     *
     * @param workouts The WorkoutList containing all sessions.
     * @param ui       The Ui instance to display results.
     */
    private void handleListAll(WorkoutList workouts, Ui ui) {
        ArrayList<Workout> workoutList = workouts.getWorkouts();
        assert workoutList != null : "Workout repository returned a null list";

        if (workoutList.isEmpty()) {
            ui.showMessage("Your workout list is currently empty :(");
            return;
        }

        ui.printWorkouts(workoutList);
    }
}
