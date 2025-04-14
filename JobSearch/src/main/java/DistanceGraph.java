// Imports for creating and rendering charts using JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * DistanceGraph generates a scatter plot showing the home location and job locations,
 * with lines connecting home to each job to visualise distances.
 */
public class DistanceGraph {

    /**
     * Displays a graph of distances between home and job locations.
     *
     * @param jobs     List of JobAdvert objects with job coordinates
     * @param homeLat  Latitude of the user's home
     * @param homeLon  Longitude of the user's home
     */
    public static void showDistanceGraph(java.util.List<JobAdvert> jobs, double homeLat, double homeLon) {
        // Dataset to hold all series (home + jobs)
        XYSeriesCollection dataset = new XYSeriesCollection();

        // Home location as a separate series for visibility
        XYSeries homeSeries = new XYSeries("Home");
        homeSeries.add(homeLon, homeLat); // X = Longitude, Y = Latitude
        dataset.addSeries(homeSeries);

        // Add a series for each job, connecting it to home
        for (JobAdvert job : jobs) {
            double distance = DistanceUtils.calculate(homeLat, homeLon, job.getLatitude(), job.getLongitude());
            String seriesLabel = job.getCompany() + " (" + String.format("%.2f", distance) + " km)";
            XYSeries jobSeries = new XYSeries(seriesLabel);
            jobSeries.add(homeLon, homeLat); // Start at home
            jobSeries.add(job.getLongitude(), job.getLatitude()); // End at job location
            dataset.addSeries(jobSeries);
        }

        // Create scatter plot with the dataset
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Distance to Jobs from Home",       // Chart title
                "Longitude",                        // X-axis label
                "Latitude",                         // Y-axis label
                dataset,                            // Data
                PlotOrientation.VERTICAL,           // Chart orientation
                true, true, false                   // Legend, tooltips, URLs
        );

        // Configure plot and renderer for custom shapes and lines
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Configure home point (first series)
        renderer.setSeriesLinesVisible(0, false); // No connecting line
        renderer.setSeriesShapesVisible(0, true); // Show a shape
        renderer.setSeriesShape(0, new Ellipse2D.Double(-5, -5, 10, 10)); // Circle for home

        // Configure job points (remaining series)
        for (int i = 1; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesLinesVisible(i, true);  // Line from home to job
            renderer.setSeriesShapesVisible(i, true); // Show shape at job
            renderer.setSeriesShape(i, new Ellipse2D.Double(-8, -8, 16, 16)); // Larger shape
        }

        plot.setRenderer(renderer); // Apply custom renderer to the plot

        // Wrap chart in a panel with fixed size
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(750, 550));

        // Create and display the chart in a new window
        JFrame frame = new JFrame("Distance Graph");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close the graph window
        frame.setContentPane(chartPanel);
        frame.pack(); // Resize to fit content
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true); // Show the window
    }
}
