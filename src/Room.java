public final class Room {
    private final int number;
    private Patient patient;
    Room(int number) { this.number = number; }
    public int getNumber() { return number; }
    public Patient getPatient() { return patient; }
    public boolean isOccupied() { return patient != null; }
    void occupy(Patient patient) {
        if (isOccupied()) throw new IllegalStateException("Room is occupied.");
        this.patient = java.util.Objects.requireNonNull(patient);
    }
    Patient release() { Patient result = patient; patient = null; return result; }
}
