import org.bson.Document;
import org.jfree.chart.demo.TimeSeriesChartDemo1;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nick on 4/29/2016.
 */
public class Intersect {

    private static List<TimeSeriesDataItem> retainA = null;
    private static List<TimeSeriesDataItem> retainB = null;

    public static List<Document> generic(TimeSeries a, TimeSeries b) {
        ArrayList<Document> arr = new ArrayList<>();
        retainify(a, b);
        //Search for crosses
        double prevA = -1;
        double prevB = -1;
        RegularTimePeriod prevTime = null;
        for(int i = 0; i < retainA.size(); i++) {
            double valA = retainA.get(i).getValue().doubleValue();
            double valB = retainB.get(i).getValue().doubleValue();
            RegularTimePeriod time = retainA.get(i).getPeriod();
            if(valA == valB) {
                arr.add(new Document("date", time.toString()).append("value", valA).append("exact", false));
            } else if (prevA != prevB) {
                if(prevA > prevB && valA < valB || prevA < prevB && valA > valB ) {
                    int days = (int) ((time.getLastMillisecond() - prevTime.getLastMillisecond())/86400000L);
                    double ma = (valA - prevA)/days;
                    double mb = (valB - prevB)/days;
                    double x = (prevA - prevB)/(mb - ma);
                    double calc = ma*x + prevA;
                    arr.add(new Document("date", time.toString()).append("value", calc).append("exact", false));
                }
            }
            prevA = valA;
            prevB = valB;
            prevTime = time;
        }

        return arr;
    }

    public static List<Document> goldenCross(TimeSeries sma50, TimeSeries sma200) {
        ArrayList<Document> arr = new ArrayList<>();
        retainify(sma50, sma200);
        //Search for crosses
        double prevA = -1;
        double prevB = -1;
        RegularTimePeriod prevTime = null;
        for(int i = 0; i < retainA.size(); i++) {
            double valA = retainA.get(i).getValue().doubleValue();
            double valB = retainB.get(i).getValue().doubleValue();
            RegularTimePeriod time = retainA.get(i).getPeriod();
            if(valA == valB) {
                arr.add(new Document("date", time.toString()).append("value", valA).append("exact", false));
            } else if (prevA != prevB) {
                //Golden cross
                if(prevA < prevB && valA > valB || prevA == prevB && valA > valB) {
                    int days = (int) ((time.getLastMillisecond() - prevTime.getLastMillisecond())/86400000L);
                    double ma = (valA - prevA)/days;
                    double mb = (valB - prevB)/days;
                    double x = (prevA - prevB)/(mb - ma);
                    double calc = ma*x + prevA;
                    arr.add(new Document("date", time.toString()).append("value", calc).append("type","golden").append("exact", false));
                //Death cross
                } else if(prevA > prevB && valA < valB || prevA == prevB && valA < valB) {
                    int days = (int) ((time.getLastMillisecond() - prevTime.getLastMillisecond())/86400000L);
                    double ma = (valA - prevA)/days;
                    double mb = (valB - prevB)/days;
                    double x = (prevA - prevB)/(mb - ma);
                    double calc = ma*x + prevA;
                    arr.add(new Document("date", time.toString()).append("value", calc).append("type","death").append("exact", false));
                }
            }
            prevA = valA;
            prevB = valB;
            prevTime = time;
        }

        return arr;
    }

    private static void retainify(TimeSeries a, TimeSeries b) {
        //Lists of Date/Value pairings
        List<TimeSeriesDataItem> arrA = a.getItems();
        List<TimeSeriesDataItem> arrB = b.getItems();
        //Result lists of retain implementation
        retainA = new ArrayList<>();
        retainB = new ArrayList<>();
        //Retain all implementation:
        // A TimeSeriesDataItem from arrA has date T <-> A TimeSeriesDataItem from arrB has date T
        HashMap<RegularTimePeriod, TimeSeriesDataItem> retainHash = new HashMap<>();
        //Fill hashmap with date to TimeSeriesDataItem pairings
        for (TimeSeriesDataItem tsdi : arrA) {
            RegularTimePeriod t = tsdi.getPeriod();
            retainHash.put(t, tsdi);
        }
        //Collisions mean a match, add TimeSeriesDataItem results to retain lists
        for (TimeSeriesDataItem tsdi : arrB) {
            RegularTimePeriod t = tsdi.getPeriod();
            if (retainHash.get(t) != null) {
                retainA.add(retainHash.get(t));
                retainB.add(tsdi);
            }
        }
    }


}
