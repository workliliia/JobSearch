package org.example;

import java.util.List;
import java.util.stream.Collectors;

public class Search {

    /**
     * Filters a list of jobs based on the given attribute and search term.
     *
     * @param jobs       the list of jobs to search within
     * @param attribute  the attribute to filter by (e.g. "Title", "Company", or "Skills")
     * @param searchTerm the text the user is looking for
     * @return           a filtered List of JobAdvert objects
     */
    public static List<JobAdvert> filterJobs(List<JobAdvert> jobs, String attribute, String searchTerm) {
        // If there's no search term, just return the entire list
        if (searchTerm == null || searchTerm.isEmpty()) {
            return jobs;
        }

        // Convert to lowercase for case-insensitive matching
        String lowerCaseTerm = searchTerm.toLowerCase();

        return jobs.stream().filter(job -> {
            switch (attribute) {
                case "Title":
                    return job.getTitle().toLowerCase().contains(lowerCaseTerm);
                case "Company":
                    return job.getCompany().toLowerCase().contains(lowerCaseTerm);
                case "Skills":
                    // Check if any skill matches the search value
                    return job.getSkills().stream()
                            .anyMatch(skill -> skill.toLowerCase().contains(lowerCaseTerm));
                default:
                    // Unsupported attribute => do no filtering
                    return true;
            }
        }).collect(Collectors.toList());
    }
}
