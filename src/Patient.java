import java.time.*;
import java.util.Objects;

/** Patient identity and triage are immutable; lifecycle changes belong to the department. */
public final class Patient {
    private final String id, name, condition;
    private final int severity;
    private final long arrivedMinutesAgo;
    private LocalDateTime arrivalTime, admissionTime, dischargeTime;
    private long arrivalSequence = -1;

    /** Assignment-compatible constructor; unspecified triage defaults to urgent (3). */
    public Patient(String id, String name, String condition) {
        this(id, name, condition, 3);
    }

    public Patient(String id, String name, String condition, int severity) {
        this(id, name, condition, severity, 0);
    }

    /** Historical arrival offset used only when importing fictional presentation patients. */
    public Patient(String id, String name, String condition, int severity, long arrivedMinutesAgo) {
        this.id = required(id, "ID");
        this.name = required(name, "Name");
        this.condition = required(condition, "Condition");
        if (severity < 1 || severity > 5) throw new IllegalArgumentException("Triage must be 1–5.");
        if (arrivedMinutesAgo < 0 || arrivedMinutesAgo > 10080)
            throw new IllegalArgumentException("Arrival offset must be 0–10080 minutes.");
        this.arrivedMinutesAgo = arrivedMinutesAgo;
        this.severity = severity;
    }

    private static String required(String s, String field) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(field + " is required.");
        return s.trim();
    }

    long getArrivedMinutesAgo() {
        return arrivedMinutesAgo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public int getSeverity() {
        return severity;
    }

    public long getArrivalSequence() {
        return arrivalSequence;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public LocalDateTime getAdmissionTime() {
        return admissionTime;
    }

    public LocalDateTime getDischargeTime() {
        return dischargeTime;
    }

    void register(long sequence, LocalDateTime time) {
        if (arrivalTime != null) throw new IllegalArgumentException("Patient already registered.");
        arrivalSequence = sequence;
        arrivalTime = Objects.requireNonNull(time);
    }

    void admit(LocalDateTime time) {
        admissionTime = time;
    }

    void discharge(LocalDateTime time) {
        dischargeTime = time;
    }

    public Duration waitingDuration(LocalDateTime now) {
        return arrivalTime == null
                ? Duration.ZERO
                : Duration.between(arrivalTime, admissionTime == null ? now : admissionTime);
    }

    public Duration treatmentDuration(LocalDateTime now) {
        return admissionTime == null
                ? Duration.ZERO
                : Duration.between(admissionTime, dischargeTime == null ? now : dischargeTime);
    }

    public String status() {
        return dischargeTime != null
                ? "Discharged"
                : admissionTime != null ? "In treatment" : "Waiting";
    }

    @Override
    public String toString() {
        return id + " · " + name + " · Triage " + severity;
    }
}
