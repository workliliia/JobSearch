import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JobStressTester {

    public static void main(String[] args) throws ParseException {
        int jobCount = 100;


        System.out.println("Generating " + jobCount + " fake jobs...");

        long startTime = System.currentTimeMillis();
        List<JobAdvert> jobs = generateFakeJobs(jobCount);
        long endTime = System.currentTimeMillis();

        System.out.println("Generated " + jobCount + " jobs in " + (endTime - startTime) + " ms");

        measureMemoryUsage();
    }

    public static List<JobAdvert> generateFakeJobs(int count) throws ParseException {
        List<JobAdvert> jobs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = sdf.parse("2025-12-31");

        for (int i = 0; i < count; i++) {
            JobAdvert job = new JobAdvert(
                    i,
                    "Software Engineer " + i,
                    "Company " + (i % 100),
                    List.of("Java", "C++", "Python"),
                    50.0 + (i % 10),
                    -1.0 + (i % 10),
                    50_000 + (i % 10) * 1000,
                    deadline
            );
            jobs.add(job);
        }
        return jobs;
    }

    public static void measureMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        System.out.println("Approx memory used: " + memoryUsed + " MB");
    }
}
