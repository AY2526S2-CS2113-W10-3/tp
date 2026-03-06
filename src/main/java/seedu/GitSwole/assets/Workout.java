package seedu.GitSwole.assets;

import seedu.GitSwole.ui.Ui;

import java.util.ArrayList;

/**
 * Represents a single workout session containing a name and a list of exercises.
 */
public class Workout {
	private ArrayList<Exercise> exerciseList;
	private String workoutName;

	/**
	 * Constructs a Workout with the given name and an empty exercise list.
	 *
	 * @param workoutName The name of the workout session.
	 */
	public Workout(String workoutName) {
		this.exerciseList = new ArrayList<>();
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
}
