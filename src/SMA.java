import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Nick on 12/31/2015.
 */
public class SMA {

    /**
     * Returns a documents for a specified SMA for a symbol.
     *
     * @param symb the stock symbol to query.
     * @param days the number of days for the SMA.
     * @param date the date to query.
     * @throws IllegalArgumentException if days is <1.
     * @return a list of all documents for this symbol.
     */
    public static Document getOne(String symb, int days, String date) {
        if (days < 1) {
            throw new IllegalArgumentException(Error.ArgInvalid());
        }
        List<Document> arr = RawData.getSome(symb, date, days);
        Document doc = new Document("date", date).append("sma" + days, SMAify(arr));
        return doc;
    }

    /**
     * Returns a list of some documents for a SMA for a symbol.
     *
     * You provide the symbol, the SMA days, the date to start looking,
     * and the number of previous documents to consider before the date, inclusive.
     *
     * @param symb the stock symbol to query.
     * @param days the SMA days.
     * @param date the date to start the query at.
     * @param previous how many days back to include (inclusive).
     * @throws NoSuchElementException if there is no matching record.
     * @throws NoSuchElementException if out of bounds.
     * @return a list of some SMA documents for this symbol.
     */
    public static List<Document> getSome(String symb, int days, String date, int previous) {
        List<Document> all = RawData.getAll(symb);
        int index = 0;
        int dateIndex = index;
        boolean found = false;
        for (Document d : all) {
            if (!found && d.get("date").toString().equals(date)) {
                found = true;
                dateIndex = index;
            }
            index++;
        }
        if (!found) {
            throw new NoSuchElementException(Error.NoMatchingRecord(date));
        }
        ArrayList<Document> ret = new ArrayList<>();
        for (int x = 0; x < previous; x++) {
            try {
                List<Document> sub = all.subList(x + dateIndex, days + x + dateIndex);
                ret.add(new Document("date", all.get(x + dateIndex).get("date").toString())
                        .append("sma" + days, SMAify(sub)));
            } catch(IndexOutOfBoundsException e) {
                throw new NoSuchElementException(Error.OutOfBounds(date, days));
            }
        }
        return ret;
    }

    /**
     * Returns a list of all SMA documents for a symbol.
     *
     * @param symb the stock symbol to query.
     * @param days the SMA days.
     * @return a list of all SMA documents for this symbol.
     */
    public static List<Document> getAll(String symb, int days) {
        List<Document> all = RawData.getAll(symb);
        ArrayList<Document> ret = new ArrayList<>();
        for (int x = 0; x < all.size(); x++) {
            try {
                List<Document> sub = all.subList(x, days + x);
                ret.add(new Document("date", all.get(x).get("date").toString())
                        .append("sma" + days, SMAify(sub)));
            } catch(IndexOutOfBoundsException e) {}
        }
        return ret;
    }

    /**
     * Returns a SMA as a double for the documents given.
     *
     * @param arr list of documents.
     * @return double representing SMA.
     */
    private static double SMAify(List<Document> arr) {
        double acc = 0;
        for (Document d : arr) {
            acc += Double.parseDouble(d.get("adjClose").toString());
        }
        return acc/arr.size();
    }
}
