package seedu.GitSwole.assets;

/**
 * Represents a single exercise entry within a workout, including its name,
 * weight, sets, and repetitions.
 */
public class Exercise {
	private String exerciseName;
	private int weight;
	private int sets;
	private int reps;

	/**
	 * Constructs an Exercise with the specified attributes.
	 *
	 * @param exerciseName The name of the exercise (e.g., "bench press").
	 * @param weight       The weight used, in kilograms.
	 * @param sets         The number of sets performed.
	 * @param reps         The number of repetitions per set.
	 */
	public Exercise(String exerciseName, int weight, int sets, int reps) {
		setExerciseName(exerciseName);
		setWeight(weight);
		setSets(sets);
		setReps(reps);
	}

	/** @return The name of this exercise. */
	public String getExerciseName() {
		return exerciseName;
	}

	/** @param exerciseName The new name for this exercise. */
	public void setExerciseName(String exerciseName) {
		this.exerciseName = exerciseName;
	}

	/** @return The weight used in this exercise, in kilograms. */
	public int getWeight() {
		return weight;
	}

	/** @param weight The new weight value in kilograms. */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/** @return The number of sets for this exercise. */
	public int getSets() {
		return sets;
	}

	/** @param sets The new number of sets. */
	public void setSets(int sets) {
		this.sets = sets;
	}

	/** @return The number of repetitions per set. */
	public int getReps() {
		return reps;
	}

	/** @param reps The new number of repetitions per set. */
	public void setReps(int reps) {
		this.reps = reps;
	}
}
