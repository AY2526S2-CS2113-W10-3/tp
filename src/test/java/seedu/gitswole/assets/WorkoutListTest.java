package seedu.gitswole.assets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author vet3whale
@DisplayName("WorkoutList")
class WorkoutListTest {

    private WorkoutList workoutList;

    @BeforeEach
    void setUp() {
        workoutList = new WorkoutList();
    }

    @Test
    @DisplayName("New WorkoutList is empty")
    void newWorkoutList_isEmpty() {
        assertEquals(0, workoutList.numOfWorkouts());
        assertTrue(workoutList.getWorkouts().isEmpty());
    }

    @Test
    @DisplayName("addWorkout increases count")
    void addWorkout_increasesCount() {
        workoutList.addWorkout(new Workout("push"));
        assertEquals(1, workoutList.numOfWorkouts());
    }

    @Test
    @DisplayName("getWorkoutByName finds workout case-insensitively")
    void getWorkoutByName_caseInsensitive() {
        workoutList.addWorkout(new Workout("Push"));
        Workout found = workoutList.getWorkoutByName("push");
        assertNotNull(found);
        assertEquals("Push", found.getWorkoutName());
    }

    @Test
    @DisplayName("getWorkoutByName returns null when not found")
    void getWorkoutByName_notFound_returnsNull() {
        assertNull(workoutList.getWorkoutByName("legs"));
    }

    @Test
    @DisplayName("getWorkoutByName trims surrounding whitespace")
    void getWorkoutByName_trimsInput() {
        workoutList.addWorkout(new Workout("push"));
        assertNotNull(workoutList.getWorkoutByName("  push  "));
    }

    @Test
    @DisplayName("removeWorkout removes the correct workout and leaves others")
    void removeWorkout_removesCorrectWorkout() {
        workoutList.addWorkout(new Workout("push"));
        workoutList.addWorkout(new Workout("pull"));
        assertTrue(workoutList.removeWorkout("push"));
        assertEquals(1, workoutList.numOfWorkouts());
        assertNull(workoutList.getWorkoutByName("push"));
        assertNotNull(workoutList.getWorkoutByName("pull"));
    }

    @Test
    @DisplayName("removeWorkout returns false when workout not found")
    void removeWorkout_notFound_returnsFalse() {
        assertFalse(workoutList.removeWorkout("legs"));
    }

    @Test
    @DisplayName("removeExercise delegates correctly to the target workout")
    void removeExercise_delegatesToWorkout() {
        Workout w = new Workout("push");
        w.addExercise(new Exercise("bench press", 60, 3, 8));
        workoutList.addWorkout(w);

        assertTrue(workoutList.removeExercise("push", "bench press"));
        assertEquals(0, w.getNumOfExercises());
    }

    @Test
    @DisplayName("removeExercise returns false for an unknown workout")
    void removeExercise_unknownWorkout_returnsFalse() {
        assertFalse(workoutList.removeExercise("nonexistent", "bench press"));
    }

    @Test
    @DisplayName("getWorkoutByExerciseName finds the correct workout")
    void getWorkoutByExerciseName_findsCorrectWorkout() {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("benchpress", 60, 3, 8));
        workoutList.addWorkout(push);

        Workout pull = new Workout("pull");
        pull.addExercise(new Exercise("deadlift", 100, 1, 5));
        workoutList.addWorkout(pull);

        Workout result = workoutList.getWorkoutByExerciseName("deadlift");
        assertNotNull(result);
        assertEquals("pull", result.getWorkoutName());
    }

    @Test
    @DisplayName("getWorkoutByExerciseName returns null when not found")
    void getWorkoutByExerciseName_notFound_returnsNull() {
        Workout push = new Workout("push");
        push.addExercise(new Exercise("benchpress", 60, 3, 8));
        workoutList.addWorkout(push);

        assertNull(workoutList.getWorkoutByExerciseName("squats"));
    }
}
//@@author
