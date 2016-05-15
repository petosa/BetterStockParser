import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import org.bson.Document;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Nick on 12/31/2015.
 */
public class RawData {

    /**
     * Syncs raw data in Mongo to current data, downloaded from Yahoo CSVs.
     *
     * Given a stock symbol, downloads its CSV file from Yahoo. It's then
     * parsed all into raw data values and inserted in that symbol's collection.
     * Error means unsuccessful CSV acquisition, and failure to push into table.
     * Other messages are self explanatory.
     *
     * @param symb the stock symbol to update.
     */
    public static void update(String symb) throws IOException {
        String date = null;
        List<Document> arr = null;
        try {
            arr = scrapeRawData(symb);
        } catch (FileNotFoundException e) {
            System.out.println("Error! - " + symb);
            return;
        }
        try {
            date = getLastDate(symb);
        } catch (NoSuchElementException e) {}
        if (date == null) {
            Main.db.getCollection(symb).insertMany(arr);
            System.out.println("Fresh! - " + symb);
        } else if (!arr.get(0).get("date").toString().equals(date)) {
            int index = 0;
            while (!arr.get(index).get("date").toString().equals(date)) {
                Main.db.getCollection(symb).insertOne(arr.get(index));
                index++;
            }
            System.out.println("Updated! (" + index + " documents added) - " + symb);
        } else {
            System.out.println("No update needed! - " + symb);
        }
    }

    /**
     * Returns a document for a symbol.
     *
     * You provide the symbol, the matching document is returned.
     *
     * @param symb the stock symbol to query.
     * @param date the date to query at.
     * @throws NoSuchElementException if there is no matching record.
     * @return one document for this symbol.
     */
    public static Document getOne(String symb, String date) {
        FindIterable<Document> query = Main.db.getCollection(symb).find(new Document("date", date));
        Document d = query.first();
        if (d == null) {
            throw new NoSuchElementException(Error.NoMatchingRecord(date));
        }
        return d;
    }

    /**
     * Returns a list of some documents for a symbol.
     *
     * You provide the symbol, the date to start looking, and
     * how many documents back to include.
     *
     * @param symb the stock symbol to query.
     * @param date the date to start the query at.
     * @param previous how many days back to include (inclusive).
     * @throws NoSuchElementException if there is no matching record.
     * @throws NoSuchElementException if out of bounds.
     * @return a list of some documents for this symbol.
     */
    public static List<Document> getSome(String symb, String date, int previous) {
        if (previous <= 0) {
            throw new IllegalArgumentException(Error.ArgInvalid());
        }
        FindIterable<Document> query = Main.db.getCollection(symb).find()
                .sort(new Document("date", -1));
        ArrayList<Document> arr = new ArrayList<>();
        boolean found = false;
        int count = 0;
        for (Document d : query) {
            if (d.get("date").toString().equals(date)) {
                found = true;
            }
            if (found && count < previous) {
                arr.add(d);
                count++;
            }
        }
        if (found == false) {
            throw new NoSuchElementException(Error.NoMatchingRecord(date));
        }
        if (count != previous) {
            throw new NoSuchElementException(Error.OutOfBounds(date, previous));
        }
        return arr;
    }

    /**
     * Returns a list of all documents for a symbol.
     *
     * @param symb the stock symbol to query.
     * @return a list of all documents for this symbol.
     */
    public static List<Document> getAll(String symb) {
        FindIterable<Document> query = Main.db.getCollection(symb).find()
                .sort(new Document("date", -1));
        ArrayList<Document> arr = new ArrayList<>();
        for (Document d : query) {
            arr.add(d);
        }
        return arr;
    }

    /**
     * Scrapes Yahoo CSVs for stock data.
     *
     * @param symb the stock symbol to scrape for.
     * @return a list of document synthesized from scraping.
     */
        private static List<Document> scrapeRawData(String symb) throws IOException {
        URL url = new URL("http://real-chart.finance.yahoo.com/table.csv?s=" + symb + "&ignore=.csv");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
        CSVReader csvReader = new CSVReader(in);
        String[] row = null;
        ArrayList<Document> arr = new ArrayList<>();
        while((row = csvReader.readNext()) != null) {
            Document doc = new Document("date", row[0])
                    .append("open", row[1])
                    .append("high", row[2])
                    .append("low", row[3])
                    .append("close", row[4])
                    .append("volume", row[5])
                    .append("adjClose", row[6]);
            arr.add(doc);
        }
        arr.remove(0);
        csvReader.close();
        return arr;
    }

    /**
     * Returns the most recent date in database for a stock.
     *
     * @param symb the stock to find last update for.
     * @throws NoSuchElementException if the specified symbol does not exist.
     * @return the date.
     */
    private static String getLastDate(String symb) {
        long size = Main.db.getCollection(symb).count();
        if (size == 0) {
            throw new NoSuchElementException(Error.CollectionDNE(symb));
        }
        List<Document> l = getAll(symb);
        Document doc = l.get(0);
        return doc.get("date").toString();
    }

}
