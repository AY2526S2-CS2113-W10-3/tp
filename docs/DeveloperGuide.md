# Developer Guide

## Acknowledgements

The **Architecture Diagram** below gives a high-level design overview of GitSwole.

<img src="diagrams/ArchitectureDiagram.png" width="450" />

Given below is a quick overview of the main components and how they interact with each other.

#### Main components of the architecture

**`GitSwole`** (the class `GitSwole.java`) is in charge of app launch and shut down:
- At app launch, it calls `setupLogger()`, instantiates `Ui` and `Storage`, loads persisted workout data into a `WorkoutList`, then enters the main command loop via `run()`.
- At shut down (when `Command.isExit()` returns `true`), the loop exits cleanly and the application terminates.

The bulk of the app's work is done by the following four components:
- [**`UI`**](#ui-component): The UI of the App — reads user input and displays all output.
- [**`Parser`**](#parser-component): The command interpreter — translates raw user input strings into executable `Command` objects.
- [**`Command`**](#command-component): The command executor — each subclass encapsulates the logic for one specific operation.
- [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

**`Assets`** represents the in-memory data model, consisting of `WorkoutList`, `Workout`, and `Exercise`. **`Commons`** contains shared utility classes (e.g., `GitSwoleException`) used across all components.

#### How the architecture components interact with each other

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `add w/Push Day`.

<img src="diagrams/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components:
- defines its API through a well-scoped class boundary.
- implements its functionality using a concrete class that can be substituted or tested independently.

## Design & implementation

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
