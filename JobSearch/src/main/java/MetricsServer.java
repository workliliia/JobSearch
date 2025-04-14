import io.prometheus.client.Counter;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

/**
 * MetricsServer sets up and runs a Prometheus HTTP server to expose application metrics.
 * It includes a custom counter for job additions and default JVM/system metrics.
 */
public class MetricsServer {

    // Custom metric to count how many times jobs are added
    public static final Counter jobsAdded = Counter.build()
            .name("job_add_total") // Metric name as it appears in Prometheus
            .help("Total number of jobs added") // Description for Prometheus
            .register(); // Register with Prometheus collector

    // Starts the Prometheus metrics server on port 8000
    public static void start() {
        try {
            HTTPServer server = new HTTPServer(8000); // Launch metrics server
            DefaultExports.initialize(); // Expose JVM and system-level metrics
            System.out.println("Prometheus metrics server running on http://localhost:8000/metrics");
        } catch (Exception e) {
            e.printStackTrace(); // Log any errors
        }
    }
}
