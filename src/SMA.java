import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Nick on 12/31/2015.
 */
public class SMA {

    public static Document getOne(String symb, int days, String date) {
        List<Document> arr = RawData.getSome(symb, date, days);
        Document doc = new Document("date", date).append("sma" + days, SMAify(arr));
        return doc;
    }

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
            throw new NoSuchElementException("Date does not exist");
        }
        ArrayList<Document> ret = new ArrayList<>();
        for (int x = 0; x < previous; x++) {
            try {
                List<Document> sub = all.subList(x + dateIndex, days + x + dateIndex);
                ret.add(new Document("date", all.get(x + dateIndex).get("date").toString())
                        .append("sma" + days, SMAify(sub)));
            } catch(IndexOutOfBoundsException e) {}
        }
        return ret;
    }

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

    private static double SMAify(List<Document> arr) {
        double acc = 0;
        for (Document d : arr) {
            acc += Double.parseDouble(d.get("adjClose").toString());
        }
        return acc/arr.size();
    }
}
