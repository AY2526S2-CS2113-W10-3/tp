# GitSwole Project Portfolio Page (Shuo Jie)

## Project: GitSwole

GitSwole provides fast, CLI-based workout tracking for gym-goers who dislike slow GUI apps and manual logs. Single-command entry enables efficient logging of exercises, sets, and weights, with instant access to workout history and progress tracking optimized for keyboard-comfortable users. It is written in Java, and has about 3000 LoC.

Given below are my contributions to the project.

---

## Code Contributed

[RepoSense Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=shuojiee&tabRepo=AY2526S2-CS2113-W10-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

Total: **976 LoC** (319 functional) — primary files created include `LogListCommand.java`, `LogListCommandTest.java`, `MarkCommand.java`, and `MarkCommandTest.java`, with additional contributions (new methods, bug fixes, parsing logic) across supporting files such as `Parser.java`, `HistoryStorage.java`, `Ui.java`, and `ListCommand.java`.

---

## Features Implemented

### New Feature: Add Workout Session and Add Exercise (`AddCommand`)

**What it does:** Allows users to create a new workout session (`add w/WORKOUT`) or add an exercise with its stats to an existing session (`add e/EXERCISE w/WORKOUT wt/WEIGHT s/SETS r/REPS`).

**Justification:** This is the entry point for all user data — without the ability to add workouts and exercises, none of the other features have anything to operate on. Getting this right was critical for the rest of the team to build on top of.

**Highlights:** The trickiest part was handling multi-word exercise and workout names. The default parser assumed single-token inputs, so I redesigned the parsing logic in both `AddCommand.java` and `Parser.java` to correctly extract flag-delimited values even when names contained spaces.

---

### New Feature: Mark/Unmark Workout Completion (`MarkCommand`)

**What it does:** Lets users mark a workout session as complete or incomplete, with a visible status symbol shown alongside the workout when listed — e.g. `[X] Push Day` for completed, `[ ] Push Day` for incomplete.

**Justification:** Users need a way to track which sessions they have actually completed, not just planned. A visible completion indicator makes it easy to review progress at a glance without relying on memory.

**Highlights:** The status symbol required coordinated changes across `MarkCommand.java`, `ListCommand.java`, and `Ui.java` to ensure display consistency regardless of how the list was accessed. The implementation was refined across several PRs based on team feedback.

---

### New Feature: View Workout Log History (`LogListCommand`)

**What it does:** Displays a chronological history of the user's completed workout sessions via the `loglist` command.

**Justification:** Logging a workout is only useful if the user can look back at it. Without a retrieval mechanism, the log data written by `LogCommand` would be completely invisible from within the app.

**Highlights:** This was my most technically involved feature. I built `LogListCommand.java` from scratch and created `HistoryStorage.java` to handle reading workout history from disk. Managing file I/O correctly — ensuring history is read in the right order and gracefully handling edge cases like an empty history file — required the most iteration. `LogListCommand` is my top contributor by LoC (169 lines of functional code, 187 lines of test code).

---

## Enhancements to Existing Features

* **Improved `MarkCommand` display:** Enhanced the mark feature to show a status symbol next to each workout in the list output, making completion state immediately visible without needing a separate command.
  * *Example:* A completed session shows `[X] Push Day` while an incomplete one shows `[ ] Push Day`.
* **Robust test coverage for `LogListCommand`:** Extended `LogListCommandTest.java` with additional defensive checks after writing tests revealed edge cases around empty history and malformed entries.

---

## Project Management

* Kept integration tests (`text-ui-test/EXPECTED.TXT`) up to date across multiple PRs as the team's features evolved, preventing CI failures from accumulating.

---

## Documentation

### User Guide

* Wrote documentation for the `add`, `mark`, and `loglist` commands, covering usage format, expected output, and examples.
* Made follow-up corrections across multiple iterations as the feature implementations were refined.

### Developer Guide

* Documented the design and implementation of all three of my features (`AddCommand`, `MarkCommand`, `LogListCommand`).
* Created the sequence diagram for `lqoglist` (`loglistSD.puml` / `loglistSD.png`), illustrating how `LogListCommand` retrieves and displays history by interacting with `HistoryStorage`.

---

## Community

* Reviewed PR [#38](https://github.com/AY2526S2-CS2113-W10-3/tp/pull/38) by rpraveen7 (`fix-ci-errors`), resolving merge conflicts with upstream master to unblock the team's CI pipeline.
* Reviewed PR [#99](https://github.com/AY2526S2-CS2113-W10-3/tp/pull/99) by rpraveen7 (`coding-standards-check`), a refactoring PR covering Javadoc additions for `WorkoutList.java` and `Workout.java`, a variable rename in `Ui.java` for clarity, type explicitness improvements in `GitSwole.java`, and architectural simplification of static field initialization — all verified to pass Checkstyle and existing tests.
* Reported bugs and suggestions for other teams in the class during PE dry run.
