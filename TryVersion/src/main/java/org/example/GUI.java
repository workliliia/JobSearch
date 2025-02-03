package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUI {
    private JFrame frame;
    private JTable jobTable;
    private JScrollPane scrollPane;
    private JButton searchButton;
    private JButton displayJobsButton;
    private JButton setHomeLocationButton;
    private JButton addJobButton;
    private JButton removeJobButton;
    private JTextField searchField;
    private JTextField homeLatField, homeLonField;
    private JButton distanceButton;
    private JButton sortButton;
    private JobTableModel tableModel;
    private JComboBox<String> searchCriteriaComboBox;
    private JComboBox<String> columnDropdown;

    private final List<JobAdvert> jobs = new ArrayList<>();
    private final Search jobSearch = new Search();
    private double homeLatitude = 0.0;
    private double homeLongitude = 0.0;

    public GUI() {
        frame = new JFrame("Job Advertisements");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1200, 600)); // Set the minimum size of the frame

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);

        // Initialize the table model and table
        tableModel = new JobTableModel(jobs, new String[]{
                "Job ID", "Title", "Company", "Skills", "Latitude", "Longitude", "Salary", "Deadline"
        });
        jobTable = new JTable(tableModel);
        scrollPane = new JScrollPane(jobTable);

        // Job Table Internal Frame
        JInternalFrame tableInternalFrame = new JInternalFrame("Job Table", true, true, true, true);
        tableInternalFrame.setSize(600, 400);
        tableInternalFrame.add(scrollPane);
        tableInternalFrame.setVisible(true);

        // Distance Graph Internal Frame
        JInternalFrame graphInternalFrame = new JInternalFrame("Distance Graph", true, true, true, true);
        graphInternalFrame.setSize(600, 400);
        graphInternalFrame.setVisible(true);

        // Desktop pane for internal frames
        JDesktopPane desktopPane = new JDesktopPane();
        desktopPane.add(tableInternalFrame);
        desktopPane.add(graphInternalFrame);
        mainPanel.add(desktopPane, BorderLayout.CENTER);

        // Buttons and Fields
        displayJobsButton = new JButton("Display Jobs");
        setHomeLocationButton = new JButton("Set Home Location");
        addJobButton = new JButton("Add Job");
        removeJobButton = new JButton("Remove Job");
        distanceButton = new JButton("Calculate Distance");
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        searchCriteriaComboBox = new JComboBox<>(new String[]{"Title", "Company", "Skills"});
        sortButton = new JButton("Sort");
        columnDropdown = new JComboBox<>(new String[]{"Job ID", "Title", "Company", "Salary", "Deadline"});


        // Set colors for search components
        searchField.setBackground(Color.YELLOW);
        searchButton.setBackground(Color.YELLOW);
        searchCriteriaComboBox.setBackground(Color.YELLOW);

        // Set colors for sort components
        sortButton.setBackground(Color.ORANGE);
        columnDropdown.setBackground(Color.ORANGE);

        // Input fields for home location
        homeLatField = new JTextField(10);
        homeLonField = new JTextField(10);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1)); // 3 rows, 1 column
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        row1.add(displayJobsButton);
        row1.add(setHomeLocationButton);
        row1.add(new JLabel("Home Latitude:"));
        row1.add(homeLatField);
        row1.add(new JLabel("Home Longitude:"));
        row1.add(homeLonField);
        row1.add(addJobButton);
        row1.add(removeJobButton);

        row2.add(sortButton);
        row2.add(columnDropdown);


        row3.add(searchField);
        row3.add(searchButton);
        row3.add(searchCriteriaComboBox);
        JButton displayGraphButton = new JButton("Display Distance Graph");
        displayGraphButton.addActionListener(e -> displayDistanceGraph()); // Ensure you have defined displayDistanceGraph() method
        row4.add(displayGraphButton);
        buttonPanel.add(row4); // Adding the new row to the panel

// Ensure your GridLayout on buttonPanel accommodates the new row
        buttonPanel.setLayout(new GridLayout(4, 1));

        buttonPanel.add(row1);
        buttonPanel.add(row2);
        buttonPanel.add(row3);
        buttonPanel.add(row4);


        // Add the button panel to the main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack(); // Adjust frame size to contents
        frame.setVisible(true);

        setHomeLocationButton.addActionListener(e -> {
            try {
                homeLatitude = Double.parseDouble(homeLatField.getText());
                homeLongitude = Double.parseDouble(homeLonField.getText());
                JOptionPane.showMessageDialog(frame, "Home location set to: " + homeLatitude + ", " + homeLongitude, "Location Set", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(frame, "Please enter valid coordinates.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        try {
            addInitialJobs();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        initAddJobButtonListener();
        initRemoveJobButtonListener();
        setupDistanceButton(frame);
        searchButton.addActionListener(e -> handleSearch());
        sortButton.addActionListener(e -> handleSort());

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });
    }

    private void addInitialJobs() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JobAdvert job1 = new JobAdvert(1, "Software Engineer", "Google", Arrays.asList("Java", "Python", "Cloud"), 37.4219983, -122.084, 150000.00, sdf.parse("2024-12-31"));
        JobAdvert job2 = new JobAdvert(3, "Full-Stack Developer", "Amazon", Arrays.asList("JavaScript", "React", "Node.js"), 47.6062095, -122.3320708, 100000.00, sdf.parse("2024-11-30"));
        JobAdvert job3 = new JobAdvert(2, "Data Scientist", "IBM", Arrays.asList("Python", "Machine Learning", "Data Analysis"), 41.881832, -87.623177, 120000.00, sdf.parse("2024-10-15"));

        jobs.add(job1);
        jobs.add(job2);
        jobs.add(job3);
        jobSearch.insert(job1);
        jobSearch.insert(job2);
        jobSearch.insert(job3);
        updateTableWithJobs(jobs);
    }

    private void handleSearch() {
        searchField.setEnabled(true);
        String searchTerm = searchField.getText().trim();
        String attribute = (String) searchCriteriaComboBox.getSelectedItem();
        if (searchTerm.isEmpty()) {
            updateTableWithJobs(jobs);
        } else {
            List<JobAdvert> results = jobSearch.getMatchingJobs(attribute, searchTerm);
            updateTableWithJobs(results);
        }
    }

    private void handleSort() {
        String sortColumn = (String) columnDropdown.getSelectedItem();
        Sort.quickSort(jobs, 0, jobs.size() - 1, sortColumn);
        updateTableWithJobs(jobs);
    }

    private void initAddJobButtonListener() {
        addJobButton.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                JobAdvert newJob = new JobAdvert(
                        999, "Example Role", "Example Company", Arrays.asList("Java", "Spring", "SQL"),
                        37.7749, -122.4194, 105000.00, sdf.parse("2025-12-31"));
                jobs.add(newJob);
                jobSearch.insert(newJob);
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
                jobSearch.delete(jobToRemove);
                updateTableWithJobs(jobs);
            }
        });
    }

    private void updateTableWithJobs(List<JobAdvert> jobs) {
        tableModel.setJobs(jobs);
    }

    private void setupDistanceButton(JFrame frame) {
        distanceButton.addActionListener(e -> {
            int selectedRow = jobTable.getSelectedRow();
            if (selectedRow != -1) {
                double jobLat = (double) jobTable.getValueAt(selectedRow, 4);
                double jobLon = (double) jobTable.getValueAt(selectedRow, 5);
                double distance = calculateDistance(homeLatitude, homeLongitude, jobLat, jobLon);
                double roundedDistance = Math.round(distance * 100.0) / 100.0; // Round to two decimal places
                JOptionPane.showMessageDialog(frame, "Distance to home: " + roundedDistance + " km", "Distance Calculation", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "No job selected. Please select a job to calculate distance.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in kilometers
    }
    private void displayDistanceGraph() {
        DistanceGraph.showDistanceGraph(frame, jobs, homeLatitude, homeLongitude); // Assuming such a method exists
    }

}