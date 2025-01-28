package org.example;

import java.util.Date;
import java.util.List;

/**
 * Simple model class to represent a Job Advertisement.
 */
public class JobAdvert {
    private int jobId;
    private String title;
    private String company;
    private List<String> skills;
    private double latitude;
    private double longitude;
    private double salary;
    private Date deadline;

    public JobAdvert(int jobId, String title, String company,
                     List<String> skills, double latitude,
                     double longitude, double salary, Date deadline) {
        this.jobId = jobId;
        this.title = title;
        this.company = company;
        this.skills = skills;
        this.latitude = latitude;
        this.longitude = longitude;
        this.salary = salary;
        this.deadline = deadline;
    }

    // --- Getters and setters (optional) ---
    public int getJobId() {
        return jobId;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public List<String> getSkills() {
        return skills;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getSalary() {
        return salary;
    }

    public Date getDeadline() {
        return deadline;
    }

    // --- Setters ---
    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    // --- toString() for debugging ---

    @Override
    public String toString() {
        return "JobAdvert{" +
                "jobId=" + jobId +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", skills=" + skills +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", salary=" + salary +
                ", deadline=" + deadline +
                '}';
    }
}
