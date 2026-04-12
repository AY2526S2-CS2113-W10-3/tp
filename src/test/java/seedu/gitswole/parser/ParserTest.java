package seedu.gitswole.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.command.AddCommand;
import seedu.gitswole.command.DeleteCommand;
import seedu.gitswole.command.ExitCommand;
import seedu.gitswole.command.FindCommand;
import seedu.gitswole.command.HelpCommand;
import seedu.gitswole.command.ListCommand;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.command.MarkCommand;
import seedu.gitswole.command.EditCommand;
import seedu.gitswole.command.LogCommand;
import seedu.gitswole.command.LogListCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
//@@author vet3whale
@DisplayName("Parser")
class ParserTest {

    private Parser parser;
    private WorkoutList workouts;

    @BeforeEach
    void setUp() {
        parser = new Parser();
        workouts = new WorkoutList();
    }

    // parseValue
    @Test
    @DisplayName("parseValue extracts value after prefix")
    void parseValue_extractsValue() {
        assertEquals("push", Parser.parseValue("add w/push", "w/"));
    }

    @Test
    @DisplayName("parseValue returns null when prefix is absent")
    void parseValue_missingPrefix_returnsNull() {
        assertNull(Parser.parseValue("add e/squat", "w/"));
    }

    @Test
    @DisplayName("parseValue stops at the next space")
    void parseValue_stopsAtSpace() {
        assertEquals("push", Parser.parseValue("add w/push e/bench", "w/"));
    }

    @Test
    @DisplayName("parseValue returns rest of string when no trailing space")
    void parseValue_noTrailingSpace() {
        assertEquals("pull", Parser.parseValue("list w/pull", "w/"));
    }

    // parseOptionalInt
    @Test
    @DisplayName("parseOptionalInt extracts integer correctly")
    void parseOptionalInt_validInt() {
        assertEquals(60, Parser.parseOptionalInt("add e/bench wt/60 s/3 r/8", "wt/", 0));
    }

    @Test
    @DisplayName("parseOptionalInt returns default when prefix is missing")
    void parseOptionalInt_missingPrefix_returnsDefault() {
        assertEquals(0, Parser.parseOptionalInt("add e/bench w/push", "wt/", 0));
    }

    @Test
    @DisplayName("parseOptionalInt returns default on non-numeric value")
    void parseOptionalInt_nonNumeric_returnsDefault() {
        assertEquals(0, Parser.parseOptionalInt("add e/bench wt/abc", "wt/", 0));
    }

    // readResponse
    @Test
    @DisplayName("readResponse returns ExitCommand for 'exit'")
    void readResponse_exit() throws GitSwoleException {
        assertInstanceOf(ExitCommand.class, parser.readResponse("exit", workouts));
    }

    @Test
    @DisplayName("readResponse returns HelpCommand for 'help'")
    void readResponse_help() throws GitSwoleException {
        assertInstanceOf(HelpCommand.class, parser.readResponse("help", workouts));
    }

    @Test
    @DisplayName("readResponse returns ListCommand for 'list'")
    void readResponse_list() throws GitSwoleException {
        assertInstanceOf(ListCommand.class, parser.readResponse("list", workouts));
    }

    @Test
    @DisplayName("readResponse returns AddCommand for 'add w/push'")
    void readResponse_add() throws GitSwoleException {
        assertInstanceOf(AddCommand.class, parser.readResponse("add w/push", workouts));
    }

    @Test
    @DisplayName("readResponse returns DeleteCommand for 'delete w/push'")
    void readResponse_delete() throws GitSwoleException {
        assertInstanceOf(DeleteCommand.class, parser.readResponse("delete w/push", workouts));
    }

    @Test
    @DisplayName("readResponse returns FindCommand for 'find push'")
    void readResponse_find() throws GitSwoleException {
        assertInstanceOf(FindCommand.class, parser.readResponse("find push", workouts));
    }

    @Test
    @DisplayName("readResponse is case-insensitive on the command word")
    void readResponse_caseInsensitive() throws GitSwoleException {
        assertInstanceOf(ExitCommand.class, parser.readResponse("EXIT", workouts));
    }

    // readResponse - error paths
    @Test
    @DisplayName("readResponse throws UNKNOWN_COMMAND for unrecognised input")
    void readResponse_unknownCommand_throws() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> parser.readResponse("flap", workouts));
        assertEquals(GitSwoleException.ErrorType.UNKNOWN_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("readResponse throws INCOMPLETE_COMMAND for 'add' with no arguments")
    void readResponse_addNoArgs_throws() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> parser.readResponse("add", workouts));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("readResponse throws INCOMPLETE_COMMAND for 'delete' with no arguments")
    void readResponse_deleteNoArgs_throws() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> parser.readResponse("delete", workouts));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("readResponse throws INCOMPLETE_COMMAND for 'find' with no arguments")
    void readResponse_findNoArgs_throws() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> parser.readResponse("find", workouts));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("readResponse returns MarkCommand for 'mark 1'")
    void readResponse_mark() throws GitSwoleException {
        assertInstanceOf(MarkCommand.class, parser.readResponse("mark 1", workouts));
    }

    @Test
    @DisplayName("readResponse returns MarkCommand for 'unmark 1'")
    void readResponse_unmark() throws GitSwoleException {
        assertInstanceOf(MarkCommand.class, parser.readResponse("unmark 1", workouts));
    }

    @Test
    @DisplayName("readResponse returns EditCommand for 'edit w/push'")
    void readResponse_edit() throws GitSwoleException {
        assertInstanceOf(EditCommand.class, parser.readResponse("edit w/push", workouts));
    }

    @Test
    @DisplayName("readResponse returns LogCommand for 'log w/push'")
    void readResponse_log() throws GitSwoleException {
        assertInstanceOf(LogCommand.class, parser.readResponse("log w/push", workouts));
    }

    @Test
    @DisplayName("readResponse returns LogListCommand for 'loglist'")
    void readResponse_loglist() throws GitSwoleException {
        assertInstanceOf(LogListCommand.class, parser.readResponse("loglist", workouts));
    }

    @Test
    @DisplayName("readResponse throws INCOMPLETE_COMMAND for empty input")
    void readResponse_emptyInput_throws() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> parser.readResponse("", workouts));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("readResponse throws INCOMPLETE_COMMAND for 'mark' with no args")
    void readResponse_markNoArgs_throws() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> parser.readResponse("mark", workouts));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("readResponse throws INCOMPLETE_COMMAND for 'edit' with no args")
    void readResponse_editNoArgs_throws() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> parser.readResponse("edit", workouts));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("readResponse throws INCOMPLETE_COMMAND for 'log' with no args")
    void readResponse_logNoArgs_throws() {
        GitSwoleException ex = assertThrows(GitSwoleException.class,
            () -> parser.readResponse("log", workouts));
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, ex.getType());
    }

    @Test
    @DisplayName("parseValue returns null for null input")
    void parseValue_nullInput_returnsNull() {
        assertNull(Parser.parseValue(null, "w/"));
    }

    @Test
    @DisplayName("parseValue returns null for null prefix")
    void parseValue_nullPrefix_returnsNull() {
        assertNull(Parser.parseValue("add w/push", null));
    }

    @Test
    @DisplayName("parseValue returns null for blank prefix")
    void parseValue_blankPrefix_returnsNull() {
        assertNull(Parser.parseValue("add w/push", "  "));
    }

    @Test
    @DisplayName("parseValue handles prefix at start of string")
    void parseValue_prefixAtStart() {
        assertEquals("push", Parser.parseValue("w/push", "w/"));
    }

    @Test
    @DisplayName("parseValue returns null when value after prefix is empty")
    void parseValue_emptyValue_returnsNull() {
        assertNull(Parser.parseValue("add w/ e/bench", "w/"));
    }

    @Test
    @DisplayName("parseValue extracts multi-word value")
    void parseValue_multiWordValue() {
        assertEquals("bench press", Parser.parseValue("add e/bench press wt/60", "e/"));
    }
}
//@@author
