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
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            ProgressBar progressBar1 = new ProgressBar("title1:",100,true);
            ProgressBar progressBar2 = new ProgressBar("title2:",100,true);
            ProgressBar progressBar3 = new ProgressBar("title3:",100,true);
            executorService.submit(()->{
                IntStream.range(1,101).forEach(i-> {
                    Base.sleep(100);
                    progressBar1.print(i);
                });
            });
            executorService.submit(()->{
                IntStream.range(1,101).forEach(i-> {
                    Base.sleep(150);
                    progressBar2.print(i);
                });
            });
            executorService.submit(()->{
                IntStream.range(1,101).forEach(i-> {
                    Base.sleep(200);
                    progressBar3.print(i);
                });
            });
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            executorService.shutdown();
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }
}
