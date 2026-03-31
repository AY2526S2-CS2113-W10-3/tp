package seedu.gitswole.command;

import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.ui.Ui;

import java.util.logging.Logger;

/**
 * Represents an abstract command that can be executed within the GitSwole application.
 * All concrete command types must extend this class and implement {@link #execute}.
 */
// @@author vet3whale
public abstract class Command {
    protected static final Logger LOGGER = Logger.getLogger(Command.class.getName());
    protected boolean isExit = false;

    /**
     * Executes the command, performing the appropriate action on the workout list
     * and updating the user interface accordingly.
     *
     * @param workouts The current list of workouts to operate on.
     * @param ui       The user interface used to display results and messages.
     * @throws GitSwoleException If the command cannot be completed due to invalid input or missing data.
     */
    public abstract void execute(WorkoutList workouts, Ui ui) throws GitSwoleException;

    /**
     * Returns whether this command signals the application to terminate.
     *
     * @return {@code true} if the application should exit; {@code false} otherwise.
     */
    public boolean isExit() {
        return isExit;
    }
}
// @@author
