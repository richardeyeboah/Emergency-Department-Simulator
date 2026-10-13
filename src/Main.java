import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Application entry point: create the hospital model, then display its Swing interface. */
public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> {
                    try {
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    } catch (Exception ignored) {
                        // Keep the default appearance if the requested look and feel is
                        // unavailable.
                    }
                    EmergencyDepartment department = new EmergencyDepartment();
                    EmergencyDepartmentGUI dashboard = new EmergencyDepartmentGUI(department);
                    dashboard.setVisible(true);
                });
    }
}
