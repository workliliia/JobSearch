import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JobStressTester is a utility class for stress testing the JobAdvert model.
 * It generates a large number of fake job adverts and measures performance
 * and memory usage for testing scalability and efficiency.
 */
public class JobStressTester {

    public static void main(String[] args) throws ParseException {
        int jobCount = 100; // Number of jobs to generate for the test

        System.out.println("Generating " + jobCount + " fake jobs...");

        // Measure time taken to generate job adverts
        long startTime = System.currentTimeMillis();
        List<JobAdvert> jobs = generateFakeJobs(jobCount);
        long endTime = System.currentTimeMillis();

        System.out.println("Generated " + jobCount + " jobs in " + (endTime - startTime) + " ms");

        // Measure memory usage after generation
        measureMemoryUsage();
    }

    // Generates a list of fake JobAdvert objects for testing
    public static List<JobAdvert> generateFakeJobs(int count) throws ParseException {
        List<JobAdvert> jobs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date deadline = sdf.parse("31/12/2025"); // Common deadline for all jobs

        for (int i = 0; i < count; i++) {
            JobAdvert job = new JobAdvert(
                    i,
                    "Software Engineer " + i,
                    "Company " + (i % 100), // Repeating company names
                    List.of("Java", "C++", "Python"),
                    50.0 + (i % 10),        // Varying latitude
                    -1.0 + (i % 10),        // Varying longitude
                    50_000 + (i % 10) * 1000, // Varying salary
                    deadline
            );
            jobs.add(job);
        }

        return jobs;
    }

    // Measures and prints the approximate memory used by the JVM
    public static void measureMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Suggest garbage collection to get a more accurate reading

        long memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        System.out.println("Approx memory used: " + memoryUsed + " MB");
    }
}
