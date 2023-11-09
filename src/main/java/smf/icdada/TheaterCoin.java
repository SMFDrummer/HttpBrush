package smf.icdada;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import smf.icdada.HttpUtils.Check;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import static smf.icdada.HttpUtils.Base.*;
import static smf.icdada.RequestType.V303;
import static smf.icdada.RequestType.V904;
import static smf.icdada.UserJsonUtils.JsonUtil;

public class TheaterCoin {
    private static final ConcurrentHashMap<String, Integer> t7 = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> t100 = new ConcurrentHashMap<>();

    private static void init() {
        try {
            initUsersMap();
            String stringUrl = System.getProperty("user.dir") + File.separator + "user.json";
            Path userPath = Paths.get(stringUrl);
            if (Files.exists(userPath)) {
                JSONObject userData = JSONObject.parse(Files.readString(userPath));
                JSONArray usersArray = userData.getJSONArray("Users");
                JSONObject theaterDefault = new JSONObject();
                theaterDefault.put("t7", 150000);
                theaterDefault.put("t100", 1500);
                for (String userId : getUserList()) {
                    JSONObject userObject = usersArray.getJSONObject(getIndex(userId));
                    if (!userObject.containsKey("theater")) {
                        userObject.put("theater", theaterDefault);
                    }
                    JSONObject theater = userObject.getJSONObject("theater");
                    t7.put(userId, theater.getIntValue("t7"));
                    t100.put(userId, theater.getIntValue("t100"));
                }
                try (FileWriter fileWriter = new FileWriter(stringUrl)) {
                    fileWriter.write(userData.toJSONString(JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat));
                    fileWriter.flush();
                }
            } else {
                Log.e("用户库文件异常，请检查:" + System.getProperty("user.dir") + File.separator + "user.json文件是否存在");
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                System.exit(0);
            }
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void single() {
        Log.v("请输入拓维userId");
        String userId = smfScanner.String(false, "^.{8,}$");
        Log.v("请输入V904-t7发包数量");
        t7.put(userId, smfScanner.Int(false));
        Log.v("请输入V904-t100发包数量");
        t100.put(userId, smfScanner.Int(false));
        theaterMesh(userId);
        Log.s("刷取完毕，程序退出");
        System.exit(0);
    }

    public static void measure() {
        init();
        List<String> userIds = getUserList();
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        for (String userId : userIds) {
            sleep(100);
            executor.submit(() -> {
                Log.v("账号:" + userId + " || 已读取，开始执行");
                Interface(userId);
                JsonUtil(userId, "activate", false);
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

    private static void Interface(String userId) {
        refresh(userId);
        Result uisk = getUisk(userId);
        if (!"banned".equals(uisk.getUi()) && !"banned".equals(uisk.getSk())) {
            theaterMesh(userId);
        } else {
            JsonUtil(userId, "isBanned", true);
            JsonUtil(userId, "activate", false);
        }
    }

    private static void theaterMesh(String userId) {
        boolean keepRunning = true;
        while (keepRunning) {
            try {
                Future<String> futureV303 = getExecutor(userId).submit(() -> getResponseBody(V303, userId, 10771));
                Check.V303 v303 = new Check.V303();
                v303.setResponseBody(futureV303.get(3, TimeUnit.SECONDS));
                if (v303.isValid(0)) {
                    keepRunning = false;
                    while (t7.get(userId) != 0) {
                        Future<String> futureT7 = getExecutor(userId).submit(() -> getResponseBody(V904, userId, "7"));
                        Check.V904 v904 = new Check.V904();
                        T(userId, futureT7, v904, t7);
                    }
                    while (t100.get(userId) != 0) {
                        Future<String> futureT100 = getExecutor(userId).submit(() -> getResponseBody(V904, userId, "100"));
                        Check.V904 v904 = new Check.V904();
                        T(userId, futureT100, v904, t100);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static void T(String userId, Future<String> futureT, Check.V904 v904, ConcurrentHashMap<String, Integer> t) {
        try {
            v904.setResponseBody(futureT.get(3, TimeUnit.SECONDS));
            if (v904.isValid(0)) {
                t.compute(userId, (k, v) -> {
                    assert v != null;
                    return v - 1;
                });
                JsonUtil(userId, "theater", theaterObject(t7.get(userId), t100.get(userId)));
            }
        } catch (Exception ignored) {
        }
    }

    private static JSONObject theaterObject(int t7, int t100) {
        JSONObject theater = new JSONObject();
        theater.put("t7", t7);
        theater.put("t100", t100);
        return theater;
    }
}
