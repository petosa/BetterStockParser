/**
 * Created by Nick on 12/30/2015.
 */
import com.jaunt.NotFound;
import com.jaunt.ResponseException;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            Symbols.update();
            List<String> arr = Symbols.get();

            for (String s : arr)
            RawData.update(s);

            for (String s : arr)
            System.out.println(RawData.get(s));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }
    }

}