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

public class DistanceGraph {

    public static void showDistanceGraph(java.util.List<JobAdvert> jobs, double homeLat, double homeLon) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        // Home location series (for visibility as a distinct point)
        XYSeries homeSeries = new XYSeries("Home");
        homeSeries.add(homeLon, homeLat); // Note: X-axis is longitude, Y-axis is latitude
        dataset.addSeries(homeSeries);

        // Adding each job as a new series with a line from home to the job location
        for (JobAdvert job : jobs) {
            double distance = DistanceUtils.calculate(homeLat, homeLon, job.getLatitude(), job.getLongitude());
            String seriesLabel = job.getCompany() + " (" + String.format("%.2f", distance) + " km)"; // Use company name and distance
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
        chartPanel.setPreferredSize(new Dimension(750, 550)); // Set preferred size for the chart panel

        // Create a new JFrame to display the chart
        JFrame frame = new JFrame("Distance Graph");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }
}