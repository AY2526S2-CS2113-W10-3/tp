package seedu.gitswole.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("MarkCommand")
public class MarkCommandTest {

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

    // mark tests
    @Test
    @DisplayName("mark w/WORKOUT - marks existing workout as done")
    void markWorkout_validName_marksasDone() throws GitSwoleException {
        workouts.addWorkout(new Workout("push"));
        new MarkCommand("mark w/push").execute(workouts, ui);
        assertTrue(workouts.getWorkoutByName("push").isDone());
    }

    @Test
    @DisplayName("mark w/WORKOUT - prints [X] confirmation message")
    void markWorkout_validName_printsStatusIcon() throws GitSwoleException {
        workouts.addWorkout(new Workout("push"));
        new MarkCommand("mark w/push").execute(workouts, ui);
        assertTrue(outContent.toString().contains("[X] push"));
    }

    @Test
    @DisplayName("mark w/UNKNOWN - throws NOT_FOUND when workout does not exits")
    void markWorkout_unknownWorkout_throwsNotFound() {
        GitSwoleException e = assertThrows(GitSwoleException.class,
                () -> new MarkCommand("mark w/unknown").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.NOT_FOUND, e.getType());
    }

    @Test
    @DisplayName("mark w/ - throws INCOMPLETE_COMMAND when workout name is blank")
    void markWorkout_blankName_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
                () -> new MarkCommand("mark w/").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("mark with no w/ flag - throws INCOMPLETE_COMMAND")
    void markWorkout_missingFlag_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
                () -> new MarkCommand("mark push").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    // unmark tests
    @Test
    @DisplayName("unmark w/WORKOUT - unmarks existing workout")
    void unmarkWorkout_validName_unmarksWorkout() throws GitSwoleException {
        Workout push = new Workout("push");
        push.markDone(true);
        workouts.addWorkout(push);
        new MarkCommand("unmark w/push").execute(workouts, ui);
        assertTrue(!workouts.getWorkoutByName("push").isDone());
    }

    @Test
    @DisplayName("unmark w/WORKOUT - prints [ ] confirmation message")
    void unmarkWorkout_validName_printsUnmarkedMessage() throws GitSwoleException {
        Workout push = new Workout("push");
        push.markDone(true);
        workouts.addWorkout(push);
        new MarkCommand("unmark w/push").execute(workouts, ui);
        assertTrue(outContent.toString().contains("[ ] push"));
    }

    @Test
    @DisplayName("unmark w/UNKNOWN - throws NOT_FOUND when workout does not exist")
    void unmarkWorkout_unknownWorkout_throwsNotFound() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
                () -> new MarkCommand("unmark w/ghost").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.NOT_FOUND, ex.getType());
    }

    @Test
    @DisplayName("unmark w/ - throws INCOMPLETE_COMMAND when workout name is blank")
    void unmarkWorkout_blankName_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
                () -> new MarkCommand("unmark w/").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("unmark with no w/ flag - throws INCOMPLETE_COMMAND")
    void unmarkWorkout_missingFlag_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
                () -> new MarkCommand("unmark push").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    // edge case tests

    @Test
    @DisplayName("mark w/WORKOUT - marking already marked workout keeps it marked")
    void markWorkout_alreadyMarked_remainsMarked() throws GitSwoleException {
        Workout push = new Workout("push");
        push.markDone(true);
        workouts.addWorkout(push);
        new MarkCommand("mark w/push").execute(workouts, ui);
        assertTrue(workouts.getWorkoutByName("push").isDone());
    }

    @Test
    @DisplayName("unmark w/WORKOUT - unmarking already unmarked workout keeps it unmarked")
    void unmarkWorkout_alreadyUnmarked_remainsUnmarked() throws GitSwoleException {
        workouts.addWorkout(new Workout("push"));
        new MarkCommand("unmark w/push").execute(workouts, ui);
        assertTrue(!workouts.getWorkoutByName("push").isDone());
    }

    @Test
    @DisplayName("mark w/WORKOUT - multi-word workout name is handled correctly")
    void markWorkout_multiWordName_marksCorrectly() throws GitSwoleException {
        workouts.addWorkout(new Workout("push day"));
        new MarkCommand("mark w/push day").execute(workouts, ui);
        assertTrue(workouts.getWorkoutByName("push day").isDone());
    }
}
