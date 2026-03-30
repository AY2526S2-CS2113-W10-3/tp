# Developer Guide

## Setup Guide
### Steps
1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/AY2526S2-CS2113-W10-3/tp
2. navigate into the project directory:
   ```bash
   cd tp
3. Run the application using Gradle:
   ```bash
   ./gradlew run

## Design

The **Architecture Diagram** below gives a high-level design overview of GitSwole.

<img src="diagrams\architecture\ArchitectureDiagram.png" width="450" />

Given below is a quick overview of the main components and how they interact with each other.

#### Main components of the architecture

**`GitSwole`** (the class `GitSwole.java`) is in charge of app launch and shut down:
- At app launch, it calls `setupLogger()`, instantiates `Ui` and `Storage`, loads persisted workout data into a `WorkoutList`, then enters the main command loop via `run()`.
- At shut down (when `Command.isExit()` returns `true`), the loop exits cleanly and the application terminates.

The bulk of the app's work is done by the following four components:
- [**`UI`**](#ui-component): The UI of the App - reads user input and displays all output.
- [**`Parser`**](#parser-component): The command interpreter - translates raw user input strings into executable `Command` objects.
- [**`Command`**](#command-component): The command executor - each subclass encapsulates the logic for one specific operation.
- [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

**`Assets`** represents the in-memory data model, consisting of `WorkoutList`, `Workout`, and `Exercise`. **`Commons`** contains shared utility classes (e.g., `GitSwoleException`) used across all components.

#### How the architecture components interact with each other

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `add w/Push Day`.

<img src="diagrams/architecture/ArchitectureSequenceDiagram.png" width="574" />

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

<img src="diagrams/architecture/Ui/UiComponent.png" width="400" />

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

The following commands are currently recognised:

| Keyword | Maps to |
|---|---|
| `add` | `AddCommand` |
| `delete` | `DeleteCommand` |
| `edit` | `EditCommand` |
| `find` | `FindCommand` |
| `list` | `ListCommand` |
| `mark` / `unmark` | `MarkCommand` |
| `log` | `LogCommand` |
| `loglist` | `LogListCommand` |
| `help` | `HelpCommand` |
| `exit` | `ExitCommand` |

> **Note:** `parseValue` and `parseOptionalInt` are `public static` methods, allowing
> `Command` subclasses to reuse the same flag-parsing logic directly without re-instantiating
> a `Parser`.

<img src="diagrams/architecture/Parser/ParserComponent.png" width="500" />

---

### Command Component

**API:** `Command.java`

The `Command` component defines the contract that all executable actions must follow.
`Command` is an abstract class with a single abstract method:

```java
public abstract void execute(WorkoutList workouts, Ui ui) throws GitSwoleException;
```

Each concrete subclass encapsulates the full logic for exactly one user-facing operation.
The subclasses are:

- `AddCommand` - adds a new `Workout` or `Exercise` to the `WorkoutList`.
- `DeleteCommand` - removes a `Workout` or `Exercise` by index.
- `EditCommand` - modifies the name of an existing `Workout` or `Exercise`.
- `FindCommand` - searches for workouts by keyword.
- `ListCommand` - lists workouts at summary, workout-specific, or full-detail scope.
- `MarkCommand` - marks or unmarks a `Workout` as done.
- `LogCommand` - initialises a workout logging session or logs an individual exercise stat.
- `LogListCommand` - displays the full workout history from `HistoryStorage`.
- `HelpCommand` - displays all available commands and their formats.
- `ExitCommand` - sets `isExit = true` to signal the main loop to terminate.

The `isExit()` method is defined in the base class and returns `false` for all commands
except `ExitCommand`, which overrides it to return `true`.

<img src="diagrams/architecture/Command/CommandComponent.png" width="962" />

---

### Storage Component

**API:** `Storage.java`, `HistoryStorage.java`

The `Storage` component is responsible for persisting and loading application data
to and from plain text files on disk. It is split into two classes with distinct responsibilities:

**`Storage.java`** manages the primary workout data file. It uses a structured
pipe-delimited format:

<img src="diagrams/architecture/Storage/StorageComponent.png" width="962" />

## Implementation

{Describe the design and implementation of the product. Use UML diagrams and short code snippets where applicable.}

Workout Logging and Smart Overwriting Feature
The logging mechanism allows users to record their workout sessions and track specific exercise statistics. 
It is facilitated primarily by the LogCommand class, which interacts with the WorkoutList (in-memory state) 
and HistoryStorage (persistent state).

Implementation Details
The LogCommand class extends Command and handles two primary states based on the presence of the e/ (exercise) 
flag during parsing:

Session Initialization (log w/WORKOUT_NAME):

The command parses the target workout name and verifies its existence in the WorkoutList.

Sticky Session State: It updates the active workout name in WorkoutList via setActiveWorkoutName(). 
This improves UX by allowing subsequent exercise logs to omit the w/ flag.

Header Management: It checks HistoryStorage.hasSessionToday() before calling writeSessionHeader(). 
This ensures that multiple logs for the same workout on the same day fall under a single header rather 
than creating redundant entries.

Exercise Stat Logging (log e/EXERCISE_NAME ...):

If the w/ flag is missing, the command falls back to the "sticky" active session stored in WorkoutList.

It updates the in-memory Exercise object with the optionally provided weight, sets, and reps.

Smart Overwriting: It calls HistoryStorage.updateExerciseLog(). Instead of blindly appending a new line 
to the end of the text file, this method isolates the current day's session block and updates the 
specific exercise entry, keeping the storage file concise and clean.

Sequence Diagram Placeholder:
The sequence diagram below illustrates the interactions between LogCommand, WorkoutList, and 
HistoryStorage when a user executes log e/Bench Press wt/80.

``
<img src="diagrams/LogCommandSequenceDiagram.png" width="600" />

Design Considerations
Dependency Injection for Storage: The LogCommand includes an overloaded constructor 
that accepts a HistoryStorage instance.

Why it is implemented this way: This allows the command to manage its own specific storage needs 
without modifying the global execute(WorkoutList, Ui) signature used by all other commands. It also makes 
LogCommand highly testable, as a mock storage class can be injected during unit testing.

Alternatives Considered:

Alternative 1: Append-only logging. Every log command simply appends a new line to the history file.

Pros: Significantly easier to implement file I/O.

Cons: Fails to handle typos well. If a user logs 80kg instead of 90kg and re-enters the command, 
both entries are saved, leading to corrupted data tracking and file bloat. 
Smart overwriting was chosen to maintain data integrity.

Tiered Listing Scope Feature
The listing enhancement allows users to view their data at three different granularities 
(summary, workout-specific, and global) without needing multiple, fragmented commands. 
This is driven by the ListCommand class.

Implementation Details
ListCommand extends Command and uses string matching on the parsed user input to route the 
execution flow to one of three helper methods:

handleListSummary(): Triggered by the base list command. Iterates through the WorkoutList and 
returns high-level workout names and their completion statuses.

handleListWorkout(): Triggered when the w/ flag is present. Fetches a specific Workout object and 
utilizes the Ui component to iterate through and print its inner ExerciseList.

handleListAll(): Triggered by list all. Iterates through every Workout in the WorkoutList and 
subsequently every Exercise within them, passing the full data structure to the Ui for rendering.

Sequence Diagram Placeholder:
The sequence diagram below shows the execution path and object retrieval when the 
user issues a list w/Push Day command.

``
<img src="diagrams/ListCommandSequenceDiagram.png" width="600" />

Design Considerations
Single Command Class Routing:

Why it is implemented this way: Handling all list variations within a single 
ListCommand class centralizes the read-only display logic. The alternative would be 
creating a class explosion (e.g., ListAllCommand, ListWorkoutCommand), which violates the 
DRY principle since all three operations rely on the same UI rendering methods and underlying WorkoutList structures.

---

### Edit Workout Feature

The edit feature allows users to rename an existing workout or modify the details of
a specific exercise within a workout. It is facilitated by `EditCommand`, which interacts
with `WorkoutList` (to locate the target) and `Ui` (to drive an interactive prompt for new values).

#### *How does it work?*

**Edit Workout**
> Only the workout name is changed.

<img src="docs/diagrams/commands/edit/EditWorkout.png">

**Edit Exercise**
> The workout name, exercise name, weight, sets, and reps can all be modified.

<img src="docs/diagrams/commands/edit/EditExercise.png">

#### Implementation

`EditCommand` extends `Command` and routes execution to one of two private handlers based
on the presence of the `e/` flag in the raw input string:

- `handleEditWorkout(WorkoutList, Ui)` — triggered when only the `w/` flag is present.
  Renames the target workout.
- `handleEditExercise(WorkoutList, Ui)` — triggered when both `w/` and `e/` flags are
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

The following sequence diagram shows how `edit w/Push Day e/Bench Press` is handled:

<img src="diagrams/commands/edit/EditCommand.png" width="1047" />

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

## Product scope
### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
