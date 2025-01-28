package org.example;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JobTableModel extends AbstractTableModel {
    private List<JobAdvert> jobs;
    private final String[] columnNames;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public JobTableModel(List<JobAdvert> jobs, String[] columnNames) {
        this.jobs = new ArrayList<>(jobs);
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        return jobs.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        JobAdvert job = jobs.get(rowIndex);
        switch (columnIndex) {
            case 0: return job.getJobId();
            case 1: return job.getTitle();
            case 2: return job.getCompany();
            case 3: return String.join(";", job.getSkills());
            case 4: return job.getLatitude();
            case 5: return job.getLongitude();
            case 6: return job.getSalary();
            case 7: return sdf.format(job.getDeadline());
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        JobAdvert job = jobs.get(rowIndex);
        switch (columnIndex) {
            case 0: job.setJobId(Integer.parseInt(aValue.toString())); break;
            case 1: job.setTitle(aValue.toString()); break;
            case 2: job.setCompany(aValue.toString()); break;
            case 3: job.setSkills(List.of(aValue.toString().split(";"))); break;
            case 4: job.setLatitude(Double.parseDouble(aValue.toString())); break;
            case 5: job.setLongitude(Double.parseDouble(aValue.toString())); break;
            case 6: job.setSalary(Double.parseDouble(aValue.toString())); break;
            case 7: try {
                job.setDeadline(sdf.parse(aValue.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            } break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public void setJobs(List<JobAdvert> jobs) {
        this.jobs = new ArrayList<>(jobs);
        fireTableDataChanged();
    }
}