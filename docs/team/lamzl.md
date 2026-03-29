# GitSwole Project Portfolio Page (Ethan)

## Project: GitSwole

GitSwole provides fast, CLI-based workout tracking for gym-goers who dislike slow GUI apps and manual logs. 
Single-command entry enables efficient logging of exercises, sets, and weights, with instant access to workout history 
and progress tracking optimized for keyboard-comfortable users.
 It is written in Java, and has about 700 LoC.

Given below are my contributions to the project.

---

### New Feature: Delete Workout Session and Delete Exercise

* **What it does:** Allows the user to delete an entire workout session by name
  (`delete w/WORKOUT`), or remove a specific exercise from within a workout
  (`delete e/EXERCISE w/WORKOUT`).
* **Justification:** This feature is essential because users need to be able to
  remove outdated or mistakenly added workout sessions and exercises. Without it,
  errors in the workout list cannot be corrected.
* **Highlights:** The implementation uses flag-based dispatch — the `execute()` method
  inspects the raw input string for `e/` and `w/` prefixes to determine which
  operation to perform, avoiding the need for two separate command classes. Name
  matching is case-insensitive so users do not need to remember exact capitalisation.

---

### New Feature: Persistent Storage

* **What it does:** Automatically saves all workout and exercise data to a plain
  text file (`data/gitswole.txt`) after every session, and reloads it on the next
  application launch. No user action is required.
* **Justification:** Without persistence, all data would be lost every time the
  application is closed. This feature makes GitSwole viable as a long-term workout
  log rather than a single-session tool.
* **Highlights:** The storage format uses tagged lines (`WORKOUT`, `EXERCISE`, `---`)
  to represent the parent-child relationship between workouts and exercises. Field
  values that contain the `|` delimiter are escaped to `{PIPE}` on write and
  unescaped on load to prevent parse corruption. The inner `StorageException` class
  provides line-number-aware error messages when a corrupted file is detected.

---

### Code Contributed

[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=lamzl&tabRepo=AY2526S2-CS2113-W10-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

---

### Project Management

* Created and published the first release `v1.1.jar` file on GitHub. 

---

### Enhancements to Existing Features

* Implemented edge case handler in the storage class to handle workouts that contain the `|` 
character.

---

### Documentation

#### User Guide
* Created a Google Doc tab to draft the UG so that it would be easier to collaborate.
* Added documentation for **Feature 4: Delete Workout Session**.
* Added documentation for **Feature 5: Delete an Exercise**.
* Added documentation for **Feature 12: Storage**.
* Added entries to the Command Summary table for all delete-related commands.

#### Developer Guide
* Added implementation details of the `delete` feature including architecture-level
  description, component interaction walkthrough, and sequence diagrams.
* Added implementation details of the `storage` feature including file format
  specification, save/load sequence diagrams, and design considerations comparing
  plain text vs JSON vs CSV formats.
* Added PlantUML sequence diagrams: `classDiagram.puml`,
  `DeleteSD.puml`, `StorageSave.puml`, `StorageLoad.puml`.
* Added Instructions for Manual Testing for storage and delete classes.

---

## Community

* PRs reviewed (with non-trivial review comments): 
* Reported bugs and suggestions for other teams in the class: 
