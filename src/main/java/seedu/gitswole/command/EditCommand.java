package seedu.gitswole.command;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.parser.Parser;
import seedu.gitswole.ui.Ui;

import java.util.logging.Level;

/**
 * Represents a command that edits an existing workout or exercise in the workout list.
 * <p>
 * Supported formats:
 * <ul>
 * <li>{@code edit w/WORKOUT_NAME} — renames an existing workout session</li>
 * <li>{@code edit w/WORKOUT_NAME e/EXERCISE_NAME} — edits an exercise within
 * an existing workout, prompting the user to update each field interactively</li>
 * </ul>
 * Fields left blank during the interactive prompt are not modified.
 */
public class EditCommand extends Command{
    private String response;
    private boolean hasChanged = false;

    /**
     * Constructs an EditCommand with the raw user input string.
     *
     * @param response The full command string entered by the user.
     */
    public EditCommand(String response){
        this.response = response;
    }

    /**
     * Executes the edit command by determining whether to edit a workout name
     * or an exercise, based on the presence of the {@code e/} flag in the input.
     *
     * @param workouts The current list of workouts.
     * @param ui       The user interface for displaying prompts and results.
     * @throws GitSwoleException If required flags are missing, their values are empty,
     *                           or the specified workout or exercise does not exist.
     */
    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        assert response != null : "Response cannot be null";
        if (response.contains(" e/")) {
            handleEditExercise(workouts, ui);
        } else {
            handleEditWorkout(workouts, ui);
        }
        ui.showLine();
    }

    /**
     * Processes the renaming of an existing workout session.
     * <p>
     * Parses the {@code w/} flag to locate the target workout by name, then
     * interactively prompts the user for a new workout name. Pressing enter
     * without input leaves the name unchanged.
     *
     * @param workouts The {@link WorkoutList} used to locate the target workout.
     * @param ui       The {@link Ui} instance used to display prompts and results.
     * @throws GitSwoleException If the {@code w/} flag is absent or empty,
     *                           or if the specified workout does not exist.
     */
    private void handleEditWorkout(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String workoutToEditString = Parser.parseValue(response, "w/");
        if (workoutToEditString == null || workoutToEditString.isEmpty()) {
            LOGGER.log(Level.WARNING, "EditWorkout failed: Missing 'w/' or 'e/' flag or value.");
            throw new GitSwoleException(
                GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
                "Missing name of workout. Usage: edit w/WORKOUT_NAME or edit w/WORKOUT_NAME e/EXERCISE"
            );
        }

        Workout workoutToEdit = workouts.getWorkoutByName(workoutToEditString);
        if (workoutToEdit == null) {
            LOGGER.log(Level.WARNING, "EditWorkout failed: Workout provided does not exist.");
            throw new GitSwoleException(GitSwoleException.ErrorType.NOT_FOUND , workoutToEditString);
        }
        assert workoutToEdit != null : "workoutToEdit should not be null here";

        printCurrentWorkout(ui, workoutToEditString);

        changeWorkoutName(ui, workoutToEdit);
        printUpdatedWorkout(ui, workoutToEdit);
    }

    /**
     * Processes the editing of an exercise within an existing workout session.
     * <p>
     * Parses the {@code w/} flag for the target workout name and the {@code e/} flag
     * for the target exercise name. Interactively prompts the user to update the
     * workout name, exercise name, weight, sets, and reps in sequence. Pressing
     * enter without input for any field leaves that field unchanged.
     *
     * @param workouts The {@link WorkoutList} used to locate the target workout.
     * @param ui       The {@link Ui} instance used to display prompts and results.
     * @throws GitSwoleException If the {@code w/} or {@code e/} flag is missing or empty,
     *                           or if the specified workout or exercise does not exist.
     */
    private void handleEditExercise(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String workoutToEditString = Parser.parseValue(response, "w/");
        String exerciseToEditString = Parser.parseValue(response, "e/");

        boolean validInput = (workoutToEditString == null || workoutToEditString.isEmpty()) ||
            (exerciseToEditString == null || exerciseToEditString.isEmpty());
        if (validInput) {
            LOGGER.log(Level.WARNING, "EditWorkout failed: Missing 'w/' or 'e/' flag.");
            throw new GitSwoleException(
                GitSwoleException.ErrorType.INCOMPLETE_COMMAND,
                "Missing name of workout. Usage: edit w/WORKOUT_NAME or edit w/WORKOUT_NAME e/EXERCISE"
            );
        }

        Workout workoutToEdit = workouts.getWorkoutByName(workoutToEditString);
        if (workoutToEdit == null) {
            LOGGER.log(Level.WARNING, "EditWorkout failed: Workout provided does not exist.");
            throw new GitSwoleException(GitSwoleException.ErrorType.NOT_FOUND , workoutToEditString);
        }
        assert workoutToEdit != null : "workoutToEdit should not be null here";

        ui.showMessage(exerciseToEditString);
        Exercise exerciseToEdit = workoutToEdit.getExerciseByName(exerciseToEditString);
        if (exerciseToEdit == null) {
            LOGGER.log(Level.WARNING, "EditWorkout failed: Exercise provided does not exist.");
            throw new GitSwoleException(GitSwoleException.ErrorType.NOT_FOUND , exerciseToEditString);
        }
        assert exerciseToEdit != null : "exerciseToEdit cannot be null";

        printCurrentExercise(ui, workoutToEditString, exerciseToEdit);

        changeWorkoutName(ui, workoutToEdit);
        changeExerciseName(ui, exerciseToEdit);
        changeWeight(ui, exerciseToEdit);
        changeSets(ui, exerciseToEdit);
        changeReps(ui,exerciseToEdit);

        printUpdatedWorkout(ui, workoutToEdit);
    }

    /**
     * Interactively prompts the user to update the reps of the given exercise.
     * Non-positive or non-numeric input leaves the reps unchanged.
     *
     * @param ui             The {@link Ui} instance used to display the prompt and read input.
     * @param exerciseToEdit The {@link Exercise} whose reps may be updated.
     */
    private void changeReps(Ui ui, Exercise exerciseToEdit) {
        ui.showLine();
        ui.showMessage("Change REPS to: ");
        int newExerciseReps;
        try {
            newExerciseReps = Integer.parseInt(ui.readLine());
        } catch (NumberFormatException e) {
            return;
        }
        if (newExerciseReps <= 0) {
            return;
        }
        exerciseToEdit.setReps(newExerciseReps);
        this.hasChanged = true;
    }

    /**
     * Interactively prompts the user to update the sets of the given exercise.
     * Non-positive or non-numeric input leaves the sets unchanged.
     *
     * @param ui             The {@link Ui} instance used to display the prompt and read input.
     * @param exerciseToEdit The {@link Exercise} whose sets may be updated.
     */
    private void changeSets(Ui ui, Exercise exerciseToEdit) {
        ui.showLine();
        ui.showMessage("Change SETS to: ");
        int newExerciseSets;
        try {
            newExerciseSets = Integer.parseInt(ui.readLine());
        } catch (NumberFormatException e) {
            return;
        }
        if (newExerciseSets <= 0) {
            return;
        }
        exerciseToEdit.setSets(newExerciseSets);
        this.hasChanged = true;
    }

    /**
     * Interactively prompts the user to update the weight of the given exercise.
     * Non-positive or non-numeric input leaves the weight unchanged.
     *
     * @param ui             The {@link Ui} instance used to display the prompt and read input.
     * @param exerciseToEdit The {@link Exercise} whose weight may be updated.
     */
    private void changeWeight(Ui ui, Exercise exerciseToEdit) {
        ui.showLine();
        ui.showMessage("Change WEIGHT to: ");
        int newExerciseWeight;
        try {
            newExerciseWeight = Integer.parseInt(ui.readLine());
        } catch (NumberFormatException e) {
            return;
        }
        if (newExerciseWeight <= 0) {
            return;
        }
        exerciseToEdit.setWeight(newExerciseWeight);
        this.hasChanged = true;
    }

    /**
     * Interactively prompts the user to update the name of the given exercise.
     * Empty or blank input leaves the exercise name unchanged.
     *
     * @param ui             The {@link Ui} instance used to display the prompt and read input.
     * @param exerciseToEdit The {@link Exercise} whose name may be updated.
     */
    private void changeExerciseName(Ui ui, Exercise exerciseToEdit) {
        ui.showLine();
        ui.showMessage("Change EXERCISE NAME to: ");
        String newExerciseName = ui.readLine();
        if (newExerciseName == null || newExerciseName.isEmpty()) {
            return;
        }
        assert exerciseToEdit != null : "exerciseToEdit cannot be null";

        exerciseToEdit.setExerciseName(newExerciseName);
        this.hasChanged = true;
    }

    /**
     * Interactively prompts the user to update the name of the given workout.
     * Empty or blank input leaves the workout name unchanged.
     *
     * @param ui           The {@link Ui} instance used to display the prompt and read input.
     * @param workoutToEdit The {@link Workout} whose name may be updated.
     */
    private void changeWorkoutName(Ui ui, Workout workoutToEdit) {
        ui.showLine();
        ui.showMessage("Change WORKOUT NAME to: ");
        String newWorkoutName = ui.readLine();
        if (newWorkoutName == null || newWorkoutName.isEmpty()) {
            return;
        }
        assert workoutToEdit != null : "workoutToEdit cannot be null";

        workoutToEdit.setWorkoutName(newWorkoutName);
        this.hasChanged = true;
    }

    /**
     * Displays the current state of a workout session and the target exercise
     * before editing begins.
     *
     * @param ui                  The {@link Ui} instance used to display details.
     * @param workoutToEditString The name of the workout containing the exercise.
     * @param exerciseToEdit      The {@link Exercise} to be edited.
     */
    private static void printCurrentExercise(Ui ui, String workoutToEditString, Exercise exerciseToEdit) {
        ui.showMessage("CURRENT WORKOUT: " + workoutToEditString);
        ui.printExercise(exerciseToEdit);
        ui.showLine();
        ui.showMessage("Enter the new values below (press enter to NOT edit): ");
    }

    /**
     * Displays the current state of a workout session before editing begins.
     *
     * @param ui                  The {@link Ui} instance used to display the workout details.
     * @param workoutToEditString The name of the workout being edited.
     */
    private static void printCurrentWorkout(Ui ui, String workoutToEditString) {
        ui.showMessage("CURRENT WORKOUT: " + workoutToEditString);
        ui.showMessage("Enter the new values below (press enter to NOT edit): ");
    }
    /**
     * Displays the updated workout after editing is complete. If no fields were
     * changed, notifies the user that no changes were recorded.
     *
     * @param ui            The {@link Ui} instance used to display the result.
     * @param workoutToEdit The {@link Workout} reflecting any applied changes.
     */
    private void printUpdatedWorkout(Ui ui, Workout workoutToEdit) {
        if (hasChanged) {
            ui.showMessage("Change Recorded! Edited Workout:");
            ui.showLine();
            ui.printWorkout(workoutToEdit);
        } else {
            ui.showMessage("No Changes recorded!");
            ui.showLine();
        }
    }
}
