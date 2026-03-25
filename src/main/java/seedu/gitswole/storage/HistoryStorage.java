package seedu.gitswole.storage;

import seedu.gitswole.assets.Exercise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the persistent storage of workout session history with smart overwriting.
 * <p>
 * This class identifies session blocks by date and workout name to prevent duplicates
 * and allows for updating existing exercise logs within a session.
 */
public class HistoryStorage {
    private static final String HISTORY_FILE_PATH = "docs/history.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");

    /**
     * Checks if a session for the given workout already exists for today.
     *
     * @param workoutName The name of the workout to check.
     * @return {@code true} if a session header for today exists; {@code false} otherwise.
     * @throws IOException If an I/O error occurs.
     */
    public boolean hasSessionToday(String workoutName) throws IOException {
        ensureFileExists();
        List<String> lines = Files.readAllLines(Paths.get(HISTORY_FILE_PATH));
        String today = LocalDateTime.now().format(DATE_FORMATTER);
        String headerTrigger = "[" + today;
        String workoutTrigger = workoutName.toUpperCase() + " workout";

        for (String line : lines) {
            if (line.startsWith(headerTrigger) && line.contains(workoutTrigger)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Appends a new session header to the history file.
     *
     * @param workoutName The name of the workout session.
     * @throws IOException If an I/O error occurs.
     */
    public void writeSessionHeader(String workoutName) throws IOException {
        ensureFileExists();
        String timestamp = LocalDateTime.now().format(FULL_FORMATTER);
        Path path = Paths.get(HISTORY_FILE_PATH);
        List<String> lines = new ArrayList<>(Files.readAllLines(path));
        
        if (!lines.isEmpty()) {
            lines.add("--------------------------------------------");
        }
        
        lines.add("[" + timestamp + "] " + workoutName.toUpperCase() + " workout");
        Files.write(path, lines);
    }

    /**
     * Updates or adds an exercise log in the history file for today's session.
     * <p>
     * This method coordinates the identification of the session block and applies the update.
     *
     * @param workoutName  The name of the workout session.
     * @param exercise     The {@link Exercise} object to log.
     * @param remark       An optional comment for the session.
     * @throws IOException If an I/O error occurs.
     */
    public void updateExerciseLog(String workoutName, Exercise exercise, String remark) throws IOException {
        ensureFileExists();
        Path path = Paths.get(HISTORY_FILE_PATH);
        List<String> lines = new ArrayList<>(Files.readAllLines(path));
        
        int startIndex = findSessionStartIndex(lines, workoutName);
        if (startIndex == -1) {
            return;
        }

        int endIndex = findSessionEndIndex(lines, startIndex);
        applyExerciseUpdate(lines, startIndex, endIndex, exercise, remark);
        
        Files.write(path, lines);
    }

    private int findSessionStartIndex(List<String> lines, String workoutName) {
        String today = LocalDateTime.now().format(DATE_FORMATTER);
        String trigger = "[" + today;
        String workoutHeader = workoutName.toUpperCase() + " workout";

        for (int i = lines.size() - 1; i >= 0; i--) {
            if (lines.get(i).startsWith(trigger) && lines.get(i).contains(workoutHeader)) {
                return i;
            }
        }
        return -1;
    }

    private int findSessionEndIndex(List<String> lines, int startIndex) {
        for (int i = startIndex + 1; i < lines.size(); i++) {
            if (lines.get(i).startsWith("---")) {
                return i;
            }
        }
        return lines.size();
    }

    private void applyExerciseUpdate(List<String> lines, int start, int end, Exercise exercise, String remark) {
        String exerciseKey = exercise.getExerciseName() + ":";
        String formattedLine = formatExerciseLine(exercise);
        
        for (int i = start + 1; i < end; i++) {
            if (lines.get(i).trim().startsWith(exerciseKey)) {
                updateExistingEntry(lines, i, formattedLine, remark);
                return;
            }
        }
        
        addNewEntry(lines, end, formattedLine, remark);
    }

    private void updateExistingEntry(List<String> lines, int index, String formattedLine, String remark) {
        lines.set(index, formattedLine);
        
        // Remove old remark if it exists in the next line
        if (index + 1 < lines.size() && lines.get(index + 1).trim().startsWith("Remark:")) {
            lines.remove(index + 1);
        }
        
        // Insert new remark if provided
        if (remark != null && !remark.isBlank()) {
            lines.add(index + 1, "  Remark: " + remark.trim());
        }
    }

    private void addNewEntry(List<String> lines, int insertionPoint, String formattedLine, String remark) {
        lines.add(insertionPoint, formattedLine);
        if (remark != null && !remark.isBlank()) {
            lines.add(insertionPoint + 1, "  Remark: " + remark.trim());
        }
    }

    private String formatExerciseLine(Exercise exercise) {
        String nameWithColon = exercise.getExerciseName() + ":";
        return String.format("%-18s: %4dkg | %2d sets | %2d reps",
                nameWithColon, exercise.getWeight(), 
                exercise.getSets(), exercise.getReps());
    }

    private void ensureFileExists() throws IOException {
        Path path = Paths.get(HISTORY_FILE_PATH);
        if (Files.exists(path)) {
            return;
        }
        Files.createDirectories(path.getParent());
        Files.createFile(path);
    }

    /**
     * Returns all lines from the history file.
     *
     * @return A list of all lines in the history file, or empty if none exist.
     * @throws IOException If an I/O error occurs.
     */
    public List<String> getAllEntries() throws IOException {
        ensureFileExists();
        List<String> lines = Files.readAllLines(Paths.get(HISTORY_FILE_PATH));
        return lines.isEmpty() ? new ArrayList<>() : lines;
    }

    /**
    * Returns all lines from the history file that belong to sessions matching the given workout name.
    *
    * @param workoutName The workout name to filter by.
    * @return A list of lines belonging to matching sessions.
    * @throws IOException If an I/O error occurs.
    */
    public List<String> getEntriesByWorkout(String workoutName) throws IOException {
        ensureFileExists();
        List<String> lines = Files.readAllLines(Paths.get(HISTORY_FILE_PATH));
        List<String> result = new ArrayList<>();
        String workoutTrigger = workoutName.toUpperCase() + " workout";

        boolean inMatchingSession = false;
        for (String line : lines) {
            if (line.contains(workoutTrigger)) {
                inMatchingSession = true;
            } else if (line.startsWith("---")) {
                if (inMatchingSession) {
                    result.add(line);
                }
                inMatchingSession = false;
            }

            if (inMatchingSession) {
                result.add(line);
            }
        }
        return result;
    }

    /**
    * Returns all lines from the history file that belong to sessions on the given date.
    *
    * @param date The date string to filter by (format: dd-MM-yyyy).
    * @return A list of lines belonging to sessions on that date.
    * @throws IOException If an I/O error occurs.
    */
    public List<String> getEntriesByDate(String date) throws IOException {
        ensureFileExists();
        List<String> lines = Files.readAllLines(Paths.get(HISTORY_FILE_PATH));
        List<String> result = new ArrayList<>();
        String dateTrigger = "[" + date;

        boolean inMatchingSession = false;
        for (String line : lines) {
            if (line.startsWith(dateTrigger)) {
                inMatchingSession = true;
            } else if (line.startsWith("---")) {
                if (inMatchingSession) {
                    result.add(line);
                }
                inMatchingSession = false;
            }

            if (inMatchingSession) {
                result.add(line);
            }
        }
        return result;
    }
}
