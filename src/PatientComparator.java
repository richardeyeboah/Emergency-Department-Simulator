import java.util.Comparator;
public final class PatientComparator implements Comparator<Patient> {
    @Override public int compare(Patient a, Patient b) {
        int severity = Integer.compare(a.getSeverity(), b.getSeverity());
        return severity != 0 ? severity : Long.compare(a.getArrivalSequence(), b.getArrivalSequence());
    }
}
