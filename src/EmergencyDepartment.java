import java.time.*;
import java.util.*;

/** All mutations validate before changing queue, rooms, or timestamps. */
public final class EmergencyDepartment {
    private final PriorityQueue<Patient> waitingQueue = new PriorityQueue<>(new PatientComparator());
    private final HashMap<String, Patient> patients = new HashMap<>();
    private final Room[] rooms = new Room[10];
    private final Stack<Patient> dischargeHistory = new Stack<>();
    private final Clock clock;
    private long sequence;
    public EmergencyDepartment() { this(Clock.systemDefaultZone()); }
    public EmergencyDepartment(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
        for (int i = 0; i < rooms.length; i++) rooms[i] = new Room(i + 1);
    }
    public LocalDateTime now() { return LocalDateTime.now(clock); }
    public synchronized void registerPatient(Patient p) {
        if (p == null) throw new IllegalArgumentException("Patient is required.");
        if (patients.containsKey(p.getId())) throw new IllegalArgumentException("ID already exists.");
        p.register(sequence++, now()); patients.put(p.getId(), p); waitingQueue.add(p);
    }
    /** Validate the entire batch before registering anyone. Import appends to current data. */
    public synchronized void importPatients(List<Patient> batch) {
        if (batch == null || batch.isEmpty()) throw new IllegalArgumentException("Import contains no patients.");
        HashSet<String> ids = new HashSet<>(patients.keySet());
        for (Patient p : batch) {
            if (p == null || p.getArrivalTime() != null) throw new IllegalArgumentException("Import requires new patients.");
            if (!ids.add(p.getId())) throw new IllegalArgumentException("Duplicate ID: " + p.getId());
        }
        for (Patient p : batch) registerPatient(p);
    }
    public synchronized Patient peekNextPatient() { return waitingQueue.peek(); }
    /** Get/call next admits to a free room, so no patient can disappear between queue and room. */
    public synchronized Patient getNextPatient() {
        if (waitingQueue.isEmpty()) return null;
        for (Room room : rooms) if (!room.isOccupied()) return admitPatient(room.getNumber(), waitingQueue.peek());
        throw new IllegalStateException("All 10 rooms are occupied. Patient remains waiting.");
    }
    public synchronized Patient admitPatient(int roomNumber, Patient p) {
        Room room = room(roomNumber);
        if (p == null) throw new IllegalArgumentException("Patient is required.");
        if (room.isOccupied()) throw new IllegalStateException("Room is occupied. Patient remains waiting.");
        if (p.getArrivalTime() == null) {
            if (patients.containsKey(p.getId())) throw new IllegalArgumentException("ID already exists.");
            registerPatient(p);
        }
        if (patients.get(p.getId()) != p || !waitingQueue.contains(p))
            throw new IllegalArgumentException("Patient must be registered and waiting.");
        LocalDateTime time = now();
        room.occupy(p); waitingQueue.remove(p); p.admit(time); return p;
    }
    public synchronized Patient dischargePatient(int roomNumber) {
        Room room = room(roomNumber);
        if (!room.isOccupied()) throw new IllegalStateException("Room is already empty.");
        LocalDateTime time = now();
        Patient p = room.release(); p.discharge(time); dischargeHistory.push(p); return p;
    }
    private Room room(int number) {
        if (number < 1 || number > rooms.length) throw new IllegalArgumentException("Room must be 1–10.");
        return rooms[number - 1];
    }
    public synchronized Patient searchPatient(String id) { return id == null ? null : patients.get(id.trim()); }
    public synchronized List<Patient> waitingPatients() {
        ArrayList<Patient> result = new ArrayList<>(waitingQueue); result.sort(new PatientComparator()); return List.copyOf(result);
    }
    public synchronized List<Room> rooms() { return List.of(rooms); }
    public synchronized List<Patient> dischargeHistory() {
        ArrayList<Patient> result = new ArrayList<>(dischargeHistory); Collections.reverse(result); return List.copyOf(result);
    }
    public synchronized int totalPatients() { return patients.size(); }
    public synchronized int waitingCount() { return waitingQueue.size(); }
    public synchronized int occupiedCount() { return (int) Arrays.stream(rooms).filter(Room::isOccupied).count(); }
    public synchronized int availableCount() { return rooms.length - occupiedCount(); }
    public static EmergencyDepartment demo() {
        EmergencyDepartment ed = new EmergencyDepartment();
        String[] names = {"Avery Morgan", "Jordan Lee", "Casey Patel", "Taylor Jones", "Riley Chen",
                          "Sam Rivera", "Alex Williams", "Jamie Brooks", "Morgan Davis", "Drew Wilson", "Robin Kim", "Blake Ross"};
        String[] conditions = {"Critical injury", "Severe pain", "Broken wrist", "Fever", "Minor sprain"};
        for (int i = 0; i < names.length; i++) ed.registerPatient(new Patient("DEMO-" + (i + 1), names[i], conditions[i % 5], i % 5 + 1));
        for (int i = 0; i < 4; i++) ed.getNextPatient();
        return ed;
    }
}
