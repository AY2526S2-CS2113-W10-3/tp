package seedu.gitswole.command;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.ui.Ui;

import java.util.logging.Level;

/**
 * Represents a command that performs a global search across all workouts and exercises.
 * <p>
 * Format: {@code find KEYWORD}
 * <p>
 * Searches both workout names and exercise names for the given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Constructs a FindCommand with the search keyword.
     *
     * @param keyword The keyword to search for (already trimmed by parser).
     */
    public FindCommand(String keyword) {
        assert keyword != null : "FindCommand keyword should not be null";
        this.keyword = keyword.trim();
    }

    @Override
    public void execute(WorkoutList workouts, Ui ui) throws GitSwoleException {
        assert workouts != null : "WorkoutList should not be null";
        assert ui != null : "Ui should not be null";
        LOGGER.log(Level.INFO, "Executing FindCommand with keyword: {0}", keyword);

        if (keyword.isEmpty()) {
            throw new GitSwoleException(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "find KEYWORD");
        }

        String lowerKeyword = keyword.toLowerCase();
        boolean found = false;

        for (Workout workout : workouts.getWorkouts()) {
            boolean workoutMatches = workout.getWorkoutName().toLowerCase().contains(lowerKeyword);

            if (workoutMatches) {
                ui.showMessage(String.format("[Workout] %s | Exercises: %d",
                    workout.getWorkoutName(),
                    workout.getExerciseList().size()));
                found = true;
            }

            for (Exercise exercise : workout.getExerciseList()) {
                if (exercise.getExerciseName().toLowerCase().contains(lowerKeyword)) {
                    ui.showMessage(String.format("[Exercise] %s (in %s) | Weight: %dkg | Sets: %d | Reps: %d",
                        exercise.getExerciseName(),
                        workout.getWorkoutName(),
                        exercise.getWeight(),
                        exercise.getSets(),
                        exercise.getReps()));
                    found = true;
                }
            }
        }

        if (!found) {
            LOGGER.log(Level.INFO, "No matches found for keyword: {0}", keyword);
            ui.showMessage("No matching workouts or exercises found :( ");
            ui.showMessage("Correct command format: find KEYWORD (Don't need to add w/ and e/)");
        }
        ui.showLine();
    }
}
