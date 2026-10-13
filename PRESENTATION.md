# Presenting tests 1–10

## Close and start fresh from Cursor
Close the simulator using the red close button. If running in Cursor's terminal, Ctrl+C also stops it.
In Cursor, choose Terminal → New Terminal. From this project folder, run:

    ./run.command

Every launch starts with zero patients and ten empty rooms. There is no demo button.
Click **Import patients**, select **patients/assignment-patients.csv**, and open it.
Expected: registered 12, waiting 12, occupied 0, available 10. Arrivals are staggered from 82 to 4 minutes before import, so live waits immediately show a realistic patient history. These are fictional historical times supplied by the CSV.
Import appends rather than replacing patients. To repeat the presentation, click **Reset everything**, confirm, then import the file again. No restart is needed.

## The exact sequence

For each admission, select the indicated ID in the waiting table, click **Admit selected**, and enter the room number. Use manual admission for this numbered demonstration; Call next follows severity automatically and would produce different room assignments.

1. **Admit patients:** P001 → room 1, P003 → room 3, P006 → room 10. Say: “Each admission removes that patient from the waiting queue and assigns one room.” Expected: 3 occupied, 9 waiting.
2. **Display room statuses:** Click **Display room statuses** to open the current room list. Say: “Rooms 1, 3, and 10 are occupied; seven rooms are free.”
3. **Occupied room:** Select P002 and attempt room 1. Dismiss the warning. Say: “It rejects this admission, and P002 remains waiting.” Expected: unchanged.
4. **Fill rooms:** Admit P002 → 2, P005 → 4, P004 → 5, P008 → 6, P007 → 7, P009 → 8, P010 → 9. Expected: occupied 10, waiting 2, available 0. Click Call / get next to show full-capacity rejection.
5. **Null patient:** Click **Test null admission** and dismiss the warning “Patient is required.” Say: “This button attempts to admit null, meaning no patient object. The app rejects it, and the queue and rooms stay unchanged.” It attempts the first available room, or room 1 when all rooms are full; null validation happens before occupancy validation.
6. **Discharge non-critical patients:** Click rooms 4, 6, and 8 and confirm each discharge. Say: “I simulate the doctor's decision to discharge these non-critical patients. Severity alone does not automatically discharge anyone.”
7. **Updated statuses:** Click **Display room statuses** again and point to empty rooms 4, 6, and 8. Expected: occupied 7, available 3, waiting 2, discharged 3.
8. **Doctor-advised discharge:** Discharge rooms 1 and 3. Say: “After treatment, I simulate the doctor's advice by discharging these patients.” Expected: occupied 5, available 5, discharged 5.
9. **Empty-room discharge:** Click room 6. The discharge attempt is rejected with “Room 6 has no patient. Cannot discharge.” Dismiss the warning and show that counts remain unchanged.
10. **Final queue and occupancy:** Click Peek next: P011 is next because severity 1 outranks P012's severity 2. Search P006: Lisa Anderson is in treatment in room 10. Expected: registered 12, waiting 2, occupied 5, available 5, discharged 5. Open history to show newest discharge first.

## Explain the code briefly

- Patient.java stores ID, name, condition, triage, and arrival/admission/discharge times.
- PatientComparator.java compares severity first, arrival time second, then arrival sequence for exact ties. Explain FIFO within equal severity.
- EmergencyDepartment.java contains PriorityQueue for waiting, HashMap for ID lookup, ten Room objects, and Stack for discharge history.
- In admitPatient, show that validation happens before moving the patient. New patients can also be admitted directly using the professor's three-argument constructor; unspecified triage defaults to urgent (3).
- EmergencyDepartmentGUI.java connects buttons to those operations and refreshes durations every second.
- Run ./test.command: the general tests run first, followed by the ten numbered assignment scenarios and additional compatibility/import checks.

The patient names and conditions are fictional examples. The handout's partial snippets do not specify all initial room assignments, so this fixture defines a reproducible sequence that demonstrates all ten supplied scenarios, including empty room 6 at Test 9.
