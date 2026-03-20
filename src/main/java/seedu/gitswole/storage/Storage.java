package seedu.gitswole.storage;

import seedu.gitswole.assets.Exercise;
import seedu.gitswole.assets.Workout;
import seedu.gitswole.assets.WorkoutList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles the loading and saving of {@link WorkoutList} data to and from a plain text file.
 *
 * <p>File format (one block per workout):
 * <pre>
 * WORKOUT | &lt;workoutName&gt; | &lt;isDone&gt;
 * EXERCISE | &lt;name&gt; | &lt;weight&gt; | &lt;sets&gt; | &lt;reps&gt;
 * EXERCISE | &lt;name&gt; | &lt;weight&gt; | &lt;sets&gt; | &lt;reps&gt;
 * ...
 * ---
 * </pre>
 * Each workout block ends with a {@code ---} separator line.
 * The {@code |} character is used as a field delimiter and must not appear in exercise/workout names.
 */
public class Storage {

    /** Delimiter used to separate fields within a line. */
    private static final String DELIMITER = " | ";

    /** Regex-safe version of the delimiter for splitting. */
    private static final String DELIMITER_REGEX = " \\| ";

    /** Separator line that marks the end of each workout block. */
    private static final String WORKOUT_SEPARATOR = "---";

    /** Prefix tag for workout header lines. */
    private static final String WORKOUT_TAG = "WORKOUT";

    /** Prefix tag for exercise entry lines. */
    private static final String EXERCISE_TAG = "EXERCISE";

    /** Path to the storage file. */
    private final String filePath;

    /**
     * Constructs a Storage instance that reads from and writes to the given file path.
     *
     * @param filePath Path to the plain text file used for persistence.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "File path must not be null or blank";
        this.filePath = filePath;
    }

    /**
     * Saves all workouts in the given {@link WorkoutList} to the storage file.
     *
     * <p>The parent directories are created automatically if they do not yet exist.
     *
     * @param workoutList The {@link WorkoutList} to persist.
     * @throws IOException If an I/O error occurs while writing.
     */
    public void save(WorkoutList workoutList) throws IOException {
        assert workoutList != null : "WorkoutList to save must not be null";

        File file = new File(filePath);
        // Create parent directories if they don't exist
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (Workout workout : workoutList.getWorkouts()) {
                writeWorkout(writer, workout);
            }
        }
    }

    /**
     * Writes a single workout (header + exercises + separator) to the writer.
     *
     * @param writer  The {@link FileWriter} to write to.
     * @param workout The {@link Workout} to serialise.
     * @throws IOException If an I/O error occurs.
     */
    private void writeWorkout(FileWriter writer, Workout workout) throws IOException {
        // WORKOUT | <name> | <isDone>
        writer.write(WORKOUT_TAG
                + DELIMITER + escape(workout.getWorkoutName())
                + DELIMITER + workout.isDone()
                + System.lineSeparator());

        // EXERCISE | <name> | <weight> | <sets> | <reps>
        for (Exercise exercise : workout.getExerciseList()) {
            writer.write(EXERCISE_TAG
                    + DELIMITER + escape(exercise.getExerciseName())
                    + DELIMITER + exercise.getWeight()
                    + DELIMITER + exercise.getSets()
                    + DELIMITER + exercise.getReps()
                    + System.lineSeparator());
        }

        // Separator
        writer.write(WORKOUT_SEPARATOR + System.lineSeparator());
    }

    /**
     * Loads workouts from the storage file and returns them as a {@link WorkoutList}.
     *
     * <p>If the file does not exist, an empty {@link WorkoutList} is returned and
     * no exception is thrown — this is the expected behaviour on first run.
     *
     * @return A {@link WorkoutList} populated with the persisted data, or an empty
     *         list if the file does not exist.
     * @throws IOException          If an I/O error occurs while reading.
     * @throws StorageException     If the file contains a line that cannot be parsed.
     */
    public WorkoutList load() throws IOException, StorageException {
        WorkoutList workoutList = new WorkoutList();
        File file = new File(filePath);

        if (!file.exists()) {
            return workoutList;
        }

        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            Workout currentWorkout = null;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;

                if (line.isEmpty()) {
                    continue;
                }

                if (line.equals(WORKOUT_SEPARATOR)) {
                    // Finish the current workout block
                    if (currentWorkout != null) {
                        workoutList.addWorkout(currentWorkout);
                        currentWorkout = null;
                    }
                    continue;
                }

                String[] parts = line.split(DELIMITER_REGEX);

                if (parts[0].equals(WORKOUT_TAG)) {
                    currentWorkout = parseWorkoutLine(parts, lineNumber);

                } else if (parts[0].equals(EXERCISE_TAG)) {
                    if (currentWorkout == null) {
                        throw new StorageException(
                                "Line " + lineNumber + ": EXERCISE entry found outside of a WORKOUT block.");
                    }
                    Exercise exercise = parseExerciseLine(parts, lineNumber);
                    currentWorkout.addExercise(exercise);

                } else {
                    throw new StorageException(
                            "Line " + lineNumber + ": Unrecognised tag \"" + parts[0] + "\".");
                }
            }

            // Handle file that is missing a final separator
            if (currentWorkout != null) {
                workoutList.addWorkout(currentWorkout);
            }
        }

        return workoutList;
    }

    /**
     * Parses a WORKOUT header line into a {@link Workout} object.
     *
     * <p>Expected format: {@code WORKOUT | <name> | <isDone>}
     *
     * @param parts      The line split by the delimiter.
     * @param lineNumber The line number in the file (for error messages).
     * @return The parsed {@link Workout}.
     * @throws StorageException If the line does not have the expected number of fields
     *                          or the {@code isDone} field is not a valid boolean.
     */
    private Workout parseWorkoutLine(String[] parts, int lineNumber) throws StorageException {
        if (parts.length < 3) {
            throw new StorageException(
                    "Line " + lineNumber + ": WORKOUT line must have 3 fields, found " + parts.length + ".");
        }
        String name = unescape(parts[1].trim());
        boolean isDone = parseBoolean(parts[2].trim(), lineNumber);
        Workout workout = new Workout(name);
        workout.markDone(isDone);
        return workout;
    }

    /**
     * Parses an EXERCISE line into an {@link Exercise} object.
     *
     * <p>Expected format: {@code EXERCISE | <name> | <weight> | <sets> | <reps>}
     *
     * @param parts      The line split by the delimiter.
     * @param lineNumber The line number in the file (for error messages).
     * @return The parsed {@link Exercise}.
     * @throws StorageException If the line does not have the expected number of fields
     *                          or any numeric field cannot be parsed.
     */
    private Exercise parseExerciseLine(String[] parts, int lineNumber) throws StorageException {
        if (parts.length < 5) {
            throw new StorageException(
                    "Line " + lineNumber + ": EXERCISE line must have 5 fields, found " + parts.length + ".");
        }
        String name   = unescape(parts[1].trim());
        int weight    = parseInt(parts[2].trim(), "weight",  lineNumber);
        int sets      = parseInt(parts[3].trim(), "sets",    lineNumber);
        int reps      = parseInt(parts[4].trim(), "reps",    lineNumber);
        return new Exercise(name, weight, sets, reps);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Parses a string to {@code int}, throwing a {@link StorageException} on failure.
     */
    private int parseInt(String value, String fieldName, int lineNumber) throws StorageException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new StorageException(
                    "Line " + lineNumber + ": Cannot parse \"" + value + "\" as integer for field \"" + fieldName + "\".");
        }
    }

    /**
     * Parses a string to {@code boolean}, accepting only {@code "true"} or {@code "false"}
     * (case-insensitive). Throws a {@link StorageException} for any other value.
     */
    private boolean parseBoolean(String value, int lineNumber) throws StorageException {
        if (value.equalsIgnoreCase("true"))  return true;
        if (value.equalsIgnoreCase("false")) return false;
        throw new StorageException(
                "Line " + lineNumber + ": Cannot parse \"" + value + "\" as boolean for isDone field.");
    }

    /**
     * Escapes the delimiter within a field value so the file format stays unambiguous.
     * Replaces {@code " | "} with {@code " {PIPE} "}.
     */
    private String escape(String value) {
        return value.replace(" | ", " {PIPE} ");
    }

    /**
     * Reverses {@link #escape(String)}.
     */
    private String unescape(String value) {
        return value.replace(" {PIPE} ", " | ");
    }

    // -------------------------------------------------------------------------
    // Inner exception class
    // -------------------------------------------------------------------------

    /**
     * Thrown when the storage file contains data that cannot be parsed.
     */
    public static class StorageException extends Exception {
        /**
         * Constructs a StorageException with the given detail message.
         *
         * @param message A human-readable description of the parse error.
         */
        public StorageException(String message) {
            super(message);
        }
    }
}
