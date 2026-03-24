package seedu.gitswole.ui;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;

import java.io.InputStream;
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
    public Ui() {
        in = new Scanner(System.in);
    }

    /**
     * Constructs an Ui instance and initializes the input scanner.
     * USED FOR TESTING
     */
    public Ui(InputStream in) {
        this.in = new Scanner(in);
    }

    /**
     * Reads and returns a single line of input from the user.
     *
     * @return The full string entered by the user.
     */
    public String readLine() {
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    /**
     * Reads and returns a single line of input from the user.
     *
     * @return The full command string entered by the user.
     */
    public String readCommand() {
        String response = in.nextLine();
        return response;
    }

    /**
     * Displays the application logo and a welcome message on startup.
     */
    public void helloGreeting() {
        showMessage(" _____ _ _   _____               _      ");
        showMessage("|  __ (_) | /  ___|             | |     ");
        showMessage("| |  \\/_| |_\\ `--.__      _____ | | ___ ");
        showMessage("| | __| | __|`--. \\ \\ /\\ / / _ \\| |/ _ \\");
        showMessage("| |_\\ \\ | |_/\\__/ /\\ V  V / (_) | |  __/");
        showMessage(" \\____/_|\\__\\____/  \\_/\\_/ \\___/|_|\\___|");
        showMessage("                                        ");
        showMessage("                                        ");

        showMessage("Welcome to GitSwole! LET'S GET THEM GAINS");
        showLine();
    }

    /**
     * Displays a horizontal separator line for visual clarity.
     */
    public void showLine() {
        showMessage("_".repeat(getDashes()));
    }

    /**
     * Displays a goodbye message when the application terminates.
     */
    public void byeGreeting() {
        showLine();
        showMessage("Bye! Keep getting swole!");
        showLine();
    }

    /**
     * Displays a formatted error message surrounded by separator lines.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        showLine();
        showMessage(" " + message);
        showLine();
    }

    /**
     * Displays a general informational message.
     *
     * @param s The message to display.
     */
    public void showMessage(String s) {
        System.out.println(s);
    }

    /**
     * Prints all workouts in the provided list, each separated by a line.
     *
     * @param workoutList The list of {@link Workout} objects to display.
     */
    public void printWorkouts(ArrayList<Workout> workoutList) {
        showMessage("=== COMPLETE WORKOUT LOG ===");
        for (Workout workout : workoutList) {
            String status = workout.isDone() ? "[X]" : "[ ]";
            showMessage(status + "[" + workout.getWorkoutName().toUpperCase() + "]");
            printExercises(workout.getExerciseList());
            showMessage(""); //Add a new line between workouts
        }
        showLine();
    }

    /**
     * Prints all exercises in the provided list, each separated by a line.
     *
     * @param exercises The list of {@link Exercise} objects to display.
     */
    public void printExercises(ArrayList<Exercise> exercises) {
        if (exercises.isEmpty()) {
            showMessage("Your exercises list is currently empty :(");
            return;
        }
        for (int i = 0; i < exercises.size(); i++) {
            Exercise e = exercises.get(i);
            printIndividualExercise(e, i);
        }
    }

    private void printIndividualExercise(Exercise e, int i) {
        StringBuilder details = new StringBuilder();
        if (e.getWeight() != 0) details.append(e.getWeight() + "kg");
        if (e.getSets() != 0) {
            if (details.length() > 0) details.append(" | ");
            details.append(e.getSets() + "s");
        }
        if (e.getReps() != 0) {
            if (details.length() > 0) details.append(" | ");
            details.append(e.getReps() + "r");
        }

        String detailsStr = details.length() > 0 ? " (" + details + ")" : "";
        showMessage(String.format(" %d. %s%s", (i + 1), e.getExerciseName(), detailsStr));
    }

    /**
     * Prints a single workout's name and all its exercises.
     *
     * @param workout The {@link Workout} to display.
     */
    public void printWorkout(Workout workout) {
        String status = workout.isDone() ? "[X]" : "[ ]";
        showMessage(status + "[" + workout.getWorkoutName().toUpperCase() + "]");
        printExercises(workout.getExerciseList());
        showLine();
    }

    /**
     * Prints a single exercise's details in a formatted row.
     *
     * @param exercise The {@link Exercise} to display.
     */
    public void printExercise(Exercise exercise) {
        showMessage(String.format(" %s (%dkg | %ds | %dr)",
            exercise.getExerciseName(), exercise.getWeight(),
            exercise.getSets(), exercise.getReps()));
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
