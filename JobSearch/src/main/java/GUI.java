import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.*;

/**
 * GUI class creates and manages the main window of the Job Advertisement Manager.
 * It includes a job table, controls for managing job data, and features like search,
 * sort, CSV handling, and graph visualisation.
 */
public class GUI {
    // Main components
    private final JFrame frame;
    private JTable jobTable;
    private JobTableModel tableModel;
    private final ButtonPanelBuilder buttonBuilder;
    private List<JobAdvert> allJobs = new ArrayList<>();

    // Buttons and fields for UI interaction
    private final JButton searchButton;
    private final JButton setHomeLocationButton;
    private final JButton addJobButton;
    private final JButton removeJobButton;
    private final JButton distanceButton;
    private final JButton sortButton;
    private final JButton loadCSVButton;
    private final JButton saveCSVButton;
    private final JTextField searchField;
    private final JTextField homeLatField;
    private final JTextField homeLonField;
    private final JComboBox<String> searchCriteriaComboBox;
    private final JComboBox<String> columnDropdown;

    // Logic & data handling
    private final Search jobSearch = new Search();
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private double homeLatitude = 0.0;
    private double homeLongitude = 0.0;

    public GUI() {
        // Set up the main window
        frame = new JFrame("Job Advertisements");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1200, 700));

        // Create and set the main layout panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);

        // Job table panel in the center
        mainPanel.add(createJobTablePanel(), BorderLayout.CENTER);

        // Build button panel and help button
        buttonBuilder = new ButtonPanelBuilder();
        JPanel buttonPanel = buttonBuilder.buildButtonPanel();
        JButton helpButton = buttonBuilder.getButton("Help");

        // Help button in top-right corner
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        helpPanel.add(helpButton);
        mainPanel.add(helpPanel, BorderLayout.NORTH);

        // Add table and buttons
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Final setup
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Add sample jobs to display
        try {
            addInitialJobs();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Link buttons and fields from builder
        searchButton = buttonBuilder.getButton("Search");
        setHomeLocationButton = buttonBuilder.getButton("Set Home Location");
        addJobButton = buttonBuilder.getButton("Add Job");
        removeJobButton = buttonBuilder.getButton("Remove Job");
        distanceButton = buttonBuilder.getButton("Calculate Distance");
        sortButton = buttonBuilder.getButton("Sort");
        loadCSVButton = buttonBuilder.getButton("Load CSV");
        saveCSVButton = buttonBuilder.getButton("Save CSV");

        if (helpButton != null) {
            helpButton.addActionListener(e -> showHelpPopup());
        }

        searchField = buttonBuilder.getSearchField();
        homeLatField = buttonBuilder.getHomeLatField();
        homeLonField = buttonBuilder.getHomeLonField();
        searchCriteriaComboBox = buttonBuilder.getSearchCriteriaComboBox();
        columnDropdown = buttonBuilder.getColumnDropdown();

        initListeners(); // Setup all event listeners
    }

    /**
     * Creates and returns the job table panel with padding and scroll bar.
     */
    private JPanel createJobTablePanel() {
        tableModel = new JobTableModel(new ArrayList<>(), new String[]{
                "Job ID", "Title", "Company", "Skills", "Latitude", "Longitude", "Salary", "Deadline"
        });
        jobTable = new JTable(tableModel);

        // Light grey border for table cells
        jobTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
                return label;
            }
        });

        // Bold font for table headers
        jobTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(jobTable);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Sets up event listeners for all interactive buttons and fields.
     */
    private void initListeners() {
        // Set home location and check for duplicates
        setHomeLocationButton.addActionListener(e -> {
            try {
                double enteredLat = Double.parseDouble(homeLatField.getText());
                double enteredLon = Double.parseDouble(homeLonField.getText());

                for (JobAdvert job : tableModel.getJobs()) {
                    if (Double.compare(job.getLatitude(), enteredLat) == 0 &&
                            Double.compare(job.getLongitude(), enteredLon) == 0) {
                        JOptionPane.showMessageDialog(frame,
                                "The entered home location matches an existing job at \"" + job.getTitle() + "\" at " + job.getCompany() + ".\nPlease choose a different location.",
                                "Duplicate Location", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                homeLatitude = enteredLat;
                homeLongitude = enteredLon;

                JOptionPane.showMessageDialog(frame,
                        "Home location set to: " + homeLatitude + ", " + homeLongitude,
                        "Location Set", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter valid numbers for latitude and longitude.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add sample job (can be replaced with user input in future)
        addJobButton.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                JobAdvert newJob = new JobAdvert(0, "Example Role", "Example Company",
                        Arrays.asList("Required skills for the job"),
                        0, 0, 0, sdf.parse("01/01/2025"));

                tableModel.addJob(newJob);
                allJobs = new ArrayList<>(tableModel.getJobs());
                jobSearch.insert(newJob);
                MetricsServer.jobsAdded.inc(); // metric tracker (optional)

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });

        // Remove selected job
        removeJobButton.addActionListener(e -> {
            int selectedRow = jobTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to delete this job?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    JobAdvert jobToRemove = tableModel.getJobs().get(selectedRow);
                    tableModel.removeJob(selectedRow);
                    jobSearch.delete(jobToRemove);
                    allJobs.remove(jobToRemove);
                    updateTableWithJobs(tableModel.getJobs());
                }
            } JOptionPane.showMessageDialog(frame,
                    "Please select a job to remove.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);

        });

        // Calculate distance to selected job
        distanceButton.addActionListener(e -> {
            int selectedRow = jobTable.getSelectedRow();
            if (selectedRow != -1) {
                double jobLat = (double) jobTable.getValueAt(selectedRow, 4);
                double jobLon = (double) jobTable.getValueAt(selectedRow, 5);
                double distance = DistanceUtils.calculate(homeLatitude, homeLongitude, jobLat, jobLon);
                double rounded = Math.round(distance * 100.0) / 100.0;

                JOptionPane.showMessageDialog(frame,
                        "Distance to home: " + rounded + " km",
                        "Distance Calculation", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "No job selected. Please select a job to calculate distance.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Search jobs on button click or Enter key
        ActionListener searchAction = e -> handleSearch();
        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        // Sort job list by selected column
        sortButton.addActionListener(e -> handleSort());

        // Load job list from CSV
        loadCSVButton.addActionListener(e -> loadJobTableFromCSV());

        // Save job list to CSV
        saveCSVButton.addActionListener(e -> saveJobTableToCSV());

        // Show distance graph
        JButton displayGraphButton = buttonBuilder.getButton("Display Graph");
        if (displayGraphButton != null) {
            displayGraphButton.addActionListener(e -> displayDistanceGraph());
        }
    }
    private void showHelpPopup() {
        String helpMessage = """
<html>
<h2>📘 Welcome to the Job Advertisement Manager!</h2>
<p>Here’s a quick guide to help you get started:</p>

<b>🏡 Set Your Home Location</b><br>
- Enter your home latitude & longitude.<br>
- Click <b>"Set Home Location"</b>.<br>
- This is needed to calculate distances to jobs and to display map-like graph of jobs.<br><br>


<b>➕ Add a New Job</b><br>
- Click <b>"Add Job"</b> to add a sample job.<br>
- Or load jobs from a CSV using <b>"Load CSV"</b>.<br>
- Format: Title, Company, Skills, Latitude, Longitude, Salary, Deadline (dd/MM/yyyy)<br><br>

<b>🔍 Search for Jobs</b><br>
- Type a keyword into the search field.<br>
- Choose criteria: Title, Company, or Skills.<br>
- Hit button <b>"Search"</b><br>
- To come back to all jobs: you need to leave the search bar empty and press <b>"Search"</b>.<br><br>

<b>📊 Sort Jobs</b><br>
- Pick a column (Title, Salary, etc.) and click <b>"Sort"</b>.<br><br>

<b>📏 Calculate Distance</b><br>
- Select a job from the table.<br>
- Click <b>"Calculate Distance"</b> to see how far it is from your home.<br><br>

<b>📈 Visualise Distances</b><br>
- Click <b>"Display Graph"</b> to see a map-like graph of jobs.<br>
- Make sure you have set your home location first — the graph won’t work without it!<br><br>

<b>📁 Save or Load Jobs</b><br>
- Save jobs to a CSV with <b>"Save CSV"</b>.<br>
- Load jobs from a CSV with <b>"Load CSV"</b>.<br><br>

<b>🗑️ Remove a Job</b><br>
- Select a job and click <b>"Remove Job"</b>.<br>
- You’ll be asked to confirm before it’s deleted. 😉<br><br>

<b>📬 Found a bug? Have feedback?</b><br>
- Feel free to email me: <a href="mailto:b01147264@studentmail.uws.ac.uk">b01147264@studentmail.uws.ac.uk</a><br><br>


<b>❓ Need Help?</b><br>
- Click <b>"i" (info)</b> button (You’re here already 🎉)<br><br>

<i>Just don’t try to actually apply to these jobs... they’re fake! Input our own jobs!</i>
</html>
""";

        JOptionPane.showMessageDialog(frame, helpMessage, "How to Use This App", JOptionPane.INFORMATION_MESSAGE);
    }

    // Adds a predefined list of job adverts to the system at startup.
    private void addInitialJobs() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        // Create sample job adverts with realistic roles and data
        JobAdvert job1 = new JobAdvert(1, "AI Research Engineer", "Quantex Labs",
                Arrays.asList("Python", "TensorFlow", "Deep Learning"),
                37.774929, -122.419416, 145000.00, sdf.parse("20/05/2025"));

        JobAdvert job2 = new JobAdvert(2, "Backend Developer", "Nebula Systems",
                Arrays.asList("Java", "Spring Boot", "Docker"),
                34.052235, -118.243683, 115000.00, sdf.parse("05/06/2025"));

        JobAdvert job3 = new JobAdvert(3, "Frontend Engineer", "PixelHive",
                Arrays.asList("JavaScript", "Vue.js", "CSS"),
                51.507351, -0.127758, 95000.00, sdf.parse("18/07/2025"));

        JobAdvert job4 = new JobAdvert(4, "Cloud Infrastructure Specialist", "SkyGrid Technologies",
                Arrays.asList("AWS", "Terraform", "Linux"),
                52.520008, 13.404954, 130000.00, sdf.parse("02/08/2025"));

        JobAdvert job5 = new JobAdvert(5, "Cybersecurity Analyst", "CipherNest",
                Arrays.asList("Network Security", "SIEM", "Python"),
                48.856613, 2.352222, 110000.00, sdf.parse("28/05/2025"));

        // Insert and store them
        List<JobAdvert> initialJobs = List.of(job1, job2, job3, job4, job5);
        jobSearch.insertJobs(initialJobs);
        tableModel.setJobs(initialJobs);
        allJobs = new ArrayList<>(initialJobs);

    }

    // Filters the job list based on a keyword and selected search criteria.
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        String attribute = (String) searchCriteriaComboBox.getSelectedItem();

        if (searchTerm.isEmpty()) {
            updateTableWithJobs(jobSearch.getAllJobs()); // ✅ Full unsorted list
            return;
        }

        // Get matching jobs
        List<JobAdvert> results = jobSearch.getMatchingJobs(attribute, searchTerm);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "No jobs matched your search criteria.",
                    "No Results",
                    JOptionPane.WARNING_MESSAGE);
            updateTableWithJobs(jobSearch.getAllJobs()); // Restore full list
        } else {
            updateTableWithJobs(results);
        }
    }

    // Sorts job adverts in the table based on the selected column.
    private void handleSort() {
        String sortColumn = (String) columnDropdown.getSelectedItem();
        List<JobAdvert> sortedJobs = new ArrayList<>(tableModel.getJobs());
        Sort.quickSort(sortedJobs, 0, sortedJobs.size() - 1, sortColumn);
        tableModel.setJobs(sortedJobs);
    }


    // Updates the job table and internal list with a new set of jobs.
    private void updateTableWithJobs(List<JobAdvert> jobs) {
        tableModel.setJobs(jobs);
        allJobs = new ArrayList<>(jobs);
    }

    // Displays a graphical visualisation of distances from home to each job.
    private void displayDistanceGraph() {
        if (homeLatitude == 0.0 && homeLongitude == 0.0) {
            JOptionPane.showMessageDialog(frame,
                    "Please set your home location before displaying the graph.",
                    "Missing Home Location",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        DistanceGraph.showDistanceGraph(tableModel.getJobs(), homeLatitude, homeLongitude);
    }

    // Saves the current job table to a CSV file selected by the user.
    private void saveJobTableToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Job Table to CSV");
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                JobCSVHandler.saveJobListToCSV(fileToSave, tableModel.getJobs());
                JOptionPane.showMessageDialog(frame, "Job table saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Loads job data from a CSV file selected by the user and populates the table.
    private void loadJobTableFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Job Table from CSV");
        int userSelection = fileChooser.showOpenDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try {
                List<JobAdvert> loadedJobs = JobCSVHandler.loadJobsFromCSV(fileToOpen);
                jobSearch.clear();
                jobSearch.insertJobs(loadedJobs);
                updateTableWithJobs(loadedJobs);
                JOptionPane.showMessageDialog(frame, "Job table loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ParseException e) {
                JOptionPane.showMessageDialog(frame, "Error loading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Generates a large number of fake jobs and loads them into the table for testing performance.
    public void testJobTableModelWithFakeData(int count) {
        System.out.println("Generating " + count + " fake jobs...");

        List<JobAdvert> jobs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        try {
            for (int i = 0; i < count; i++) {
                JobAdvert job = new JobAdvert(
                        i,
                        "Software Engineer " + i,
                        "Company " + (i % 100),
                        List.of("Java", "Python", "C++"),
                        50.0 + (i % 10),
                        -1.0 + (i % 10),
                        50_000 + (i % 10) * 1000,
                        sdf.parse("01/01/2026")
                );
                jobs.add(job);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();
        tableModel.setJobs(jobs);
        long end = System.currentTimeMillis();

        System.out.println("Loaded " + count + " jobs into JobTableModel in " + (end - start) + " ms");
        System.out.println("Final row count: " + tableModel.getRowCount());
    }

}
