import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 5/12/2016.
 */
public class Basket {

    private List<Tracker> basket;

    public Basket() {
        basket = new ArrayList<Tracker>();
    }

    public void add(Tracker t){
        if(t == null)
            throw new IllegalArgumentException(Error.ArgInvalid());
        basket.add(t);
    }

    public List<Tracker> getList() {
        return basket;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i = 0; i < basket.size(); i++) {
            sb.append(basket.get(i).toString());
            if(i < basket.size() - 1)
                sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }



}
