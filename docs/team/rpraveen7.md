# GitSwole Project Portfolio Page (Rajan Praveen)

## Project: GitSwole

GitSwole provides fast, CLI-based workout tracking for gym-goers who dislike slow GUI apps and manual logs. 
Single-command entry enables efficient logging of exercises, sets, and weights, with instant access to workout history 
and progress tracking optimized for keyboard-comfortable users.
It is written in Java, and has about 3000 LoC.

Given below are my contributions to the project.

---

### New Feature: Multi-Tiered Listing Mechanism (`ListCommand`)

* **What it does:** Allows users to view their data at three different granularities: a summary of all workout 
sessions (`list`), a detailed view of a specific workout and its exercises (`list w/WORKOUT`), and a 
global view of all exercises across all sessions (`list all`).
* **Justification:** Users need to quickly navigate their routines. A summary view helps find the right session, 
while the detailed view provides the actual training data. "List all" serves as a quick inventory of the user's 
entire exercise repertoire.
* **Highlights:** Implementation uses a single command class that routes logic based on parsed flags. This minimizes 
class bloat and centralizes display logic, ensuring consistency in how data is presented to the user.

---

### New Feature: Smart Workout Logging (`LogCommand`)

* **What it does:** Enables real-time logging of training performance (weight, sets, reps). It introduces a 
"sticky session" state where the app remembers the last workout name accessed, allowing subsequent exercise logs to 
omit the workout flag.
* **Justification:** Manual data entry during a workout should be as friction-less as possible. By remembering the 
active session, the number of keystrokes required is significantly reduced, allowing the user to focus on their training.
* **Highlights:** The implementation handles dual-state logic — it can either initialize a session (`log w/WORKOUT`) 
or update specific exercise stats (`log e/EXERCISE`), providing a seamless flow for the user.

---

### New Feature: Persistent History Storage (`HistoryStorage`)

* **What it does:** Manages the `history.txt` file, which records every completed training session with a 
"Smart Overwriting" mechanism.
* **Justification:** Unlike a template-based storage, history tracking requires maintaining a chronological log. 
Users often make typos while logging stats in the gym; smart overwriting ensures that re-entering a log corrects 
the existing entry for the day rather than creating duplicates.
* **Highlights:** The storage logic identifies session blocks by date and workout name, then performs 
surgical updates within that block. This keeps the history file clean and human-readable.

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

* **Smart Session Resumption:** Implemented a check in `LogCommand` and `HistoryStorage` to detect if a session for a 
workout has already been started today. This prevents redundant header entries in `history.txt`.
    * *Example:* If a user logs an exercise, then logs another one later, both will be grouped under one 
  "Push Day" header for that date.
* **Robust Input Validation & Error Handling:** Enhanced `LogCommand` to catch incomplete commands or missing workout 
context, throwing custom `GitSwoleException` with helpful usage instructions.
    * *Example:* Typing `log e/` without a name triggers a warning and shows the correct 
  `log e/EXERCISE [w/WORKOUT]...` format.
* **Case-Insensitive Input Handling:** Optimized both `ListCommand` and `LogCommand` to handle user input 
case-insensitively, reducing friction for CLI users.
    * *Example:* Both `list w/puSh dAy` and `log w/PUSH DAY` will correctly target the "Push Day" workout session.


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

## Community

* PRs reviewed (with non-trivial review comments): 
* Reported bugs and suggestions for other teams in the class: 
