# Emergency Department Simulator

A 100% Java 21 application with a Swing dashboard. No external Java dependencies, database, or web frontend. Fictional demo data only.

## Run on Mac / Cursor

Open this folder in Cursor. Java 21 must be available in your terminal.

Double-click **run.command** to open an empty simulator, then click **Load demo**, or run:

```sh
./run.command --demo
```

Compile and run manually on any operating system:

```sh
mkdir -p build
javac --release 21 -d build src/*.java
java -cp build Main --demo
```

Run automated tests:

```sh
./test.command
```

Or compile src and test into build and run `java -cp build EmergencyDepartmentTest`.

## Dashboard

Register a unique ID, name, condition, and triage level. The waiting queue refreshes every second in actual service order. Five levels appear as labeled colored rows and room cards: 1 red critical, 2 orange emergent, 3 yellow urgent, 4 blue less urgent, 5 green non-urgent.

- **Peek next** shows patient details without changing the queue.
- **Call / get next → admit** assigns the highest-priority waiting patient to the first available room. With all rooms full, it reports an error and preserves the queue.
- **Admit selected** assigns a selected waiting patient to a chosen room (1–10). This deliberately permits a manual override of priority.
- Click an occupied room and confirm to discharge its patient.
- **Search by ID** finds waiting, admitted, and discharged patients.
- The history tab lists discharged patients newest first, with arrival/admission/discharge times and final wait/treatment durations.
- **Load demo** replaces simulator data after confirmation when data already exists. Demo starts with 12 fictional patients: 4 in rooms and 8 waiting.

Statistics count every registered patient, including discharged patients. The invariant is:
`registered = waiting + occupied rooms + discharged`.

## Data structures and lifecycle

- `Patient`: immutable ID/name/condition/severity; department-controlled lifecycle timestamps.
- `PatientComparator`: ascending severity, then ascending registration sequence. Equal timestamps still preserve FIFO.
- `EmergencyDepartment`: `PriorityQueue<Patient>`, `HashMap<String, Patient>`, `Room[10]`, and `Stack<Patient>`.
- `Room`: one occupant, numbered 1–10.
- `EmergencyDepartmentGUI`: Swing event-dispatch-thread interface, tables, room controls, and one-second timer.
- `Main`: launches Swing and supports `--demo`.

Registration stamps `LocalDateTime` arrival. Admission stamps admission and freezes waiting duration. Discharge stamps discharge and freezes treatment duration. An injectable `Clock` makes duration tests deterministic. Displayed durations use hours:minutes:seconds. Wall-clock time changes can affect LocalDateTime durations; the interface clamps negative display values to zero.

Admission validates room, occupancy, identity, and waiting membership before mutating state. Null, duplicate IDs, invalid severity, foreign patients, repeated admission, invalid rooms, empty-room discharge, and full-capacity calls are rejected.

**Get-next semantics:** it retrieves and admits atomically, rather than leaving a removed patient unattached. This preserves patient conservation. There is no exposed destructive queue polling operation.

## Automated verification

The dependency-free test runner throws assertions on failure and exits unsuccessfully. Twelve tests cover:

1. All five severity levels ordered correctly.
2. FIFO with identical arrival timestamps.
3. Empty queue and non-destructive peek.
4. Admission and room/queue updates.
5. Occupied room rejection without patient loss.
6. Full capacity, preserved queue, and retry after discharge.
7. Discharge, room release, and LIFO history.
8. Invalid discharge and room bounds.
9. Null, duplicate, invalid and foreign patients; lookup.
10. Deterministic arrival, wait, treatment, and frozen durations.
11. Read-only collection snapshots and lifecycle reuse rejection.
12. Demo statistics and patient conservation.

The original professor's DS_in-ED assignment and exact ten test definitions were not accessible in the referenced conversation. These tests cover the supplied requirements; exact assignment/API compliance remains to be checked against the original handout.

## Video presentation (about 4–5 minutes)

1. **Introduction (20 seconds):** “This emergency department simulator is built entirely in Java 21 using Swing and Java Collections.”
2. **Demo (40 seconds):** Launch with `--demo`. Point out all five triage colors, the ten rooms, and statistics. Explain that lower severity numbers are treated first.
3. **Priority and FIFO (50 seconds):** Register TEST-A and TEST-B at level 1. They appear ahead of lower-priority patients, with TEST-A ahead of TEST-B. Peek next and show that waiting count stays unchanged.
4. **Admission and timing (40 seconds):** Call next. Show the waiting count falling and occupied count rising. Search the ID to show arrival, admission, frozen wait, and growing treatment time.
5. **Safety (40 seconds):** Select a waiting patient and try admitting to an occupied room. Explain the error and show that the patient stays waiting. Call next until all ten rooms are full; another call is rejected without losing the patient.
6. **Discharge (30 seconds):** Discharge an occupied room. Show the freed room and newest history record. Call next to reuse the room.
7. **Code and tests (60 seconds):** In Cursor, show PatientComparator, the four data structures in EmergencyDepartment, and validation before admission. Run `./test.command` and show twelve passes.
8. **Close (15 seconds):** “The queue, rooms, lookup, and history remain consistent across registration, admission, failed actions, and discharge.”

Record your screen with macOS Shift–Command–5 and choose the application area. Use fictional patients. No data is saved across app restarts; this is an in-memory educational simulator.
