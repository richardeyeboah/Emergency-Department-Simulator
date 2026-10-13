import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class PatientImporter {
    private PatientImporter() {}

    /** CSV with quoted fields (including escaped quotes), one patient per line. */
    public static List<Patient> read(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        String header = lines.isEmpty() ? "" : lines.getFirst().replace("\uFEFF", "").trim();
        boolean timed = header.equalsIgnoreCase("id,name,condition,severity,arrived_minutes_ago");
        if (!timed && !header.equalsIgnoreCase("id,name,condition,severity"))
            throw new IllegalArgumentException(
                    "Expected id,name,condition,severity with optional arrived_minutes_ago"
                        + " column.");
        List<Patient> patients = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            try {
                List<String> fields = fields(lines.get(i));
                if (fields.size() != (timed ? 5 : 4))
                    throw new IllegalArgumentException("Column count does not match header.");
                patients.add(
                        new Patient(
                                fields.get(0),
                                fields.get(1),
                                fields.get(2),
                                Integer.parseInt(fields.get(3).trim()),
                                timed ? Long.parseLong(fields.get(4).trim()) : 0));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Line " + (i + 1) + ": " + ex.getMessage());
            }
        }
        return List.copyOf(patients);
    }

    private static List<String> fields(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false, closed = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        value.append('"');
                        i++;
                    } else {
                        quoted = false;
                        closed = true;
                    }
                } else value.append(c);
            } else if (c == ',') {
                result.add(value.toString());
                value.setLength(0);
                closed = false;
            } else if (c == '"' && value.length() == 0 && !closed) quoted = true;
            else {
                if (closed || c == '"') throw new IllegalArgumentException("Invalid CSV quoting.");
                value.append(c);
            }
        }
        if (quoted) throw new IllegalArgumentException("Unclosed CSV quote.");
        result.add(value.toString());
        return result;
    }
}
