package seedu.gitswole.command;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.parser.Parser;
import seedu.gitswole.ui.Ui;

import java.util.logging.Level;

//@@author vet3whale
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

        Parser.validateNoUnknownFlags(response, "w/", "e/");

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

        ui.showMessage("Edit fields (e.g. wn/NewName): ");
        String editLine = ui.readLine();

        applyWorkoutEdits(editLine, workoutToEdit, ui, workouts);
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

        ui.showMessage("Edit fields (e.g. wn/NewWorkout en/NewExercise wt/100 s/3 r/10): ");
        String editLine = ui.readLine();

        applyExerciseEdits(editLine, workoutToEdit, exerciseToEdit, ui, workouts, workoutToEdit);

        printUpdatedWorkout(ui, workoutToEdit);
    }

    /**
     * Parses a single-line edit string and applies any specified changes to the
     * given workout and exercise. Only fields whose flags are present in the input
     * are updated; all others remain unchanged.
     * <p>
     * Supported flags:
     * <ul>
     *   <li>{@code wn/} — new workout name</li>
     *   <li>{@code en/} — new exercise name</li>
     *   <li>{@code wt/} — new weight (must be a positive integer)</li>
     *   <li>{@code s/}  — new sets (must be a positive integer)</li>
     *   <li>{@code r/}  — new reps (must be a positive integer)</li>
     * </ul>
     * Example input: {@code wn/LegDay en/Squat wt/120 s/4 r/8}
     *
     * @param editLine       The raw input string containing one or more flag-value pairs.
     *                       If {@code null} or blank, no changes are applied.
     * @param workoutToEdit  The {@link Workout} whose name may be updated via {@code wn/}.
     * @param exerciseToEdit The {@link Exercise} whose fields may be updated.
     */
    private void applyExerciseEdits(String editLine, Workout workoutToEdit, Exercise exerciseToEdit, Ui ui,
                                    WorkoutList workouts, Workout workout)
            throws GitSwoleException {
        if (editLine == null || editLine.isBlank()) {
            return;
        }

        String wn = Parser.parseValue(editLine, "wn/");
        String en = Parser.parseValue(editLine, "en/");
        String wt = Parser.parseValue(editLine, "wt/");
        String s  = Parser.parseValue(editLine, "s/");
        String r  = Parser.parseValue(editLine, "r/");

        if (wn != null && !wn.isEmpty()) {
            Parser.validateName(wn, "Workout");
            workoutToEdit.setWorkoutName(wn);
            hasChanged = true;
        }
        if (en != null && !en.isEmpty()) {
            Parser.validateName(en, "Exercise");
            exerciseToEdit.setExerciseName(en);
            hasChanged = true;
        }
        
        // Use parseAndValidateInt directly - it handles its own null checks and parsing
        int originalWeight = exerciseToEdit.getWeight();
        int newWeight = Parser.parseAndValidateInt(editLine, "wt/", originalWeight, 1000, "Weight");
        if (newWeight != originalWeight) {
            exerciseToEdit.setWeight(newWeight);
            hasChanged = true;
        }

        int originalSets = exerciseToEdit.getSets();
        int newSets = Parser.parseAndValidateInt(editLine, "s/", originalSets, 50, "Sets");
        if (newSets != originalSets) {
            exerciseToEdit.setSets(newSets);
            hasChanged = true;
        }

        int originalReps = exerciseToEdit.getReps();
        int newReps = Parser.parseAndValidateInt(editLine, "r/", originalReps, 100, "Reps");
        if (newReps != originalReps) {
            exerciseToEdit.setReps(newReps);
            hasChanged = true;
        }
    }

    /**
     * Parses a single-line edit string and applies any specified changes to the
     * given workout. Only fields whose flags are present in the input are updated.
     * <p>
     * Supported flags:
     * <ul>
     *   <li>{@code wn/} — new workout name</li>
     * </ul>
     * Example input: {@code wn/LegDay}
     *
     * @param editLine      The raw input string containing one or more flag-value pairs.
     *                      If {@code null} or blank, no changes are applied.
     * @param workoutToEdit The {@link Workout} whose name may be updated via {@code wn/}.
     */
    private void applyWorkoutEdits(String editLine, Workout workoutToEdit, Ui ui, WorkoutList workouts)
                                    throws GitSwoleException {
        if (editLine == null || editLine.isBlank()) {
            return;
        }
        String wn = Parser.parseValue(editLine, "wn/");
        if (wn != null && !wn.isEmpty()) {
            Parser.validateName(wn, "Workout");
            workoutToEdit.setWorkoutName(wn);
            hasChanged = true;
        }
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
            ui.printWorkout(workoutToEdit);
        } else {
            ui.showMessage("No Changes recorded!");
        }
    }
}
//@@author
