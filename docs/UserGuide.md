# GitSwole User Guide

GitSwole provides fast, CLI-based workout tracking for gym-goers who dislike slow GUI apps and manual logs. Single-command entry enables efficient logging of exercises, sets, and weights, with instant access to workout history and progress tracking optimized for keyboard-comfortable users.

---

## Table of Contents

- [Quick Start](#quick-start)
- [Features](#features)
    - [Feature 1: Help](#feature-1-help)
    - [Feature 2: Add Workout Session](#feature-2-add-workout-session)
    - [Feature 3: Add an Exercise](#feature-3-add-an-exercise)
    - [Feature 4: Delete Workout Session](#feature-4-delete-workout-session)
    - [Feature 5: Delete an Exercise](#feature-5-delete-an-exercise)
    - [Feature 6: List Workout Summary](#feature-6-list-workout-summary)
    - [Feature 7: List Workout Exercises](#feature-7-list-workout-exercises)
    - [Feature 8: List All Data](#feature-8-list-all-data)
    - [Feature 9: Find a Workout](#feature-9-find-a-workout)
    - [Feature 10: Find an Exercise](#feature-10-find-an-exercise)
    - [Feature 11: Exit](#feature-11-exit)
    - [Feature 12: Mark](#feature-12-mark)
    - [Feature 13: Storage](#feature-13-storage)
    - [Feature 14: Date](#feature-14-date)
    - [Feature 15: Edit](#feature-15-edit)
    - [Feature 16: Remarks](#feature-16-remarks)
    - [Feature 17: Log Workout Session](#feature-17-log-workout-session)
    - [Feature 18: Log Exercise Stats](#feature-18-log-exercise-stats)
    - [Feature 19: History Storage](#feature-19-history-storage)
    - [Feature 20: Log List](#feature-20-log-list)
- [FAQ](#faq)
- [Known Issues](#known-issues)
- [Command Summary](#command-summary)

---

## Quick Start

1. Ensure Java 11 or above is installed on your machine.
2. Download the latest `gitswole.jar` from the [releases page](https://github.com/AY2526S2-CS2113-W10-3/tp/releases).
3. Open a terminal and navigate to the folder containing the jar file.
4. Run the application with:
```
   java -jar gitswole.jar
```
5. Type `help` to see all available commands.
6. Refer to the [Features](#features) section below for the full command list.

---

## Features

### Feature 1: Help

**Purpose:** Shows a message explaining the use of available commands.

**Format:**
```
help
```

**Example:**
```
Input:  help
Output: Refer to the user guide: https://xxxxxxxxx.com
```

---

### Feature 2: Add Workout Session

**Purpose:** Adds a workout to the list of workouts.

**Format:**
```
add w/WORKOUT
```

**Example:**
```
Input:  add w/push
Output: Successfully added a Push Session! Remember to add your exercises :)
```

---

### Feature 3: Add an Exercise

**Purpose:** Adds an exercise to the list of exercises under a workout and initialises the weights, sets, and repetitions per set to default or specified values.

**Format:**
```
add e/EXERCISE w/WORKOUT [wt/WEIGHT] [s/SET] [r/REPETITION]
```

**Example:**
```
Input:  add e/benchpress w/push
        add e/benchpress w/push wt/40 s/3 r/8
Output: Your exercise has been successfully added! Looking swole g
```

---

### Feature 4: Delete Workout Session

**Purpose:** Deletes a workout from the list of workouts.

**Format:**
```
delete w/WORKOUT
```

**Example:**
```
Input:  delete w/push
Output: Successfully deleted a Push Session!
```

---

### Feature 5: Delete an Exercise

**Purpose:** Deletes an exercise from the list of exercises under a workout.

**Format:**
```
delete e/EXERCISE w/WORKOUT [wt/WEIGHT] [s/SET] [r/REPETITION]
```

**Example:**
```
Input:  delete e/benchpress w/push
        delete e/benchpress w/push wt/40 s/3 r/8
Output: Your exercise has been successfully deleted!
```

---

### Feature 6: List Workout Summary

**Purpose:** Lists the names and completion status of all workout sessions currently in your list.

**Format:**
```
list
```

**Example:**
```
Input:  list
Output: 
  Your Workouts:
  1. [ ] PUSH DAY
  2. [X] PULL DAY
```

---

### Feature 7: List Workout Exercises

**Purpose:** Lists all exercises within a specific workout session.

**Format:**
```
list w/WORKOUT
```

**Example:**
```
Input:  list w/push
Output:
  PUSH Workout Exercises:
  Bench Press | Weight: 80kg | Sets: 4 | Reps: 10
  ... remaining exercises ...
```

---

### Feature 8: List All Data

**Purpose:** Lists every exercise across every workout session stored in the application.

**Format:**
```
list all
```

**Example:**
```
Input:  list all
Output:
  PUSH DAY:
    Bench Press | Weight: 80kg | Sets: 4 | Reps: 10
  PULL DAY:
    Lat Pulldown | Weight: 60kg | Sets: 3 | Reps: 12
```

---

### Feature 9: Find a Workout

**Purpose:** Helps you find a specific workout session in your list.

**Format:**
```
find w/WORKOUT
```

**Example:**
```
Input:  find w/push
Output: Push | Exercises: 3
```

---

### Feature 10: Find an Exercise

**Purpose:** Helps you find a specific exercise across your workouts.

**Format:**
```
find e/EXERCISE w/WORKOUT
```

**Example:**
```
Input:  find e/benchpress w/push
Output:
  Bench Press | Weight: 80kg | Sets: 4 | Reps: 10
```

---

### Feature 11: Exit

**Purpose:** Exits the program.

**Format:**
```
exit
```

---

### Feature 12: Mark

**Purpose:** Marks or unmarks a workout session as completed.

**Format:**
```
mark w/WORKOUT
unmark w/WORKOUT
```

**Example:**
```
Input:  mark w/push
Output: Successfully marked 'push' as done!

Input:  unmark w/push
Output: Successfully unmarked 'push'!
```

> **Note:** Completion status is shown in `list` as `[X]` (done) or `[ ]` (not done).

---

### Feature 13: Storage

**Purpose:** Keeps a record of past workout sessions (to use as templates).

---

### Feature 14: Date

**Purpose:** Assigns a date to each workout.

**Format:**

```
Input:  loglist
Output: === COMPLETE LOG HISTORY ===
        [24-03-2026, 23:09] LEGS workout
        deadlift:         :  100kg |  4 sets | 10 reps
        leg press:        :  140kg |  3 sets | 10 reps
        hamstring curl:   :  110kg |  4 sets |  8 reps
        --------------------------------------------
        [30-03-2026, 14:15] LEGS workout
        deadlift:         :  100kg |  4 sets | 10 reps
        leg press:        :  140kg |  3 sets | 10 reps
```
---

### Feature 15: Edit

**Purpose:** Edits the name of an existing workout session, or updates any combination of fields
within a specific exercise - without needing to delete and re-add.

**Format:**
```
edit w/WORKOUT_NAME
edit w/WORKOUT_NAME e/EXERCISE_NAME
```

After entering the command, you will be prompted to enter the fields you wish to change.
Only the flags you provide will be updated; all others remain unchanged.

**Flags for editing:**

| Flag | Meaning |
|------|---------|
| `wn/` | New workout name |
| `en/` | New exercise name |
| `wt/` | New weight (positive integer) |
| `s/`  | New number of sets (positive integer) |
| `r/`  | New number of reps (positive integer) |

**Example — Edit a workout name:**
```
Input:  edit w/push
Prompt: Edit fields (e.g. wn/NewName):
Input:  wn/Push Day
Output: Change Recorded! Edited Workout:
        Push Day | Exercises: ...
```

**Example — Edit an exercise:**
```
Input:  edit w/Push Day e/Bench Press
Prompt: Edit fields (e.g. wn/NewWorkout en/NewExercise wt/100 s/3 r/10):
Input:  wt/90 s/4 r/8
Output: Change Recorded! Edited Workout:
        Push Day
        Bench Press | Weight: 90kg | Sets: 4 | Reps: 8
```

> **Note:** Pressing Enter without typing anything at the prompt will be regarded as no changes.

### Feature 16: Remarks

**Purpose:** Adds comments and remarks to a workout session.

**Format:**
```
log e/EXERCISE_NAME [w/WORKOUT_NAME] remark/REMARK
```

**Example:**
```
Input:  log e/Bench Press w/Push Day remark/Lightweight babyyy
Output: Stats updated for Bench Press in Push Day!
        Remark added: Lightweight babyyy
```

> **Note:** Remarks are saved to `history.txt` and displayed when you run `loglist`.

### Feature 17: Log Workout Session

**Purpose:** Starts or resumes a logging session for today. This command identifies the workout you are currently training and lists its exercises.

**Format:**
```
log w/WORKOUT_NAME
```

**Example:**
```
Input:  log w/Push Day
Output: Session started for Push Day! Let's get those gains.
        PUSH DAY Workout Exercises:
        ...
```

---

### Feature 18: Log Exercise Stats

**Purpose:** Records your actual performance (weight, sets, reps) and an optional remark for a specific exercise. GitSwole uses "Smart Overwriting" to ensure your history remains clean.

**Format:**
```
log e/EXERCISE_NAME [w/WORKOUT_NAME] [wt/WEIGHT] [s/SETS] [r/REPS] [remark/REMARK]
```

* If `w/` is omitted, the most recent active session name is used (Sticky Session).
* If `wt/`, `s/`, or `r/` are omitted, the current values stored in the workout template are used.

**Example:**
```
Input:  log e/Bench Press wt/85 s/3 r/5 remark/Too ez, try up weight
Output: Stats updated for Bench Press in Push Day!
        Remark added: Too ez, try up weight
```

---

### Feature 19: History Storage

**Purpose:** Automatically records every `log` entry into a persistent history file (`docs/history.txt`). This file serves as a chronological diary of your training progress.

**Format:**
(Automatic upon using the `log` command)

---

### Feature 20: Log List

**Purpose:** Displays your complete chronological training diary from `history.txt`. Supports filtering by workout name 
or date so you can quickly review past sessions without scrolling through everything.

**Format:**
```
loglist
loglist w/WORKOUT_NAME
loglist d/DATE
```
* `loglist` alone prints every logged entry across all dates, in chronological order.
* `loglist w/WORKOUT_NAME` filters the history to only entries belonging to that workout.
* `loglist d/DATE` filters the history to all entries logged on a specific date. Date must follow the `dd-MM-yyyy` format.

**Example** - View complete history:
```
Input:  loglist
Output:
=== COMPLETE LOG HISTORY ===
[24-03-2026, 23:09] LEGS workout
deadlift:         :  100kg |  4 sets | 10 reps
leg press:        :  140kg |  3 sets | 10 reps
hamstring curl:   :  110kg |  4 sets |  8 reps
  --------------------------------------------
[30-03-2026, 14:15] LEGS workout
deadlift:         :  100kg |  4 sets | 10 reps
leg press:        :  140kg |  3 sets | 10 reps
leg extension:    :   60kg |  3 sets | 15 reps
```

**Example** - Filter by workout:  
```
Input:  loglist w/legs
Output:
=== LOG HISTORY FOR: LEGS ===
[24-03-2026, 23:09] LEGS workout
deadlift:         :  100kg |  4 sets | 10 reps
...
```

**Example** - Filter by date:
```
Input:  loglist d/30-03-2026
Output:
=== LOG HISTORY FOR: 30-03-2026 ===
[30-03-2026, 14:15] LEGS workout
deadlift:         :  100kg |  4 sets | 10 reps
...
```

---

## FAQ

**Q: Where is my data stored?**  
A: Workout templates are saved in `data/workouts.txt`. Workout history logs are saved in `data/history.txt`. Both files are created automatically on first run.

**Q: What happens if I accidentally close the app mid-session?**  
A: Data is saved automatically after every mutating command (`add`, `delete`, `mark`, `edit`), so you will not lose any confirmed entries.

**Q: Can I edit `workouts.txt` or `history.txt` manually?**  
A: Yes, but exercise caution — malformed entries may cause loading errors on the next launch.

**Q: What happens if I type an unrecognised command?**  
A: GitSwole will display an error message. Type `help` to see the list of valid commands.
---

## Known Issues

**_To be added..._**

---

## Command Summary

| Action | Format | Example |
|--------|--------|---------|
| Add workout | `add w/WORKOUT` | `add w/push` |
| Add exercise | `add e/EXERCISE w/WORKOUT [wt/WEIGHT] [s/SET] [r/REPETITION]` | `add e/benchpress w/push wt/40 s/3 r/8` |
| Delete workout | `delete w/WORKOUT` | `delete w/push` |
| Delete exercise | `delete e/EXERCISE w/WORKOUT` | `delete e/benchpress w/push` |
| List summary | `list` | `list` |
| List specific workout | `list w/WORKOUT` | `list w/push` |
| List all | `list all` | `list all` |
| Log workout session | `log w/WORKOUT` | `log w/push day` |
| Log exercise stats | `log e/EXERCISE [w/WORKOUT] [wt/WEIGHT] [s/SETS] [r/REPS] [remark/REMARK]` | `log e/bench press wt/80 s/3 r/10` |
| Find workout | `find w/WORKOUT` | `find w/push` |
| Find exercise | `find e/EXERCISE w/WORKOUT` | `find e/benchpress w/push` |
| Edit workout name | `edit w/WORKOUT` | `edit w/push` |
| Edit exercise | `edit w/WORKOUT e/EXERCISE` | `edit w/Push Day e/Bench Press` |
| Help | `help` | `help` |
| Exit program | `exit` | `exit` |

---

**Get Swole! 💪🏽**
