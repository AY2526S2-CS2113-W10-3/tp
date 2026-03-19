package seedu.gitswole;

import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.command.Command;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.parser.Parser;
import seedu.gitswole.ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for the GitSwole application.
 * Initializes core components and manages the main application loop.
 */
public class GitSwole {
    private static final Logger logger = Logger.getLogger(GitSwole.class.getName());
    private static Ui ui;
    private static WorkoutList workouts = new WorkoutList();
    public GitSwole() {
        ui = new Ui();
    }

    /**
     * Starts the main application loop, reading and executing user commands
     * until an exit command is issued.
     */
    public static void run() {
        Parser parser = new Parser();
        ui.helloGreeting();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command c = parser.readResponse(fullCommand, workouts);
                c.execute(workouts, ui);
                isExit = c.isExit();
            } catch (GitSwoleException e) {
                logger.log(Level.WARNING, "GitSwoleException occurred: " + e.getMessage());
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * The main entry point of the GitSwole application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        logger.log(Level.INFO, "GitSwole application starting...");
        new GitSwole();
        run();
        logger.log(Level.INFO, "GitSwole application terminated.");
    }
}
