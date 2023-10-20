package smf.icdada;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static smf.icdada.HttpUtils.Base.sleep;

public class Console {
    public static void startConsole() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable clearConsoleTask = Console::clearConsole;
        scheduler.scheduleAtFixedRate(clearConsoleTask, 15, 5, TimeUnit.MINUTES);
        sleep(1000);
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
            Log.v("已清空控制台，控制台定时触发已加载");
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }
}
