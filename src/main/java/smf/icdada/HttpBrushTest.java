package smf.icdada;

public class HttpBrushTest {
    public static void main(String[] args) {
        try {
            Log.d("Debug Start");
            System.out.println(smfScanner.LongString(true));
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }
}
