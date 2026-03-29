package seedu.gitswole.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Storage} class.
 *
 */
// @@author lamzl
class StorageTest {

    @TempDir
    Path tempDir;

    private String testFilePath;
    private Storage storage;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("data/gitswole.txt").toString();
        storage = new Storage(testFilePath);
    }

    // Builds a WorkoutList with one push workout containing two exercises.
    private WorkoutList buildSingleWorkout() {
        WorkoutList list = new WorkoutList();
        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 80, 3, 10));
        push.addExercise(new Exercise("overhead press", 50, 3, 8));
        list.addWorkout(push);
        return list;
    }

    // Builds a WorkoutList with two workouts.
    private WorkoutList buildTwoWorkouts() {
        WorkoutList list = new WorkoutList();

        Workout push = new Workout("push");
        push.addExercise(new Exercise("bench press", 80, 3, 10));
        list.addWorkout(push);

        Workout pull = new Workout("pull");
        pull.addExercise(new Exercise("pull up", 0, 4, 12));
        pull.addExercise(new Exercise("lat pulldown", 60, 3, 10));
        list.addWorkout(pull);

        return list;
    }

    private void writeRawFile(String content) throws IOException {
        File file = new File(testFilePath);
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
    }

    @Test
    void saveAndLoad_singleWorkoutWithExercises_roundTripCorrect() throws IOException,
            Storage.StorageException {
        WorkoutList original = buildSingleWorkout();
        storage.save(original);

        WorkoutList loaded = storage.load();

        assertEquals(1, loaded.numOfWorkouts());
        Workout w = loaded.getWorkoutByName("push");
        assertNotNull(w);
        assertEquals(2, w.getExerciseList().size());

        Exercise e1 = w.getExerciseList().get(0);
        assertEquals("bench press", e1.getExerciseName());
        assertEquals(80, e1.getWeight());
        assertEquals(3, e1.getSets());
        assertEquals(10, e1.getReps());

        Exercise e2 = w.getExerciseList().get(1);
        assertEquals("overhead press", e2.getExerciseName());
        assertEquals(50, e2.getWeight());
        assertEquals(3, e2.getSets());
        assertEquals(8, e2.getReps());
    }

    @Test
    void saveAndLoad_twoWorkouts_allDataPreserved() throws IOException, Storage.StorageException {
        WorkoutList original = buildTwoWorkouts();
        storage.save(original);

        WorkoutList loaded = storage.load();

        assertEquals(2, loaded.numOfWorkouts());
        assertNotNull(loaded.getWorkoutByName("push"));
        assertNotNull(loaded.getWorkoutByName("pull"));
        assertEquals(1, loaded.getWorkoutByName("push").getExerciseList().size());
        assertEquals(2, loaded.getWorkoutByName("pull").getExerciseList().size());
    }

    @Test
    void saveAndLoad_workoutWithNoExercises_loadsCorrectly() throws IOException,
            Storage.StorageException {
        WorkoutList list = new WorkoutList();
        list.addWorkout(new Workout("legs"));
        storage.save(list);

        WorkoutList loaded = storage.load();

        assertEquals(1, loaded.numOfWorkouts());
        Workout w = loaded.getWorkoutByName("legs");
        assertNotNull(w);
        assertEquals(0, w.getExerciseList().size());
    }

    @Test
    void saveAndLoad_emptyWorkoutList_loadsEmpty() throws IOException, Storage.StorageException {
        storage.save(new WorkoutList());

        WorkoutList loaded = storage.load();

        assertEquals(0, loaded.numOfWorkouts());
    }

    @Test
    void saveAndLoad_isDoneFlagTrue_preservedAfterLoad() throws IOException,
            Storage.StorageException {
        WorkoutList list = new WorkoutList();
        Workout w = new Workout("push");
        w.markDone(true);
        list.addWorkout(w);
        storage.save(list);

        WorkoutList loaded = storage.load();
        assertTrue(loaded.getWorkoutByName("push").isDone());
    }

    @Test
    void saveAndLoad_isDoneFlagFalse_preservedAfterLoad() throws IOException,
            Storage.StorageException {
        WorkoutList list = new WorkoutList();
        Workout w = new Workout("push");
        w.markDone(false);
        list.addWorkout(w);
        storage.save(list);

        WorkoutList loaded = storage.load();
        assertFalse(loaded.getWorkoutByName("push").isDone());
    }

    @Test
    void load_fileDoesNotExist_returnsEmptyWorkoutList() throws IOException,
            Storage.StorageException {
        // No save() called — file does not exist
        WorkoutList loaded = storage.load();
        assertEquals(0, loaded.numOfWorkouts());
    }

    @Test
    void save_parentDirectoriesDoNotExist_createsThemAndSaves() throws IOException,
            Storage.StorageException {
        // Use a deeply nested path that definitely does not exist
        String deepPath = tempDir.resolve("a/b/c/gitswole.txt").toString();
        Storage deepStorage = new Storage(deepPath);

        WorkoutList list = new WorkoutList();
        list.addWorkout(new Workout("push"));
        deepStorage.save(list);

        assertTrue(new File(deepPath).exists());

        WorkoutList loaded = deepStorage.load();
        assertEquals(1, loaded.numOfWorkouts());
    }

    @Test
    void saveAndLoad_workoutNameContainsDelimiter_escapedAndRestoredCorrectly()
            throws IOException, Storage.StorageException {
        WorkoutList list = new WorkoutList();
        list.addWorkout(new Workout("push | pull"));
        storage.save(list);

        WorkoutList loaded = storage.load();
        assertNotNull(loaded.getWorkoutByName("push | pull"));
    }

    @Test
    void saveAndLoad_exerciseNameContainsDelimiter_escapedAndRestoredCorrectly()
            throws IOException, Storage.StorageException {
        WorkoutList list = new WorkoutList();
        Workout w = new Workout("push");
        w.addExercise(new Exercise("bench | press", 80, 3, 10));
        list.addWorkout(w);
        storage.save(list);

        WorkoutList loaded = storage.load();
        Exercise e = loaded.getWorkoutByName("push").getExerciseList().get(0);
        assertEquals("bench | press", e.getExerciseName());
    }

    @Test
    void load_missingFinalSeparator_lastWorkoutStillLoaded() throws IOException,
            Storage.StorageException {
        // Write a valid file but omit the final "---"
        writeRawFile(
                "WORKOUT | push | false\n" +
                        "EXERCISE | bench press | 80 | 3 | 10\n"
        );

        WorkoutList loaded = storage.load();
        assertEquals(1, loaded.numOfWorkouts());
        assertNotNull(loaded.getWorkoutByName("push"));
    }

    @Test
    void load_fileWithEmptyLines_emptyLinesIgnored() throws IOException,
            Storage.StorageException {
        writeRawFile(
                "\n" +
                        "WORKOUT | push | false\n" +
                        "\n" +
                        "EXERCISE | bench press | 80 | 3 | 10\n" +
                        "\n" +
                        "---\n" +
                        "\n"
        );

        WorkoutList loaded = storage.load();
        assertEquals(1, loaded.numOfWorkouts());
    }

    @Test
    void load_unrecognisedTag_throwsStorageException() throws IOException {
        writeRawFile("BADTAG | something | false\n---\n");

        assertThrows(Storage.StorageException.class, () -> storage.load());
    }

    @Test
    void load_exerciseOutsideWorkoutBlock_throwsStorageException() throws IOException {
        // EXERCISE line appears before any WORKOUT line
        writeRawFile("EXERCISE | bench press | 80 | 3 | 10\n---\n");

        assertThrows(Storage.StorageException.class, () -> storage.load());
    }

    @Test
    void load_workoutLineTooFewFields_throwsStorageException() throws IOException {
        writeRawFile("WORKOUT | push\n---\n");

        assertThrows(Storage.StorageException.class, () -> storage.load());
    }

    @Test
    void load_exerciseLineTooFewFields_throwsStorageException() throws IOException {
        writeRawFile(
                "WORKOUT | push | false\n" +
                        "EXERCISE | bench press | 80\n" +
                        "---\n"
        );

        assertThrows(Storage.StorageException.class, () -> storage.load());
    }

    @Test
    void load_nonIntegerWeight_throwsStorageException() throws IOException {
        writeRawFile(
                "WORKOUT | push | false\n" +
                        "EXERCISE | bench press | abc | 3 | 10\n" +
                        "---\n"
        );

        assertThrows(Storage.StorageException.class, () -> storage.load());
    }

    @Test
    void load_nonIntegerSets_throwsStorageException() throws IOException {
        writeRawFile(
                "WORKOUT | push | false\n" +
                        "EXERCISE | bench press | 80 | three | 10\n" +
                        "---\n"
        );

        assertThrows(Storage.StorageException.class, () -> storage.load());
    }

    @Test
    void load_nonIntegerReps_throwsStorageException() throws IOException {
        writeRawFile(
                "WORKOUT | push | false\n" +
                        "EXERCISE | bench press | 80 | 3 | ten\n" +
                        "---\n"
        );

        assertThrows(Storage.StorageException.class, () -> storage.load());
    }

    @Test
    void load_invalidIsDoneField_throwsStorageException() throws IOException {
        writeRawFile("WORKOUT | push | maybe\n---\n");

        assertThrows(Storage.StorageException.class, () -> storage.load());
    }

    @Test
    void load_corruptedLine_exceptionMessageContainsLineNumber() throws IOException {
        writeRawFile("BADTAG | something\n");

        Storage.StorageException ex = assertThrows(
                Storage.StorageException.class, () -> storage.load()
        );
        assertTrue(ex.getMessage().contains("Line 1"),
                "Expected error message to contain 'Line 1' but got: " + ex.getMessage());
    }

    @Test
    void load_corruptedExerciseLine_exceptionMessageContainsCorrectLineNumber()
            throws IOException {
        writeRawFile(
                "WORKOUT | push | false\n" +
                        "EXERCISE | bench press | 80 | 3 | 10\n" +
                        "EXERCISE | bad exercise | notAnInt | 3 | 10\n" +
                        "---\n"
        );

        Storage.StorageException ex = assertThrows(
                Storage.StorageException.class, () -> storage.load()
        );
        assertTrue(ex.getMessage().contains("Line 3"),
                "Expected error message to contain 'Line 3' but got: " + ex.getMessage());
    }
}
