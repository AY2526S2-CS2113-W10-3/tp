package seedu.gitswole.ui;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Random;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all user interface interactions for the GitSwole application,
 * including reading user input and displaying messages, workouts, and exercises.
 */
public class Ui {
    private Scanner scanner;
    private int dashes = 120; // minimally 120, for help print statements

    /**
     * Constructs an Ui instance and initializes the input scanner.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Constructs an Ui instance and initializes the input scanner.
     * USED FOR TESTING
     */
    public Ui(InputStream in) {
        this.scanner = new Scanner(in);
    }

    /**
     * Reads and returns a single line of input from the user.
     *
     * @return The full string entered by the user.
     */
    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    }

    /**
     * Reads and returns a single line of input from the user.
     * <p>
     * Returns {@code null} when the underlying input stream has no more lines
     * (i.e. EOF has been reached, such as when stdin is closed or a piped
     * input stream ends). Callers must handle the {@code null} case to avoid
     * {@link NullPointerException}.
     *
     * @return The full command string entered by the user, or {@code null} at EOF.
     */
    public String readCommand() {
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            showMessage("Ouch. Why bro? Please exit through the \"exit\" command next time!");
            return "exit";
        }
    }

    /**
     * Displays the application logo and a welcome message on startup.
     */
    public void helloGreeting(WorkoutList workouts) {
        int total = workouts.numOfWorkouts();
        int done = workouts.numOfCompletedWorkouts();
        int exercises = workouts.numOfTotalExercises();
        String[] quotes = {
            "The only bad workout is the one that didn't happen.",
            "Your body is the most complex hardware you’ll ever own; keep it optimized.",
            "Your body can stand almost anything. It's your mind you have to convince.",
            "Success starts with self-discipline.",
            "Great strength isn't built in a single push; it's a series of successful merges",
            "Debugging your life starts with upgrading your health.",
            "Commit to the process and the results will follow."
        };

        String quote = quotes[new Random().nextInt(quotes.length)];
        showLine();
        showMessage("|     ______      __   _____                   __");
        showMessage("|    / ____/ (_)_/ /_ / ___/__   ___  __ ___  / /  _______   ");
        showMessage("|   / / __  / //_ __/ \\_ \\_ \\ \\  | | / / __ \\/ /  / /__/ /  ");
        showMessage("|  / /_/ / / / / /_ ___/  /  \\ \\ / |/ / /_/ / /__/ _____/");
        showMessage("|  \\____/ /_/ \\__/ \\_____/    \\__/|__/\\____/____/\\_____/    ");
        showMessage("|                                       ");

        showMessage("| Welcome to GitSwole! (@w@)/");
        showMessage("| First time using it? Type 'help' to see what ya got!");
        showMessage("| v2.0 | " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        showLine();
        showMessage("| PROGRESS SNAPSHOT ");
        showMessage("| Workouts logged  : " + total);
        showMessage("| Workouts done    : " + done + " / " + total);
        showMessage("| Total exercises  : " + exercises);
        showLine();
        if (done == 1) {
            showMessage("| First workout crushed! (*´▽`*) Current Tier: Coal");
        } else if (done == 2) {
            showMessage("| Double kills! level up: Wood tier achieved (❛◡❛)");
        } else if (done == 3) {
            showMessage("| Triple workout conquered! ヽ(@´∀`@)ﾉ Current Tier: Bronze");
        } else if (done == 4) {
            showMessage("| Quadra kills! level up: Silver tier achieved Σヽ(ﾟД ﾟ; )ﾉ");
        } else if (done == 5) {
            showMessage("| Rampage! Workouts are now enslaved. Current Tier: Gold (✘Д✘๑ )");
        } else if (done == 6) {
            showMessage("| Killing spree! level up: Platinum tier (oωo)");
        } else if (done == 7) {
            showMessage("| Godlike! Workouts surrender to you,my lord. Current Tier: Diamond Σ( ° △ °|||)");
        } else if (done == 8) {
            showMessage("| Aced! The iron obeys your command. Current Tier: Master w(ﾟДﾟ)w");
        } else if (done == 9) {
            showMessage("| Unstoppable! You are the workout legend! Current Tier: Legendary (o◡o)");
        } else if (done >= 10) {
            showMessage("| Congrats! You've reached the highest tier: Eternal");
        }
        showLine();
        showMessage("| Daily quote:\"" + quote + "\"");
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
        if (e.getWeight() != 0) {
            details.append(e.getWeight() + "kg");
        }
        if (e.getSets() != 0) {
            if (details.length() > 0) {
                details.append(" | ");
            }
            details.append(e.getSets() + "s");
        }
        if (e.getReps() != 0) {
            if (details.length() > 0) {
                details.append(" | ");
            }
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
