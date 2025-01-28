package org.example;

import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class JobTableModel extends DefaultTableModel {
    private final List<JobAdvert> jobs;

    public JobTableModel(List<JobAdvert> jobs, String[] columnNames) {
        super(columnNames, 0); // Initialize with column names
        this.jobs = jobs;

        // Populate the table with initial data
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (JobAdvert job : jobs) {
            addRow(new Object[]{
                    job.getJobId(),
                    job.getTitle(),
                    job.getCompany(),
                    String.join(";", job.getSkills()),
                    job.getLatitude(),
                    job.getLongitude(),
                    job.getSalary(),
                    sdf.format(job.getDeadline())
            });
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true; // Allow editing of all cells
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        JobAdvert job = jobs.get(row); // Get the job corresponding to the row
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        switch (column) {
            case 0: // Job ID
                job.setJobId(Integer.parseInt(aValue.toString()));
                break;
            case 1: // Title
                job.setTitle(aValue.toString());
                break;
            case 2: // Company
                job.setCompany(aValue.toString());
                break;
            case 3: // Skills
                String skillsStr = aValue.toString();
                job.setSkills(
                        List.of(skillsStr.split(";"))
                                .stream()
                                .map(String::trim)
                                .collect(Collectors.toList())
                );
                break;
            case 4: // Latitude
                job.setLatitude(Double.parseDouble(aValue.toString()));
                break;
            case 5: // Longitude
                job.setLongitude(Double.parseDouble(aValue.toString()));
                break;
            case 6: // Salary
                job.setSalary(Double.parseDouble(aValue.toString()));
                break;
            case 7: // Deadline
                try {
                    job.setDeadline(sdf.parse(aValue.toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }

        super.setValueAt(aValue, row, column); // Notify the table of the update
    }
}
