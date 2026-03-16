package seedu.GitSwole.assets;

import seedu.GitSwole.ui.Ui;

import java.util.ArrayList;

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
		(this.exerciseList).add(exercise);
	}

	/**
	 * Removes an exercise from this workout session by its name.
	 *
	 * @param exerciseName The name of the exercise to remove.
	 * @return true if the exercise was successfully removed, false if it was not found.
	 */
	public boolean removeExercise(String exerciseName) {
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
		return exerciseList.size();
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
}