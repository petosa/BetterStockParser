/**
 * Created by Nick on 12/31/2015.
 */
public class Error {

    public static String NoMatchingRecord(String query) {
        return "The document you have queried for (with field " + query + ") does not exist.";
    }

    public static String OutOfBounds(String query, int days) {
        return "There are not " + days + " days of raw data before" + query + ".";
    }

    public static String ArgInvalid() {
        return "One of the arguments passed in is invalid in this context.";
    }

    public static String CollectionDNE(String col) {
        return "The collection " + col + " does not exist.";
    }

    public static String NoTimeOverlap() {
        return "There is no overlap between these two TimeSeriesDataItem lists, intersection is impossible.";
    }

}
