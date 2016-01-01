import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Nick on 12/30/2015.
 */
public class Main {

    public static void main(String[] args) {
        try {
           Symbols.update();
            List<String> l = Symbols.get();
            for (String s : l) {
                try {
                    System.out.println(s + " " + SMA.getOne(s, 10, "2015-12-30"));
                }catch(NoSuchElementException e){}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}