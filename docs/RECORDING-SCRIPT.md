# Emergency Department Simulator — complete recording script

## How to use this script

Read the paragraphs labeled **Say** aloud. The **Show** and **Point** instructions are for you; do not read them aloud. Keep this script beside Cursor or on another screen. Open the application and the project in Cursor before recording.

The full script is deliberately detailed. Allow roughly 30–40 minutes with clicks and code navigation. If your teacher has a shorter limit, keep the ten app demonstrations and the code sections on data structures, comparator, admission validation, discharge, and time tracking. The additional explanations can be used when answering questions.

To open a file quickly in Cursor, press Command+P and type its filename. To jump to a line, press Control+G, type the number, and press Enter. Alternatively, search the exact method name with Command+F. Line numbers below refer to the code checked when this guide was prepared.

Start a new recording from an empty state: click Reset everything, confirm, and import patients/assignment-patients.csv. Use the sequence below if you want the final screen to match your screenshot. Use this single guide for the recording.

The automated AssignmentTests runner uses its own deliberately fixed room assignments. Its final counts differ from this interactive recording sequence. Both exercise the supplied ten scenarios; the room choices in your GUI demonstration determine your own final counts.

## Start by explaining the project structure in Cursor

**Show:** Cursor's Explorer with src, test, patients, and docs expanded. Do this before opening the app.

    Emergency-Department-Simulator/
    ├── src/
    │   ├── Main.java
    │   ├── EmergencyDepartment.java
    │   ├── EmergencyDepartmentGUI.java
    │   ├── Patient.java
    │   ├── PatientComparator.java
    │   ├── PatientImporter.java
    │   └── Room.java
    ├── test/
    │   ├── AssignmentTests.java
    │   └── EmergencyDepartmentTest.java
    ├── patients/
    │   └── assignment-patients.csv
    ├── docs/
    │   └── RECORDING-SCRIPT.md
    ├── README.md
    ├── .gitignore
    ├── run.command
    └── test.command

**Say:**

“Before I demonstrate the application, I will explain the project structure.

“The src folder contains the seven Java classes that make up the actual application. Main.java is the program's entry point: it is where Java starts execution. EmergencyDepartment.java manages the hospital's state and operations. EmergencyDepartmentGUI.java builds the Swing dashboard and connects its buttons to the hospital logic.

“Patient.java represents an individual patient's information and timestamps. Room.java represents one treatment room. PatientComparator.java defines severity and arrival ordering. PatientImporter.java reads and validates the CSV used to load patients.

“The test folder is separate from the application code. AssignmentTests.java runs the ten assignment scenarios, while EmergencyDepartmentTest.java checks additional edge cases, ordering, and time calculations. These classes verify the application; they do not open the normal dashboard.

“The patients folder contains the fictional input data. The docs folder contains my recording guide. README.md explains setup and operation. run.command compiles and launches Main.java, and test.command compiles and runs the tests. The .gitignore file prevents generated build files from being committed.

“The build folder may appear after compilation. It contains generated class files, not the Java source I explain or edit.”

**Show:** Open src/Main.java, then point to main, the new EmergencyDepartment, the new EmergencyDepartmentGUI, and setVisible.

**Say:**

“This is Main.java, the main Java program. Its main method is the starting point. It schedules the Swing startup, creates an empty hospital model, creates a dashboard using that model, and makes the window visible. From here, the user's button clicks call the methods in the other classes.”

Now demonstrate the app tests below. After the demonstration, return to the detailed code walkthrough.

## Opening — show the empty dashboard and import

**Show:** Empty dashboard, then Import patients and the CSV filename.

**Say:**

“Hello. This is my Emergency Department Simulator, built in Java 21. The graphical interface uses Java Swing, and the hospital logic uses Java Collections.

“The program represents the patient journey through registration, waiting, admission, treatment, and discharge. It has ten treatment rooms and five triage levels. Patients are ordered by urgency, and patients with the same urgency are ordered by their arrival time.

“I start with an empty hospital. Instead of entering every example by hand, I import a CSV containing twelve fictional patients. Importing places them in the waiting queue; it does not immediately occupy any rooms.

“The CSV also includes how many minutes before import each patient arrived. That creates a realistic historical arrival timeline for this simulation. These are fictional timestamps from the presentation file. When I manually register a new patient, the application records the actual current registration time.”

**Show:** Queue, triage legend, arrival times, and header counts.

**Say:**

“The five levels are critical in red, emergent in orange, urgent in yellow, less urgent in blue, and non-urgent in green. Lower numbers represent higher priority. The colors are supported by text labels, so the classification remains clear even without relying on color alone.

“At this point there are twelve registered patients, twelve waiting patients, no occupied rooms, and ten available rooms.”

## App demonstration — all ten tests

### Test 1 — admit Lisa to room 10

**Show:** Select P006, Lisa Anderson, in the waiting queue. Click Admit selected and enter 10. Scroll the queue to find her if necessary.

**Say:**

“For the first test, I admit Lisa Anderson, patient P006, to room ten, matching the example in the assignment. This is a manual room assignment.

“After admission, Lisa disappears from the waiting queue and appears in room ten. The occupied count becomes one, the waiting count becomes eleven, and the available count becomes nine. Her admission timestamp is recorded, and her treatment timer begins.

“Manual admission lets me select a particular patient and room. The separate Call next button demonstrates automatic priority-based admission.”

### Test 2 — display room statuses

**Show:** Click Display room statuses.

**Say:**

“This button builds a report from the actual current room objects. It displays the room number, whether the room is available or occupied, the patient ID and name, triage, and treatment duration.

“Room ten is occupied by Lisa, and the other nine rooms are available. The report reads the hospital state without changing it.”

Close the report.

### Test 3 — attempt admission to an occupied room

**Show:** Select P005, David Brown, then Admit selected → 10. Dismiss the warning.

**Say:**

“I now try to place David in room ten, which is already occupied. The application rejects the admission.

“Lisa remains in room ten, David remains in the waiting queue, and the counts do not change. This verifies that an unsuccessful admission does not overwrite an existing patient or remove a waiting patient.”

### Test 4 — fill the remaining rooms

**Show:** Click Call / get next → admit nine times, pausing to point out which patients are being admitted. Then click it once more.

**Say:**

“I use automatic admission to fill the remaining nine rooms. Each call selects the highest-priority waiting patient and uses the first available room.

“The critical patients are admitted first, followed by emergent, urgent, less urgent, and then non-urgent patients. Within an equal triage level, the earlier arrival is selected first.

“Now all ten rooms are occupied, and two patients remain waiting. Another admission attempt reports that all ten rooms are occupied. The waiting patients remain in the queue.”

Checkpoint: room 1 John, 2 Michael, 3 Noah, 4 Sarah, 5 Ava, 6 James, 7 Olivia, 8 Emma, 9 Emily, 10 Lisa. David and William remain waiting.

### Test 5 — attempt to admit null

**Show:** Click Test null admission.

**Say:**

“This test attempts admission with null. Null means that no Patient object has been supplied; it is not a patient whose name is Null.

“The application responds that a patient is required. The null check runs before the occupied-room check, so this still demonstrates the missing-patient error even when all rooms are occupied.

“The room assignments and waiting queue remain unchanged. This prevents an invalid request from creating a false room occupant or crashing the application.”

Dismiss the warning.

### Test 6 — discharge, then reuse a room

**Show:** Click room 9 and discharge Emily Davis. Select P005 David Brown and admit him to room 9.

**Say:**

“For this test, I simulate a doctor approving the discharge of a non-critical patient. Emily is in room nine with a minor cut and a non-urgent classification.

“Discharging her releases room nine. I then admit David Brown, who has a sprained ankle, into that same room. This shows that a room can be reused after discharge.

“The assignment's Test 6 comment mentions discharge, while its example calls admitPatient. I demonstrate both steps: releasing a room and admitting another patient. The example uses room four; I use room nine here because that is where the non-critical patient is placed by this automatic admission sequence.”

**Show:** Discharge James Miller from room 6 as an additional simulated doctor-approved discharge.

**Say:**

“I also simulate the doctor approving James's discharge from room six after treatment. His urgent classification is not a critical classification. The application does not diagnose recovery; I supply the discharge decision.

“This leaves room six empty for the later invalid-discharge test.”

Checkpoint: registered 12, waiting 1, occupied 9, available 1, discharged 2.

### Test 7 — display updated room statuses

**Show:** Click Display room statuses again.

**Say:**

“The report now reflects the changes. Room nine contains David Brown, and room six is available. There are nine occupied rooms and one available room.

“This confirms that the status report shows current data after admissions and discharges, rather than a fixed initial display.”

Close the report.

### Test 8 — discharge patients from rooms 1 and 3

**Show:** Discharge room 1, then room 3.

**Say:**

“The assignment next calls dischargePatient with one and three. These arguments are room numbers, not patient IDs.

“I simulate the doctor advising that the patients in rooms one and three can be discharged after treatment. Each discharge frees its room, records a discharge timestamp, and adds the patient to the discharge history.”

Checkpoint: registered 12, waiting 1, occupied 7, available 3, discharged 4.

### Test 9 — try to discharge from empty room 6

**Show:** Click room 6.

**Say:**

“Room six is already empty. Clicking it attempts discharge, but the application rejects the request with the message: Room six has no patient. Cannot discharge.

“No new discharge record is created, and the room and statistics remain unchanged.”

Dismiss the warning.

### Test 10 — final queue, search, and occupancy

**Show:** Peek next, then Search by ID → P006, then Display room statuses.

**Say:**

“The final waiting queue contains William Jones, patient P009. Peek returns his details without removing him.

“I search for P006 and find Lisa Anderson. She is still in treatment in room ten. The search can find patients whether they are waiting, receiving treatment, or already discharged.

“The final room report shows seven occupied rooms and three available rooms: one, three, and six. There is one patient waiting and four discharged patients.

“The counts remain consistent: one waiting patient, seven admitted patients, and four discharged patients equal the twelve registered patients.”

Close the report and show Discharge history.

**Say:**

“Discharge history shows the most recently discharged patient first, and includes the recorded arrival, admission, discharge, waiting duration, and treatment duration.”

## Code explanation — open these actual files in Cursor

### 1. Program entry point and separation of responsibilities

**Open:** [src/Main.java — line 6](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/Main.java#L6)
**Point:** Point to main, SwingUtilities.invokeLater, the department and dashboard variables, and setVisible.

**Say:**

“The program starts in the main method. SwingUtilities.invokeLater schedules interface creation on Swing's event dispatch thread, which is the thread responsible for updating Swing controls.

“I create a new EmergencyDepartment for the hospital's data and logic, and pass it to EmergencyDepartmentGUI. This separates hospital operations from how they are displayed.

“Main starts with a new empty department. It does not import patients automatically. The window becomes visible with setVisible.”

### 2. Patient model, required fields, and triage validation

**Open:** [src/Patient.java — line 6](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/Patient.java#L6)
**Point:** Point to the fields, the three constructors, and required(String, String).

**Say:**

“The Patient class represents one patient. It stores an ID, name, condition, severity, arrival time, admission time, discharge time, and arrival sequence.

“ID, name, condition, and severity are final fields. Once a patient is created, these values are not changed by the interface. Keeping ordering fields stable is useful because changing severity while an object is inside a priority queue would require removing and reinserting it.

“The constructor validates required text and accepts only severity levels one through five. Missing names, missing IDs, and invalid triage values produce an error instead of entering the system.

“The three-argument constructor supports the assignment's example. If severity is not provided, it defaults to urgent, level three. This is a programming default, not a medical decision based on the condition text. The import file and registration form supply explicit triage values.”

### 3. Ten rooms and room occupancy

**Open:** [src/Room.java — line 2](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/Room.java#L2)
**Point:** Point to number, patient, isOccupied(), occupy(), and release().

**Say:**

“Each Room object has a room number and either one Patient reference or null. Null means the room is empty.

“isOccupied checks whether that reference exists. occupy prevents assigning another patient to an occupied room and also rejects null. release returns the previous occupant and sets the room's patient reference to null.

“The department controls when these operations occur. The interface does not directly assign a new occupant to a room.”

### 4. The four main data structures

**Open:** [src/EmergencyDepartment.java — line 6](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/EmergencyDepartment.java#L6)
**Point:** Point to waitingQueue, patients, rooms, dischargeHistory, and the room initialization in the constructor.

**Say:**

“This is the central hospital manager. The PriorityQueue stores waiting patients using my custom PatientComparator.

“The HashMap stores every registered patient by ID. It supports direct lookup without scanning every room or every queue entry. It retains discharged patients so their records remain searchable.

“The room array has exactly ten positions. The constructor creates rooms numbered one through ten. The array positions are zero-based, so a room number is converted to its array position by subtracting one.

“The Stack stores discharged patients in last-in, first-out order. The newest discharge is pushed onto the stack. The interface reads a reversed snapshot to display newest records first without popping or losing them.”

### 5. Priority and FIFO: the custom comparator

**Open:** [src/PatientComparator.java — line 5](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/PatientComparator.java#L5)
**Point:** Point to compare(): severity comparison, arrival comparison, then arrivalSequence.

**Say:**

“This comparator determines which waiting patient has priority. First, Integer.compare compares the severity values. A smaller severity value comes first, so level one precedes level two and the other levels.

“If severity is equal, I compare arrival timestamps. The earlier arrival comes first.

“If both severity and arrival time are identical, I compare the arrival sequence numbers. Each registration receives a different increasing sequence number, so equal timestamps still produce a deterministic first-in, first-out order.

“The key idea is severity first, arrival time second, and registration sequence as the final tie-break. A later critical patient can therefore come before an earlier non-urgent patient, but equal-priority patients follow their arrival order.”

### 6. Registration, import validation, and realistic arrival times

**Open:** [src/EmergencyDepartment.java — line 27](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/EmergencyDepartment.java#L27)
**Point:** Point to registerPatient(), then importPatients(), particularly the validation loop and importedAt.minusMinutes(...).
**Then open:** [src/PatientImporter.java — line 9](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/PatientImporter.java#L9) and patients/assignment-patients.csv.

**Say:**

“Manual registration checks for a real patient and a unique ID. It records the current time, assigns an arrival sequence, adds the patient to the HashMap, and inserts the patient into the priority queue.

“The CSV reader validates the header, converts severity and arrival offsets to numbers, and constructs Patient objects. Invalid rows report their line number.

“Before importing any patient into the department, importPatients checks the entire batch for invalid entries and duplicate IDs, including duplicates against patients already registered. Only after those checks pass does it change the hospital state.

“The import uses one reference time for the batch, then subtracts each patient's arrived_minutes_ago value. This is why the patients have different arrival times and waiting durations even though I import the file in one action.

“Importing a historical patient also works with FIFO: the comparator uses their actual assigned arrival timestamp, rather than simply treating the import order as the arrival order.”

### 7. Admission, validation, and protecting patients from loss

**Open:** [src/EmergencyDepartment.java — line 67](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/EmergencyDepartment.java#L67)
**Point:** Point to admitPatient(): validation first, then room.occupy(), waitingQueue.remove(), and p.admit().

**Say:**

“This method handles admission into a specific room. First it validates the room number, then checks that a patient exists and the room is not occupied.

“If this is a new patient supplied directly by the assignment-style constructor, the method registers the patient after validating the room. If the patient is already registered, it checks that this is the same stored object and that the patient is still waiting.

“The important design choice is that the checks happen before moving the patient. An occupied room, null patient, duplicate ID, or already-admitted patient is rejected before a successful transition occurs.

“After validation, the method captures the admission time, assigns the room, removes the patient from the waiting queue, and records admission on the Patient object.

“This is the implementation behind Tests one, three, four, and five. Test three verifies that a failed occupied-room admission does not lose the waiting patient.”

**Point:** room(int number), which validates and converts the room number.

**Say:**

“The room helper rejects numbers outside one through ten. It converts the human-facing room number into the zero-based array index.”

### 8. Peek versus get-next admission

**Open:** [src/EmergencyDepartment.java — line 55](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/EmergencyDepartment.java#L55)
**Point:** Point to peekNextPatient(), then getNextPatient().

**Say:**

“Peek returns the head of the priority queue without removing it. If the queue is empty, it returns null.

“Get next first checks whether anyone is waiting. It then searches the rooms in number order and admits the next patient into the first free room.

“In this application, getting the next patient is combined with successful admission. I do not remove a patient and leave them unattached to both the queue and the rooms.

“If no room is free, an exception is raised and the patient remains waiting. This is the full-capacity protection shown in Test four.

“Automatic admission respects the comparator. Admit selected is a manual override that permits the specific room assignments required for the demonstration.”

### 9. Discharge, empty-room protection, and history

**Open:** [src/EmergencyDepartment.java — line 86](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/EmergencyDepartment.java#L86)
**Point:** Point to dischargePatient(), then dischargeHistory().

**Say:**

“Discharge looks up the room and checks whether someone is inside. If the room is empty, the method rejects the request before releasing a patient or adding history.

“For an occupied room, it records the current time, releases the patient, stamps the patient's discharge time, and pushes the patient onto the discharge stack.

“This method supports discharge and room reuse in Test six, the doctor-advised discharges in Test eight, and the invalid empty-room discharge in Test nine.

“The program does not automatically decide that a patient is cured. The user simulates the doctor's decision by confirming discharge.

“The patient remains in the HashMap after discharge, so search still finds the record. The history method creates and reverses a copy of the stack for display, then returns a read-only list.”

### 10. Arrival, waiting time, and treatment time

**Open:** [src/Patient.java — line 6](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/Patient.java#L6)
**Point:** Point to register(), admit(), discharge(), waitingDuration(), and treatmentDuration().

**Say:**

“Registration records arrival. Admission records admission time, and discharge records discharge time.

“Waiting duration is the difference between arrival and admission. If admission has not happened, the method uses the current time, so the waiting duration grows live. Once admitted, it uses the fixed admission time, so the waiting duration stops growing.

“Treatment duration is zero before admission. After admission, it measures the difference between admission and the current time. After discharge, it uses the fixed discharge time, so treatment duration also stops growing.

“Duration.between calculates the difference between LocalDateTime values. An injectable Clock lets the automated tests control time without actually waiting for minutes to pass.”

**Open:** [src/EmergencyDepartmentGUI.java — line 131](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/EmergencyDepartmentGUI.java#L131)
**Point:** the one-second timer in the GUI constructor, then refresh().

**Say:**

“The Swing timer runs refresh every thousand milliseconds. Refresh rebuilds the displayed queue, history, room cards, and counts from the department's current data.

“The timer does not change triage or create new arrivals. It updates the presentation of time and state.”

### 11. Monitoring, search, and correct queue display

**Open:** [src/EmergencyDepartment.java — line 104](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/EmergencyDepartment.java#L104)
**Point:** Point to searchPatient(), waitingPatients(), totalPatients(), waitingCount(), occupiedCount(), and availableCount().

**Say:**

“Search reads from the HashMap using the patient's ID. Queue size comes from the priority queue, occupied count comes from the room occupants, and available count is ten minus occupied count.

“Registered count includes all patients, including those already discharged. During a session, registered patients are partitioned into waiting, admitted, and discharged patients. Their counts should add up.

“A Java PriorityQueue does not guarantee that iterating through every element produces sorted order. Therefore waitingPatients copies the queue, sorts that copy using the same comparator, and returns a read-only list. This makes the dashboard show the true service order without disturbing the underlying queue.

“The room-status report is a snapshot. Its treatment values are captured when I click Display room statuses; reopening it gives a new snapshot. The dashboard itself refreshes continuously.”

### 12. How the buttons call the logic and display errors

**Open:** [src/EmergencyDepartmentGUI.java — line 172](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/EmergencyDepartmentGUI.java#L172)
**Point:** Point to displayRoomStatuses(), testNullAdmission(), dischargeFromRoom(), and button(). Each button now has a named handler.

**Say:**

“The Swing buttons call methods on the EmergencyDepartment object. Display room statuses loops through the current rooms and builds a table.

“The null-test button actually calls admitPatient with null, so it exercises the same backend validation as the automated test. It does not just display a pretend error.

“Clicking a room calls dischargePatient. Occupied rooms require confirmation; empty rooms attempt discharge and receive the backend validation error.

“The shared button handler catches runtime exceptions and displays their message in a warning dialog. It then refreshes the interface. This connects backend validation to visible feedback while keeping the app running.”

### 13. Reset and clean repetition

**Open:** [src/EmergencyDepartment.java — line 125](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/src/EmergencyDepartment.java#L125)
**Point:** Point to reset() in the department and resetEverything() in the GUI.

**Say:**

“Reset clears the waiting queue, lookup map, and history, releases all room occupants, and resets the arrival sequence counter. The interface also clears its form and selections.

“This lets me import the patient file again and repeat the demonstration in the same application session. Reset asks for confirmation before clearing existing session data.”

### 14. Automated tests and evidence

**Open:** [test/AssignmentTests.java — line 5](https://github.com/richardeyeboah/Emergency-Department-Simulator/blob/main/test/AssignmentTests.java#L5)
**Point:** Point to check(), rejected(), then the numbered pass messages and the state checks preceding them.

**Say:**

“AssignmentTests exercises the ten supplied scenarios directly against the hospital logic. The check helper fails when a condition is false. The rejected helper fails if an invalid operation unexpectedly succeeds. The pass messages are printed after those checks succeed.

“The tests verify actual state, not only whether the program prints an error. For example, occupied-room rejection checks that the waiting count is unchanged, full-capacity rejection preserves the queue, and invalid discharge does not grow the history.

“This test runner uses its own fixed room assignments, so its final counts are not intended to match every interactive demonstration. My video uses the automatic-priority sequence shown earlier.

“The additional general tests cover all five severity levels, FIFO even with identical timestamps, queue peeking, deterministic time calculations, patient conservation, and lifecycle validation.”

**Show:** Cursor terminal. Run ./test.command.

**Say:**

“This command compiles the Java source and both test runners with Java twenty-one. It then runs the general tests and the ten assignment scenarios. If a check fails, the process exits unsuccessfully.

“The passing results provide repeatable verification in addition to the visible application demonstration.”

## Technical questions — accurate answers you can use

**Why use a priority queue instead of a normal queue?**

“A normal queue would serve patients only in arrival order. The priority queue lets severity take precedence, with arrival order preserved within equal severity.”

**What is the time complexity?**

“Java PriorityQueue insertion is O(log n), and peek is O(1). Removing an arbitrary selected patient by object and checking membership are O(n). This implementation uses those operations during admission, so I would not claim that its entire admission method is O(log n). Sorting the queue snapshot for display is O(n log n). HashMap lookup is expected O(1). Room scans examine at most ten rooms.”

**Why not say every operation is logarithmic?**

“The queue supports logarithmic heap insertion and head removal, but the code currently uses remove(patient) for both manual and automatic admission. That is an arbitrary-object removal operation, so this is an area for future optimization.”

**Why synchronized methods?**

“They keep department operations from interleaving if called from multiple threads. The Swing interface primarily calls them from the event dispatch thread. Synchronization is not a database transaction; the design protects normal application state transitions by validating inputs before mutation.”

**Are the returned rooms immutable?**

“The returned room list cannot have entries added or removed, but it still contains the actual Room objects. Their occupancy-changing methods are package-private. For this single-package educational application, the department controls their use by convention.”

**Does the system infer triage or cure from symptoms?**

“No. Triage is supplied explicitly by the registration form or CSV, and discharge is a manual simulation of a doctor's decision. The three-argument constructor uses a documented default.”

**Does patient data persist after closing?**

“No. This is an in-memory educational simulation. Restarting creates a new empty department.”

**Why does a discharged patient still appear in search?**

“The map retains the patient's record and updated status, allowing review after discharge. They are no longer in the waiting queue or a treatment room.”

## Closing

**Say:**

“This project combines severity-based scheduling, FIFO ordering, limited room capacity, patient lookup, discharge history, and time tracking in a Java Swing interface.

“The demonstration shows successful admissions and discharges, as well as rejection of occupied-room admission, null admission, full-capacity admission, and empty-room discharge. The queue, rooms, and history remain consistent after those operations, and the automated tests verify the same behaviors.”
