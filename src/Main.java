/**
 * Created by Nick on 12/30/2015.
 */
public class Main {

    public static void main(String[] args) {
        try {
           //System.out.println(SMA.getOne("AA", "1962-10-15", 200));
            System.out.println(SMA.getSome("AA", 200, "2015-12-29", 3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}