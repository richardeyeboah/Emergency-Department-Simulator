import javax.swing.*;
import java.util.Arrays;
public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new EmergencyDepartmentGUI(Arrays.asList(args).contains("--demo") ? EmergencyDepartment.demo() : new EmergencyDepartment()).setVisible(true);
        });
    }
}
