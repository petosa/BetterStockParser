import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;

public class LineChart extends JFrame {

    private static final long serialVersionUID = 1L;

    public LineChart(String label, TimeSeriesCollection tsc) {
        super("BetterStockParser");

        // based on the dataset we create the chart
        JFreeChart c = ChartFactory.createTimeSeriesChart(label, "Date", "Price", tsc, true, true, false);

        // Adding chart into a chart panel
        ChartPanel chartPanel = new ChartPanel(c);

        // setting default size
        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 540));

        // add to contentPane
        setContentPane(chartPanel);
    }

}