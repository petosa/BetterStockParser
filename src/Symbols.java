import com.jaunt.*;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 12/31/2015.
 */
public class Symbols {

    private static MongoClient mongoClient = new MongoClient();
    private static MongoDatabase db = mongoClient.getDatabase("symbol-db");

    /**
     * Fetches database for symbols.
     *
     * @return a list of all symbols saved to Mongo.
     */
    public static List<String> get() {
        FindIterable<Document> cur = db.getCollection("symbols").find()
                .sort(new Document("symbol", 1));
        ArrayList<String> arr = new ArrayList<>();
        for (Document d : cur) {
            arr.add(d.get("symbol").toString());
        }
        return arr;
    }

    /**
     * Updates Mongo's list of symbols from internet.
     */
    public static void update() throws NotFound, ResponseException, IOException {
        db.getCollection("symbols").drop();
        db.createCollection("symbols");
        List<Document> arr = scrapeSymbols();
        ArrayList<Document> docs = new ArrayList<>();
        for (Document d : arr) {
            docs.add(d);
        }
        db.getCollection("symbols").insertMany(docs);
        System.out.println("Updated symbols (" + db.getCollection("symbols").count() + " total)");
    }

    /**
     * Scrapes weekly options from CBOE site, returns them as string list.
     *
     * @return a list of scraped symbols.
     */
    private static List<Document> scrapeSymbols() throws IOException {
        URL url = new URL("https://www.cboe.com/publish/scheduledtask/mktdata/cboesymboldirweeklys.csv");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
        CSVReader csvReader = new CSVReader(in);
        String[] row = null;
        ArrayList<Document> arr = new ArrayList<>();
        boolean first = true;
        while((row = csvReader.readNext()) != null) {
            if(!first) {
                Document d = new Document("name", row[0])
                        .append("symbol", row[1]);
                arr.add(d);
            } else {
                first = false;
            }
        }
        arr.remove(0);
        csvReader.close();
        return arr;
    }

}