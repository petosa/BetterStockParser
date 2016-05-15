import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Nick on 12/30/2015.
 */
public class Main {

    private static MongoClient mc = new MongoClient();
    public static MongoDatabase db = mc.getDatabase("raw-db");

    public enum STATE_MACHINE {
        MENU, SYMBOLS, BASKET, ADD, ADD_SYMBOL, VALIDATE_SYMBOL, TRACKER, FINISH_ADD, SMA, CANCEL, PLOT
    }

    public static STATE_MACHINE state = STATE_MACHINE.MENU;

    public static void main(String[] args) throws IOException, InterruptedException {
        //Turn off logging messages
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        //State machine references
        Scanner in = null;
        String cmd = "";
        Basket b = new Basket();
        Tracker t = null;
        String[] metrics = {"adjClose", "close", "open", "high", "low", "volume", "sma"};

        for (; ; ) {
            switch (state) {
                case MENU:
                    cmd = "";
                    in = new Scanner(System.in);
                    frame("Menu:\n[1] Symbols\n[2] Basket\n[3] Add\n[4] Remove\n[5] Plot");
                    System.out.print("\n>");
                    cmd = in.nextLine();
                    if (cmd.equals("1")) state = STATE_MACHINE.SYMBOLS;
                    if (cmd.equals("2")) state = STATE_MACHINE.BASKET;
                    if (cmd.equals("3")) state = STATE_MACHINE.ADD;
                    if (cmd.equals("5")) state = STATE_MACHINE.PLOT;
                    break;
                case CANCEL:
                    System.out.println("\nOperation canceled.\n");
                    state = STATE_MACHINE.MENU;
                    break;
                case SYMBOLS:
                    frame("Symbols:\n\n" + Symbols.format(8));
                    state = STATE_MACHINE.MENU;
                    break;
                case BASKET:
                    frame("Basket: " + b.toString() + "\n");
                    state = STATE_MACHINE.MENU;
                    break;
                case ADD:
                    frame("Adding a new metric to basket.\nType q at anytime cancel.\n");
                    t = new Tracker();
                    state = STATE_MACHINE.ADD_SYMBOL;
                    break;
                case ADD_SYMBOL:
                    System.out.print("Enter symbol: ");
                    cmd = in.nextLine();
                    state = STATE_MACHINE.VALIDATE_SYMBOL;
                    break;
                case VALIDATE_SYMBOL:
                    if (cmd.equals("q")) {
                        state = STATE_MACHINE.CANCEL;
                        break;
                    }
                    if (!Symbols.get().contains(cmd.toUpperCase())) {
                        state = STATE_MACHINE.ADD_SYMBOL;
                        break;
                    }
                    t.setSymbol(cmd.toUpperCase());
                    state = STATE_MACHINE.TRACKER;
                    break;
                case TRACKER:
                    String toPrint = "\nSelect tracker:\n";
                    for (int i = 1; i <= metrics.length; i++) {
                        toPrint += "[" + i + "] " + metrics[i - 1] + "\n";
                    }
                    System.out.println(toPrint);
                    System.out.print(">");
                    int sel = -1;
                    cmd = in.nextLine();
                    if(cmd.equals("q")){
                        state = STATE_MACHINE.CANCEL;
                        break;
                    }
                    System.out.println();
                    try {
                        sel = Integer.parseInt(cmd);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid.");
                        break;
                    }
                    if (!(sel > 0 && sel <= metrics.length)) {
                        System.out.println("Invalid.");
                        break;

                    }
                    cmd = metrics[sel - 1];
                    if(!cmd.equals("sma")) {
                        t.setTracker(cmd);
                        state = STATE_MACHINE.FINISH_ADD;
                        break;
                    }
                    state = STATE_MACHINE.SMA;
                    break;
                case FINISH_ADD:
                    System.out.println("Added tracker for " + t.toString() + ".\n");
                    b.add(t);
                    state = STATE_MACHINE.MENU;
                    break;
                case SMA:
                    System.out.print("Number of days: ");
                    int numDays = -1;
                    cmd = in.nextLine();
                    if(cmd.equals("q")){
                        state = STATE_MACHINE.CANCEL;
                        break;
                    }
                    try {
                        numDays = Integer.parseInt(cmd);
                    } catch (NumberFormatException e) {
                        break;
                    }
                    if(numDays < 1){
                        break;
                    }
                    t.setTracker("sma" + numDays);
                    System.out.println();
                    state = STATE_MACHINE.FINISH_ADD;
                    break;
                case PLOT:
                    LineChart chart = new LineChart(b.toString(), DatasetBuilder.parseBasket(b));
                    chart.pack();
                    chart.setVisible(true);
                    state = STATE_MACHINE.MENU;
                    break;
            }
        }
    }

    /**
     * Prints a String within a decorative frame.
     *
     * @param q the String to print in a frame
     * @throws InvalidArgumentException if the specified symbol does not exist.
     */
    private static void frame(String q) {
        if(q == null)
            throw new IllegalArgumentException(Error.ArgInvalid());
        String[] a = q.split("\n");
        int l = 0;
        for (String s : a)
            if (s.length() > l)
                l = s.length();
        for (int i = 0; i < l + 2; i++)
            System.out.print("=");
        System.out.println();
        for (String s : a) {
            System.out.print(" ");
            System.out.println(s);
        }
        for (int i = 0; i < l + 2; i++)
            System.out.print("=");
        System.out.println();

    }

    /* OLD CODE, KEEP FOR NOW */
    /*
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
     */

}