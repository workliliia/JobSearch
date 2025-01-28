package org.example;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GUI {
    private JTable jobTable;
    private JScrollPane scrollPane;
    private JButton searchButton;
    private JButton displayJobsButton;
    private JButton setHomeLocationButton;
    private JButton addJobButton;
    private JButton removeJobButton;
    private JTextField searchField;
    private JButton distanceButton;
    private JButton sortButton;
    private DefaultTableModel tableModel;
    private JComboBox<String> searchCriteriaComboBox;
    private JComboBox<String> columnDropdown;

    // Unified job list
    private final List<JobAdvert> jobs = new ArrayList<>();

    public GUI() {
        JFrame frame = new JFrame("Job Advertisements");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tableModel = new DefaultTableModel(new Object[]{
                "Job ID", "Title", "Company", "Skills", "Latitude", "Longitude", "Salary", "Deadline"
        }, 0);
        jobTable = new JTable(tableModel);
        scrollPane = new JScrollPane(jobTable);

        displayJobsButton = new JButton("Display Jobs");
        setHomeLocationButton = new JButton("Set Home Location");
        addJobButton = new JButton("Add Job");
        removeJobButton = new JButton("Remove Job");
        distanceButton = new JButton("Calculate Distance");
        searchField = new JTextField(15);
        searchButton = new JButton("Search");  // We'll keep it, but you can remove if you want purely live search
        sortButton = new JButton("Sort");
        searchCriteriaComboBox = new JComboBox<>(new String[]{"Title", "Company", "Skills"});
        columnDropdown = new JComboBox<>(new String[]{"Job ID", "Title", "Company", "Salary", "Deadline"});

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.add(displayJobsButton);
        topPanel.add(setHomeLocationButton);
        topPanel.add(addJobButton);
        topPanel.add(removeJobButton);
        topPanel.add(distanceButton);

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search By:"));
        searchPanel.add(searchCriteriaComboBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);  // Optional: could remove if you want purely live

        // Add a DocumentListener for live updates
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleSearch();
            }
        });

        // Sort panel
        JPanel sortPanel = new JPanel();
        sortPanel.add(new JLabel("Sort By:"));
        sortPanel.add(columnDropdown);
        sortPanel.add(sortButton);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(sortPanel, BorderLayout.SOUTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Populate with initial jobs
        try {
            addInitialJobs();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Listeners
        initAddJobButtonListener();
        initRemoveJobButtonListener();
        setupDistanceButton(frame, 0.0, 0.0);

        // Search Button Listener (Optional)
        searchButton.addActionListener(e -> handleSearch());

        // Sort Button Listener
        sortButton.addActionListener(e -> {
            String sortColumn = (String) columnDropdown.getSelectedItem();
            List<JobAdvert> sortedJobs = Sort.sortJobs(jobs, sortColumn);
            updateTableWithJobs(sortedJobs);
        });


    }

    /**
     * Populates the initial list and table with jobs.
     */
    private void addInitialJobs() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        JobAdvert job1 = new JobAdvert(
                1,
                "Software Engineer",
                "Google",
                Arrays.asList("Java", "Python", "Cloud"),
                37.4219983, -122.084,
                150000.00,
                sdf.parse("2024-12-31")
        );
        JobAdvert job2 = new JobAdvert(
                3,
                "Full-Stack Developer",
                "Amazon",
                Arrays.asList("JavaScript", "React", "Node.js"),
                47.6062095,
                -122.3320708,
                140000.00,
                sdf.parse("2024-11-30")
        );

        JobAdvert job3 = new JobAdvert(
                2,
                "Data Scientist",
                "IBM",
                Arrays.asList("Python", "Machine Learning", "Data Analysis"),
                41.881832,
                -87.623177,
                120000.00,
                sdf.parse("2024-10-15")
        );

        // ... add jobs 4 through 10 similarly ...

        jobs.add(job1);
        jobs.add(job2);
        jobs.add(job3);
        // ... add the rest of the jobs ...

        // Show them in the table
        updateTableWithJobs(jobs);
    }

    /**
     * Called every time the user modifies text in the search box.
     */
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        String attribute = (String) searchCriteriaComboBox.getSelectedItem();

        // If user cleared the search box, show all jobs
        if (searchTerm.isEmpty()) {
            updateTableWithJobs(jobs);
        } else {
            // Otherwise filter the currently loaded data
            List<JobAdvert> filteredJobs = Search.filterJobs(jobs, attribute, searchTerm);
            updateTableWithJobs(filteredJobs);
        }
    }

    private void initAddJobButtonListener() {
        addJobButton.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                JobAdvert newJob = new JobAdvert(
                        999,
                        "Example Role",
                        "Example Company",
                        Arrays.asList("Java", "Spring", "SQL"),
                        37.7749,
                        -122.4194,
                        105000.00,
                        sdf.parse("2025-12-31")
                );
                jobs.add(newJob);
                updateTableWithJobs(jobs);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void initRemoveJobButtonListener() {
        removeJobButton.addActionListener(e -> {
            int selectedRow = jobTable.getSelectedRow();
            if (selectedRow != -1) {
                JobAdvert jobToRemove = jobs.get(selectedRow);
                jobs.remove(jobToRemove);
                tableModel.removeRow(selectedRow);
            }
        });
    }

    public List<JobAdvert> getJobsFromTable() {
        List<JobAdvert> currentJobs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int jobId = (int) tableModel.getValueAt(i, 0);
            String title = (String) tableModel.getValueAt(i, 1);
            String company = (String) tableModel.getValueAt(i, 2);
            String skillsStr = (String) tableModel.getValueAt(i, 3);
            List<String> skills = Arrays.stream(skillsStr.split(";"))
                    .map(String::trim)
                    .collect(Collectors.toList());
            double latitude = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
            double longitude = Double.parseDouble(tableModel.getValueAt(i, 5).toString());
            double salary = Double.parseDouble(tableModel.getValueAt(i, 6).toString());
            String deadlineStr = tableModel.getValueAt(i, 7).toString();
            Date deadline;

            try {
                deadline = sdf.parse(deadlineStr);
            } catch (ParseException e) {
                e.printStackTrace();
                deadline = new Date(); // Fallback to current date if parsing fails
            }

            currentJobs.add(new JobAdvert(
                    jobId, title, company, skills, latitude, longitude, salary, deadline
            ));
        }
        return currentJobs;
    }


    public void setupDistanceButton(JFrame frame, double homeLatitude, double homeLongitude) {
        distanceButton.addActionListener(e -> {
            System.out.println("Distance Button clicked. Home lat/lon: "
                    + homeLatitude + ", " + homeLongitude);
            // Implement distance calculation logic here
        });
    }

    public void plotGraph(Graph<JobAdvert, DefaultWeightedEdge> graph) {
        System.out.println("Plotting graph with " + graph.vertexSet().size() + " vertices.");
        // Implement graph plotting logic here
    }

}
