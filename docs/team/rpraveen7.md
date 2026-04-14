# GitSwole Project Portfolio Page (Rajan Praveen)

## Project: GitSwole

GitSwole provides fast, CLI-based workout tracking for gym-goers who dislike slow GUI apps and manual logs. 
Single-command entry enables efficient logging of exercises, sets, and weights, with instant access to workout history 
and progress tracking optimized for keyboard-comfortable users.
It is written in Java, and has about 3000 LoC.

Given below are my contributions to the project.

---

### New Feature: Multi-Tiered Listing Mechanism (`ListCommand`)

* **What it does:** Provides data at three granularities: summary (`list`), workout-specific (`list w/WORKOUT`), and global inventory (`list all`).
* **Justification:** Optimizes navigation via a single command, allowing rapid switching between routines and granular data.
* **Highlights:** Uses flag-routing for display depth. Integrates completion status (`[X]/[ ]`) and handles whitespace-resilient input.

---

### New Feature: Smart Workout Logging (`LogCommand`)

* **What it does:** Records training via `log e/EXERCISE` with optional flags (`wt/`, `s/`, `r/`, `remark/`). Supports **"Sticky Sessions"**—starting a session with `log w/WORKOUT` allows subsequent logs to omit the workout flag.
* **Justification:** Minimizes training friction by reducing repetitive input while supporting partial updates via template inheritance for omitted fields.
* **Highlights:** Manages dual-state logic (sessions vs. updates). Uses specialized parsing for `remark/` and prevents numeric overflows via strict validation.

---

### New Feature: Persistent History Storage (`HistoryStorage`)

* **What it does:** Manages `history.txt`, recording training sessions with "Smart Overwriting" to prevent duplicate entries from user typos.
* **Justification:** Essential for maintaining a clean chronological log. Ensures re-entering logs corrects the day's existing entry.
* **Highlights:** Identifies session blocks by date/name for surgical updates.

---

### Code Contributed

[RepoSense Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=rpraveen7&tabRepo=AY2526S2-CS2113-W10-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

---

### Project Management

* **Architectural Oversight:** Designed and implemented the core command routing logic for logging and history, 
ensuring a scalable pattern for persistent session tracking
* **Code Quality Assurance:** Implemented extensive assertion checks in the command classes to ensure system state 
integrity during execution.

---

### Enhancements to Existing Features

* **Data Integrity:** Refactored `Parser` to prevent numeric overflows and enforced physical limits (e.g. 1000kg).
* **Smart Guidance:** Implemented global search to suggest correct workouts when logging under the wrong session.
* **Strict Validation:** Enhanced parser to detect unrecognized flags and resolve string parsing collisions.
* **Collision Resolution:** Fixed bug where shorter names (e.g. "ARM") matched longer suffixes (e.g. "WARM") in storage.
* **Operational Guardrails:** Blocked creation of empty sessions or marking exercise-less workouts as complete.
* **UX Optimization:** Implemented full case-insensitivity across all commands to reduce user input friction.

---

## Community

* PRs reviewed before merging (with non-trivial review comments).
* Reported bugs and suggestions for other teams in the class.
* Created Issues and Milestone tracker for ease of workflow tracking.

---

### Documentation

#### User Guide
* Updated **Feature 6 (List Summary)**, **Feature 7 (List Workout)**, and added **Feature 8 (List All)** to 
reflect the multi-tiered listing implementation.
* Added **Feature 17 (Log Workout Session)** and **Feature 18 (Log Exercise Stats)** to explain real-time 
performance tracking and the "Sticky Session" workflow.
* Added **Feature 19 (History Storage)** to document the purpose and automatic nature of session history 
persistence in `history.txt`.

#### Developer Guide
* Added implementation details for the **Tiered Listing Mechanism** (`ListCommand`), explaining the design 
routing and justification for centralizing display logic.
* Documented the **Smart Workout Logging** (`LogCommand`) feature, including the "sticky session" state logic 
and UX improvements.
* Detailed the **Persistent History Storage** (`HistoryStorage`) mechanism, explaining the "Smart Overwriting" 
logic and file format specifications for `history.txt`.
* Created and integrated PlantUML sequence diagrams to illustrate core logic:
    * `listSD.puml`: Determining listing scope and object retrieval.
    * `logSD.puml`: Execution flow for active session logging and sticky state management.
    * `historystorageSD.puml`: Internal logic for session block identification and surgical file updates.
    * `architectureDiagram.puml`: High-level component overview mapping the relationships between UI, Parser, 
  Command, Storage, and Assets.
    * `architectureSD.puml`: Architectural sequence diagram tracing the end-to-end lifecycle of an `add` command 
  from input to persistence.
---
