package org.example;
import java.util.List;

public class Sort {

    // Swap function adapted for JobAdvert list
    private static void swap(List<JobAdvert> jobs, int i, int j) {
        JobAdvert temp = jobs.get(i);
        jobs.set(i, jobs.get(j));
        jobs.set(j, temp);
    }

    // Partition function adapted for JobAdvert list and an attribute
    private static int partition(List<JobAdvert> jobs, int low, int high, String attribute) {
        JobAdvert pivot = jobs.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (compare(jobs.get(j), pivot, attribute) <= 0) {
                i++;
                swap(jobs, i, j);
            }
        }
        swap(jobs, i + 1, high);
        return i + 1;
    }

    // QuickSort function for JobAdvert list
    public static void quickSort(List<JobAdvert> jobs, int low, int high, String attribute) {
        if (low < high) {
            int pi = partition(jobs, low, high, attribute);
            quickSort(jobs, low, pi - 1, attribute);
            quickSort(jobs, pi + 1, high, attribute);
        }
    }

    private static int compare(JobAdvert job1, JobAdvert job2, String attribute) {
        switch (attribute) {
            case "Salary":
                return Double.compare(job1.getSalary(), job2.getSalary());
            case "Title":
                return job1.getTitle().compareTo(job2.getTitle());
            case "Job ID":
                return Integer.compare(job1.getJobId(), job2.getJobId());
            case "Company":
                return job1.getCompany().compareTo(job2.getCompany());
            case "Deadline":
                return job1.getDeadline().compareTo(job2.getDeadline());
            default:
                throw new IllegalArgumentException("Unknown attribute for comparison");
        }
    }

}
