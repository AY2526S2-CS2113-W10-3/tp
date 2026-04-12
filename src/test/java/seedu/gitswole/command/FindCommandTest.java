package seedu.gitswole.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FindCommand")
class FindCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;
    private WorkoutList workouts;
    private Ui ui;

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

    private void populateWorkouts() {
        Workout push = new Workout("push day");
        push.addExercise(new Exercise("bench press", 60, 3, 8));
        push.addExercise(new Exercise("overhead press", 40, 3, 10));
        workouts.addWorkout(push);

        Workout pull = new Workout("pull day");
        pull.addExercise(new Exercise("deadlift", 100, 3, 5));
        pull.addExercise(new Exercise("barbell row", 70, 3, 8));
        workouts.addWorkout(pull);
    }

    // --- Matching workout names ---

    @Test
    @DisplayName("keyword matching workout name shows [Workout] result")
    void find_workoutNameMatch_showsWorkoutResult() throws GitSwoleException {
        populateWorkouts();
        new FindCommand("push").execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("[Workout]"));
        assertTrue(output.contains("push day"));
        assertTrue(output.contains("2")); // exercise count
    }

    @Test
    @DisplayName("partial keyword matches multiple workouts")
    void find_partialKeyword_matchesMultipleWorkouts() throws GitSwoleException {
        populateWorkouts();
        new FindCommand("day").execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("push day"));
        assertTrue(output.contains("pull day"));
    }

    @Test
    @DisplayName("search is case-insensitive for workout names")
    void find_caseInsensitive_workoutMatch() throws GitSwoleException {
        populateWorkouts();
        new FindCommand("PUSH").execute(workouts, ui);
        assertTrue(outContent.toString().contains("push day"));
    }

    // --- Matching exercise names ---

    @Test
    @DisplayName("keyword matching exercise name shows [Exercise] result with details")
    void find_exerciseNameMatch_showsExerciseResult() throws GitSwoleException {
        populateWorkouts();
        new FindCommand("bench").execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("[Exercise]"));
        assertTrue(output.contains("bench press"));
        assertTrue(output.contains("push day"));
        assertTrue(output.contains("60"));  // weight
        assertTrue(output.contains("3"));   // sets
        assertTrue(output.contains("8"));   // reps
    }

    @Test
    @DisplayName("partial keyword matches multiple exercises across workouts")
    void find_partialKeyword_matchesMultipleExercises() throws GitSwoleException {
        populateWorkouts();
        new FindCommand("press").execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("bench press"));
        assertTrue(output.contains("overhead press"));
    }

    @Test
    @DisplayName("search is case-insensitive for exercise names")
    void find_caseInsensitive_exerciseMatch() throws GitSwoleException {
        populateWorkouts();
        new FindCommand("DEADLIFT").execute(workouts, ui);
        assertTrue(outContent.toString().contains("deadlift"));
    }

    // --- keyword matches both workout and exercise ---

    @Test
    @DisplayName("keyword matching both workout and exercise shows both results")
    void find_matchesBothWorkoutAndExercise() throws GitSwoleException {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("push up", 0, 3, 20));
        workouts.addWorkout(push);

        new FindCommand("push").execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("[Workout]"));
        assertTrue(output.contains("[Exercise]"));
    }

    // --- No match ---

    @Test
    @DisplayName("no match shows 'No matching' message")
    void find_noMatch_showsNoMatchMessage() throws GitSwoleException {
        populateWorkouts();
        new FindCommand("legs").execute(workouts, ui);
        assertTrue(outContent.toString().contains("No matching workouts or exercises found :("));
    }

    @Test
    @DisplayName("empty workout list shows 'No matching' message")
    void find_emptyList_showsNoMatchMessage() throws GitSwoleException {
        new FindCommand("push").execute(workouts, ui);
        assertTrue(outContent.toString().contains("No matching workouts or exercises found :("));
    }

    // --- Empty keyword ---

    @Test
    @DisplayName("empty keyword throws INCOMPLETE_COMMAND")
    void find_emptyKeyword_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> new FindCommand("").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("whitespace-only keyword throws INCOMPLETE_COMMAND")
    void find_whitespaceKeyword_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> new FindCommand("   ").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    // --- isExit ---

    @Test
    @DisplayName("isExit always returns false")
    void findCommand_isExitFalse() {
        FindCommand cmd = new FindCommand("push");
        assertFalse(cmd.isExit());
    }
}
