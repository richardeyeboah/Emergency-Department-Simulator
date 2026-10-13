import java.nio.file.Path;
import java.util.*;
public final class AssignmentTests {
    private static void check(boolean value) { if(!value) throw new AssertionError("Unexpected state"); }
    private static void rejected(Runnable action) { try { action.run(); } catch(IllegalArgumentException | IllegalStateException expected) { return; } throw new AssertionError("Should reject"); }
    private static void pass(int n,String text) { System.out.println("PASS Test "+n+": "+text); }
    public static void main(String[] args) throws Exception {
        EmergencyDepartment ed=new EmergencyDepartment();
        ed.importPatients(PatientImporter.read(Path.of("patients/assignment-patients.csv")));
        ed.admitPatient(1,ed.searchPatient("P001")); ed.admitPatient(3,ed.searchPatient("P003")); ed.admitPatient(10,ed.searchPatient("P006"));
        check(ed.occupiedCount()==3 && ed.waitingCount()==9); pass(1,"Admit patients");
        check(ed.rooms().size()==10 && ed.availableCount()==7); pass(2,"Display room statuses"); statuses(ed);
        rejected(()->ed.admitPatient(1,ed.searchPatient("P002"))); check(ed.waitingCount()==9); pass(3,"Occupied room rejected");
        String[] ids={"P002","P005","P004","P008","P007","P009","P010"}; int[] rooms={2,4,5,6,7,8,9};
        for(int i=0;i<rooms.length;i++) ed.admitPatient(rooms[i],ed.searchPatient(ids[i]));
        check(ed.occupiedCount()==10 && ed.waitingCount()==2); rejected(ed::getNextPatient); check(ed.waitingCount()==2); pass(4,"Fill all rooms; full-capacity call rejected");
        rejected(()->ed.admitPatient(1,null)); EmergencyDepartment empty=new EmergencyDepartment(); rejected(()->empty.admitPatient(1,null)); check(empty.totalPatients()==0 && ed.occupiedCount()==10); pass(5,"Null patient rejected");
        for(int room:new int[]{4,6,8}) { check(ed.rooms().get(room-1).getPatient().getSeverity()>=4); ed.dischargePatient(room); }
        check(ed.availableCount()==3); pass(6,"Discharge selected non-critical patients");
        check(!ed.rooms().get(3).isOccupied() && !ed.rooms().get(5).isOccupied() && !ed.rooms().get(7).isOccupied()); pass(7,"Updated room statuses"); statuses(ed);
        ed.dischargePatient(1); ed.dischargePatient(3); check(ed.dischargeHistory().size()==5); pass(8,"Discharge rooms 1 and 3 on simulated doctor advice");
        rejected(()->ed.dischargePatient(6)); check(ed.dischargeHistory().size()==5); pass(9,"Empty room 6 discharge rejected");
        check(ed.peekNextPatient().getId().equals("P011") && ed.searchPatient("P006").status().equals("In treatment"));
        check(ed.occupiedCount()==5 && ed.availableCount()==5 && ed.waitingCount()==2 && ed.totalPatients()==12); pass(10,"Final queue, lookup and occupancy"); statuses(ed);
        System.out.println("All ten supplied assignment scenarios passed.");
        EmergencyDepartment direct=new EmergencyDepartment(); direct.admitPatient(10,new Patient("P006","Lisa Anderson","Flu")); check(direct.occupiedCount()==1);
        rejected(()->direct.admitPatient(10,new Patient("P099","New patient","Flu"))); check(direct.searchPatient("P099")==null);
        rejected(()->direct.importPatients(List.of(new Patient("NEW","New","Flu"),new Patient("P006","Duplicate","Flu")))); check(direct.searchPatient("NEW")==null && direct.totalPatients()==1);
        System.out.println("PASS: direct constructor/admission compatibility and atomic import rollback");
        EmergencyDepartment timed=new EmergencyDepartment(java.time.Clock.fixed(java.time.Instant.parse("2026-10-13T12:00:00Z"),java.time.ZoneOffset.UTC));
        timed.importPatients(PatientImporter.read(Path.of("patients/assignment-patients.csv")));
        check(timed.searchPatient("P001").waitingDuration(timed.now()).toMinutes()==82);
        check(timed.searchPatient("P012").waitingDuration(timed.now()).toMinutes()==4);
        timed.registerPatient(new Patient("RECENT","Recent","Condition",1));
        timed.importPatients(List.of(new Patient("OLDER","Older","Condition",1,90)));
        check(timed.peekNextPatient().getId().equals("OLDER"));
        timed.getNextPatient();
        check(timed.searchPatient("OLDER").waitingDuration(timed.now()).toMinutes()==90);
        rejected(()->new Patient("BAD","Bad","Condition",1,-1));
        System.out.println("PASS: historical arrival offsets, chronological FIFO across imports, and frozen wait");
    }
    private static void statuses(EmergencyDepartment ed) {
        for(Room room:ed.rooms()) System.out.println("Room "+room.getNumber()+": "+(room.isOccupied()?room.getPatient():"Available"));
    }
}
