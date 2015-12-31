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

    private static MongoClient mongoClient = new MongoClient();
    private static MongoDatabase db = mongoClient.getDatabase("raw-db");

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
            db.getCollection(symb).insertMany(arr);
            System.out.println("Fresh! - " + symb);
        } else if (!arr.get(0).get("date").toString().equals(date)) {
            int index = 0;
            while (!arr.get(index).get("date").toString().equals(date)) {
                db.getCollection(symb).insertOne(arr.get(index));
                index++;
            }
            System.out.println("Updated! (" + index + " documents added) - " + symb);
        } else {
            System.out.println("No update needed! - " + symb);
        }
    }

    public static List<Document> get(String symb) {
        FindIterable<Document> cur = db.getCollection(symb).find()
                .sort(new Document("date", -1));
        ArrayList<Document> arr = new ArrayList<>();
        for (Document d : cur) {
            arr.add(d);
        }
        return arr;
    }

    //Feed it a symbol. It finds the matching CSV and returns it as a list of BSON documents.
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

    //Returns the date of the latest entry for the raw stock data of this symbol. "YYYY-MM-DD"
    //If it is a new symbol, throws exception.
    private static String getLastDate(String symb) {
        long size = db.getCollection(symb).count();
        if (size == 0) {
            throw new NoSuchElementException("Collection does not exist");
        }
        List<Document> l = get(symb);
        Document doc = l.get(0);
        return doc.get("date").toString();
    }

}
