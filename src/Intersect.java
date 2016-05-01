import org.bson.Document;
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

    public static List<Document> generic(TimeSeries a, TimeSeries b) {
        ArrayList<Document> arr = new ArrayList<>();
        //Lists of Date/Value pairings
        List<TimeSeriesDataItem> arrA = a.getItems();
        List<TimeSeriesDataItem> arrB = b.getItems();
        //Result lists of retain implementation
        List<TimeSeriesDataItem> retainA = new ArrayList<>();
        List<TimeSeriesDataItem> retainB = new ArrayList<>();
        //Retain all implementation:
        // A TimeSeriesDataItem from arrA has date T <-> A TimeSeriesDataItem from arrB has date T
        HashMap<RegularTimePeriod, TimeSeriesDataItem> retainHash = new HashMap<>();
        //Fill hashmap with date to TimeSeriesDataItem pairings
        for(TimeSeriesDataItem tsdi : arrA) {
            RegularTimePeriod t = tsdi.getPeriod();
            retainHash.put(t, tsdi);
        }
        //Collisions mean a match, add TimeSeriesDataItem results to retain lists
        for(TimeSeriesDataItem tsdi : arrB) {
            RegularTimePeriod t = tsdi.getPeriod();
            if(retainHash.get(t) != null) {
                retainA.add(retainHash.get(t));
                retainB.add(tsdi);
            }
        }

        //Search for crosses
        double prevA = -1;
        double prevB = -1;
        RegularTimePeriod prevTime = null;
        for(int i = 0; i < retainA.size(); i++) {
            double valA = retainA.get(i).getValue().doubleValue();
            double valB = retainB.get(i).getValue().doubleValue();
            RegularTimePeriod time = retainA.get(i).getPeriod();
            if(valA == valB) {
                arr.add(pack(time, valA, true));
            } else if (prevA != prevB) {
                if(prevA > prevB && valA < valB || prevA < prevB && valA > valB ) {
                    int days = (int) ((time.getLastMillisecond() - prevTime.getLastMillisecond())/86400000L);
                    double ma = (valA - prevA)/days;
                    double mb = (valB - prevB)/days;
                    double x = (prevA - prevB)/(mb - ma);
                    double calc = ma*x + prevA;
                    arr.add(pack(time, calc, false));
                }
            }
            prevA = valA;
            prevB = valB;
            prevTime = time;
        }

        return arr;
    }

    private static Document pack(RegularTimePeriod t, double value, boolean exact) {
        return new Document("date", t.toString()).append("value", value).append("exact", exact);
    }

}
