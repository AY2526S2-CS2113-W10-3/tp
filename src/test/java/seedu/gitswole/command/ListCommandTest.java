//@@author rpraveen7
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

@DisplayName("ListCommand")
class ListCommandTest {

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

    /**
     * Adds two populated workouts to the list for use in tests.
     */
    private void populateWorkouts() {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 60, 3, 8));
        push.addExercise(new Exercise("overhead press", 40, 3, 10));
        workouts.addWorkout(push);

        Workout pull = new Workout("pull");
        pull.addExercise(new Exercise("deadlift", 100, 3, 5));
        workouts.addWorkout(pull);
    }

    // list (summary)
    @Test
    @DisplayName("list — empty workout list shows empty message")
    void listSummary_emptyList_showsEmptyMessage() throws GitSwoleException {
        new ListCommand("list").execute(workouts, ui);
        assertTrue(outContent.toString().contains("empty"));
    }

    @Test
    @DisplayName("list — shows all workout names")
    void listSummary_showsWorkoutNames() throws GitSwoleException {
        populateWorkouts();
        new ListCommand("list").execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("push"));
        assertTrue(output.contains("pull"));
    }

    @Test
    @DisplayName("list — does not show individual exercises")
    void listSummary_doesNotShowExercises() throws GitSwoleException {
        populateWorkouts();
        new ListCommand("list").execute(workouts, ui);
        assertFalse(outContent.toString().contains("bench press"));
    }

    // list w/WORKOUT
    @Test
    @DisplayName("list w/WORKOUT — shows exercises for that workout")
    void listWorkout_showsExercises() throws GitSwoleException {
        populateWorkouts();
        new ListCommand("list w/push").execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("bench press"));
        assertTrue(output.contains("overhead press"));
    }

    @Test
    @DisplayName("list w/WORKOUT — does not show exercises from other workouts")
    void listWorkout_isolatesWorkout() throws GitSwoleException {
        populateWorkouts();
        new ListCommand("list w/push").execute(workouts, ui);
        assertFalse(outContent.toString().contains("deadlift"));
    }

    @Test
    @DisplayName("list w/WORKOUT — throws IDX_OUTOFBOUNDS for an unknown workout")
    void listWorkout_unknownWorkout_throwsException() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> new ListCommand("list w/legs").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.IDX_OUTOFBOUNDS, ex.getType());
    }

    @Test
    @DisplayName("list w/ — throws INCOMPLETE_COMMAND when workout name is missing")
    void listWorkout_missingName_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> new ListCommand("list w/").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("list w/WORKOUT — shows empty message when workout has no exercises")
    void listWorkout_emptyExercises_showsEmptyMessage() throws GitSwoleException {
        workouts.addWorkout(new Workout("legs"));
        new ListCommand("list w/legs").execute(workouts, ui);
        assertTrue(outContent.toString().contains("empty"));
    }

    // list all
    @Test
    @DisplayName("list all — empty workout list shows empty message")
    void listAll_emptyList_showsEmptyMessage() throws GitSwoleException {
        new ListCommand("list all").execute(workouts, ui);
        assertTrue(outContent.toString().contains("empty"));
    }

    @Test
    @DisplayName("list all — shows all workouts and their exercises")
    void listAll_showsAllExercises() throws GitSwoleException {
        populateWorkouts();
        new ListCommand("list all").execute(workouts, ui);
        String output = outContent.toString();
        assertTrue(output.contains("PUSH"));
        assertTrue(output.contains("bench press"));
        assertTrue(output.contains("PULL"));
        assertTrue(output.contains("deadlift"));
    }

    @Test
    @DisplayName("list all — shows the complete workout log header")
    void listAll_showsHeader() throws GitSwoleException {
        populateWorkouts();
        new ListCommand("list all").execute(workouts, ui);
        assertTrue(outContent.toString().contains("COMPLETE WORKOUT LOG"));
    }

    // isExit
    @Test
    @DisplayName("ListCommand isExit always returns false")
    void listCommand_isExitFalse() throws GitSwoleException {
        ListCommand cmd = new ListCommand("list");
        cmd.execute(workouts, ui);
        assertFalse(cmd.isExit());
    }
}
//@@author
