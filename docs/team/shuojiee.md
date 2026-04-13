# GitSwole Project Portfolio Page (Aw Shuo Jie)

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

Allows users to create new sessions (`add w/WORKOUT`) or add specific exercise stats (`add e/EXERCISE w/WORKOUT wt/WEIGHT s/SETS r/REPS`). This serves as the primary entry point for all user data, making it the critical foundation for all other app features.

**Highlights:** The trickiest part was handling multi-word exercise and workout names. The default parser assumed single-token inputs, so I redesigned the parsing logic in both `AddCommand.java` and `Parser.java` to correctly extract flag-delimited values even when names contained spaces.

---

### New Feature: Mark/Unmark Workout Completion (`MarkCommand`)

Provides a way to track session completion with visible status symbols (e.g., `[X]` for completed, `[ ]` for incomplete). This allows users to review their actual progress at a glance rather than relying on memory.

**Highlights:** The status symbol required coordinated changes across `MarkCommand.java`, `ListCommand.java`, and `Ui.java` to ensure display consistency regardless of how the list was accessed. The implementation was refined across several PRs based on team feedback.

---

### New Feature: View Workout Log History (`LogListCommand`)

Displays a chronological history of the user's completed workout sessions. This is essential for the "tracking" aspect of the app, as it allows users to retrieve data written to disk.

**Highlights:** This was my most technically involved feature. I built `LogListCommand.java` from scratch and created `HistoryStorage.java` to handle reading workout history from disk. Managing file I/O correctly — ensuring history is read in the right order and gracefully handling edge cases like an empty history file — required the most iteration. `LogListCommand` is my top contributor by LoC (169 lines of functional code, 187 lines of test code).

---

## Enhancements to Existing Features

* **Improved `MarkCommand` display:** Enhanced the mark feature to show a status symbol next to each workout in the list output, making completion state immediately visible without needing a separate command.

  * *Example:* A completed session shows `[X] Push Day` while an incomplete one shows `[ ] Push Day`.

* **Robust test coverage for `LogListCommand`:** Extended `LogListCommandTest.java` with additional defensive checks after writing tests revealed edge cases around empty history and malformed entries.

---

## Community

* **Technical PR Reviews:**
  * Reviewed PR #38: Resolved merge conflicts with the upstream master to unblock the team's CI pipeline.
  * Reviewed PR #99: A major refactoring PR. Verified Javadoc additions, variable renames for clarity, and architectural simplifications to ensure they passed Checkstyle and existing tests.
* **Quality Assurance:** Reported bugs and provided UX suggestions for other teams during the Practical Exam (PE) dry run.

---

## Documentation

### User Guide

* Wrote documentation for the `add`, `mark`, and `loglist` commands, covering usage format, expected output, and examples.
* Made follow-up corrections across multiple iterations as the feature implementations were refined.

### Developer Guide

* Documented the design and implementation of all three of my features (`AddCommand`, `MarkCommand`, `LogListCommand`).
* Created the sequence diagram for `lqoglist` (`loglistSD.puml` / `loglistSD.png`), illustrating how `LogListCommand` retrieves and displays history by interacting with `HistoryStorage`.
