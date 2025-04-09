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

public class GUI {
    private JFrame frame;
    private JTable jobTable;
    private JobTableModel tableModel;
    private ButtonPanelBuilder buttonBuilder;
    private List<JobAdvert> allJobs = new ArrayList<>();


    // Buttons and fields
    private JButton searchButton;
    private JButton setHomeLocationButton;
    private JButton addJobButton;
    private JButton removeJobButton;
    private JButton distanceButton;
    private JButton sortButton;
    // Create buttons for loading and saving job data
    private JButton loadCSVButton;
    private JButton saveCSVButton;


    private JTextField searchField;
    private JTextField homeLatField, homeLonField;
    private JComboBox<String> searchCriteriaComboBox;
    private JComboBox<String> columnDropdown;

    // Data & Search
    private final Search jobSearch = new Search();
    private double homeLatitude = 0.0;
    private double homeLongitude = 0.0;

    public GUI() {
        // Set up the main frame
        frame = new JFrame("Job Advertisements");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1200, 700));

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);

        // Add the job table panel directly to the main panel
        mainPanel.add(createJobTablePanel(), BorderLayout.CENTER);

        buttonBuilder = new ButtonPanelBuilder();
        JPanel buttonPanel = buttonBuilder.buildButtonPanel();
        JButton helpButton = buttonBuilder.getButton("Help");

// Wrap Help button in a right-aligned panel
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        helpPanel.add(helpButton);

// Add help button above the table
        mainPanel.add(helpPanel, BorderLayout.NORTH);
        mainPanel.add(createJobTablePanel(), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);


        // Finalize frame setup
        frame.pack();
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);

        // Add initial jobs
        try {
            addInitialJobs();
//            testJobTableModelWithFakeData(10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

        // Initialize all listeners
        initListeners();
    }

    /**
     * Creates the job table panel.
     */
    private JPanel createJobTablePanel() {
        tableModel = new JobTableModel(new ArrayList<>(), new String[]{
                "Job ID", "Title", "Company", "Skills", "Latitude", "Longitude", "Salary", "Deadline"
        });
        jobTable = new JTable(tableModel);

        // Set table cell borders with light color
        jobTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));  // Light grey border
                return label;
            }
        });

        // Make header font bold
        jobTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(jobTable);

        // Create a panel with padding around the table
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add 20px padding on all sides
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    /**
     * Registers all event listeners.
     */
    private void initListeners() {
        setHomeLocationButton.addActionListener(e -> {
            try {
                double enteredLat = Double.parseDouble(homeLatField.getText());
                double enteredLon = Double.parseDouble(homeLonField.getText());

                // Check for conflict with existing job locations
                for (JobAdvert job : tableModel.getJobs()) {
                    if (Double.compare(job.getLatitude(), enteredLat) == 0 &&
                            Double.compare(job.getLongitude(), enteredLon) == 0) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "The entered home location matches an existing job at \"" + job.getTitle() + "\" at " + job.getCompany() + ".\nPlease choose a different location.",
                                "Duplicate Location",
                                JOptionPane.WARNING_MESSAGE,
                                null
                        );
                        return;
                    }
                }

                homeLatitude = enteredLat;
                homeLongitude = enteredLon;
                JOptionPane.showMessageDialog(
                        frame,
                        "Home location set to: " + homeLatitude + ", " + homeLongitude,
                        "Location Set",
                        JOptionPane.INFORMATION_MESSAGE,
                        null
                );

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(
                        frame,
                        "❗ Please enter valid numbers for latitude and longitude.",
                        "❗ Invalid Input",
                        JOptionPane.ERROR_MESSAGE,
                        null
                );
            }
        });


        addJobButton.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                JobAdvert newJob = new JobAdvert(
                        0, // Placeholder; the model will set the correct ID
                        "Example Role",
                        "Example Company",
                        Arrays.asList("Java", "Spring", "SQL"),
                        37.7749, -122.4194,
                        105000.00,
                        sdf.parse("22/01/2026")
                );

                tableModel.addJob(newJob);
                allJobs = new ArrayList<>(tableModel.getJobs());
                jobSearch.insert(newJob);

                MetricsServer.jobsAdded.inc();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });

        removeJobButton.addActionListener(e -> {
            int selectedRow = jobTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to delete this job?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    JobAdvert jobToRemove = tableModel.getJobs().get(selectedRow);
                    tableModel.removeJob(selectedRow);
                    jobSearch.delete(jobToRemove);
                    allJobs.remove(jobToRemove);
                    updateTableWithJobs(tableModel.getJobs());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a job to remove.");
            }
        });



        distanceButton.addActionListener(e -> {
            int selectedRow = jobTable.getSelectedRow();
            if (selectedRow != -1) {
                double jobLat = (double) jobTable.getValueAt(selectedRow, 4);
                double jobLon = (double) jobTable.getValueAt(selectedRow, 5);
                double distance = DistanceUtils.calculate(homeLatitude, homeLongitude, jobLat, jobLon);
                double roundedDistance = Math.round(distance * 100.0) / 100.0;
                JOptionPane.showMessageDialog(frame,
                        "Distance to home: " + roundedDistance + " km",
                        "Distance Calculation",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "No job selected. Please select a job to calculate distance.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        // Search
        ActionListener searchAction = e -> handleSearch();
        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction); // Trigger search on Enter key

// Sort
        sortButton.addActionListener(e -> handleSort());

// Load CSV
        loadCSVButton.addActionListener(e -> loadJobTableFromCSV());

// Save CSV
        saveCSVButton.addActionListener(e -> saveJobTableToCSV());

// Display Graph
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


    private void addInitialJobs() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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

        List<JobAdvert> initialJobs = List.of(job1, job2, job3, job4, job5);
        jobSearch.insertJobs(initialJobs);
        tableModel.setJobs(initialJobs);
        allJobs = new ArrayList<>(initialJobs);

    }

    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        String attribute = (String) searchCriteriaComboBox.getSelectedItem();

        if (searchTerm.isEmpty()) {
            updateTableWithJobs(jobSearch.getAllJobs()); // ✅ Full unsorted list
            return;
        }

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

    private void handleSort() {
        String sortColumn = (String) columnDropdown.getSelectedItem();
        List<JobAdvert> sortedJobs = new ArrayList<>(tableModel.getJobs());
        Sort.quickSort(sortedJobs, 0, sortedJobs.size() - 1, sortColumn);
        tableModel.setJobs(sortedJobs);
    }


    private void updateTableWithJobs(List<JobAdvert> jobs) {
        tableModel.setJobs(jobs);
        allJobs = new ArrayList<>(jobs);
    }

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
    public void testJobTableModelWithFakeData(int count) {
        System.out.println("Generating " + count + " fake jobs...");

        List<JobAdvert> jobs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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
