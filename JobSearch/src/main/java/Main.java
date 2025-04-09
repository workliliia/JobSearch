import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Start Prometheus metrics HTTP server (http://localhost:8000/metrics)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MetricsServer.start();
        GUI gui = new GUI();
    }
}
