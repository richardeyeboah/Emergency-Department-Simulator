import java.util.Comparator;
public final class PatientComparator implements Comparator<Patient> {
    @Override public int compare(Patient a, Patient b) {
        int severity = Integer.compare(a.getSeverity(), b.getSeverity());
        if (severity != 0) return severity;
        int arrival = a.getArrivalTime().compareTo(b.getArrivalTime());
        return arrival != 0 ? arrival : Long.compare(a.getArrivalSequence(), b.getArrivalSequence());
    }
}
