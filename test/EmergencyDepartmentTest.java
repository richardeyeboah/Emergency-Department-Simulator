import java.time.*;
import java.util.*;

public final class EmergencyDepartmentTest {
    private static int passed;

    private static Patient p(String id, int severity) {
        return new Patient(id, "Patient " + id, "Example condition", severity);
    }

    private static void check(boolean condition) {
        if (!condition) throw new AssertionError("Check failed");
    }

    private static void fails(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException expected) {
            return;
        }
        throw new AssertionError("Expected rejection");
    }

    private static void test(String label, Runnable action) {
        action.run();
        passed++;
        System.out.println("PASS " + passed + ": " + label);
    }

    public static void main(String[] args) {
        test(
                "Severity ordering across all five levels",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    for (int i = 5; i > 0; i--) e.registerPatient(p("" + i, i));
                    for (int i = 1; i <= 5; i++) check(e.getNextPatient().getSeverity() == i);
                });
        test(
                "FIFO tie break even with identical timestamps",
                () -> {
                    EmergencyDepartment e =
                            new EmergencyDepartment(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
                    for (int i = 0; i < 5; i++) e.registerPatient(p("" + i, 2));
                    for (int i = 0; i < 5; i++) check(e.getNextPatient().getId().equals("" + i));
                });
        test(
                "Peek and empty queue",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    check(e.peekNextPatient() == null && e.getNextPatient() == null);
                    Patient a = p("A", 1);
                    e.registerPatient(a);
                    check(
                            e.peekNextPatient() == a
                                    && e.peekNextPatient() == a
                                    && e.waitingCount() == 1);
                });
        test(
                "Admission updates exactly one room and queue",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    Patient a = p("A", 1);
                    e.registerPatient(a);
                    e.admitPatient(7, a);
                    check(
                            e.rooms().get(6).getPatient() == a
                                    && e.waitingCount() == 0
                                    && e.occupiedCount() == 1
                                    && a.getAdmissionTime() != null);
                });
        test(
                "Occupied room rejection preserves waiting patient",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    Patient a = p("A", 1), b = p("B", 2);
                    e.registerPatient(a);
                    e.registerPatient(b);
                    e.admitPatient(1, a);
                    fails(() -> e.admitPatient(1, b));
                    check(
                            e.waitingCount() == 1
                                    && e.peekNextPatient() == b
                                    && b.getAdmissionTime() == null
                                    && e.rooms().get(0).getPatient() == a);
                });
        test(
                "Full capacity rejection and later retry",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    for (int i = 0; i < 11; i++) e.registerPatient(p("" + i, 1));
                    for (int i = 0; i < 10; i++) e.getNextPatient();
                    Patient next = e.peekNextPatient();
                    fails(e::getNextPatient);
                    check(
                            e.waitingCount() == 1
                                    && e.availableCount() == 0
                                    && next.getAdmissionTime() == null);
                    e.dischargePatient(3);
                    check(
                            e.getNextPatient() == next
                                    && e.waitingCount() == 0
                                    && e.occupiedCount() == 10);
                });
        test(
                "Discharge frees room; history uses LIFO",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    Patient a = p("A", 1), b = p("B", 1);
                    e.registerPatient(a);
                    e.registerPatient(b);
                    e.getNextPatient();
                    e.getNextPatient();
                    e.dischargePatient(1);
                    e.dischargePatient(2);
                    check(
                            e.dischargeHistory().equals(List.of(b, a))
                                    && e.availableCount() == 10
                                    && a.getDischargeTime() != null
                                    && e.searchPatient("A") == a);
                });
        test(
                "Invalid discharge and room numbers do not mutate state",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    fails(() -> e.dischargePatient(1));
                    fails(() -> e.dischargePatient(0));
                    fails(() -> e.dischargePatient(11));
                    check(e.occupiedCount() == 0 && e.dischargeHistory().isEmpty());
                });
        test(
                "Null, duplicate, invalid and foreign patients rejected",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    fails(() -> e.registerPatient(null));
                    fails(() -> new Patient("", "A", "B", 1));
                    fails(() -> p("bad", 0));
                    fails(() -> p("bad", 6));
                    Patient a = p("A", 1);
                    e.registerPatient(a);
                    fails(() -> e.registerPatient(p("A", 2)));
                    fails(() -> e.admitPatient(1, null));
                    fails(() -> e.admitPatient(1, p("A", 1)));
                    check(
                            e.searchPatient(null) == null
                                    && e.searchPatient("missing") == null
                                    && e.totalPatients() == 1
                                    && e.waitingCount() == 1);
                });
        test(
                "Arrival, live wait, treatment and frozen discharge durations",
                () -> {
                    MutableClock clock = new MutableClock();
                    EmergencyDepartment e = new EmergencyDepartment(clock);
                    Patient a = p("A", 1);
                    e.registerPatient(a);
                    clock.advance(120);
                    check(a.waitingDuration(e.now()).getSeconds() == 120);
                    e.getNextPatient();
                    clock.advance(180);
                    check(
                            a.waitingDuration(e.now()).getSeconds() == 120
                                    && a.treatmentDuration(e.now()).getSeconds() == 180);
                    e.dischargePatient(1);
                    clock.advance(600);
                    check(
                            a.treatmentDuration(e.now()).getSeconds() == 180
                                    && a.status().equals("Discharged"));
                });
        test(
                "Snapshots cannot be edited; registered lifecycle cannot be reused",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    Patient a = p("A", 1);
                    e.registerPatient(a);
                    try {
                        e.waitingPatients().clear();
                        throw new AssertionError();
                    } catch (UnsupportedOperationException expected) {
                    }
                    fails(() -> new EmergencyDepartment().registerPatient(a));
                    e.getNextPatient();
                    fails(() -> e.admitPatient(2, a));
                    e.dischargePatient(1);
                    fails(() -> e.admitPatient(2, a));
                    check(
                            e.totalPatients()
                                    == e.waitingCount()
                                            + e.occupiedCount()
                                            + e.dischargeHistory().size());
                });
        test(
                "Imported fixture conserves all 12 fictional patients",
                () -> {
                    EmergencyDepartment e = new EmergencyDepartment();
                    try {
                        e.importPatients(
                                PatientImporter.read(
                                        java.nio.file.Path.of("patients/assignment-patients.csv")));
                    } catch (java.io.IOException ex) {
                        throw new AssertionError(ex);
                    }
                    for (int i = 0; i < 4; i++) e.getNextPatient();
                    check(
                            e.totalPatients() == 12
                                    && e.waitingCount() == 8
                                    && e.occupiedCount() == 4
                                    && e.availableCount() == 6);
                });
        System.out.println("All " + passed + " automated tests passed.");
    }

    private static final class MutableClock extends Clock {
        private Instant instant = Instant.parse("2026-01-01T12:00:00Z");

        void advance(long seconds) {
            instant = instant.plusSeconds(seconds);
        }

        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        public Clock withZone(ZoneId zone) {
            return Clock.fixed(instant, zone);
        }

        public Instant instant() {
            return instant;
        }
    }
}
