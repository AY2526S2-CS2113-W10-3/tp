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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AddCommand")
class AddCommandTest {

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

    // add workout
    @Test
    @DisplayName("add w/WORKOUT — creates a new workout in the list")
    void addWorkout_createsWorkout() throws GitSwoleException {
        new AddCommand("add w/push").execute(workouts, ui);
        assertEquals(1, workouts.numOfWorkouts());
        assertNotNull(workouts.getWorkoutByName("push"));
    }

    @Test
    @DisplayName("add w/WORKOUT — success message contains the workout name")
    void addWorkout_successMessage() throws GitSwoleException {
        new AddCommand("add w/push").execute(workouts, ui);
        assertTrue(outContent.toString().contains("push"));
    }

    @Test
    @DisplayName("add w/WORKOUT — multiple workouts can be added independently")
    void addWorkout_multipleWorkouts() throws GitSwoleException {
        new AddCommand("add w/push").execute(workouts, ui);
        new AddCommand("add w/pull").execute(workouts, ui);
        new AddCommand("add w/legs").execute(workouts, ui);
        assertEquals(3, workouts.numOfWorkouts());
    }

    @Test
    @DisplayName("add w/ — throws INCOMPLETE_COMMAND when workout name is blank")
    void addWorkout_blankName_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> new AddCommand("add w/").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("add with no w/ prefix — throws INCOMPLETE_COMMAND")
    void addWorkout_missingPrefix_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> new AddCommand("add push").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("add w/WORKOUT — new workout starts with an empty exercise list")
    void addWorkout_newWorkoutHasNoExercises() throws GitSwoleException {
        new AddCommand("add w/push").execute(workouts, ui);
        Workout w = workouts.getWorkoutByName("push");
        assertNotNull(w);
        assertEquals(0, w.getNumOfExercises());
    }

    @Test
    @DisplayName("add w/WORKOUT — throws exception for duplicate workout name")
    void addWorkout_duplicateName_throwsException() throws GitSwoleException {
        new AddCommand("add w/push").execute(workouts, ui);
        // Same name but different case should also fail
        assertThrows(GitSwoleException.class,
            () -> new AddCommand("add w/PUSH").execute(workouts, ui));
    }

    // add exercise
    @Test
    @DisplayName("add e/EXERCISE w/WORKOUT — adds exercise to the correct workout")
    void addExercise_addsToCorrectWorkout() throws GitSwoleException {
        workouts.addWorkout(new Workout("push"));
        new AddCommand("add e/bench press w/push wt/60 s/3 r/8").execute(workouts, ui);
        Workout w = workouts.getWorkoutByName("push");
        assertEquals(1, w.getNumOfExercises());
        assertEquals("bench press", w.getExerciseList().get(0).getExerciseName());
    }

    @Test
    @DisplayName("add e/EXERCISE w/WORKOUT — parses weight, sets and reps correctly")
    void addExercise_parsesOptionalFields() throws GitSwoleException {
        workouts.addWorkout(new Workout("push"));
        new AddCommand("add e/bench press w/push wt/60 s/3 r/8").execute(workouts, ui);
        Exercise e = workouts.getWorkoutByName("push").getExerciseList().get(0);
        assertEquals(60, e.getWeight());
        assertEquals(3, e.getSets());
        assertEquals(8, e.getReps());
    }

    @Test
    @DisplayName("add e/EXERCISE w/WORKOUT — optional fields default to 0 when omitted")
    void addExercise_defaultsToZeroWhenOptionalFieldsOmitted() throws GitSwoleException {
        workouts.addWorkout(new Workout("push"));
        new AddCommand("add e/plank w/push").execute(workouts, ui);
        Exercise e = workouts.getWorkoutByName("push").getExerciseList().get(0);
        assertEquals(0, e.getWeight());
        assertEquals(0, e.getSets());
        assertEquals(0, e.getReps());
    }

    @Test
    @DisplayName("add e/EXERCISE w/WORKOUT — success message is shown")
    void addExercise_successMessage() throws GitSwoleException {
        workouts.addWorkout(new Workout("push"));
        new AddCommand("add e/bench press w/push wt/60 s/3 r/8").execute(workouts, ui);
        assertFalse(outContent.toString().isBlank());
    }

    @Test
    @DisplayName("add e/EXERCISE w/WORKOUT — multiple exercises can be added to same workout")
    void addExercise_multipleExercises() throws GitSwoleException {
        workouts.addWorkout(new Workout("push"));
        new AddCommand("add e/bench press w/push wt/60 s/3 r/8").execute(workouts, ui);
        new AddCommand("add e/overhead press w/push wt/40 s/3 r/10").execute(workouts, ui);
        assertEquals(2, workouts.getWorkoutByName("push").getNumOfExercises());
    }

    @Test
    @DisplayName("add e/EXERCISE w/UNKNOWN — throws IDX_OUTOFBOUNDS for missing workout")
    void addExercise_unknownWorkout_throwsIdxOutOfBounds() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> new AddCommand("add e/squat w/legs").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.IDX_OUTOFBOUNDS, ex.getType());
    }

    @Test
    @DisplayName("add e/ w/WORKOUT — throws INCOMPLETE_COMMAND when exercise name is blank")
    void addExercise_blankExerciseName_throwsIncompleteCommand() {
        workouts.addWorkout(new Workout("push"));
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> new AddCommand("add e/ w/push").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("add e/EXERCISE — throws INCOMPLETE_COMMAND when workout prefix is absent")
    void addExercise_missingWorkoutPrefix_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> new AddCommand("add e/bench press").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    // isExit
    @Test
    @DisplayName("isExit always returns false")
    void addCommand_isExitFalse() throws GitSwoleException {
        workouts.addWorkout(new Workout("push"));
        AddCommand cmd = new AddCommand("add w/pull");
        cmd.execute(workouts, ui);
        assertFalse(cmd.isExit());
    }
}
