import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class PatientImporter {
    private PatientImporter() {}
    /** CSV with quoted fields (including escaped quotes), one patient per line. */
    public static List<Patient> read(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        if (lines.isEmpty() || !lines.getFirst().replace("\uFEFF","").trim().equalsIgnoreCase("id,name,condition,severity"))
            throw new IllegalArgumentException("Expected header: id,name,condition,severity");
        List<Patient> patients = new ArrayList<>();
        for (int i=1;i<lines.size();i++) {
            if(lines.get(i).isBlank()) continue;
            try {
                List<String> fields = fields(lines.get(i));
                if(fields.size()!=4) throw new IllegalArgumentException("Expected four columns.");
                patients.add(new Patient(fields.get(0),fields.get(1),fields.get(2),Integer.parseInt(fields.get(3).trim())));
            } catch(IllegalArgumentException ex) {
                throw new IllegalArgumentException("Line "+(i+1)+": "+ex.getMessage());
            }
        }
        return List.copyOf(patients);
    }
    private static List<String> fields(String line) {
        List<String> result=new ArrayList<>();
        StringBuilder value=new StringBuilder();
        boolean quoted=false, closed=false;
        for(int i=0;i<line.length();i++) {
            char c=line.charAt(i);
            if(quoted) {
                if(c=='"') {
                    if(i+1<line.length() && line.charAt(i+1)=='"') { value.append('"'); i++; }
                    else { quoted=false; closed=true; }
                } else value.append(c);
            } else if(c==',') {
                result.add(value.toString()); value.setLength(0); closed=false;
            } else if(c=='"' && value.length()==0 && !closed) quoted=true;
            else {
                if(closed || c=='"') throw new IllegalArgumentException("Invalid CSV quoting.");
                value.append(c);
            }
        }
        if(quoted) throw new IllegalArgumentException("Unclosed CSV quote.");
        result.add(value.toString()); return result;
    }
}
