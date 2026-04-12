package seedu.gitswole.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GitSwoleException")
class GitSwoleExceptionTest {

    @Test
    @DisplayName("INCOMPLETE_COMMAND message contains command and prompt")
    void incompleteCommand_message() {
        GitSwoleException e = new GitSwoleException(
            GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "add w/push");
        assertTrue(e.getMessage().contains("add w/push"));
        assertTrue(e.getMessage().contains("Please complete your command"));
    }

    @Test
    @DisplayName("UNKNOWN_COMMAND message contains the bad command word")
    void unknownCommand_message() {
        GitSwoleException e = new GitSwoleException(
            GitSwoleException.ErrorType.UNKNOWN_COMMAND, "foobar");
        assertTrue(e.getMessage().contains("foobar"));
        assertTrue(e.getMessage().contains("Invalid command"));
    }

    @Test
    @DisplayName("IDX_OUTOFBOUNDS message mentions workout not existing")
    void idxOutOfBounds_message() {
        GitSwoleException e = new GitSwoleException(
            GitSwoleException.ErrorType.IDX_OUTOFBOUNDS, "push");
        assertTrue(e.getMessage().contains("Workout does not exist"));
    }

    @Test
    @DisplayName("DEFAULT error message returns the command string directly")
    void defaultError_message() {
        GitSwoleException e = new GitSwoleException(
            GitSwoleException.ErrorType.DEFAULT, "praveen was here");
        assertEquals("praveen was here", e.getMessage());
    }

    @Test
    @DisplayName("getType returns the correct ErrorType")
    void getType_returnsCorrectType() {
        GitSwoleException e = new GitSwoleException(
            GitSwoleException.ErrorType.INCOMPLETE_COMMAND, "test");
        assertEquals(GitSwoleException.ErrorType.INCOMPLETE_COMMAND, e.getType());
    }

    @Test
    @DisplayName("getCommand returns the original command string")
    void getCommand_returnsCommand() {
        GitSwoleException e = new GitSwoleException(
            GitSwoleException.ErrorType.UNKNOWN_COMMAND, "foo");
        assertEquals("foo", e.getCommand());
    }
}
