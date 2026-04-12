package seedu.gitswole;

import seedu.gitswole.assets.WorkoutList;
import seedu.gitswole.command.Command;
import seedu.gitswole.exceptions.GitSwoleException;
import seedu.gitswole.parser.Parser;
import seedu.gitswole.storage.Storage;
import seedu.gitswole.ui.Ui;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.io.IOException;

/**
 * Main class for the GitSwole application.
 * Initializes core components and manages the main application loop.
 */
public class GitSwole {
    private static final Logger logger = Logger.getLogger(GitSwole.class.getName());
    private static final String STORAGE_FILE_PATH = "docs/workouts.txt";

    private final Ui ui;
    private final Storage storage;
    private WorkoutList workouts;

    /**
     * Constructs a GitSwole instance and initializes core components.
     * Loads existing workouts from storage if available.
     */
    public GitSwole() {
        this.ui = new Ui();
        this.storage = new Storage(STORAGE_FILE_PATH);
        this.workouts = loadWorkoutsStatic(this.storage, this.ui);
    }

    /**
     * Attempts to load workouts from the storage file on startup.
     * If the file does not exist yet (first run), returns an empty WorkoutList silently.
     * If the file is corrupted, warns the user and starts with an empty WorkoutList.
     *
     * @return A {@link WorkoutList} populated from disk, or a fresh empty one on failure.
     */
    private static WorkoutList loadWorkoutsStatic(Storage storage, Ui ui) {
        try {
            WorkoutList loaded = storage.load();
            logger.log(Level.INFO, "Loaded " + loaded.numOfWorkouts() + " workout from " + STORAGE_FILE_PATH);
            return loaded;
        } catch (Storage.StorageException e) {
            logger.log(Level.WARNING, "Corrupted save file: " + e.getMessage());
            ui.showError("Save file appears corrupted — starting with an empty workout list.\n" + e.getMessage());
            return new WorkoutList();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not read save file: " + e.getMessage());
            return new WorkoutList();
        }
    }

    /**
     * Attempts to save workouts to the storage file.
     * Logs and shows an error to the user if saving fails.
     */
    private void saveWorkouts() {
        try {
            storage.save(workouts);
            logger.log(Level.INFO, "Workouts saved to " + STORAGE_FILE_PATH);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not save workouts: " + e.getMessage());
            ui.showError("Warning: Could not save workouts. Your changes may not persist.\n" + e.getMessage());
        }
    }


    /**
     * Configures the application logger to write to {@code log.txt} instead of the terminal.
     */
    private static void setupLogger() {
        try {
            Logger rootLogger = Logger.getLogger("");
            for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            // add a file handler pointing to log.txt
            FileHandler fileHandler = new FileHandler("log.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);

        } catch (IOException e) {
            System.err.println("Warning: Could not set up log file. " + e.getMessage());
        }
    }

    /**
     * Starts the main application loop, reading and executing user commands
     * until an exit command is issued or stdin is closed (EOF).
     * <p>
     * All {@link GitSwoleException}s are caught and shown to the user so the loop
     * can continue. Any other unchecked exception is also caught as a safety net
     * to prevent a single bad command from terminating the entire session.
     */
    public void run() {
        Parser parser = new Parser();
        ui.helloGreeting(workouts);
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                if (fullCommand == null) {
                    logger.log(Level.INFO, "End of input stream reached — exiting loop.");
                    break;
                }
                Command c = parser.readResponse(fullCommand, workouts);
                c.execute(workouts, ui);
                saveWorkouts();
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
        setupLogger();
        logger.log(Level.INFO, "GitSwole application starting...");

        new GitSwole().run();

        logger.log(Level.INFO, "GitSwole application terminated.");
    }
}
