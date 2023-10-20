package smf.icdada;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgressBar {
    private static int lines = 1;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String progressTitle;
    private final long progressMax;
    private final boolean usePercentage;
    private final DecimalFormat Decimal = new DecimalFormat("#.##%");
    private long previousProgressValue = 0;
    private final int outLines;

    ProgressBar(String progressTitle, long progressMax, boolean usePercentage) {
        this.progressTitle = progressTitle;
        this.progressMax = progressMax;
        this.usePercentage = usePercentage;
        this.outLines = lines++;
    }

    public void print(long progressValue) {
        executor.submit(() -> {
            if (progressValue < 0 || progressValue > progressMax) return;
            System.out.print("\033[" + outLines + ";1H");
            System.out.print("\033[K");
            if (previousProgressValue == progressValue) {
                Log.b(Log.p(bar(progressValue), Log.Color.RED));
            } else {
                Log.b(Log.p(bar(progressValue), Log.Color.GREEN));
            }
            previousProgressValue = progressValue;
        });
    }

    private String bar(long progressValue) {
        float percentage = progressValue * 1.0f / progressMax;
        long length = Math.round(percentage * 50);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(progressTitle).append(" [");
        for (int i = 0; i < length; i++) stringBuilder.append('=');
        for (int i = 0; i < 50 - length; i++) stringBuilder.append(' ');
        stringBuilder.append("] ");
        if (usePercentage) {
            stringBuilder.append(Decimal.format(percentage));
        } else {
            stringBuilder.append(progressValue).append('/').append(progressMax);
        }
        return stringBuilder.toString();
    }
}