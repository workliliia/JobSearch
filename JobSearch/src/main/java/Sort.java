import java.util.List;

/**
 * Sort provides a static implementation of the QuickSort algorithm
 * tailored to sort a list of JobAdvert objects by various attributes
 * such as salary, title, company, job ID, or deadline.
 */
public class Sort {

    // Swaps two elements in the job list
    private static void swap(List<JobAdvert> jobs, int i, int j) {
        JobAdvert temp = jobs.get(i);
        jobs.set(i, jobs.get(j));
        jobs.set(j, temp);
    }

    // Partitions the list around a pivot for QuickSort (using middle element as pivot)
    private static int partition(List<JobAdvert> jobs, int low, int high, String attribute) {
        int mid = low + (high - low) / 2;
        JobAdvert pivot = jobs.get(mid); // Choose middle element as pivot
        swap(jobs, mid, high); // Move pivot to the end for standard partitioning

        int i = low - 1;
        for (int j = low; j < high; j++) {
            // Compare each element with the pivot based on the selected attribute
            if (compare(jobs.get(j), pivot, attribute) <= 0) {
                i++;
                swap(jobs, i, j); // Move smaller element to the left partition
            }
        }

        swap(jobs, i + 1, high); // Place pivot in correct position
        return i + 1;
    }

    // Public method to perform QuickSort on the list by the given attribute
    public static void quickSort(List<JobAdvert> jobs, int low, int high, String attribute) {
        if (low < high) {
            int pi = partition(jobs, low, high, attribute); // Partition index
            quickSort(jobs, low, pi - 1, attribute);  // Sort left side
            quickSort(jobs, pi + 1, high, attribute); // Sort right side
        }
    }

    // Compares two JobAdvert objects based on the selected attribute
    private static int compare(JobAdvert job1, JobAdvert job2, String attribute) {
        return switch (attribute) {
            case "Salary" -> Double.compare(job1.getSalary(), job2.getSalary());
            case "Title" -> job1.getTitle().compareTo(job2.getTitle());
            case "Job ID" -> Integer.compare(job1.getJobId(), job2.getJobId());
            case "Company" -> job1.getCompany().compareTo(job2.getCompany());
            case "Deadline" -> job1.getDeadline().compareTo(job2.getDeadline());
            default -> throw new IllegalArgumentException("Unknown attribute for comparison");
        };
    }
}
