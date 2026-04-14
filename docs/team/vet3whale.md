# GitSwole Project Portfolio Page (Karthikeyan Vetrivel)

## Project: GitSwole

GitSwole provides fast, CLI-based workout tracking for gym-goers who dislike slow GUI apps and manual logs.
Single-command entry enables efficient logging of exercises, sets, and weights, with instant access to workout history
and progress tracking optimized for keyboard-comfortable users.
It is written in Java, and has about 3000 LoC.

Given below are my contributions to the project.

---

### New Feature: Core Application Architecture

**What it does:** Sets up the foundational classes - `GitSwole`, `Command`, `Parser`, `Ui`, and the assets layer 
(`WorkoutList`, `Workout`, `Exercise`).

**Justification:** Clean separation of concerns lets team members implement commands independently. 
The abstract `Command` class enforces a consistent `execute`/`isExit` interface across all commands.

**Highlights:**
- `GitSwole` manages the full lifecycle: setup, read-execute loop, load-on-startup, and save-on-command.
- `Parser` resolves command keywords to `CommandType` via a `HashMap` (O(1) lookup), then constructs and 
returns the appropriate `Command` subclass.
- `Ui` centralises terminal I/O, keeping commands independently testable.
- The assets layer exposes shared lookup methods (`getWorkoutByName`, `getExerciseByName`) used across commands.  

---

### New Feature: Workout and Exercise Editing (`EditCommand`)

**What it does:** Edits existing workouts and exercises in-place via flag-based input. Supports two modes:
- `edit w/WORKOUT_NAME` — renames a workout.
- `edit w/WORKOUT_NAME e/EXERCISE_NAME` — edits any combination of workout name, exercise name, weight, sets, and reps (e.g. `wn/LegDay en/Squat wt/120 s/4 r/8`).

**Justification:** A delete-and-re-add workflow is too disruptive for routine updates and typo fixes, so in-place editing with selective field updates was used instead.

**Highlights:**
- Numeric fields (`wt/`, `s/`, `r/`) are independently optional — omitted fields are left unchanged.
- Invalid values (negative, out-of-range) are caught and the user is prompted to retry.
- A `hasChanged` flag tracks whether any field was actually modified, preventing false confirmation messages.  

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

### Community

* Reviewed teammates PRs before merging.
* Reported bugs and suggestions for other teams in the class.
* Created Issues and Milestone tracker for ease of workflow tracking.

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
