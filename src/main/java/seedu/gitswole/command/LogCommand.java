package seedu.gitswole.command;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.parser.Parser;
import seedu.gitswole.storage.HistoryStorage;
import seedu.gitswole.ui.Ui;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Represents a command that logs performance data for a workout session with smart overwriting.
 * <p>
 * Supported formats:
 * <ul>
 *   <li>{@code log w/WORKOUT_NAME} — starts a session and lists exercises</li>
 *   <li>{@code log e/EXERCISE_NAME [w/WORKOUT_NAME] [wt/WEIGHT] [s/SETS] [r/REPS] [remark/REMARK]} 
 *   — updates stats for an exercise and updates the history log in a smart way. 
 *   If {@code w/} is omitted, the most recent active session name is used.</li>
 * </ul>
 */
public class LogCommand extends Command {
    private String response;
    private HistoryStorage historyStorage;

    /**
     * Constructs a LogCommand with the raw user input string.
     * <p>
     * This constructor is used by the production application and initializes a real {@link HistoryStorage}.
     *
     * @param response The full command string entered by the user.
     */
    public LogCommand(String response) {
        this(response, new HistoryStorage());
    }

    /**
     * Constructs a LogCommand with a specific {@link HistoryStorage} instance.
     * <p>
     * This constructor supports dependency injection, allowing for the use of mock or stub
     * storage objects during testing.
     *
     * @param response       The full command string entered by the user.
     * @param historyStorage The {@link HistoryStorage} instance to use for session logging.
     */
    public LogCommand(String response, HistoryStorage historyStorage) {
        assert response != null : "Response cannot be null";
        assert historyStorage != null : "HistoryStorage cannot be null";
        this.response = response;
        this.historyStorage = historyStorage;
    }

    /**
     * Executes the log command by either starting a session or updating an exercise's stats.
     *
     * @param workouts The current list of workouts.
     * @param ui       The user interface for displaying results.
     * @throws GitSwoleException If required flags are missing or the workout/exercise is not found.
     */
    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        assert workouts != null : "WorkoutList must be initialized";
        assert ui != null : "Ui must be initialized";

        if (response.contains(" e/")) {
            handleLogExercise(workouts, ui);
        } else {
            handleLogWorkout(workouts, ui);
        }
    }

    /**
     * Starts or resumes a logging session for a specific workout.
     * <p>
     * Only writes a new header if no session for this workout exists for today.
     *
     * @param workouts The list of available workouts.
     * @param ui       The UI to display the session start message.
     * @throws GitSwoleException If the workout name flag (w/) is missing or the workout is not found.
     */
    private void handleLogWorkout(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String workoutName = Parser.parseValue(response, "w/");
        if (workoutName == null || workoutName.isEmpty()) {
            LOGGER.log(Level.WARNING, "LogWorkout failed: Missing 'w/' flag.");
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "log w/WORKOUT_NAME");
        }

        Workout workout = workouts.getWorkoutByName(workoutName);
        if (workout == null) {
            LOGGER.log(Level.INFO, "LogWorkout failed: Workout '{0}' not found.", workoutName);
            throw new GitSwoleException(GitSwoleException.ErrorType.NOT_FOUND, workoutName);
        }

        // SMART CHECK: Only write header if a session doesn't exist for today
        try {
            if (!historyStorage.hasSessionToday(workout.getWorkoutName())) {
                historyStorage.writeSessionHeader(workout.getWorkoutName());
                ui.showMessage("Session started for " + workout.getWorkoutName() + "! Let's get those gains.");
            } else {
                ui.showMessage("Resuming your " + workout.getWorkoutName() + " session for today!");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to manage session header: " + e.getMessage());
        }

        // Set the "sticky" active session name
        workouts.setActiveWorkoutName(workout.getWorkoutName());

        ui.showLine();
        ui.showMessage(workout.getWorkoutName().toUpperCase() + " Workout Exercises:");
        ui.printExercises(workout.getExerciseList());
        ui.showLine();
        ui.showMessage("Continue to log your workout by: log e/EXERCISE wt/WEIGHT s/SETS r/REPS remark/REMARK");
        ui.showLine();
    }

    /**
     * Updates statistics for an exercise and performs a smart update in the history file.
     *
     * @param workouts The list of available workouts.
     * @param ui       The UI to display the updated stats.
     * @throws GitSwoleException If required flags (e/) are missing or workout context cannot be found.
     */
    private void handleLogExercise(WorkoutList workouts, Ui ui) throws GitSwoleException {
        String exerciseName = Parser.parseValue(response, "e/");
        String workoutName = Parser.parseValue(response, "w/");
        String remark = Parser.parseRemark(response);

        // Use the sticky session if w/ flag is missing
        if (workoutName == null) {
            workoutName = workouts.getActiveWorkoutName();
        }

        if (workoutName == null) {
            LOGGER.log(Level.WARNING, "LogExercise failed: Missing workout context.");
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, 
                "No active workout session found. Please specify the workout using w/WORKOUT_NAME " +
                "(e.g., log e/" + exerciseName + " w/push)");
        }

        if (exerciseName == null) {
            LOGGER.log(Level.WARNING, "LogExercise failed: Missing e/ flag.");
            String usage = "log e/EXERCISE [w/WORKOUT] wt/WEIGHT s/SETS r/REPS [remark/REMARK]";
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, usage);
        }

        Workout workout = workouts.getWorkoutByName(workoutName);
        if (workout == null) {
            LOGGER.log(Level.INFO, "LogExercise failed: Workout '{0}' not found.", workoutName);
            throw new GitSwoleException(GitSwoleException.ErrorType.NOT_FOUND, workoutName);
        }

        Exercise exercise = workout.getExerciseByName(exerciseName);
        if (exercise == null) {
            LOGGER.log(Level.INFO, "LogExercise failed: Exercise '{0}' not found in '{1}'.", 
                new Object[]{exerciseName, workoutName});
            throw new GitSwoleException(GitSwoleException.ErrorType.NOT_FOUND, exerciseName);
        }

        // Update the stats in memory
        int weight = Parser.parseOptionalInt(response, "wt/", exercise.getWeight());
        int sets = Parser.parseOptionalInt(response, "s/", exercise.getSets());
        int reps = Parser.parseOptionalInt(response, "r/", exercise.getReps());

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

        exercise.setWeight(weight);
        exercise.setSets(sets);
        exercise.setReps(reps);

        // SMART UPDATE: Update the specific exercise within today's session block in the file
        try {
            // Ensure session exists (in case user jumped straight to log e/ without log w/)
            if (!historyStorage.hasSessionToday(workoutName)) {
                historyStorage.writeSessionHeader(workoutName);
            }
            historyStorage.updateExerciseLog(workoutName, exercise, remark);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to update exercise log in history: " + e.getMessage());
        }

        ui.showMessage("Stats updated for " + exerciseName + " in " + workoutName + "!");
        if (remark != null && !remark.isBlank()) {
            ui.showMessage("Remark added: " + remark.trim());
        }
        ui.printExercises(workout.getExerciseList());
        ui.showLine();
    }
}
