/**
 * Created by Nick on 5/14/2016.
 */
public class Tracker {

    private String symbol;
    private String tracker;

    public Tracker(){
    }

    public Tracker(String s, String t){
        symbol = s;
        tracker = t;
    }

    public String getSymbol(){
        return symbol;
    }

    public String getTracker(){
        return tracker;
    }

    public void setSymbol(String s){
        symbol = s;
    }

    public void setTracker(String t){
        tracker = t;
    }

    public String toString() {
        return "[" + symbol + "->" + tracker + "]";
    }

}