package seedu.gitswole.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("HelpCommand")
class HelpCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;
    private Ui ui;
    private WorkoutList workouts;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        ui = new Ui();
        workouts = new WorkoutList();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }


    @Test
    @DisplayName("execute does not throw any exception")
    void execute_doesNotThrow() {
        assertDoesNotThrow(() -> new HelpCommand().execute(workouts, ui));
    }

    @Test
    @DisplayName("isExit always returns false")
    void execute_isExitFalse() throws GitSwoleException {
        HelpCommand cmd = new HelpCommand();
        cmd.execute(workouts, ui);
        assertFalse(cmd.isExit());
    }


    @Test
    @DisplayName("Output contains the GITSWOLE COMMAND SUMMARY title")
    void output_containsTitle() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("GITSWOLE COMMAND SUMMARY"));
    }

    @Test
    @DisplayName("Output contains column headers ACTION, FORMAT, EXAMPLE")
    void output_containsColumnHeaders() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("ACTION"));
        assertTrue(output.contains("FORMAT"));
        assertTrue(output.contains("EXAMPLE"));
    }

    @Test
    @DisplayName("Output contains a user-guide URL")
    void output_containsUserGuideUrl() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("https://"));
    }


    @Test
    @DisplayName("Output contains add workout format")
    void output_containsAddWorkoutFormat() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("add w/WORKOUT"));
    }

    @Test
    @DisplayName("Output contains add exercise format with optional flags")
    void output_containsAddExerciseFormat() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("e/EXERCISE_NAME"));
        assertTrue(output.contains("wt/WEIGHT"));
        assertTrue(output.contains("s/SETS"));
        assertTrue(output.contains("r/REPS"));
    }

    @Test
    @DisplayName("Output contains delete workout format")
    void output_containsDeleteWorkoutFormat() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("delete w/WORKOUT"));
    }

    @Test
    @DisplayName("Output contains delete exercise format")
    void output_containsDeleteExerciseFormat() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("delete e/EXERCISE w/WORKOUT"));
    }

    @Test
    @DisplayName("Output contains list, list w/workout, and list all formats")
    void output_containsListFormats() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("list w/WORKOUT"));
        assertTrue(output.contains("list all"));
    }

    @Test
    @DisplayName("Output contains find format")
    void output_containsFindFormat() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("find KEYWORD"));
    }

    @Test
    @DisplayName("Output contains edit workout format")
    void output_containsEditWorkoutFormat() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("edit w/WORKOUT_NAME"));
    }

    @Test
    @DisplayName("Output contains edit exercise format")
    void output_containsEditExerciseFormat() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("edit w/WORKOUT_NAME e/EXERCISE_NAME"));
    }

    @Test
    @DisplayName("Output contains help and exit commands")
    void output_containsHelpAndExit() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("help"));
        assertTrue(output.contains("exit"));
    }

    @Test
    @DisplayName("Output contains add workout example")
    void output_containsAddWorkoutExample() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("add w/push"));
    }

    @Test
    @DisplayName("Output contains add exercise example")
    void output_containsAddExerciseExample() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("add e/benchpress w/push"));
    }

    @Test
    @DisplayName("Output contains delete workout example")
    void output_containsDeleteWorkoutExample() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("delete w/push"));
    }

    @Test
    @DisplayName("Output contains find example")
    void output_containsFindExample() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("find push"));
    }

    @Test
    @DisplayName("Output contains separator lines made of '=' characters")
    void output_containsEqualsSeparators() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("=".repeat(10)));
    }

    @Test
    @DisplayName("Output contains a '-' divider between headers and rows")
    void output_containsDashDivider() throws GitSwoleException {
        new HelpCommand().execute(workouts, ui);
        assertTrue(outContent.toString().contains("-".repeat(10)));
    }
}
