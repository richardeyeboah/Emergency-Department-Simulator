import javax.swing.*;
public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new EmergencyDepartmentGUI(new EmergencyDepartment()).setVisible(true);
        });
    }
}
