package seedu.gitswole.assets;

import java.util.ArrayList;

//@@author vet3whale
/**
 * Represents a single workout session containing a name and a list of exercises.
 */
public class Workout {
    private ArrayList<Exercise> exerciseList;
    private String workoutName;
    private boolean isDone;

    /**
     * Constructs a Workout with the given name, an empty exercise list,
     * and a default completion status of {@code false}.
     *
     * @param workoutName The name of the workout session.
     */
    public Workout(String workoutName) {
        this.exerciseList = new ArrayList<>();
        this.isDone = false;
        setWorkoutName(workoutName);
    }

    /**
     * Adds an exercise to this workout session.
     *
     * @param exercise The {@link Exercise} to add.
     */
    public void addExercise(Exercise exercise) {
        // assertions
        assert exercise != null : "Exercise to add must not be null";
        int sizeBefore = exerciseList.size();
        (this.exerciseList).add(exercise);
        assert exerciseList.size() == sizeBefore + 1 : "Exercise list size should increase by 1 after add";
    }

    /**
     * Removes an exercise from this workout session by its name.
     *
     * @param exerciseName The name of the exercise to remove.
     * @return true if the exercise was successfully removed, false if it was not found.
     */
    public boolean removeExercise(String exerciseName) {
        // assertions
        assert exerciseName != null && !exerciseName.isBlank() : "Exercise name must not be null or blank";
        for (int i = 0; i < exerciseList.size(); i++) {
            if (exerciseList.get(i).getExerciseName().equalsIgnoreCase(exerciseName.trim())) {
                exerciseList.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of exercises in this workout.
     *
     * @return The exercise count.
     */
    public int getNumOfExercises() {
        // assertions
        assert exerciseList != null : "Exercise list should never be null";
        int count = exerciseList.size();
        assert count >= 0 : "Number of exercises cannot be negative";
        return count;
    }

    /**
     * Returns the list of exercises in this workout.
     *
     * @return An {@link ArrayList} of {@link Exercise} objects.
     */
    public ArrayList<Exercise> getExerciseList() {
        return exerciseList;
    }

    /**
     * Returns the name of this workout.
     *
     * @return The workout name.
     */
    public String getWorkoutName() {
        return workoutName;
    }

    /**
     * Sets the name of this workout.
     *
     * @param workoutName The new name for this workout.
     */
    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    /**
     * Returns whether this workout has been marked as done.
     *
     * @return {@code true} if done, {@code false} otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Sets the completion status of this workout.
     *
     * @param isDone {@code true} to mark as completed, {@code false} to unmark.
     */
    public void markDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Finds and returns an exercise within this workout session by its name.
     *
     * @param exerciseToEditString The exact name of the exercise to find.
     * @return The {@link Exercise} object if found, or {@code null} otherwise.
     */
    public Exercise getExerciseByName(String exerciseToEditString) {
        for (Exercise e : exerciseList) {
            if (exerciseToEditString.equals(e.getExerciseName())) {
                return e;
            }
        }
        return null;
    }
}
//@@author
