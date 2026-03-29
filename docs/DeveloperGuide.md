# Developer Guide

## Setup Guide
### Steps
1. {Step one e.g., Clone the repository: git clone https://github.com/your-repo}
2. {Step two e.g., Install dependencies}
3. {Step three e.g., Run the application}
Workout Logging and Smart Overwriting Feature

## Acknowledgements

The **Architecture Diagram** below gives a high-level design overview of GitSwole.

<img src="team/architectureDiagram.png" width="450" />

Given below is a quick overview of the main components and how they interact with each other.

#### Main components of the architecture

**`GitSwole`** (the class `GitSwole.java`) is in charge of app launch and shut down:
- **At app launch:** It calls `setupLogger()`, instantiates `Ui` and `Storage`, loads persisted workout data into a `WorkoutList`, then enters the main command loop via `run()`.
- **At shut down:** When `Command.isExit()` returns `true`, the loop exits cleanly, and the application terminates.

The bulk of the app's work is done by the following four components:
- [**`UI`**](#ui-component): The user interface — responsible for reading raw user input and displaying all formatted output back to the terminal.
- [**`Parser`**](#parser-component): The command interpreter — handles complex string processing and flag extraction (e.g., `w/`, `e/`) to translate raw user input into executable `Command` objects.
- [**`Command`**](#command-component): The command executor — each subclass encapsulates the specific business logic for one operation (e.g., `AddCommand`, `DeleteCommand`).
- [**`Storage`**](#storage-component): The data persistence layer — manages file I/O operations for `workouts.txt` (templates) and `history.txt` (session logs).

**`Assets`** represents the in-memory data model, consisting of `WorkoutList`, `Workout`, and `Exercise`. **`Commons`** contains shared utility classes (e.g., `GitSwoleException`) used across all components.

#### How the architecture components interact with each other

The *Sequence Diagram* below shows how the components interact with each other for the scenario 
where the user issues the command `add w/Push Day`.

<img src="team/architectureSD.png" width="574" />

Each of the four main components:
- defines its API through a well-scoped class boundary.
- implements its functionality using a concrete class that can be substituted or tested independently.

## Design & implementation

The design of GitSwole follows a modular architecture inspired by the N-tier pattern, specifically tailored for a 
CLI-based CRUD application. The system is divided into four primary logic components: `UI`, `Parser`, `Command`, 
and `Storage`. These components interact with a central `Assets` data model to perform operations. The application is 
designed to be extensible, allowing new commands and storage formats to be added with minimal friction by extending the 
base `Command` class and utilizing dedicated storage handlers.

### Praveen's enhancement

This enhancement introduces a robust workout logging and history tracking system, along with a multi-tiered listing 
mechanism. It is composed of the `ListCommand`, `LogCommand`, `LogListCommand`, `HistoryStorage` classes, and the 
`history.txt` persistent file.

#### 1. Tiered Listing Feature (`ListCommand`)
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

#### 2. Smart Workout Logging (`LogCommand`)
The logging system allows users to record their real-time performance (weight, sets, reps) and persistent session data.

* **Implementation:**
    - `LogCommand` manages active sessions. It supports a "sticky" session state where the application remembers the last workout logged (via `setActiveWorkoutName`), allowing users to log multiple exercises without re-typing the workout name.

* **Design Considerations:**
    - **Why it is implemented this way:** The "sticky session" was implemented to improve User Experience (UX) in a CLI environment, reducing the number of keystrokes required during a workout.
    - **Alternatives considered:** Requiring the `w/` flag for every single exercise log. This was deemed too tedious for users who are actively training.

#### 3. Persistent History Storage (`HistoryStorage` & `history.txt`)
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

#### 4. Sequence Diagrams

The following sequence diagram illustrates how the `ListCommand` determines the scope of the listing and interacts with the `WorkoutList` and `Ui` components:

<img src="team/listSD.png" width="600" />

This sequence diagram shows the execution flow of the `LogCommand`, highlighting the "sticky session" logic and the interaction with `HistoryStorage`:

<img src="team/logSD.png" width="600" />

The following diagram details the internal "Smart Overwriting" mechanism within `HistoryStorage`:

<img src="team/historystorageSD.png" width="600" />


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
Performance:
Security:
Maintainability:
Portability:
{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
