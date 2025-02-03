package org.example;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class DistanceGraph {

    public static void showDistanceGraph(JFrame frame, java.util.List<JobAdvert> jobs, double homeLat, double homeLon) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        // Home location series (for visibility as a distinct point)
        XYSeries homeSeries = new XYSeries("Home");
        homeSeries.add(homeLon, homeLat); // Note: X-axis is longitude, Y-axis is latitude
        dataset.addSeries(homeSeries);

        // Adding each job as a new series with a line from home to the job location
        for (JobAdvert job : jobs) {
            String distance = calculateDistance(homeLat, homeLon, job.getLatitude(), job.getLongitude());
            String seriesLabel = job.getCompany() + " (" + distance + " km)"; // Use company name and distance
            XYSeries jobSeries = new XYSeries(seriesLabel);
            jobSeries.add(homeLon, homeLat); // Home coordinates
            jobSeries.add(job.getLongitude(), job.getLatitude()); // Job coordinates
            dataset.addSeries(jobSeries);
        }

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Distance to Jobs from Home",
                "Longitude", "Latitude",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Set rendering for home point
        renderer.setSeriesLinesVisible(0, false); // No line for the home series
        renderer.setSeriesShapesVisible(0, true); // Visible shape for the home
        renderer.setSeriesShape(0, new Ellipse2D.Double(-5, -5, 10, 10)); // Larger home location marker

        // Set rendering for job series
        for (int i = 1; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesLinesVisible(i, true); // Enable lines for job series
            renderer.setSeriesShapesVisible(i, true); // Show shapes at job points
            renderer.setSeriesShape(i, new Ellipse2D.Double(-8, -8, 16, 16)); // Larger job markers
        }

        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        JInternalFrame graphInternalFrame = new JInternalFrame("Distance Graph", true, true, true, true);
        graphInternalFrame.getContentPane().add(chartPanel);
        graphInternalFrame.pack();
        graphInternalFrame.setVisible(true);
        frame.add(graphInternalFrame); // Add this internal frame to the main GUI frame
        frame.pack();
        frame.setVisible(true);
    }

    private static String calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c; // Distance in kilometers
        return String.format("%.2f", distance); // Round to two decimal places
    }
}
