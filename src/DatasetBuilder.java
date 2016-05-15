import org.bson.Document;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 4/28/2016.
 */
public class DatasetBuilder {

    /**
     * Creates a TimeSeries item from a document
     *
     * @param var the field to extract from the documents.
     * @param doc an ArrayList of docs to package into the TimeSeries
     * @return the packaged TimeSeries
     */
    public static TimeSeries createTimeSeries(String title, String var, List<Document> doc) {
        final TimeSeries t = new TimeSeries(title, Day.class);
        for(Document d : doc) {
            t.add(dateToDay(d), Double.parseDouble(d.get(var).toString()));
        }
        return t;
    }

    /**
     * Converts the date in a specified document into a Day object.
     *
     * @param d the document to query for.
     * @throws IllegalArgumentException if document is null.
     * @return the document's date converted into a Day object.
     */
    private static Day dateToDay(Document d) {
        if(d == null) {
            throw new IllegalArgumentException("The document passed in was null.");
        }
        String date = d.get("date").toString();
        String[] splitDate = date.split("-");
        return new Day(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[0]));
    }

    /**
     * Combines the provided TimeSeries into a TimeSeriesCollection
     *
     * @param ts all TimeSeries to package.
     * @return A TimeSeriesCollection of provided TimeSeries.
     */
    public static TimeSeriesCollection createDataset(List<TimeSeries> ts) {
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        for(TimeSeries tsi : ts) {
            dataset.addSeries(tsi);
        }
        return dataset;
    }

    public static TimeSeriesCollection parseBasket(Basket b) {
        List<Tracker> trackers = b.getList();
        List<TimeSeries> toPlot = new ArrayList<>();
        for(Tracker t : trackers) {
            List<Document> docs = null;
            String symb = t.getSymbol();
            String metric = t.getTracker();
            if(metric.contains("sma")) {
                int days = Integer.parseInt(metric.substring(3));
                docs = SMA.getAll(symb, days);
            } else {
                docs = RawData.getAll(symb);
            }
            toPlot.add(DatasetBuilder.createTimeSeries(symb + "->" + metric, metric, docs));
        }
        return DatasetBuilder.createDataset(toPlot);
    }

}
