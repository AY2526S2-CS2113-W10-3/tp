# GitSwole Project Portfolio Page (Wan)

## Project: GitSwole

GitSwole provides fast, CLI-based workout tracking for gym-goers who dislike slow GUI apps and manual logs. 
Single-command entry enables efficient logging of exercises, sets, and weights, with instant access to workout history 
and progress tracking optimized for keyboard-comfortable users.
 It is written in Java, and has about 700 LoC.

Given below are my contributions to the project.

---

### New Feature: Find Workout Session and Find Exercise

* **What it does:** Allows the user to search for workout sessions by keyword
  (`find w/KEYWORD`), or search for specific exercises within a given workout
  (`find e/KEYWORD w/WORKOUT`). Matching results are displayed with summary details.
* **Justification:** As the workout list grows, manually scrolling through all entries
  becomes impractical. A search feature lets users quickly locate workouts or
  exercises without having to remember exact names.
* **Highlights:** The implementation reuses the same flag-based dispatch pattern as
  other commands — `execute()` checks for the `e/` prefix to decide between a
  workout search and an exercise search. Keyword matching is case-insensitive and
  supports partial matches, so entering `leg` will find workouts like "Leg Day" or
  "Legs & Glutes". Exercise results include weight, sets, and reps for quick reference.

### New Feature: Dynamic Welcome Page

* **What it does:** Refreshes the welcome screen with an ASCII art logo, a live date
  display, a progress snapshot (workouts logged, completed, and total exercises),
  a gamified milestone tier system (Coal → Eternal) that advances as the user
  completes more workouts, and a randomly selected daily motivational quote.
* **Justification:** A plain greeting offers no actionable information. The dynamic
  welcome page gives users an at-a-glance summary of their fitness journey every
  time they launch the app, boosting motivation and engagement without requiring
  extra commands.
* **Highlights:** The milestone tier list maps completed-workout counts to ten
  progression tiers (Coal, Wood, Bronze, Silver, Gold, Platinum, Diamond, Master,
  Legendary, Eternal), each accompanied by a unique kaomoji to keep the tone fun.
  The daily quote is chosen at random from a curated pool using `Random`, so the
  greeting feels fresh across sessions. The date is formatted with
  `LocalDate.now()` to keep the display current with no manual upkeep.

### Code Contributed

[RepoSense link](<iframe src="https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/#/widget/?search=W10&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&chartGroupIndex=2&chartIndex=4" frameBorder="0" width="800px" height="147px"></iframe>)
---


### Enhancements to Existing Features

* Robust test coverage for LogCommand,LogListCommand and Parser: Extended them with additional defensive checks.
* Add some loggings above INFO level and some assertions for commands.
---

### Documentation

#### User Guide
* Added a Glossary / Terminology section between Quick Start and Features
* Added 6 missing rows to the Command Summary table
* Rewrite documentation for **Feature 13: Storage**.
* Rewrite documentation for **Feature 14: Date**.

#### Developer Guide
* Added implementation details of the `find` feature including architecture-level
  description, component interaction walkthrough, and sequence diagrams.
---

## Community

* PRs reviewed (with non-trivial review comments): 
* Reported bugs and suggestions for other teams in the class: 
