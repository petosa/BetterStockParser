import org.bson.Document;
import org.jfree.data.time.TimeSeries;

import java.io.IOException;
import java.util.List;

/**
 * Created by Nick on 12/30/2015.
 */
public class Main {

    public static void main(String[] args) throws IOException {
       // for(String s : Symbols.get())
       // RawData.update(s);

        String label = "CMG";
        List<Document> docs = SMA.getAll(label,50);
        List<Document> docs2 = SMA.getAll(label,200);
        TimeSeries ts = DatasetBuilder.createTimeSeries("sma50", docs);
        TimeSeries ts2 = DatasetBuilder.createTimeSeries("sma200", docs2);
        LineChart chart = new LineChart(DatasetBuilder.createDataset(ts, ts2));
        for(Document d : Intersect.generic(ts, ts2)) {
            System.out.println(d.toString());
        }
        chart.pack();
        chart.setVisible(true);
    }
}