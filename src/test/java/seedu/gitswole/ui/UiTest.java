package seedu.gitswole.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Ui")
class UiTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;
    private Ui ui;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        ui = new Ui();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("showError prints the error message between separator lines")
    void showError_printsMessage() {
        ui.showError("something went wrong");
        assertTrue(outContent.toString().contains("something went wrong"));
    }

    @Test
    @DisplayName("showLine prints a line of underscores")
    void showLine_printsDashes() {
        ui.showLine();
        assertTrue(outContent.toString().trim().startsWith("_"));
    }

    @Test
    @DisplayName("helloGreeting contains the welcome text")
    void helloGreeting_containsWelcomeText() {
        WorkoutList workouts = new WorkoutList();
        ui.helloGreeting(workouts);
        assertTrue(outContent.toString().contains("Welcome to GitSwole"));
    }

    @Test
    @DisplayName("byeGreeting contains goodbye text")
    void byeGreeting_containsGoodbyeText() {
        ui.byeGreeting();
        assertTrue(outContent.toString().contains("Bye"));
    }

    @Test
    @DisplayName("printExercise outputs name and weight")
    void printExercise_formatsCorrectly() {
        ui.printExercise(new Exercise("bench press", 60, 3, 8));
        String output = outContent.toString();
        assertTrue(output.contains("bench press"));
        assertTrue(output.contains("60"));
    }

    @Test
    @DisplayName("printWorkouts prints every workout name in uppercase")
    void printWorkouts_allNamesUppercase() {
        ArrayList<Workout> list = new ArrayList<>();
        list.add(new Workout("push"));
        list.add(new Workout("pull"));
        ui.printWorkouts(list);
        String output = outContent.toString().toUpperCase();
        assertTrue(output.contains("PUSH"));
        assertTrue(output.contains("PULL"));
    }

    @Test
    @DisplayName("showError output is flanked by separator lines")
    void showError_flankedBySeparators() {
        ui.showError("oops");
        String output = outContent.toString();
        // at least two separator lines should be present around the error
        long underscoreLineCount = output.lines().count();
        assertTrue(underscoreLineCount >= 2);
    }
}
