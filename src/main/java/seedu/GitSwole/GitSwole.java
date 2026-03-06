package seedu.GitSwole;

import seedu.GitSwole.assets.WorkoutList;
import seedu.GitSwole.command.Command;
import seedu.GitSwole.exceptions.GitSwoleException;
import seedu.GitSwole.parser.Parser;
import seedu.GitSwole.ui.Ui;

/**
 * Main class for the GitSwole application.
 * Initializes core components and manages the main application loop.
 */
public class GitSwole {
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
        new GitSwole();
        run();
    }
}