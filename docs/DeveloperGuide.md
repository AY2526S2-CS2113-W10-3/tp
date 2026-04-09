# Developer Guide
## Table of Contents
1. [Acknowledgements](#Acknowledgements)
2. [Setup Guide](#setup-guide)
3. [Design](#design)
    - [UI Component](#ui-component)
    - [Parser Component](#parser-component)
    - [Command Component](#command-component)
    - [Storage Component](#storage-component)
4. [Implementation](#implementation)
    - [Add Workout and Exercise Feature (`AddCommand`)](#add-workout-and-exercise-feature-addcommand)
    - [Storage Feature](#storage-feature)
    - [Delete Feature (`DeleteCommand`)](#delete-feature-deletecommand)
    - [Edit Workout and Exercise Feature (`EditCommand`)](#edit-workout-and-exercise-feature-editcommand)
    - [Keyword-Based Find Feature (`FindCommand`)](#keyword-based-find-feature-findcommand)
    - [Tiered Listing Feature (`ListCommand`)](#tiered-listing-feature-listcommand)
    - [Mark and Unmark Workout Feature (`MarkCommand`)](#mark-and-unmark-workout-feature-markcommand)
    - [Smart Workout Logging (`LogCommand`)](#smart-workout-logging-logcommand)
    - [Persistent History Storage (`HistoryStorage`)](#persistent-history-storage-historystorage--historytxt)
    - [History Retrieval (`LogList`)](#history-retrieval-loglist)
    - [Help Command (`HelpCommand`)](#help-command-helpcommand)
    - [Exit Command (`ExitCommand`)](#exit-command-exitcommand)
5. [Product Scope](#product-scope)
    - [Target User Profile](#target-user-profile)
    - [Value Proposition](#value-proposition)
6. [User Stories](#user-stories)
7. [Non-Functional Requirements](#non-functional-requirements)
8. [Glossary](#glossary)
9. [Instructions for Manual Testing](#instructions-for-manual-testing)

## Acknowledgements

* [JUnit 5](https://junit.org/junit5/) - Used for unit testing across all components.
* [Checkstyle](https://checkstyle.sourceforge.io/) - Used to enforce coding standards and formatting.
* [AddressBook-Level3](https://se-education.org/addressbook-level3/) - Project structure, N-tier architecture design, and specific `Parser` command-handling patterns were heavily inspired by and adapted from the se-edu initiative.
* [TA ASCII Art Generator](https://patorjk.com/software/taag/) - Used to generate the GitSwole terminal startup logo.
* [Baeldung: Java FileWriter](https://www.baeldung.com/java-write-to-file) - Adapted code snippets from this guide for our `HistoryStorage` smart-overwriting logic.

## Setup Guide

### Prerequisites
* **JDK 17** (use the exact version) — verify with `java -version`
* **IntelliJ IDEA** (update to the most recent version)
* **Gradle** — the project ships with the Gradle wrapper (`gradlew`), so a
  separate Gradle installation is not required

### Getting the Source Code
1. Fork the repository on GitHub if you intend to contribute.
2. Clone your fork to your local machine:
```bash
git clone https://github.com/AY2526S2-CS2113-W10-3/tp
```
3. Navigate into the project directory:
```bash
cd tp
```

### Setting Up the IDE (IntelliJ IDEA — Recommended)
1. Open IntelliJ IDEA and choose **Open**, then select the root `tp/` folder.
2. If prompted, select **Import Gradle Project** and let IntelliJ resolve dependencies.
3. Ensure **IntelliJ JDK 17 is defined as an SDK**, as described [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk) — this step is not needed if you have used JDK 17 in a previous IntelliJ project:
   `File → Project Structure → Project → SDK`
4. Enable annotation processing:
   `Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable`
5. Install the **Checkstyle-IDEA** plugin and point it to `config/checkstyle/checkstyle.xml`
   to get inline style warnings as you code:
   `Settings → Tools → Checkstyle → Configuration File`

### Verifying the Build
Run the following from the project root to confirm everything compiles and all
tests pass before making any changes:
```bash
./gradlew check        # runs Checkstyle + all JUnit tests
./gradlew run          # launches the app
```
A successful setup will show `BUILD SUCCESSFUL` with no test failures.

## Design

The **Architecture Diagram** below gives a high-level design overview of GitSwole.

<img src="diagrams/architecture/architectureDiag.png" width="450" />

Given below is a quick overview of the main components and how they interact with each other.

### Main components of the architecture

**`GitSwole`** (the class `GitSwole.java`) is in charge of app launch and shut down:
- **At app launch:** It calls `setupLogger()`, instantiates `Ui` and `Storage`, loads persisted workout data into a `WorkoutList`, then enters the main command loop via `run()`.
- **At shut down:** When `Command.isExit()` returns `true`, the loop exits cleanly, and the application terminates.

The bulk of the app's work is done by the following four components:
- [**`UI`**](#ui-component): The user interface - responsible for reading raw user input and displaying all formatted output back to the terminal.
- [**`Parser`**](#parser-component): The command interpreter - handles complex string processing and flag extraction (e.g., `w/`, `e/`) to translate raw user input into executable `Command` objects.
- [**`Command`**](#command-component): The command executor - each subclass encapsulates the specific business logic for one operation (e.g., `AddCommand`, `DeleteCommand`).
- [**`Storage`**](#storage-component): The data persistence layer - manages file I/O operations for `workouts.txt` (templates) and `history.txt` (session logs).

**`Assets`** represents the in-memory data model, consisting of `WorkoutList`, `Workout`, and `Exercise`. **`Commons`** contains shared utility classes (e.g., `GitSwoleException`) used across all components.

### How the architecture components interact with each other

The *Sequence Diagram* below shows how the components interact with each other for the scenario 
where the user issues the command `add w/Push Day`.

<img src="diagrams/architecture/ArchitectureSequenceDiagram.png" width="648" />

Each of the four main components:
- defines its API through a well-scoped class boundary.
- implements its functionality using a concrete class that can be substituted or tested independently.

### UI Component

**API:** `Ui.java`

The `Ui` component handles all interaction with the user - it reads raw input and
renders all output to the console. It has no knowledge of business logic or the data model.

It exposes the following key operations:
- `helloGreeting(WorkoutList)` - renders the startup banner, progress snapshot, and tier status.
- `byeGreeting()` - renders a goodbye message when the application terminates
- `readCommand()` - reads a single line of input from `System.in`.
- `showMessage(String)` - prints any general output line.
- `showError(String)` - prints a formatted error message wrapped in separator lines.
- `printWorkouts(ArrayList<Workout>)` - iterates through and prints all workouts and their exercises.
- `printWorkout(Workout)` - prints a single workout and its exercise list.
- `showLine()` - prints a horizontal separator for visual clarity.

> **Note:** `Ui` provides an overloaded constructor `Ui(InputStream in)` used exclusively
> for testing, allowing simulated input to be injected without modifying the production code path.

<img src="diagrams/architecture/Ui/UiComponent.png" width="573" />

---

### Parser Component

**API:** `Parser.java`

The `Parser` component receives the raw input string from `GitSwole` and maps it to the
correct `Command` subclass. It uses an internal `HashMap<String, CommandType>` to perform
O(1) keyword lookups, avoiding long if-else chains.

It exposes the following key operations:
- `readResponse(String, WorkoutList)` - the main entry point; parses the full input string
  and returns a ready-to-execute `Command` object.
- `parseValue(String, String)` *(static)* - extracts the value of a named flag
  (e.g. `w/`, `e/`, `wt/`) from the input string using regex boundary detection.
- `parseOptionalInt(String, String, int)` *(static)* - extracts an optional integer flag
  value, returning a default if the flag is absent or malformed.

`Parser` resolves each recognised keyword to its corresponding `Command` subclass - the
full keyword-to-command mapping is documented in the [Command Component](#command-component)
below.

> **Note:** `parseValue` and `parseOptionalInt` are `public static` methods, allowing
> `Command` subclasses to reuse the same flag-parsing logic directly without re-instantiating
> a `Parser`.

<img src="diagrams/architecture/Parser/ParserComponent.png" width="1171" />

---

### Command Component

**API:** `Command.java`

The `Command` component defines the contract that all executable actions must follow.
`Command` is an abstract class with a single abstract method:

```java
public abstract void execute(WorkoutList workouts, Ui ui) throws GitSwoleException;
```

Each concrete subclass encapsulates the full logic for exactly one user-facing operation.
`Parser` resolves user input keywords to these subclasses as follows:

| Keyword           | Command Subclass                                                             | Responsibility                                                           |
|-------------------|------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| `add`             | [`AddCommand`](#add-workout-and-exercise-feature-addcommand)                 | Adds a new `Workout` or `Exercise` to the `WorkoutList`                  |
| `delete`          | [`DeleteCommand`](#delete-feature-deletecommand)                             | Removes a `Workout` or `Exercise` by index                               |
| `edit`            | [`EditCommand`](#edit-workout-and-exercise-feature-editcommand)              | Modifies the name or fields of an existing `Workout` or `Exercise`       |
| `find`            | [`FindCommand`](#keyword-based-find-feature-findcommand)                     | Searches for workouts by keyword                                         |
| `list`            | [`ListCommand`](#tiered-listing-feature-listcommand)                         | Lists workouts at summary, workout-specific, or full-detail scope        |
| `mark` / `unmark` | [`MarkCommand`](#mark-and-unmark-workout-feature-markcommand)                | Marks or unmarks a `Workout` as done                                     |
| `log`             | [`LogCommand`](#smart-workout-logging-logcommand)                            | Initialises a logging session or logs an individual exercise stat        |
| `loglist`         | [`LogListCommand`](#history-retrieval-loglist)                               | Displays the full workout history from `HistoryStorage`                  |
| `help`            | [`HelpCommand`](#help-command-helpcommand)                                   | Displays all available commands and their formats                        |
| `exit`            | [`ExitCommand`](#exit-command-exitcommand)                                   | Sets `isExit = true` to signal the main loop to terminate                |  

The `isExit()` method is defined in the base class and returns `false` for all commands
except `ExitCommand`, which overrides it to return `true`.

<img src="diagrams/architecture/Command/CommandComponent.png" width="1528" />

---

### Storage Component

**API:** `Storage.java`, `HistoryStorage.java`

The `Storage` component is responsible for persisting and loading application data
to and from plain text files on disk. It is split into two classes with distinct responsibilities:

**`Storage.java`** manages the primary workout data file. It uses a structured
pipe-delimited format:

<img src="diagrams/architecture/Storage/StorageComponent.png" width="950" />

## Implementation

The design of GitSwole follows a modular architecture inspired by the N-tier pattern, specifically tailored for a
CLI-based CRUD application.

The four primary components - `Ui`, `Parser`, `Command`, and `Storage` - described in the
[Design section](#design) above work together to implement the features below. Each feature
is owned by a dedicated `Command` subclass that is instantiated by `Parser`, executed by
`GitSwole`, and backed by `Storage` where persistence is required. The sections below 
document the implementation details, design considerations, and sequence diagrams for each feature.

---

### Add Workout and Exercise Feature (`AddCommand`)

The add feature is the entry point for building a user's workout library. 
It is facilitated by `AddCommand`, which interacts with `WorkoutList` (to store data) and `Ui` 
(to confirm the result to the user).

**How it works:** It supports two operations depending on the flags provided:

- `add w/WORKOUT` - creates a new named workout session in the `WorkoutList`
- `add e/EXERCISE w/WORKOUT wt/WEIGHT s/SETS r/REPS` - appends a new exercise with its stats to an existing workout

> **Note:** User must call command to add Workout first, before adding a new Exercise.

**Examples:**
```
add w/Push Day
add e/Bench Press w/Push Day wt/80 s/3 r/10
```

#### Architecture and Component Level Design

When a user adds a new workout or exercise, the following process occurs:

1. **Parser:** Reads the raw input, extracts the command word `add`, and returns a new `AddCommand(response)` with the full raw string passed as the argument.

2. **AddCommand:** The main loop calls `AddCommand#execute()`, which checks for the presence of the `e/` flag to determine whether the user is adding a workout or an exercise.

3. **WorkoutList:** The command delegates to either `WorkoutList#addWorkout()` or `WorkoutList#addExercise()`. Adding a workout creates a new `Workout` object; adding an exercise calls `WorkoutList#getWorkoutByName()` to locate the target, then appends a new `Exercise` object to it. A `GitSwoleException` is thrown if the target workout does not exist.

4. **Storage:** `GitSwole` calls `Storage#saveWorkouts()` after the command executes successfully to persist the new data immediately.

5. **Ui:** The result is reported back via `Ui#showMessage()`.

#### Sequence Diagram

<img src="diagrams/commands/add/AddCommandSD.png" width="481"/>

---

### Storage Feature

The `Storage` class saves and loads the data from `WorkoutList` through a plaintext file on the hardware memory. When the application is started and run, the previous data is immediately loaded into the application.

Each workout block consists of:

1. A `WORKOUT` header line with the workout name and completion status
2. Zero or more `EXERCISE` lines with name, weight, sets, and reps
3. A `---` separator marking the end of the block


#### Architecture and Component Level Design

1. **GitSwole:** Calls `loadWorkouts()` on startup to populate the `WorkoutList` before the main loop begins. Calls `saveWorkouts()` after every command that mutates the workout data.

2. **WorkoutList / Workout / Exercise:** The `Storage` class reads directly from these classes to get the output.

3. **File system:** `Storage` uses a `FileWriter` to write and a `Scanner` library to read from the plain text file `workouts.txt`. Parent directories are created automatically if they do not exist.

#### Sequence Diagrams

**Save:**

<img src="diagrams/architecture/Storage/StorageSave-Sequence_Diagram__Storage_save_WorkoutList_.png" width="950" />

**Load:**

<img src="diagrams/architecture/Storage/StorageLoad-Sequence_Diagram__Storage_load__.png" width="742" />

---

### Delete Feature (`DeleteCommand`)

The delete mechanism is facilitated by the `DeleteCommand.java` class. It extends from the abstract class `Command` and overrides the `execute()` method, which throws the exception `GitSwoleException`, to execute the deletion of workouts/exercises.

**How it works:** It receives 2 types of commands: one that deletes the workout only, and one that deletes the workout and the exercise of that workout.

- `delete w/WORKOUT` - removes the entire named workout session
- `delete e/EXERCISE w/WORKOUT` - removes a specific exercise from that workout

**Examples:**
```
delete e/bench press w/pushday
delete w/pushday
```

#### Architecture and Component Level Design

When the user types in a command like the one shown above, it goes through the following process:

1. **Parser:** Reads the raw input (e.g. `delete e/bench press w/pushday`), extracts the command word `delete`, and returns a new `DeleteCommand(response)` with the full raw string passed as the argument. No index parsing occurs - the entire string is forwarded as-is.

2. **DeleteCommand:** The main loop calls `DeleteCommand#execute()`, which inspects the raw string for the presence of `e/` and `w/` flags to determine which operation to perform.

3. **WorkoutList:** The command delegates to either `WorkoutList#removeWorkout()` or `WorkoutList#removeExercise()`, both of which perform case-insensitive name matching to locate and remove the target entry.

4. **Ui:** The result (success or not found) is reported back to the user via `Ui#showMessage()`.

#### Design Considerations
**Alternative 1 (Considered): Delete by list index**

The user specifies the target by its position number in the list (e.g. `delete 1`).

- **Pros:** Much faster typing, as you do not need to type in the flags, and if you know the index of the workout that you want to delete.
- **Cons:** The user must first list the workouts in the list, then find their workout index, which might take an even longer time. Therefore, we decided to stick with the current implementation of using flags in our command.


#### Sequence Diagram

This diagram shows the sequence in which the delete command is entered.

<img src="diagrams/commands/delete/deleteSD-Sequence_Diagram__DeleteCommand.png" width="950" />

---

### Edit Workout and Exercise Feature (`EditCommand`)

The edit feature allows users to rename an existing workout or modify the details of
a specific exercise within a workout. It is facilitated by `EditCommand`, which interacts
with `WorkoutList` (to locate the target) and `Ui` (to drive an interactive prompt for new values).

#### How it works

**Edit Workout**
> Only the workout name is changed.

```
Input:  edit w/push
Prompt: Edit fields (e.g. wn/NewName):
Input:  wn/Push Day
Output: Change Recorded! Edited Workout:
        Push Day | Exercises: ...
```
**Edit Exercise**
> The workout name, exercise name, weight, sets, and reps can all be modified.

```
Input:  edit w/Push Day e/Bench Press
Prompt: Edit fields (e.g. wn/NewWorkout en/NewExercise wt/100 s/3 r/10):
Input:  wt/90 s/4 r/8
Output: Change Recorded! Edited Workout:
        Push Day
        Bench Press | Weight: 90kg | Sets: 4 | Reps: 8
```
#### Architecture and Component Level Design

`EditCommand` extends `Command` and routes execution to one of two private handlers based
on the presence of the `e/` flag in the raw input string:

- `handleEditWorkout(WorkoutList, Ui)` - triggered when only the `w/` flag is present.
  Renames the target workout.
- `handleEditExercise(WorkoutList, Ui)` - triggered when both `w/` and `e/` flags are
  present. Edits the fields of a specific exercise within the target workout.

Given below is an example usage scenario for `edit w/Push Day e/Bench Press` and how
`EditCommand` behaves at each step.

**Step 1.** The user executes `edit w/Push Day e/Bench Press`. `Parser` creates an
`EditCommand` with the full input string and returns it to `GitSwole`.

**Step 2.** `GitSwole` calls `EditCommand#execute(workouts, ui)`. Since the input contains
`e/`, execution is routed to `handleEditExercise()`.

**Step 3.** `handleEditExercise()` calls `Parser.parseValue()` to extract the workout name
(`Push Day`) and exercise name (`Bench Press`). It calls `WorkoutList#getWorkoutByName()`
to retrieve the `Workout` object, then `Workout#getExerciseByName()` to retrieve the
`Exercise` object. A `GitSwoleException` is thrown if either is not found.

**Step 4.** The current workout and exercise details are printed via `Ui#printExercise()`.
`Ui#readLine()` is called to collect the user's edit input in the format
`wn/NewWorkout en/NewExercise wt/100 s/3 r/10`. Fields not provided are left unchanged.

**Step 5.** `applyExerciseEdits()` parses the edit line using `Parser.parseValue()` for
each supported flag (`wn/`, `en/`, `wt/`, `s/`, `r/`) and applies any non-null, non-empty
values to the target objects. The internal `hasChanged` flag is set to `true` for any
field that is modified.

**Step 6.** `printUpdatedWorkout()` checks `hasChanged`. If `true`, it calls
`Ui#printWorkout()` to show the updated workout. Otherwise, it notifies the user that
no changes were recorded.

#### Design Considerations

**Aspect: How edit input is collected**

- **Alternative 1 (current choice):** Collect all edit fields in a single follow-up
  prompt after displaying the current state.
    - Pros: Familiar UX pattern (show-then-edit). Users can see the current values
      before deciding what to change.
    - Cons: Requires a second `readLine()` call mid-execution, making the control flow
      less uniform compared to other commands.
- **Alternative 2:** Multiple `readLine()` commands to get each change one-by-one.
    - Pros: Step-by-step guidance and easy to follow, especially for new users.
    - Cons: Longer process and seasoned user would be more comfortable typing all changes in one line.
      (e.g: `wn/push en/bench wt/100 s/3 r/10`)


#### Sequence Diagram
**Overview:**  
<img src="diagrams/commands/edit/EditCommand.png" width="300"/>

**Edit Workout:**  
<img src="diagrams/commands/edit/EditWorkoutCommand.png" width="700"/>

**Edit Exercise:**  
<img src="diagrams/commands/edit/EditExerciseCommand.png" width="900"/>

---

### Keyword-Based Find Feature (`FindCommand`)

The find mechanism allows users to search their data to two levels of extent - across all workouts, or within
a specific workout's exercise list.

**Implementation:**
* `FindCommand` extends the base `Command` class and overrides `execute()`. It uses flag detection on the raw input
  string to route execution to one of two helper methods:
* `handleFindWorkout()`: Triggered by `find w/WORKOUT`. Scans all entries in `WorkoutList` via
  `WorkoutList#getWorkouts()` using case-insensitive keyword matching, then displays each match's name and
  exercise count.
* `handleFindExercise()`: Triggered by `find e/EXERCISE w/WORKOUT`. First calls
  `WorkoutList#getWorkoutByName()` to pin down the target workout, then iterates its exercise list for matches,
  displaying name, weight, sets, and reps per result.
*   In both cases, results are surfaced through `Ui#showMessage()`. If no matches are found, a "Not Found" message
    is displayed.

**Design Considerations:**

**Why it is implemented this way:** Centralising both search variants within a single `FindCommand` class
keeps the flag-routing logic cohesive and avoids complicating the command hierarchy. The two-level search
(workout vs. exercise) mirrors the natural hierarchy of the data model, making the feature easy to use.

**Alternatives considered:** Creating separate `FindWorkoutCommand` and `FindExerciseCommand` classes. This
was rejected as it would complicate the parser and duplicate the shared flag-parsing and result-display logic.

**Sequence Diagram:**

The following sequence diagram illustrates how `FindCommand` determines the search scope and interacts with
`WorkoutList` and `Ui`:

<img src="diagrams/commands/find/FindCommand.png" width="700" />

---

### Tiered Listing Feature (`ListCommand`)
The listing enhancement allows users to view their data at three different granularities without needing multiple,
fragmented commands.

* **Implementation:**
  `ListCommand` extends the base `Command` class. It uses string matching on the parsed user input to route execution
  to one of three helper methods:
    - `handleListSummary()`: Triggered by `list`. Iterates through `WorkoutList` to show names and completion status.
    - `handleListWorkout()`: Triggered by `list w/`. Fetches a specific `Workout` and displays its nested `Exercise` list.
    - `handleListAll()`: Triggered by `list all`. Performs a deep iteration across all workouts and their exercises.

* **Design Considerations:**
    - **Why it is implemented this way:** Handling all list variations within a single `ListCommand` class centralizes
      the read-only display logic. It prevents "class explosion" and adheres to the DRY principle by reusing UI rendering methods.
    - **Alternatives considered:** Creating separate commands like `ListSummaryCommand` and `ListAllCommand`. This was
      rejected as it would clutter the parser logic and make the codebase harder to maintain.

#### Sequence Diagrams

The following sequence diagram illustrates how the `ListCommand` determines the scope of the listing and interacts with the `WorkoutList` and `Ui` components:

<img src="diagrams/commands/list/listSD.png" width="646" />

---

### Mark and Unmark Workout Feature (`MarkCommand`)

The mark feature lets users track their weekly training progress by flagging workouts as done or not done. At a glance, users can see which workouts they have completed and which ones they still have left — without needing to remember manually.

**How it works:** It supports two operations:

- `mark w/WORKOUT` — marks the named workout as done
- `unmark w/WORKOUT` — marks the named workout as not done

**Examples:**
```
mark w/Push Day
unmark w/Push Day
```


The completion status is reflected immediately when running `list`, which displays a done/not done indicator alongside each workout name, giving users a clear overview of what remains for the week.

#### Architecture and Component Level Design

1. **Parser:** Reads the raw input, extracts the command word `mark` or `unmark`, and returns a new `MarkCommand(response)` with the full raw string as the argument.
2. **MarkCommand:** The main loop calls `MarkCommand#execute()`, which parses the `w/` flag to identify the target workout and sets its completion status to `true` (mark) or `false` (unmark).
3. **WorkoutList:** The command calls `WorkoutList#getWorkoutByName()` to locate the target. A `GitSwoleException` is thrown if the workout does not exist.
4. **Storage:** `GitSwole` calls `Storage#saveWorkouts()` after execution to persist the updated completion status.
5. **Ui:** The result is confirmed to the user via `Ui#showMessage()`.

#### Sequence Diagrams

The following sequence diagram illustrates how the `MarkCommand` works:

<img src="diagrams/commands/mark/MarkCommandSD.png" width="522" />


---

### Smart Workout Logging (`LogCommand`)
The logging system allows users to record their real-time performance (weight, sets, reps) and persistent session data.

* **Implementation:**
    - `LogCommand` manages active sessions. It supports a "sticky" session state where the application remembers the last workout logged (via `setActiveWorkoutName`), allowing users to log multiple exercises without re-typing the workout name.

* **Design Considerations:**
    - **Why it is implemented this way:** The "sticky session" was implemented to improve User Experience (UX) in a CLI environment, reducing the number of keystrokes required during a workout.
    - **Alternatives considered:** Requiring the `w/` flag for every single exercise log. This was deemed too tedious for users who are actively training.

#### Sequence Diagrams
This sequence diagram shows the execution flow of the `LogCommand`, highlighting the "sticky session" logic and the interaction with `HistoryStorage`:

<img src="diagrams/commands/log/logSD.png" width="886" />

---

### Persistent History Storage (`HistoryStorage` & `history.txt`)
Unlike the primary `workouts.txt` which stores the current "template" or "routine", `history.txt` stores an 
immutable (but updatable for corrections) log of every completed session.

* **Implementation:**
    `HistoryStorage` implements a "Smart Overwriting" mechanism. When a user logs an exercise:
    1. It identifies the session block for the current date.
    2. It searches for the specific exercise entry within that block.
    3. If found, it updates the stats and remarks in-place instead of appending a new line.
    4. If not found, it appends the new entry to the end of the today's session block.

* **History File Format (`history.txt`):**
    The file uses a human-readable format with date headers and dashed separators:
    ```
    [29-03-2026, 14:30] PUSH DAY workout
    Bench Press       :   80kg |  3 sets | 10 reps
      Remark: Felt heavy today
    --------------------------------------------
    ```

* **Design Considerations:**
    - **Why it is implemented this way:** Smart overwriting was chosen to maintain data integrity and file cleanliness. 
If a user makes a typo and re-logs the same exercise, the previous entry is corrected rather than duplicated.
    - **Alternatives considered:** Append-only logging. While easier to implement, it leads to "data bloat" and 
makes it difficult for users to correct mistakes.

#### Sequence Diagrams

The following diagram details the internal "Smart Overwriting" mechanism within `HistoryStorage`:

<img src="diagrams/architecture/Storage/historystorageSD.png" width="600" />

---

### History Retrieval (`LogList`)

#### Implementation

The `LogList` mechanism is centered around the `LogListCommand` class. It acts as the logic controller that bridges the `HistoryStorage` component and the `Ui` component.

##### Implementation Details: LogList Filter Logic
The `LogListCommand` utilizes a strategy-based approach to filter history. Depending on the flags detected in the user's raw input string, it routes the request to different specialized methods within `HistoryStorage`. This prevents the command class from becoming bloated with file-parsing logic.

*   **Level 1 (Architecture):** The `LogListCommand` acts as a high-level controller. It requests a `List<String>` of formatted entries from the `Storage` layer and passes that list directly to the `UI` layer for rendering. This maintains a strict separation of concerns.
*   **Level 2 (Component):** Inside the `execute()` method, the command performs flag detection:
    *   If the `d/` flag is detected: It extracts the date string and calls `historyStorage.getEntriesByDate(date)`.
    *   If the `w/` flag is detected: It extracts the workout name and calls `historyStorage.getEntriesByWorkout(name)`.
    *   Otherwise: It defaults to `historyStorage.getAllEntries()` to show the complete history.

##### Design Decision: Dependency Injection for Testing
A key design choice was the implementation of an overloaded constructor:
`LogListCommand(String response, HistoryStorage historyStorage)`.

This allows for **Dependency Injection**. In the production environment, the app uses the default constructor which initializes a real file-linked `HistoryStorage`. During testing, the JUnit suite injects a `HistoryStorageStub`. This ensures that unit tests are:
1.  **Isolated:** Tests do not fail if the actual `history.txt` file is missing or corrupted.
2.  **Deterministic:** The test always receives the exact same mock data, making it reliable across different developer machines.

#### Design Considerations

**Aspect: Data Retrieval Strategy**

*   **Alternative 1 (Current Choice): On-demand File Reading.**
    *   **Pros:** Minimal memory footprint. The application does not need to store years of history in RAM; it only reads the file when the user explicitly asks for it.
    *   **Cons:** Incurs a small File I/O overhead each time the command is run.
    *   **Reason for Choosing:** Since `loglist` is a diagnostic command rather than a high-frequency entry command (like `add`), the trade-off of a few milliseconds of disk access for significantly lower RAM usage is optimal for a CLI tool.


#### Sequence Diagram

The diagram below shows how the components interact when a user requests to see their workout history.

<img src="diagrams/commands/loglist/loglistSD.png" width="681" />

---

### Help Command (`HelpCommand`)

The help feature displays a formatted reference of all available commands and their usage syntax directly in the 
terminal.

* **Implementation:**
  `HelpCommand` extends the base `Command` class. It stores all command descriptions in a 2D `String` array, where each
  row contains a command's syntax, its corresponding description, and an example.
* On `execute()`, it iterates through the array and renders each row through `Ui#showMessage()`.

* **Design Considerations:**
    - **Why it is implemented this way:** Using a 2D array loop instead of hardcoded individual print statements makes
      it trivial to add or update command entries - only the array data needs to change, not the rendering logic.
    - **Alternatives considered:** A series of individual `Ui#showMessage()` calls, one per command. This was rejected
      as it scatters the command reference data across multiple lines and makes maintenance error-prone.

**Sequence Diagram:**  
<img src="diagrams/commands/help/HelpCommand.png" width="600"/>

---

### Exit Command (`ExitCommand`)

The exit feature cleanly terminates the application loop and displays a goodbye message.

* **Implementation:**
  `ExitCommand` extends the base `Command` class and overrides `isExit()` to return `true`. On `execute()`, it calls
  `Ui#byeGreeting()` to display the farewell message. The main loop in `GitSwole` checks `Command#isExit()` after every
  command execution and breaks out of the loop when `true` is returned, triggering a clean shutdown.

* **Design Considerations:**
    - **Why it is implemented this way:** Encoding the exit signal as an override of `isExit()` in the base `Command`
      class keeps the main loop uniform, every iteration checks the same method regardless of which command ran,
      with no special-casing needed for the exit path.
    - **Alternatives considered:** Throwing a dedicated `ExitException` to break out of the loop within `run()`.
      This was rejected because using exceptions for control flow is considered bad practice, as exceptions should
      signal unexpected errors, not a normal user-initiated shutdown. Declaring `ExitCommand` as a subclass of
      `Command` keeps the exit path uniform with every other command, requiring no special-casing in the main loop.

**Sequence Diagram:**  
<img src="diagrams/commands/exit/ExitCommand.png" width="600"/>

---

## Product Scope

### Target User Profile

* Prefers typing over using a mouse.
* Is comfortable with command-line interfaces.
* Needs a fast and intuitive way to manage their workout logging.
* Can type fast and prefers keyboard shortcuts.

### Value Proposition

GitSwole enables fitness-focused CLI users to manage, log, and track workouts entirely from the terminal - faster and with less friction than any GUI-based alternative.  

---

## User Stories

| Version | As a ...                                                    | I want to ...                                                                      | So that I can ...                                                                    |
|---------|-------------------------------------------------------------|------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| v1.0    | new user                                                    | see all available commands and their usage syntax                                  | quickly learn how to use the application without referring to external docs          |
| v1.0    | gym-goer planning my first workout                          | add workouts and exercises with their weight, sets, and reps                       | build and keep track of my training routine, so that I would have a plan when I go to the gym |
| v1.0    | user who just finished their gym session                    | quickly log my workout with a simple command                                       | record my progress without spending so much time typing                              |
| v1.0    | user who wants to seeriously use the app to manage workouts | clear sample or preloaded data witih one command                                   | start afresh with the workouts that I want to use                                    |
| v1.0    | first time user who is confused about how to use the app    | have a list of the commands I can type into the CLI                                | get started quickly without reading lengthy documentation                            |
| v2.0    | methodical user                                             | edit the name or details of an existing workout or exercise                        | correct mistakes or update my training plan without deleting and re-adding           |
| v2.0    | neat user who wants to keep track of completed workouts     | mark the workout and exercise as done                                              | so that i can visually confirm my progress                                           |
| v2.0    | committed fitspo                                            | view all my past logged workout sessions in a single command                       | identify trends or gaps in my routine and plan my next workout better                |
| v2.0    | reflective gym-goer                                         | log remarks for each exercise during a session                                     | remember how each session felt and track qualitative progress over time              |
| v2.0    | busy student                                                | see a summary of total workout duration and total volume after finishing a session | I can feel productive before heading back to study                                   |


---

## Non-Functional Requirements

* **Environment**: Should work on any mainstream OS (Windows, Linux, macOS) as long as JDK 17 or above is installed.
* **Performance**: The system should respond to user inputs within 100ms to ensure a seamless typing experience.
* **Data Integrity**: Data should be saved automatically to a local text file after every mutating command (add, delete, mark)
to prevent data loss during unexpected closures.
*  **Usability**: A user with above-average typing speed for regular English text (i.e., not necessarily code) should be able 
to accomplish tasks faster than using a mouse in a GUI.

---

## Glossary

| Term | Meaning |
|------|---------|
| **Workout Session** | A named training session that groups related exercises together (e.g. "Push Day", "Legs"). A workout session acts as a folder — you create it first, then add exercises into it. |
| **Exercise** | A specific movement within a workout session (e.g. "Bench Press", "Deadlift"). Each exercise stores its own weight, sets, and reps. An exercise always belongs to exactly one workout session. |
| **Log / Log Entry** | A timestamped record of your actual performance for an exercise during a particular session. Logs are saved to your history and can be reviewed later with `loglist`. |
| **Remark** | A free-text note attached to a log entry (e.g. "Felt strong today", "Lower back tight"). |
| **Template** | Your saved workout sessions and their exercises serve as reusable templates — the baseline plan you log against each time you train. |
| **CLI** | Command Line Interface. A text-based user interface used to interact with the software. |
| **Index** | The 1-based numerical position of a task as currently displayed in the list. |
| **Flag** | A prefix marker in a command string (e.g. `w/`, `e/`, `wt/`, `s/`, `r/`) used by the Parser to extract specific values from the user's input. |
| **Sticky Session** | A UX shortcut where the application remembers the last workout name used in a `log` command, so subsequent exercise logs do not require re-typing the `w/` flag. |
| **Smart Overwriting** | The mechanism used by `HistoryStorage` to update an existing log entry in-place (rather than appending a duplicate) when the same exercise is re-logged on the same date. |

> **In short:** GitSwole uses a two-level hierarchy — **Workout Sessions** contain **Exercises**. When you train, you **log** your performance against that template, building a chronological history you can review anytime.

---

## Instructions for Manual Testing

### Help

1. Enter `help`.
2. Expected output: A formatted table listing all available commands, their syntax, and examples.

---

### Storage

1. Add a workout and exercise:
   ```
   add w/push
   add e/bench press w/push wt/80 s/3 r/10
   ```
2. Enter `exit` to close GitSwole.
3. Relaunch the application.
4. Enter `list` , `list all` or `list w/WORKOUT` - you should see the previously added workouts and exercises.

---
### List

1. Add multiple workouts and exercises:  
    ```
    add w/push  
    add w/pull  
    add e/bench press w/push wt/80 s/3 r/10
    add e/pull up w/pull wt/0 s/4 r/8
    ```
2. List all workouts: ```list```  
**Expected output:** Both `push` and `pull` workouts are shown.

3. List all workouts and their exercises: ```list all```  
**Expected output:** Both workouts are shown with their respective exercises listed beneath.

4. List exercises under a specific workout: ```list w/push```  
**Expected output:** Only `bench press` is shown under `push`.

---

### Delete

1. Add a workout and exercise:
   ```
   add w/push
   add e/bench press w/push wt/80 s/3 r/10
   ```
2. Confirm the workout and exercise exist:
   ```
   list w/push
   ```
3. Delete the exercise:
   ```
   delete e/bench press w/push
   ```
4. Expected output: `Successfully deleted 'bench press' from 'push'!`
5. Confirm removal - `list w/push` should now return an empty exercise list.

---

### Edit
1. Add a workout and exercise:
    ```   
    add w/push
    add e/bench press w/push wt/80 s/3 r/10
    ```
2. Rename the workout:
    ```   
    edit w/push
    ```
3. Rename the exercise:
    ```   
    edit w/push e/bench press
    ```
   > Here it can be `push` or a new workout name from **Step 2**.

---

### Log
1. Set up a workout with exercises:
    ```   
    add w/push
    add e/bench press w/push wt/80 s/3 r/10
    add e/shoulder press w/push wt/50 s/3 r/12
    ```
2. Start a logging session for the workout:
   ```
    log w/push
   ```  
   **Expected output**: Session started for `push`.
3. Log an exercise within the session:
    ```   
    log e/bench press
    ```  
   **Expected output:** Stats updated for `bench press` in `push`.
4. Log an exercise with a remark:  
   ```
   log e/shoulder press remark/felt strong today
   ```
   **Expected output:** Stats updated for `shoulder press`. Remark felt strong today is saved.

---

### LogList
1. Ensure at least one prior logging session exists (complete the Log section above first).
2. View all past logged sessions:
    ```
    loglist
    ```
    **Expected output:** A chronological list of all logged workout sessions, showing the workout name, date/time, exercises logged, and any remarks.
3. View logs for a specific workout:
    ```
    loglist w/push
    ```
    **Expected output:** Only sessions logged under push are displayed.

---

### Find
1. Add workouts and exercises with varied names:
    ```
    add w/push
    add w/pull
    add e/bench press w/push wt/80 s/3 r/10
    add e/bent over row w/pull wt/60 s/3 r/10
    add e/bicep curl w/pull wt/20 s/3 r/12
    ```
2. Search for exercises by keyword:
    ```   
    find e/bench w/push
    ```
   **Expected output:** ``bench press`` is listed as a match.
3. Search for a keyword that matches multiple entries:
    ```   
    find e/b w/push
    ```
   **Expected output:** `bench press`, `bent over row`, and `bicep curl` are all listed.
4. Search for a keyword with no matches:
    ```   
    find e/squat w/legs
    ```
   **Expected output:** A message indicating no matching exercises were found.

---

### Expected Workflow
Following are the expected results from the Manual Testing:

#### Welcome page:
```
____________________________________________________________________________________________________
|     ______      __   _____                   __
|    / ____/ (_)_/ /_ / ___/__   ___  __ ___  / /  _______   
|   / / __  / //_ __/ \_ \_ \ \  | | / / __ \/ /  / /__/ /  
|  / /_/ / / / / /_ ___/  /  \ \ / |/ / /_/ / /__/ _____/
|  \____/ /_/ \__/ \_____/    \__/|__/\____/____/\_____/    
|                                       
| Welcome to GitSwole! (@w@)/
| First time using it? Type 'help' to see what ya got!
| v2.0 | 31 Mar 2026
____________________________________________________________________________________________________
| PROGRESS SNAPSHOT
| Workouts logged  : 0
| Workouts done    : 0 / 0
| Total exercises  : 0
____________________________________________________________________________________________________
____________________________________________________________________________________________________
| Daily quote:"Your body can stand almost anything. It's your mind you have to convince."
____________________________________________________________________________________________________
```
#### Add:
```
add w/push
Successfully added a push session! Remember to add your exercises :)
____________________________________________________________________________________________________
add e/bench press w/push wt/80 s/3 r/10
Your exercise has been successfully added! Looking swole g
____________________________________________________________________________________________________
list
Your Workouts:
1. [ ]push
____________________________________________________________________________________________________
add w/pull  
Successfully added a pull session! Remember to add your exercises :)
____________________________________________________________________________________________________
add e/pull up w/pull wt/0 s/4 r/8
Your exercise has been successfully added! Looking swole g
____________________________________________________________________________________________________
```
#### List:
```
list
Your Workouts:
1. [ ]push
2. [ ]pull
____________________________________________________________________________________________________
list w/push
[ ] PUSH Workout Exercises:
1. bench press (80kg | 3s | 10r)
____________________________________________________________________________________________________
```
#### Delete:
```
delete e/bench press w/push
Successfully deleted 'bench press' from 'push'!
list w/push
[ ] PUSH Workout Exercises:
Your exercises list is currently empty :(
____________________________________________________________________________________________________
add e/bench press w/push wt/80 s/3 r/10
Your exercise has been successfully added! Looking swole g
____________________________________________________________________________________________________
```
#### Edit:
```
edit w/push
CURRENT WORKOUT: push
Enter the new values below (press enter to NOT edit):
Edit fields (e.g. wn/NewName):
wn/chest day
Change Recorded! Edited Workout:
____________________________________________________________________________________________________
[ ][CHEST DAY]
1. bench press (80kg | 3s | 10r)
____________________________________________________________________________________________________
____________________________________________________________________________________________________
edit w/chest day e/bench press
bench press
CURRENT WORKOUT: chest day
bench press (80kg | 3s | 10r)
____________________________________________________________________________________________________
Enter the new values below (press enter to NOT edit):
Edit fields (e.g. wn/NewWorkout en/NewExercise wt/100 s/3 r/10):
wn/push wt/90 s/3 r/8
Change Recorded! Edited Workout:
____________________________________________________________________________________________________
[ ][PUSH]
1. bench press (90kg | 3s | 8r)
____________________________________________________________________________________________________
```
#### Log:
```
____________________________________________________________________________________________________
add e/shoulder press w/push wt/50 s/3 r/12    
Your exercise has been successfully added! Looking swole g
____________________________________________________________________________________________________
log w/push
Session started for push! Let's get those gains.
____________________________________________________________________________________________________
PUSH Workout Exercises:
 1. bench press (90kg | 3s | 8r)
 2. shoulder press (50kg | 3s | 12r)
____________________________________________________________________________________________________
Continue to log your workout by: log e/EXERCISE wt/WEIGHT s/SETS r/REPS remark/REMARK
____________________________________________________________________________________________________
log e/bench press                         
Stats updated for bench press in push!
 1. bench press (90kg | 3s | 8r)
 2. shoulder press (50kg | 3s | 12r)
____________________________________________________________________________________________________
log e/shoulder press remark/lightweight babyy
Stats updated for shoulder press in push!
Remark added: lightweight babyy
 1. bench press (90kg | 3s | 8r)
 2. shoulder press (50kg | 3s | 12r)
____________________________________________________________________________________________________
```
#### Log List:
```
loglist
=== COMPLETE LOG HISTORY ===
[31-03-2026, 21:51] PUSH workout
bench press:      :   90kg |  3 sets |  8 reps
shoulder press:   :   50kg |  3 sets | 12 reps
  Remark: lightweight babyy
____________________________________________________________________________________________________
loglist w/push
=== LOG HISTORY FOR: PUSH ===
[31-03-2026, 21:51] PUSH workout
bench press:      :   90kg |  3 sets |  8 reps
shoulder press:   :   50kg |  3 sets | 12 reps
  Remark: lightweight babyy
____________________________________________________________________________________________________
```

#### Find:
```
find e/bench w/push
bench press | Weight: 90kg | Sets: 3 | Reps: 8
____________________________________________________________________________________________________
find e/squat w/legs
____________________________________________________________________________________________________
Workout does not exist. Try again...
____________________________________________________________________________________________________
```
#### Exit:
```
exit
____________________________________________________________________________________________________
Bye! Keep getting swole!
____________________________________________________________________________________________________
```
