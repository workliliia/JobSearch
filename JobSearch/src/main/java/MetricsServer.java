import io.prometheus.client.Counter;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

public class MetricsServer {

    // Example metric: count how many times jobs are added
    public static final Counter jobsAdded = Counter.build()
            .name("job_add_total")
            .help("Total number of jobs added")
            .register();

    public static void start() {
        try {
            // Start the HTTP server on port 8000
            HTTPServer server = new HTTPServer(8000);
            DefaultExports.initialize(); // Adds JVM and system metrics
            System.out.println("Prometheus metrics server running on http://localhost:8000/metrics");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
