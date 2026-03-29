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
   - [Feature 6: List Workout Sessions](#feature-6-list-workout-sessions)
   - [Feature 7: List Workout Exercises](#feature-7-list-workout-exercises)
   - [Feature 8: Find a Workout](#feature-8-find-a-workout)
   - [Feature 9: Find an Exercise](#feature-9-find-an-exercise)
   - [Feature 10: Exit](#feature-10-exit)
   - [Feature 11: Mark](#feature-11-mark)
   - [Feature 12: Storage](#feature-12-storage)
   - [Feature 13: Date](#feature-13-date)
   - [Feature 14: Edit](#feature-14-edit)
   - [Feature 15: Remarks](#feature-15-remarks)
- [FAQ](#faq)
- [Known Issues](#known-issues)
- [Command Summary](#command-summary)

---

## Quick Start

*(TBC)*

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

### Feature 6: List Workout Sessions

**Purpose:** Lists all the types of workout sessions.

**Format:**
```
list /all
```

**Example:**
```
Input:  list /all
Output:
  Push: Benchpress
  Push: Tricep Kickbacks
  Pull: Lat pulldown
  Pull: Preacher curls
  Legs: What is legs?
```

---

### Feature 7: List Workout Exercises

**Purpose:** Lists exercises within a workout session.

**Format:**
```
list w/WORKOUT
```

**Example:**
```
Input:  list w/push
Output:
  Bench Press | Weight: 80kg | Sets: 4 | Reps: 10
  ... remaining exercises ...
```

---

### Feature 8: Find a Workout

**Purpose:** Helps you find the workout that you have logged.

**Format:**
```
find w/WORKOUT
```

**Example:**
```
Input:  find w/push
Output: Push | Exercise : 3

Input:  find w/arms
Output: Workout Not Found
```

---

### Feature 9: Find an Exercise

**Purpose:** Helps you find the exercise that you have logged.

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

### Feature 10: Exit

**Purpose:** Exits the program.

**Format:**
```
exit
```

---

### Feature 11: Mark

**Purpose:** Marks a workout as complete.

*(Details TBC)*

---

### Feature 12: Storage

**Purpose:** Keeps a record of past workout sessions (to use as templates).

*(Details TBC)*

---

### Feature 13: Date

**Purpose:** Assigns a date to each workout.

*(Details TBC)*

---

### Feature 14: Edit

**Purpose:** Edits values in a workout or exercise.

*(Details TBC)*

---

### Feature 15: Remarks

**Purpose:** Adds comments and remarks to a workout session.

*(Details TBC)*

---

## FAQ

*(TBC)*

---

## Known Issues

*(TBC)*

---

## Command Summary

| Action | Format | Example |
|--------|--------|---------|
| Add workout | `add w/WORKOUT` | `add w/push` |
| Add exercise | `add e/EXERCISE w/WORKOUT [wt/WEIGHT] [s/SET] [r/REPETITION]` | `add e/benchpress w/push wt/40 s/3 r/8` |
| Delete workout | `delete w/WORKOUT` | `delete w/push` |
| Delete exercise | `delete e/EXERCISE w/WORKOUT` | `delete e/benchpress w/push` |
| List all workouts | `list /all` | `list /all` |
| List workout exercises | `list w/WORKOUT` | `list w/push` |
| Find workout | `find w/WORKOUT` | `find w/push` |
| Find exercise | `find e/EXERCISE w/WORKOUT` | `find e/benchpress w/push` |
| Help | `help` | `help` |
| Exit program | `exit` | `exit` |
| Mark workout | *(TBC)* | |
| Store workout | *(TBC)* | |
