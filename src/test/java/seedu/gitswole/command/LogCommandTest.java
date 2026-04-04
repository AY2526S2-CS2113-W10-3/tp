//@@author rpraveen7
package seedu.gitswole.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.storage.HistoryStorage;
import seedu.gitswole.ui.Ui;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Provides unit tests for the {@link LogCommand} class.
 */
@DisplayName("LogCommand Tests")
class LogCommandTest {

    private WorkoutList workouts;
    private Ui ui;
    private HistoryStorage historyStub;

    /**
     * A stub implementation of {@link HistoryStorage} that bypasses physical file operations.
     * Used to fulfill CS2113 requirements for side effect free testing.
     */
    private static class HistoryStorageStub extends HistoryStorage {
        @Override
        public boolean hasSessionToday(String workoutName) throws IOException {
            return false;
        }
        @Override
        public void writeSessionHeader(String workoutName) throws IOException {}
        @Override
        public void updateExerciseLog(String workoutName, Exercise e, String r) throws IOException {}
    }

    @BeforeEach
    void setUp() {
        workouts = new WorkoutList();
        ui = new Ui();
        historyStub = new HistoryStorageStub();

        // Set up a basic workout for testing
        Workout push = new Workout("push");
        push.addExercise(new Exercise("benchpress", 0, 0, 0));
        workouts.addWorkout(push);
    }

    @Test
    @DisplayName("log w/WORKOUT — sets the active session name correctly")
    void execute_startSession_setsActiveWorkout() throws GitSwoleException {
        assertNull(workouts.getActiveWorkoutName());
        
        LogCommand logCmd = new LogCommand("log w/push", historyStub);
        logCmd.execute(workouts, ui);
        
        assertEquals("push", workouts.getActiveWorkoutName());
    }

    @Test
    @DisplayName("log e/EXERCISE — uses sticky session when w/ is omitted")
    void execute_stickySession_updatesCorrectWorkout() throws GitSwoleException {
        // Arrange: Start a push session
        workouts.setActiveWorkoutName("push");
        
        // Act: Log exercise without the w/ flag
        LogCommand logCmd = new LogCommand("log e/benchpress wt/60 s/3 r/8", historyStub);
        logCmd.execute(workouts, ui);
        
        // Assert: Verify the push workout's benchpress was updated
        Exercise bench = workouts.getWorkoutByName("push").getExerciseByName("benchpress");
        assertEquals(60, bench.getWeight());
        assertEquals(3, bench.getSets());
        assertEquals(8, bench.getReps());
    }

    @Test
    @DisplayName("log e/EXERCISE — updates existing data correctly in memory")
    void execute_updateData_overwritesPreviousValues() throws GitSwoleException {
        Workout push = workouts.getWorkoutByName("push");
        Exercise bench = push.getExerciseByName("benchpress");
        
        // Log first entry
        new LogCommand("log e/benchpress w/push wt/50 s/1 r/1", historyStub).execute(workouts, ui);
        assertEquals(50, bench.getWeight());

        // Log second entry (the.overwrite)
        new LogCommand("log e/benchpress w/push wt/70 s/3 r/8", historyStub).execute(workouts, ui);
        assertEquals(70, bench.getWeight());
    }

    @Test
    @DisplayName("log e/EXERCISE — throws exception when no workout context exists")
    void execute_noContext_throwsIncompleteCommand() {
        // Ensure no active session
        workouts.setActiveWorkoutName(null);
        
        LogCommand logCmd = new LogCommand("log e/benchpress wt/60", historyStub);
        
        GitSwoleException ex = assertThrows(GitSwoleException.class, () -> 
            logCmd.execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
        assertTrue(ex.getMessage().contains("No active workout session found"));
    }

    @Test
    @DisplayName("log e/EXERCISE — throws exception when workout or exercise is not found")
    void execute_notFound_throwsNotFoundException() {
        // Case 1: Unknown Workout
        LogCommand unknownWorkout = new LogCommand("log e/benchpress w/nonexistent", historyStub);
        GitSwoleException ex1 = assertThrows(GitSwoleException.class, () -> 
            unknownWorkout.execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.NOT_FOUND, ex1.getType());

        // Case 2: Unknown Exercise in valid workout
        LogCommand unknownExercise = new LogCommand("log e/squats w/push", historyStub);
        GitSwoleException ex2 = assertThrows(GitSwoleException.class, () -> 
            unknownExercise.execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.NOT_FOUND, ex2.getType());
    }

    @Test
    @DisplayName("log w/WORKOUT — resumes session when hasSessionToday is true")
    void execute_resumeSession_showsResumingMessage() throws GitSwoleException {
        HistoryStorage resumeStub = new HistoryStorageStub() {
            @Override
            public boolean hasSessionToday(String workoutName) {
                return true;
            }
        };

        LogCommand cmd = new LogCommand("log w/push", resumeStub);
        cmd.execute(workouts, ui);

        assertEquals("push", workouts.getActiveWorkoutName());
    }

    @Test
    @DisplayName("log w/ — empty workout name throws INCOMPLETE_COMMAND")
    void execute_emptyWorkoutName_throwsIncompleteCommand() {
        LogCommand cmd = new LogCommand("log w/", historyStub);
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> cmd.execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("log e/EXERCISE — remark is displayed when provided")
    void execute_withRemark_showsRemark() throws GitSwoleException {
        workouts.setActiveWorkoutName("push");

        LogCommand cmd = new LogCommand("log e/benchpress wt/60 s/3 r/8 remark/felt strong", historyStub);
        cmd.execute(workouts, ui);

        Exercise bench = workouts.getWorkoutByName("push").getExerciseByName("benchpress");
        assertEquals(60, bench.getWeight());
    }

    @Test
    @DisplayName("log e/EXERCISE — omitted fields keep previous values")
    void execute_partialUpdate_keepsDefaults() throws GitSwoleException {
        workouts.setActiveWorkoutName("push");

        new LogCommand("log e/benchpress w/push wt/50 s/4 r/10", historyStub).execute(workouts, ui);
        new LogCommand("log e/benchpress w/push wt/80", historyStub).execute(workouts, ui);
        Exercise bench = workouts.getWorkoutByName("push").getExerciseByName("benchpress");
        assertEquals(80, bench.getWeight());
        assertEquals(4, bench.getSets());
        assertEquals(10, bench.getReps());
    }

    @Test
    @DisplayName("log w/WORKOUT — IOException in storage is caught gracefully")
    void execute_storageIOException_handledGracefully() throws GitSwoleException {
        HistoryStorage ioStub = new HistoryStorageStub() {
            @Override
            public boolean hasSessionToday(String workoutName) throws IOException {
                throw new IOException("disk error");
            }
        };

        LogCommand cmd = new LogCommand("log w/push", ioStub);
        cmd.execute(workouts, ui);
        assertEquals("push", workouts.getActiveWorkoutName());
    }

    @Test
    @DisplayName("log e/EXERCISE — negative inputs throw NEG_INPUT")
    void execute_negativeInputs_throwsException() {
        workouts.setActiveWorkoutName("push");

        // Case 1: Negative weight
        LogCommand negWt = new LogCommand("log e/benchpress wt/-10", historyStub);
        assertThrows(GitSwoleException.class, () -> negWt.execute(workouts, ui));

        // Case 2: Negative sets
        LogCommand negSets = new LogCommand("log e/benchpress s/-3", historyStub);
        assertThrows(GitSwoleException.class, () -> negSets.execute(workouts, ui));

        // Case 3: Negative reps
        LogCommand negReps = new LogCommand("log e/benchpress r/-5", historyStub);
        assertThrows(GitSwoleException.class, () -> negReps.execute(workouts, ui));
    }

    @Test
    @DisplayName("log e/EXERCISE — empty e/ flag throws INCOMPLETE_COMMAND")
    void execute_emptyExerciseFlag_throwsIncompleteCommand() {
        workouts.setActiveWorkoutName("push");
        LogCommand cmd = new LogCommand("log e/ wt/80", historyStub);
        GitSwoleException ex = assertThrows(GitSwoleException.class, () -> cmd.execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("log e/EXERCISE — extremely large numbers handled by parser")
    void execute_largeNumbers_handledByParser() throws GitSwoleException {
        workouts.setActiveWorkoutName("push");
        // Parser.parseOptionalInt returns default if parsing fails (e.g. overflow)
        LogCommand cmd = new LogCommand("log e/benchpress wt/999999999999999999", historyStub);
        cmd.execute(workouts, ui);
        
        Exercise bench = workouts.getWorkoutByName("push").getExerciseByName("benchpress");
        // Should keep old value (0) because 999... is not a valid Int
        assertEquals(0, bench.getWeight());
    }

}
//@@author
