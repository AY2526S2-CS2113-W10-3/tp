# GitSwole Project Portfolio Page (Karthikeyan Vetrivel)

## Project: GitSwole

GitSwole provides fast, CLI-based workout tracking for gym-goers who dislike slow GUI apps and manual logs.
Single-command entry enables efficient logging of exercises, sets, and weights, with instant access to workout history
and progress tracking optimized for keyboard-comfortable users.
It is written in Java, and has about 3000 LoC.

Given below are my contributions to the project.

---

### New Feature: Core Application Architecture

* **What it does:** Establishes the foundational structure of the application - the `GitSwole` main class,
  the abstract `Command` base class, the `Parser`, the `Ui`, and the `Assets` layer (`WorkoutList`, `Workout`, `Exercise`).
* **Justification:** A clean separation of tasks from the start allows each team member to independently
  implement commands. The abstract `Command` class enforces a consistent interface (`execute`, `isExit`) that every
  command must implement, making the codebase scalable and predictable.
* **Highlights:**
    * The `GitSwole` main class manages the application lifecycle:
      * setup, the main read-execute loop,
      * load-on-startup, and 
      * save-on-command → behind a clean `run()` entry point.
    * `Parser` exposes `readResponse`, which delegates to `parseCommand` to validate the command keyword
      and resolve it to a `CommandType` via a `HashMap<String, CommandType>`.
      The `HashMap` allows O(1) keyword lookup and avoids fragile positional-array indexing.
      Once resolved, the corresponding `Command` subclass is constructed and returned.
    * `Ui` decouples all terminal I/O from command logic, making commands independently testable.
    * The `Assets` layer (`WorkoutList`, `Workout`, `Exercise`) models the domain cleanly, with
      lookup methods such as `getWorkoutByName` and `getExerciseByName` that are used across multiple commands.

---

### New Feature: Workout and Exercise Editing (`EditCommand`)

* **What it does:** Allows users to modify existing workouts and exercises in-place via a flag-based
  single-line input. Supports two modes:
    * `edit w/WORKOUT_NAME` - renames an existing workout session.
    * `edit w/WORKOUT_NAME e/EXERCISE_NAME` - edits any combination of workout name, exercise name,
      weight, sets, and reps in a single command (e.g. `wn/LegDay en/Squat wt/120 s/4 r/8`).
* **Justification:** Users frequently need to update their training plans and might often encounter typos. A destructive
  delete-and-re-add workflow is too troublesome, so we used in-place editing with selective field updates.
* **Highlights:**
    * Each numeric field (`wt/`, `s/`, `r/`) is independently optional - fields omitted from the edit
      line are left unchanged, avoiding unintended overwrites.
    * Invalid input such as weights, sets and reps being too large, or negative, is picked by the Command and alerts the user to retry. 
    * The command tracks a `hasChanged` flag and gives distinct feedback depending on whether any field
      was actually modified, preventing false confirmation messages.

---

### Code Contributed

[RepoSense Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=vet3whale)

---

### Project Management

* **Architectural Foundation:** Designed and implemented the end-to-end skeleton of the application
  before feature development began, unblocking all teammates to build commands independently.
* **Parser Design:** Established `parseCommand` and `readResponse`, along with the groundwork
  to parse user input and map the chosen command keyword to its corresponding `CommandType` via a `HashMap`.  
  `HashMap` allows easier implementation of the addition of new features. 

---

### Enhancements to Existing Features

* **Robust Error Handling in `EditCommand`:** Throws descriptive `GitSwoleException` messages for
  missing flags, unknown workout names, and unknown exercise names, with usage hints embedded in
  the error output.
    * *Example:* `edit w/` with no name produces `Missing name of workout. Usage: edit w/WORKOUT_NAME or edit w/WORKOUT_NAME e/EXERCISE`.

---

### Documentation

#### User Guide
* Added documentation for `Feature 1: Help Feature`
* Added documentation for `Feature 10: Exit Feature`
* Added documentation for `Feature 14: Edit Feature`

#### Developer Guide
* Documented the architecture of the core components (`GitSwole`, `Parser`, `Ui`, `Command`, `Assets`),
  including their responsibilities and interactions, with their corresponding `.puml` diagrams.
* Added implementation details for `EditCommand`, explaining the two execution paths and the
  selective field-update design.
* Created PlantUML sequence diagrams illustrating the `EditCommand` execution flow for both
  the edit-workout and edit-exercise scenarios.
* Developed Instruction Manual with an expected workflow section.

---

### Community

* Reviewed teammates PRs before merging.
* Reported bugs and suggestions for other teams in the class.
* Created Issues and Milestone tracker for ease of workflow tracking.
