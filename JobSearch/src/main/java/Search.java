import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Search {
    private JobAdvertNode root;
    private final List<JobAdvert> allJobs = new ArrayList<>();
    public Search() {
        this.root = null;
    }

    public void insert(JobAdvert job) {
        this.root = insertRec(this.root, job);
        allJobs.add(job);
    }

    public void insertJobs(List<JobAdvert> jobList) {
        for (JobAdvert job : jobList) {
            insert(job);
        }
    }

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
    public List<JobAdvert> getAllJobs() {
        return new ArrayList<>(allJobs);
    }

    public void delete(JobAdvert job) {
        root = deleteRec(root, job);
    }

    private JobAdvertNode deleteRec(JobAdvertNode current, JobAdvert job) {
        if (current == null) {
            return null;
        }

        if (job.equals(current.job)) {
            if (current.left == null && current.right == null) {
                return null;
            }
            if (current.right == null) {
                return current.left;
            }
            if (current.left == null) {
                return current.right;
            }
            JobAdvert smallestJob = findSmallest(current.right);
            current.job = smallestJob;
            current.right = deleteRec(current.right, smallestJob);
            return current;
        }

        if (Comparator.comparing(JobAdvert::getTitle).compare(job, current.job) < 0) {
            current.left = deleteRec(current.left, job);
        } else {
            current.right = deleteRec(current.right, job);
        }
        return current;
    }

    private JobAdvert findSmallest(JobAdvertNode root) {
        return root.left == null ? root.job : findSmallest(root.left);
    }

    public List<JobAdvert> getMatchingJobs(String attribute, String value) {
        List<JobAdvert> matchingJobs = new ArrayList<>();
        getMatchingJobsRec(root, attribute, value.toLowerCase(), matchingJobs);
        return matchingJobs;
    }

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

    private String getAttributeValue(JobAdvert job, String attribute) {
        switch (attribute) {
            case "Title":
                return job.getTitle();
            case "Company":
                return job.getCompany();
            case "Skills":
                return String.join(";", job.getSkills());
            default:
                return "";
        }
    }

    private class JobAdvertNode {
        JobAdvert job;
        JobAdvertNode left, right;

        public JobAdvertNode(JobAdvert job) {
            this.job = job;
            this.left = null;
            this.right = null;
        }
    }
    public void clear() {
        root = null;
        allJobs.clear();
    }
}
