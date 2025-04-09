import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JobTableModel extends AbstractTableModel {
    private List<JobAdvert> jobs;
    private final String[] columnNames;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static int nextJobId = 4;

    // Column index constants
    private static final int COL_ID = 0;
    private static final int COL_TITLE = 1;
    private static final int COL_COMPANY = 2;
    private static final int COL_SKILLS = 3;
    private static final int COL_LATITUDE = 4;
    private static final int COL_LONGITUDE = 5;
    private static final int COL_SALARY = 6;
    private static final int COL_DEADLINE = 7;

    public JobTableModel(List<JobAdvert> jobs, String[] columnNames) {
        this.jobs = new ArrayList<>(jobs);
        this.columnNames = columnNames;

        for (JobAdvert job : jobs) {
            if (job.getJobId() >= nextJobId) {
                nextJobId = job.getJobId() + 1;
            }
        }
    }

    public void addJob(JobAdvert job) {
        job.setJobId(nextJobId++);
        jobs.add(job);
        SwingUtilities.invokeLater(this::fireTableDataChanged);
    }

    public void removeJob(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < jobs.size()) {
            jobs.remove(rowIndex);
            SwingUtilities.invokeLater(() -> fireTableRowsDeleted(rowIndex, rowIndex));
        }
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
        return switch (columnIndex) {
            case COL_ID -> job.getJobId();
            case COL_TITLE -> job.getTitle();
            case COL_COMPANY -> job.getCompany();
            case COL_SKILLS -> String.join(";", job.getSkills());
            case COL_LATITUDE -> job.getLatitude();
            case COL_LONGITUDE -> job.getLongitude();
            case COL_SALARY -> job.getSalary();
            case COL_DEADLINE -> sdf.format(job.getDeadline());
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        JobAdvert job = jobs.get(rowIndex);
        String value = aValue.toString().trim();

        try {
            switch (columnIndex) {
                case COL_ID -> {
                    if (!InputValidator.isValidInteger(value)) throw new NumberFormatException();
                    int newId = Integer.parseInt(value);

                    // Check if the new ID already exists in another job
                    for (int i = 0; i < jobs.size(); i++) {
                        if (i != rowIndex && jobs.get(i).getJobId() == newId) {
                            showError("Job ID " + newId + " already exists. Please enter a unique Job ID.");
                            return;
                        }
                    }

                    job.setJobId(newId);
                }
                case COL_TITLE -> job.setTitle(value);
                case COL_COMPANY -> job.setCompany(value);
                case COL_SKILLS -> job.setSkills(List.of(value.split(";")));
                case COL_LATITUDE -> {
                    if (!InputValidator.isValidDouble(value)) throw new NumberFormatException();
                    job.setLatitude(Double.parseDouble(value));
                }
                case COL_LONGITUDE -> {
                    if (!InputValidator.isValidDouble(value)) throw new NumberFormatException();
                    job.setLongitude(Double.parseDouble(value));
                }
                case COL_SALARY -> {
                    if (!InputValidator.isValidDouble(value)) throw new NumberFormatException();
                    job.setSalary(Double.parseDouble(value));
                }
                case COL_DEADLINE -> {
                    if (!InputValidator.isValidDate(value, "yyyy-MM-dd")) throw new ParseException(value, 0);
                    job.setDeadline(sdf.parse(value));
                }
                default -> throw new IllegalArgumentException("Invalid column index: " + columnIndex);
            }
        } catch (NumberFormatException e) {
            showError("Invalid number format. Please enter numeric values for latitude, longitude, or salary.");
        } catch (ParseException e) {
            showError("Invalid date format. Please use yyyy-MM-dd.");
        }

        SwingUtilities.invokeLater(() -> fireTableCellUpdated(rowIndex, columnIndex));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    public void setJobs(List<JobAdvert> jobs) {
        this.jobs = new ArrayList<>(jobs);

        // Recalculate nextJobId based on current jobs
        nextJobId = 0;
        for (JobAdvert job : jobs) {
            if (job.getJobId() >= nextJobId) {
                nextJobId = job.getJobId() + 1;
            }
        }

        SwingUtilities.invokeLater(this::fireTableDataChanged);
    }

    public List<JobAdvert> getJobs() {
        return jobs;
    }
}
