package smf.icdada;

import smf.icdada.HttpUtils.Check;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static smf.icdada.HttpUtils.Base.*;
import static smf.icdada.RequestType.*;

public class GemBrush {
    public static void single() {
        Log.v("请输入拓维userId");
        String userId = smfScanner.String(true, "^.{8,}$");
        Log.v("请输入刷钻阈值");
        int gem = smfScanner.Int(true);
        gemBrush(userId, gem);
    }

    public static void measure() {
        initUsersMap();
        List<String> users = getUserList();
        Log.v("请输入刷钻阈值");
        int gem = smfScanner.Int(true);
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        for (String userId : users) {
            sleep(100);
            executor.submit(() -> {
                gemBrush(userId, gem);
                UserJsonUtils.JsonUtil(userId, "activate", false);
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.s("所有账号处理完成，程序退出");
        System.exit(0);
    }

    private static int gemChecker(String userId) {
        Check.V316 v316 = new Check.V316();
        refresh(userId);
        while (true) {
            Future<String> future = getExecutor(userId).submit(() -> getResponseBody(V316, userId));
            try {
                v316.setResponseBody(future.get(3, TimeUnit.SECONDS));
                if (v316.isValid(0) && v316.data.containsKey("$.p")) {
                    return Integer.parseInt(v316.data.get("$.p.fg").toString());
                }
                return 0;
            } catch (Exception ignored) {
            }
        }
    }

    private static void gemBrush(String userId, int gem) {
        int fg = gemChecker(userId);
        if (fg < gem) {
            final int i = (gem - fg) / 50 + 1;
            Check.V303 v303 = new Check.V303();
            Check.V792 v792 = new Check.V792();
            ProgressBar progressBar = new ProgressBar("账号: " + userId + " 刷取进度 || ", i + 1, true);
            while (true) {
                Future<String> futureV303 = getExecutor(userId).submit(() -> getResponseBody(V303, userId, 10814));
                try {
                    v303.setResponseBody(futureV303.get(3, TimeUnit.SECONDS));
                    if (v303.isValid(0)) {
                        progressBar.print(1);
                        int index = 0;
                        while (i != index) {
                            Future<String> futureV792 = getExecutor(userId).submit(() -> getResponseBody(V792, userId));
                            try {
                                v792.setResponseBody(futureV792.get(3, TimeUnit.SECONDS));
                                if (v792.isValid(45011)) {
                                    index++;
                                    progressBar.print(index + 1);
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        return;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}
