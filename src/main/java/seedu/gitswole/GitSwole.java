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

    private static Ui ui = new Ui();
    private static WorkoutList workouts = new WorkoutList();
    private static Storage storage = new Storage(STORAGE_FILE_PATH);

    /**
     * Attempts to load workouts from the storage file on startup.
     * If the file does not exist yet (first run), returns an empty WorkoutList silently.
     * If the file is corrupted, warns the user and starts with an empty WorkoutList.
     *
     * @return A {@link WorkoutList} populated from disk, or a fresh empty one on failure.
     */
    private static WorkoutList loadWorkoutsStatic() {
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
    private static void saveWorkouts() {
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
     * <p>
     * Removes all default console handlers from the root logger to suppress terminal output,
     * then attaches a {@link java.util.logging.FileHandler} in append mode with a
     * {@link java.util.logging.SimpleFormatter} for human-readable log entries.
     * <p>
     * If the log file cannot be created or opened, a warning is printed to
     * {@code System.err} and the application continues without file logging.
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
     * until an exit command is issued.
     */
    public static void run() {
        Parser parser = new Parser();
        ui.helloGreeting(workouts);
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
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

        workouts = loadWorkoutsStatic();
        run();

        logger.log(Level.INFO, "GitSwole application terminated.");
    }
}
