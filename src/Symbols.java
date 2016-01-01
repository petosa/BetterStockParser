import com.jaunt.*;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
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
    public static void update() throws NotFound, ResponseException {
        db.getCollection("symbols").drop();
        db.createCollection("symbols");
        List<String> arr = scrapeSymbols();
        ArrayList<Document> docs = new ArrayList<>();
        for (String s : arr) {
            docs.add(new Document("symbol", s));
        }
        db.getCollection("symbols").insertMany(docs);
        System.out.println("Updated symbols (" + db.getCollection("symbols").count() + " total)");
    }

    /**
     * Scrapes weekly options from CBOE site, returns them as string list.
     *
     * @return a list of scraped symbols.
     */
    private static List<String> scrapeSymbols() throws ResponseException, NotFound {
        UserAgent ua = new UserAgent();
        ua.visit("https://www.cboe.com/tradtool/symbols/symbolweeklys.aspx");
        Elements rows = ua.doc.findEvery("<td align=\"Center\">");
        ArrayList<String> arr = new ArrayList<>();
        for (Element i : rows) {
            arr.add(i.innerText());
        }
        arr.remove(0);
        return arr;
    }

}