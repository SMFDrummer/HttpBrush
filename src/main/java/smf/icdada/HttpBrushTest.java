package smf.icdada;

import smf.icdada.HttpUtils.Base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class HttpBrushTest {
    public static void main(String[] args) {
        try {
            Log.d("Debug Start");
            PerfectArchive.invalidChecker();
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }
}
