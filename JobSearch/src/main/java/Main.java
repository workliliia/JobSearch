import javax.swing.UIManager;

/**
 * Main class that serves as the entry point for the Job Advertisement Manager application.
 * It sets the Nimbus look and feel, starts the Prometheus metrics server, and launches the GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Set the Nimbus look and feel for the GUI (if available)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log any errors in setting the look and feel
        }

        // Start the Prometheus metrics server on port 8000
        MetricsServer.start();

        // Launch the main GUI window
        new GUI();
    }
}



