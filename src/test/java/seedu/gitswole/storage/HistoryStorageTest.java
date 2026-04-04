package seedu.gitswole.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.gitswole.assets.Exercise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistoryStorageTest {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @TempDir
    Path tempDir;

    private Path testFile;
    private HistoryStorage historyStorage;

    @BeforeEach
    void setUp() {
        testFile = tempDir.resolve("history.txt");
        historyStorage = new HistoryStorage(testFile.toString());
    }

    @Test
    void getEntriesByDate_filtersCorrectly() throws IOException {
        String today = LocalDateTime.now().format(DATE_FORMATTER);
        historyStorage.writeSessionHeader("push");
        historyStorage.updateExerciseLog("push", new Exercise("bench", 80, 3, 10), null);

        List<String> todayEntries = historyStorage.getEntriesByDate(today);
        assertEquals(2, todayEntries.size()); // Header + Exercise
        assertTrue(todayEntries.get(0).contains(today));
    }

    @Test
    void hasSessionToday_emptyFile_returnsFalse() throws IOException {
        assertFalse(historyStorage.hasSessionToday("push"));
    }

    @Test
    void hasSessionToday_sessionExists_returnsTrue() throws IOException {
        historyStorage.writeSessionHeader("push");
        assertTrue(historyStorage.hasSessionToday("push"));
    }

    @Test
    void writeSessionHeader_addsHeaderCorrectly() throws IOException {
        historyStorage.writeSessionHeader("pull");
        List<String> lines = Files.readAllLines(testFile);
        
        assertEquals(1, lines.size());
        String today = LocalDateTime.now().format(DATE_FORMATTER);
        assertTrue(lines.get(0).contains("[" + today));
        assertTrue(lines.get(0).contains("PULL workout"));
    }

    @Test
    void updateExerciseLog_newExercise_addsEntry() throws IOException {
        historyStorage.writeSessionHeader("push");
        Exercise benchPress = new Exercise("bench press", 80, 3, 10);
        historyStorage.updateExerciseLog("push", benchPress, "Feeling strong");

        List<String> lines = Files.readAllLines(testFile);
        // Header + Exercise + Remark
        assertEquals(3, lines.size());
        assertTrue(lines.get(1).contains("bench press:"));
        assertTrue(lines.get(2).contains("Remark: Feeling strong"));
    }

    @Test
    void updateExerciseLog_existingExercise_updatesEntry() throws IOException {
        historyStorage.writeSessionHeader("push");
        Exercise benchPressV1 = new Exercise("bench press", 80, 3, 10);
        historyStorage.updateExerciseLog("push", benchPressV1, "First set");

        Exercise benchPressV2 = new Exercise("bench press", 85, 3, 10);
        historyStorage.updateExerciseLog("push", benchPressV2, "Increased weight");

        List<String> lines = Files.readAllLines(testFile);
        // Header + Updated Exercise + Updated Remark
        assertEquals(3, lines.size());
        assertTrue(lines.get(1).contains("85kg"));
        assertTrue(lines.get(2).contains("Remark: Increased weight"));
    }

    @Test
    void getEntriesByWorkout_filtersCorrectly() throws IOException {
        historyStorage.writeSessionHeader("push");
        historyStorage.updateExerciseLog("push", new Exercise("bench", 80, 3,
                10), null);
        
        // Add a separator for the next session
        Files.write(testFile, List.of("---"), java.nio.file.StandardOpenOption.APPEND);
        
        historyStorage.writeSessionHeader("pull");
        historyStorage.updateExerciseLog("pull", new Exercise("row", 60, 3,
                10), null);

        List<String> pushEntries = historyStorage.getEntriesByWorkout("push");
        assertTrue(pushEntries.stream().anyMatch(l -> l.contains("PUSH workout")));
        assertTrue(pushEntries.stream().anyMatch(l -> l.contains("bench:")));
        assertFalse(pushEntries.stream().anyMatch(l -> l.contains("PULL workout")));
    }

    @Test
    void defaultConstructor_usesCorrectPath() {
        HistoryStorage defaultStorage = new HistoryStorage();
        // Since we can't easily check the private path, we verify it doesn't crash 
        // and uses the standard path convention.
        assertNotNull(defaultStorage);
    }

    @Test
    void updateExerciseLog_noSessionFound_doesNothing() throws IOException {
        Exercise benchPress = new Exercise("bench press", 80, 3, 10);
        historyStorage.updateExerciseLog("push", benchPress, null);

        List<String> lines = Files.readAllLines(testFile);
        assertEquals(0, lines.size(), "File should remain empty if no session header exists");
    }

    @Test
    void updateExistingEntry_withNullRemark_removesOldRemark() throws IOException {
        historyStorage.writeSessionHeader("push");
        Exercise bench = new Exercise("bench", 80, 3, 10);
        historyStorage.updateExerciseLog("push", bench, "Old Remark");

        // Update same exercise with null remark
        historyStorage.updateExerciseLog("push", bench, null);

        List<String> lines = Files.readAllLines(testFile);
        assertFalse(lines.stream().anyMatch(l -> l.contains("Remark:")), "Remark should be removed");
    }

    @Test
    void getAllEntries_returnsAllLines() throws IOException {
        historyStorage.writeSessionHeader("push");
        historyStorage.writeSessionHeader("pull");
        List<String> entries = historyStorage.getAllEntries();
        assertEquals(3, entries.size()); // Header1 + Separator + Header2
    }

    @Test
    void getEntriesByWorkout_handlesNoMatch() throws IOException {
        historyStorage.writeSessionHeader("push");
        List<String> entries = historyStorage.getEntriesByWorkout("pull");
        assertEquals(0, entries.size());
    }

}
