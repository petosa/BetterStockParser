import org.bson.Document;
import org.jfree.data.time.TimeSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 12/30/2015.
 */
public class Main {

    public static void main(String[] args) {
        List<Document> docs3 = RawData.getAll("GOOG");
        List<Document> docs = SMA.getAll("GOOG",50);
        List<Document> docs2 = SMA.getAll("GOOG",200);
        TimeSeries ts = DatasetBuilder.createTimeSeries("sma50", docs);
        TimeSeries ts2 = DatasetBuilder.createTimeSeries("sma200", docs2);
        TimeSeries ts3 = DatasetBuilder.createTimeSeries("adjClose", docs3);
        LineChart chart = new LineChart(DatasetBuilder.createDataset(ts, ts2, ts3));
        chart.pack();
        chart.setVisible(true);
    }
}