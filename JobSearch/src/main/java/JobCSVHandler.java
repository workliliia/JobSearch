import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobCSVHandler {

    public static void saveJobListToCSV(File file, List<JobAdvert> jobs) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write("Job ID,Title,Company,Skills,Latitude,Longitude,Salary,Deadline\n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (JobAdvert job : jobs) {
            writer.write(job.getJobId() + ",");
            writer.write(job.getTitle() + ",");
            writer.write(job.getCompany() + ",");
            writer.write(String.join(";", job.getSkills()) + ",");
            writer.write(job.getLatitude() + ",");
            writer.write(job.getLongitude() + ",");
            writer.write(job.getSalary() + ",");
            writer.write(sdf.format(job.getDeadline()) + "\n");
        }
        writer.close();
    }

    public static List<JobAdvert> loadJobsFromCSV(File file) throws IOException, ParseException {
        List<JobAdvert> jobs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine(); // Skip header
        int lineNumber = 1;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            String[] data = parseCSVLine(line);
            if (data.length != 8) continue;

            int jobId = Integer.parseInt(data[0].trim());
            String title = data[1].trim();
            String company = data[2].trim();
            List<String> skills = Arrays.asList(data[3].trim().split(";"));
            double latitude = Double.parseDouble(data[4].trim());
            double longitude = Double.parseDouble(data[5].trim());
            double salary = Double.parseDouble(data[6].trim());
            Date deadline = sdf.parse(data[7].trim());

            jobs.add(new JobAdvert(jobId, title, company, skills, latitude, longitude, salary, deadline));
        }
        reader.close();
        return jobs;
    }

    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|([^,]+)").matcher(line);
        while (matcher.find()) {
            values.add(matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
        }
        return values.toArray(new String[0]);
    }
}    