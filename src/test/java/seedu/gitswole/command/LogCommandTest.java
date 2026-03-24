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
     * Used to fulfill CS2113 requirements for side-effect free testing.
     */
    private static class HistoryStorageStub extends HistoryStorage {
        @Override
        public boolean hasSessionToday(String workoutName) { return false; }
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

        // Setup a basic workout for testing
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

        // Log second entry (the overwrite)
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
}
