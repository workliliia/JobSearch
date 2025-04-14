import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Search provides insertion, deletion, and keyword-based search capabilities
 * for JobAdvert objects using a binary search tree (BST) based on job titles.
 * Also maintains a list of all jobs for easy access and reset.
 */
public class Search {
    private JobAdvertNode root;
    private final List<JobAdvert> allJobs = new ArrayList<>();

    public Search() {
        this.root = null;
    }

    // Inserts a single job into the BST and adds it to the allJobs list
    public void insert(JobAdvert job) {
        this.root = insertRec(this.root, job);
        allJobs.add(job);
    }

    // Inserts a list of jobs
    public void insertJobs(List<JobAdvert> jobList) {
        for (JobAdvert job : jobList) {
            insert(job);
        }
    }

    // Recursive insertion into the BST based on job title
    private JobAdvertNode insertRec(JobAdvertNode current, JobAdvert job) {
        if (current == null) {
            return new JobAdvertNode(job);
        }

        if (Comparator.comparing(JobAdvert::getTitle).compare(job, current.job) < 0) {
            current.left = insertRec(current.left, job);
        } else if (Comparator.comparing(JobAdvert::getTitle).compare(job, current.job) > 0) {
            current.right = insertRec(current.right, job);
        }

        return current;
    }

    // Returns a copy of all stored jobs
    public List<JobAdvert> getAllJobs() {
        return new ArrayList<>(allJobs);
    }

    // Removes a job from the BST (does not remove from allJobs list)
    public void delete(JobAdvert job) {
        root = deleteRec(root, job);
    }

    // Recursive deletion from BST
    private JobAdvertNode deleteRec(JobAdvertNode current, JobAdvert job) {
        if (current == null) {
            return null;
        }

        if (job.equals(current.job)) {
            // Case 1: No children
            if (current.left == null && current.right == null) {
                return null;
            }
            // Case 2: One child
            if (current.right == null) return current.left;
            if (current.left == null) return current.right;

            // Case 3: Two children - replace with smallest in right subtree
            JobAdvert smallestJob = findSmallest(current.right);
            current.job = smallestJob;
            current.right = deleteRec(current.right, smallestJob);
            return current;
        }

        // Navigate left or right based on comparison
        if (Comparator.comparing(JobAdvert::getTitle).compare(job, current.job) < 0) {
            current.left = deleteRec(current.left, job);
        } else {
            current.right = deleteRec(current.right, job);
        }

        return current;
    }

    // Finds the node with the smallest value in a subtree
    private JobAdvert findSmallest(JobAdvertNode root) {
        return root.left == null ? root.job : findSmallest(root.left);
    }

    // Returns a list of jobs where the selected attribute contains the given value (case-insensitive)
    public List<JobAdvert> getMatchingJobs(String attribute, String value) {
        List<JobAdvert> matchingJobs = new ArrayList<>();
        getMatchingJobsRec(root, attribute, value.toLowerCase(), matchingJobs);
        return matchingJobs;
    }

    // Recursive helper for keyword search
    private void getMatchingJobsRec(JobAdvertNode current, String attribute, String value, List<JobAdvert> matchingJobs) {
        if (current == null) {
            return;
        }

        String currentValue = getAttributeValue(current.job, attribute).toLowerCase();
        if (currentValue.contains(value)) {
            matchingJobs.add(current.job);
        }

        getMatchingJobsRec(current.left, attribute, value, matchingJobs);
        getMatchingJobsRec(current.right, attribute, value, matchingJobs);
    }

    // Retrieves a string representation of the selected attribute for searching
    private String getAttributeValue(JobAdvert job, String attribute) {
        return switch (attribute) {
            case "Title" -> job.getTitle();
            case "Company" -> job.getCompany();
            case "Skills" -> String.join(";", job.getSkills());
            default -> "";
        };
    }

    // Inner class representing a node in the binary search tree
    private static class JobAdvertNode {
        JobAdvert job;
        JobAdvertNode left, right;

        public JobAdvertNode(JobAdvert job) {
            this.job = job;
            this.left = null;
            this.right = null;
        }
    }

    // Clears the BST and allJobs list
    public void clear() {
        root = null;
        allJobs.clear();
    }
}
