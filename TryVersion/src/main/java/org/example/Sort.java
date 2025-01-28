package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Sort {

    /**
     * Sorts a list of jobs based on the selected column.
     *
     * @param jobs       the list of jobs to sort
     * @param sortColumn the name of the column to sort by (e.g. "Job ID", "Title", "Company", "Salary", or "Deadline")
     * @return           a new List of JobAdvert objects, sorted according to the column
     */
    public static List<JobAdvert> sortJobs(List<JobAdvert> jobs, String sortColumn) {
        // If no sort column is provided, just return the original list
        if (sortColumn == null || sortColumn.isEmpty()) {
            return jobs;
        }

        // Copy the list so we don't modify the original
        List<JobAdvert> sortedList = new ArrayList<>(jobs);

        switch (sortColumn) {
            case "Job ID":
                sortedList.sort(Comparator.comparingInt(JobAdvert::getJobId));
                break;
            case "Title":
                sortedList.sort(Comparator.comparing(
                        JobAdvert::getTitle,
                        String.CASE_INSENSITIVE_ORDER
                ));
                break;
            case "Company":
                sortedList.sort(Comparator.comparing(
                        JobAdvert::getCompany,
                        String.CASE_INSENSITIVE_ORDER
                ));
                break;
            case "Salary":
                sortedList.sort(Comparator.comparingDouble(JobAdvert::getSalary));
                break;
            case "Deadline":
                // Assuming getDeadline() returns a java.util.Date
                sortedList.sort(Comparator.comparing(JobAdvert::getDeadline));
                break;
            default:
                // Unrecognized column => do no sorting
                break;
        }

        return sortedList;
    }
}
