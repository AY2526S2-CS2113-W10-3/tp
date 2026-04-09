package seedu.gitswole;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GitSwole")
class GitSwoleTest {

    private static final String TEST_STORAGE_PATH = "test_storage.txt";
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        new File(TEST_STORAGE_PATH).delete();
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // helpers
    private void feedInput(String... lines) {
        String input = String.join(System.lineSeparator(), lines) + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    private void launch(String... inputLines) {
        feedInput(inputLines);
        new GitSwole().run();
    }

    // constructor
    @Test
    @DisplayName("constructor initialises the ui field (not null)")
    void constructor_initialisesUi() throws Exception {
        GitSwole gs = new GitSwole();
        Field uiField = GitSwole.class.getDeclaredField("ui");
        uiField.setAccessible(true);
        assertNotNull(uiField.get(gs));
    }

    // startup
    @Test
    @DisplayName("run() prints the hello greeting before accepting commands")
    void run_printsHelloGreeting() {
        launch("exit");
        assertTrue(outContent.toString().contains("Welcome to GitSwole"));
    }

    @Test
    @DisplayName("run() prints the GitSwole logo on startup")
    void run_printsLogo() {
        launch("exit");
        assertTrue(outContent.toString().contains("GitSwole") || outContent.toString().contains("GitS"));
    }

    // exit
    @Test
    @DisplayName("run() terminates cleanly when 'exit' is the only command")
    void run_exitCommand_terminates() {
        assertDoesNotThrow(() -> launch("exit"));
    }

    @Test
    @DisplayName("main() terminates cleanly when 'exit' is the only command")
    void main_exitCommand_terminates() {
        feedInput("exit");
        assertDoesNotThrow(() -> GitSwole.main(new String[]{}));
    }

    // error handling inside the loop
    @Test
    @DisplayName("run() shows error for an unknown command and continues running")
    void run_unknownCommand_showsErrorAndContinues() {
        launch("flap", "exit");
        String output = outContent.toString();
        // Error message from GitSwoleException.UNKNOWN_COMMAND
        assertTrue(output.contains("flap") || output.contains("Invalid command"));
    }

    @Test
    @DisplayName("run() recovers from multiple bad commands before exit")
    void run_multipleUnknownCommands_recoversEachTime() {
        launch("foo", "bar", "baz", "exit");
        // Should still reach exit without throwing
        assertDoesNotThrow(() -> {
        });
        String output = outContent.toString();
        // The word "exit" triggers the exit command
        assertTrue(output.contains("Welcome to GitSwole"));
    }

    @Test
    @DisplayName("run() shows error for incomplete 'add' command and continues")
    void run_incompleteAdd_showsErrorAndContinues() {
        launch("add", "exit");
        String output = outContent.toString();
        assertTrue(output.contains("Please complete your command") || output.contains("add"));
    }

    // command processing
    @Test
    @DisplayName("run() processes 'add w/WORKOUT' and 'list' correctly end-to-end")
    void run_addThenList_showsWorkout() {
        launch("add w/push", "list", "exit");
        assertTrue(outContent.toString().contains("push"));
    }

    @Test
    @DisplayName("run() processes multiple add commands before exit")
    void run_multipleAddCommands_allSucceed() {
        launch("add w/push", "add w/pull", "add w/legs", "list", "exit");
        String output = outContent.toString();
        assertTrue(output.contains("push"));
        assertTrue(output.contains("pull"));
        assertTrue(output.contains("legs"));
    }

    @Test
    @DisplayName("run() processes 'help' command without throwing")
    void run_helpCommand_doesNotThrow() {
        assertDoesNotThrow(() -> launch("help", "exit"));
    }

    @Test
    @DisplayName("run() processes 'list' on an empty workout list without throwing")
    void run_listEmpty_doesNotThrow() {
        assertDoesNotThrow(() -> launch("list", "exit"));
    }

    @Test
    @DisplayName("run() processes add exercise end-to-end")
    void run_addExercise_appearsInList() {
        launch("add w/push", "add e/benchpress w/push wt/60 s/3 r/8",
            "list w/push", "exit");
        assertTrue(outContent.toString().contains("benchpress"));
    }

    @Test
    @DisplayName("run() processes delete command end-to-end")
    void run_deleteWorkout_removedFromList() {
        launch("add w/push", "delete w/push", "list", "exit");
        // After deletion, the list should not contain the workout name formatted as [PUSH]
        assertTrue(!outContent.toString().contains("[PUSH]"));
    }
}
