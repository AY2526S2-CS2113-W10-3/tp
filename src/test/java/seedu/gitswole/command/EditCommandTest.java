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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EditCommand")
class EditCommandTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
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
        System.setIn(originalIn);
    }

    private EditCommand editCommandWithInput(String response, String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        ui = new Ui(in);
        return new EditCommand(response);
    }

    // edit workout tests
    @Test
    @DisplayName("edit w/WORKOUT - renames workout when wn/ flag is provided")
    void editWorkout_validName_renamesWorkout() throws GitSwoleException {
        workouts.addWorkout(new Workout("LegDay"));
        editCommandWithInput("edit w/LegDay", "wn/ChestDay\n").execute(workouts, ui);
        assertNotNull(workouts.getWorkoutByName("ChestDay"));
    }

    @Test
    @DisplayName("edit w/WORKOUT - press enter leaves workout name unchanged")
    void editWorkout_pressEnter_noChange() throws GitSwoleException {
        workouts.addWorkout(new Workout("LegDay"));
        editCommandWithInput("edit w/LegDay", "\n").execute(workouts, ui);
        assertNotNull(workouts.getWorkoutByName("LegDay"));
        assertTrue(outContent.toString().contains("No Changes recorded!"));
    }

    @Test
    @DisplayName("edit w/ - throws INCOMPLETE_COMMAND when workout name is blank")
    void editWorkout_blankName_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> editCommandWithInput("edit w/", "\n").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("edit with no w/ - throws INCOMPLETE_COMMAND")
    void editWorkout_missingPrefix_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> editCommandWithInput("edit LegDay", "\n").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("edit w/UNKNOWN - throws NOT_FOUND when workout does not exist")
    void editWorkout_unknownWorkout_throwsNotFound() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> editCommandWithInput("edit w/Ghost", "\n").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.NOT_FOUND, ex.getType());
    }

    // edit exercise tests
    @Test
    @DisplayName("edit w/ e/EXERCISE - throws INCOMPLETE_COMMAND when workout name is blank")
    void editExercise_blankWorkoutName_throwsIncompleteCommand() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> editCommandWithInput("edit w/ e/squat", "\n").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("edit w/WORKOUT e/ - throws INCOMPLETE_COMMAND when exercise name is blank")
    void editExercise_blankExerciseName_throwsIncompleteCommand() {
        workouts.addWorkout(new Workout("push"));
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> editCommandWithInput("edit w/push e/", "\n").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("edit w/UNKNOWN e/EXERCISE - throws NOT_FOUND when workout does not exist")
    void editExercise_unknownWorkout_throwsNotFound() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> editCommandWithInput("edit w/Ghost e/squat", "\n").execute(workouts, ui));
        assertEquals(GitSwoleException.ErrorType.NOT_FOUND, ex.getType());
    }

    @Test
    @DisplayName("edit w/WORKOUT e/EXERCISE - renames exercise via en/ flag")
    void editExercise_validName_renamesExercise() throws GitSwoleException {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 60, 3, 8));
        workouts.addWorkout(push);
        editCommandWithInput("edit w/push e/bench press", "en/press\n").execute(workouts, ui);
        assertNotNull(workouts.getWorkoutByName("push").getExerciseByName("press"));
    }

    @Test
    @DisplayName("edit w/WORKOUT e/EXERCISE - press enter on all fields leaves exercise unchanged")
    void editExercise_cancelAll_noChanges() throws GitSwoleException {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 60, 3, 8));
        workouts.addWorkout(push);
        editCommandWithInput("edit w/push e/bench press", "\n").execute(workouts, ui);
        Exercise e = workouts.getWorkoutByName("push").getExerciseByName("bench press");
        assertNotNull(e);
        assertEquals("bench press", e.getExerciseName());
        assertTrue(outContent.toString().contains("No Changes recorded!"));
    }

    @Test
    @DisplayName("edit w/WORKOUT e/EXERCISE - success message contains new exercise name")
    void editExercise_successMessage_containsNames() throws GitSwoleException {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 60, 3, 8));
        workouts.addWorkout(push);
        editCommandWithInput("edit w/push e/bench press", "en/press\n").execute(workouts, ui);
        assertTrue(outContent.toString().contains("press"));
    }

    @Test
    @DisplayName("edit w/WORKOUT e/EXERCISE - updates weight via wt/ flag")
    void editExercise_newWeight_updatesWeight() throws GitSwoleException {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 60, 3, 8));
        workouts.addWorkout(push);
        editCommandWithInput("edit w/push e/bench press", "wt/80\n").execute(workouts, ui);
        assertEquals(80, workouts.getWorkoutByName("push").getExerciseByName("bench press").getWeight());
    }

    @Test
    @DisplayName("edit w/WORKOUT e/EXERCISE - updates sets via s/ flag")
    void editExercise_newSets_updatesSets() throws GitSwoleException {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 60, 3, 8));
        workouts.addWorkout(push);
        editCommandWithInput("edit w/push e/bench press", "s/5\n").execute(workouts, ui);
        assertEquals(5, workouts.getWorkoutByName("push").getExerciseByName("bench press").getSets());
    }

    @Test
    @DisplayName("edit w/WORKOUT e/EXERCISE - updates reps via r/ flag")
    void editExercise_newReps_updatesReps() throws GitSwoleException {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 60, 3, 8));
        workouts.addWorkout(push);
        editCommandWithInput("edit w/push e/bench press", "r/12\n").execute(workouts, ui);
        assertEquals(12, workouts.getWorkoutByName("push").getExerciseByName("bench press").getReps());
    }

    @Test
    @DisplayName("edit w/WORKOUT e/EXERCISE - updates all fields in one line")
    void editExercise_allFields_updatesAll() throws GitSwoleException {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 60, 3, 8));
        workouts.addWorkout(push);
        editCommandWithInput("edit w/push e/bench press",
            "wn/chest en/incline press wt/80 s/5 r/12\n").execute(workouts, ui);
        Exercise e = workouts.getWorkoutByName("chest").getExerciseByName("incline press");
        assertNotNull(e);
        assertEquals(80, e.getWeight());
        assertEquals(5, e.getSets());
        assertEquals(12, e.getReps());
    }
}
