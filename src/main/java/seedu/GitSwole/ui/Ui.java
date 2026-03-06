package seedu.GitSwole.ui;

import seedu.GitSwole.assets.Exercise;
import seedu.GitSwole.assets.Workout;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all user interface interactions for the GitSwole application,
 * including reading user input and displaying messages, workouts, and exercises.
 */
public class Ui {
	private Scanner in;
	private int dashes = 100; // minimally 100, for help print statements

	/**
	 * Constructs an Ui instance and initializes the input scanner.
	 */
	public Ui(){
		in = new Scanner(System.in);
	}

	/**
	 * Reads and returns a single line of input from the user.
	 *
	 * @return The full command string entered by the user.
	 */
	public String readCommand(){
		String response = in.nextLine();
		return response;
	}

	/**
	 * Displays the application logo and a welcome message on startup.
	 */
	public void helloGreeting() {
		String logo =
				"   _____ _ _  _____               _      \n"
						+ "  / ____(_) |/ ____|             | |     \n"
						+ " | |  __ _| | (___  __      _____| | ___ \n"
						+ " | | |_ | | |\\___ \\ \\ \\ /\\ / / _ \\ |/ _ \\\n"
						+ " | |__| | | |____) | \\ V  V / (_) | |  __/\n"
						+ "  \\_____|_|_|_____/   \\_/\\_/ \\___/|_|\\___|\n";

		System.out.println(logo);
		System.out.println("Welcome to GitSwole! LET'S GET THEM GAINS");
		showLine();
	}

	/**
	 * Displays a horizontal separator line for visual clarity.
	 */
	public void showLine() {
		System.out.println("_".repeat(getDashes()));
	}

	/**
	 * Displays a goodbye message when the application terminates.
	 */
	public void byeGreeting() {
		showLine();
		System.out.println("Bye! Keep getting swole!");
		showLine();
	}

	/**
	 * Displays a formatted error message surrounded by separator lines.
	 *
	 * @param message The error message to display.
	 */
	public void showError(String message) {
		showLine();
		System.out.println(" " + message);
		showLine();
	}

	/**
	 * Displays a general informational message.
	 *
	 * @param s The message to display.
	 */
	public void showMessage(String s) {
		System.out.println(" " + s);
	}

	/**
	 * Prints all workouts in the provided list, each separated by a line.
	 *
	 * @param workouts The list of {@link Workout} objects to display.
	 */
	public void printWorkouts(ArrayList<Workout> workouts) {
		showLine();
		for (Workout workout : workouts) {
			printWorkout(workout);
		}
		showLine();
	}

	/**
	 * Prints a single workout's name and all its exercises.
	 *
	 * @param workout The {@link Workout} to display.
	 */
	public void printWorkout(Workout workout) {
		String name = workout.getWorkoutName().toUpperCase();
		int padding = (dashes - name.length()) / 2;
		System.out.println(" ".repeat(Math.max(0, padding)) + name);
		showLine();
		for (Exercise exercise : workout.getExerciseList()) {
			printExercise(exercise);
		}
	}

	/**
	 * Prints a single exercise's details in a formatted row.
	 *
	 * @param exercise The {@link Exercise} to display.
	 */
	public void printExercise(Exercise exercise) {
		String line = String.format("%-20s | %-6s | %-4s | %-4s",
				exercise.getExerciseName(),
				exercise.getWeight(),
				exercise.getSets(),
				exercise.getReps());
		System.out.println(line);
	}

	/**
	 * Returns the number of dashes used for separator lines and table widths.
	 *
	 * @return The dash count.
	 */
	public int getDashes() {
		return dashes;
	}

}
