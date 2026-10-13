# Emergency Department Simulator

100% Java 21 Swing application with five color-coded triage levels, ten rooms, patient import, live waiting/treatment timers, and automated tests. No external Java dependencies.

## Run from Cursor

Open this folder in Cursor, open Terminal → New Terminal, and run:

    ./run.command

Every launch starts empty. Click **Import patients** and choose **patients/assignment-patients.csv** to load twelve fictional patients into the waiting queue. Import validates the whole batch before changing data and appends to existing patients. Duplicate IDs reject the whole import.

CSV format: id,name,condition,severity with optional arrived_minutes_ago. The presentation file has staggered historical arrivals (82 to 4 minutes before import), recalculated every time you import. Ordinary four-column files use the current arrival time. Quoted fields and escaped quotes are supported; multiline fields are not. Severity must be 1–5.

To repeat the presentation, click **Reset everything**, confirm, then import the patient file again. This clears the queue, rooms, history, form, and arrival sequence.

To close, click the red window button or press Ctrl+C in the terminal that launched it. Run the same command to start fresh. Data is held in memory only.

Manual compilation on other systems:

    mkdir -p build
    javac --release 21 -d build src/*.java
    java -cp build Main

## Controls

- Test null admission: attempts admission with Java null and displays the validation error without changing state.
- Display room statuses: opens a current snapshot of all ten rooms, occupants, triage, treatment durations, and occupancy counts. Use it for Tests 2 and 7.
- Register patient: records arrival and adds a uniquely identified patient.
- Peek next: shows the next patient without removing them.
- Call / get next → admit: admits the highest-priority waiting patient into the first free room. Full capacity leaves the queue unchanged.
- Admit selected: manually assigns a waiting patient to room 1–10; this permits overriding priority for the assignment scenarios.
- Click an occupied room: confirm discharge, freeing the room and recording discharge time.
- Search by ID: finds waiting, admitted, and discharged patients.
- History: newest discharged patient first, with final wait/treatment durations.
- Statistics: registered = waiting + occupied + discharged.

Triage: 1 red critical, 2 orange emergent, 3 yellow urgent, 4 blue less urgent, 5 green non-urgent.

## Code

Patient holds identity, triage, arrival sequence and LocalDateTime timestamps. PatientComparator orders severity first, arrival time second, and registration sequence for exact timestamp ties.

EmergencyDepartment uses PriorityQueue, HashMap, Room[10], and Stack. Validations prevent occupied room admissions, null patients, duplicate IDs, invalid rooms, and invalid discharges. Collection snapshots are read-only.

The professor's three-argument Patient constructor is supported, with unspecified triage defaulting to 3 (urgent). Direct admission of a new patient registers it only after validating the room. A failed occupied-room admission does not create a patient.

The GUI runs on Swing's event dispatch thread and refreshes durations every second. Waiting duration freezes at admission; treatment duration freezes at discharge. An injectable Clock supports deterministic tests. Wall-clock adjustments can affect LocalDateTime durations; the GUI clamps negative display durations to zero.

## Tests and video

Run:

    ./test.command

The runner executes twelve general tests, followed by all ten supplied assignment scenarios and additional direct-admission/import rollback checks. Failures exit unsuccessfully.

See [the recording script](docs/RECORDING-SCRIPT.md) for the project structure, on-screen tests 1–10, expected counts, detailed spoken explanations, and code walkthrough. The sample fixture defines room assignments because the supplied handout excerpts do not specify every initial patient/room.

For the video, record using macOS Shift–Command–5. The full detailed script takes roughly 30–40 minutes with demonstrations; shorten it if the teacher specifies a time limit. Null admission can be demonstrated directly using the Test null admission button.

Discharge is a manual simulation of doctor advice; the application does not determine who is cured. No database or persistence is included.
