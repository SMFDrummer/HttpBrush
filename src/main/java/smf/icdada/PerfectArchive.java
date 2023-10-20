package smf.icdada;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import smf.icdada.HttpUtils.Check;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static smf.icdada.HttpUtils.Base.*;
import static smf.icdada.RequestType.V302;
import static smf.icdada.RequestType.V316;

public class PerfectArchive {
    public static void single() {
        Log.v("请输入拓维userId");
        rewardBrush(smfScanner.Int(true, "^\\d{8,}$"));
        Log.s("账号处理完成，程序退出");
    }

    public static void measure() {
        initUsersMap();
        List<Integer> users = getUserList();
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        for (int user : users) {
            sleep(100);
            executorService.submit(() -> {
                rewardBrush(user);
                UserJsonUtils.JsonUtil(user, "activate", false);
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.s("所有账号处理完成，程序退出");
        System.exit(0);
    }

    private static void rewardBrush(int userId) {
        Check.V316 v316 = new Check.V316();
        Check.V302 v302 = new Check.V302();
        ProgressBar progressBar = new ProgressBar("账号: " + userId + " 刷取进度 || ", 25, true);
        refresh(userId);
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        while (true) {
            Future<String> futureV316 = executor.submit(() -> getResponseBody(V316, userId));
            try {
                v316.setResponseBody(futureV316.get(3, TimeUnit.SECONDS));
                if (v316.isValid(0)) {
                    int uk = Integer.parseInt(v316.data.get("$.p.uk").toString());
                    progressBar.print(1);
                    while (true) {
                        int artifactUk = ++uk;
                        Future<String> futureV302Artifact = executor.submit(() -> getResponseBody(V302, userId, rewardArtifacts(artifactUk), artifactUk));
                        try {
                            v302.setResponseBody(futureV302Artifact.get(10, TimeUnit.SECONDS));
                            if (v302.isValid(11102)) {
                                progressBar.print(1);
                                uk = uk + 1;
                            } else if (v302.isValid(0)) {
                                progressBar.print(2);
                                int i = 0;
                                while (i != 23) {
                                    uk++;
                                    int plantUk = uk;
                                    Future<String> futureV302Plant = executor.submit(() -> getResponseBody(V302, userId, rewardPlants(plantUk), plantUk));
                                    try {
                                        v302.setResponseBody(futureV302Plant.get(10, TimeUnit.SECONDS));
                                        if (v302.isValid(0)) {
                                            i++;
                                        }
                                        progressBar.print(2 + i);
                                    } catch (Exception ignored) {
                                        refresh(userId);
                                    }
                                }
                                return;
                            }
                        } catch (Exception ignored) {
                            refresh(userId);
                        }
                    }
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }

    private static JSONArray rewardPlants(int uk) {
        JSONArray array = new JSONArray();
        IntStream.range(1001, 1038).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(1039, 1046).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(1047, 1048).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(1049, 1087).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(1088, 1096).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(1097, 1100).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(111001, 111005).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(111006, 111007).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(111008, 111057).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(111058, 111059).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(111060, 111077).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(111078, 111080).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(111081, 111083).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(111084, 111092).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(200000, 200036).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(200037, 200040).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(200041, 200042).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(200043, 200075).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        return array;
    }

    private static JSONArray rewardArtifacts(int uk) {
        JSONArray array = new JSONArray();
        IntStream.range(60001, 60101).forEach(i -> array.add(rewardObjectFormat(i, uk)));
        IntStream.range(22050, 22073).forEach(i -> array.add(rewardPieceFormat(i, uk)));
        return array;
    }

    private static JSONObject rewardObjectFormat(int rewardId, int uk) {
        JSONObject object = new JSONObject();
        object.put("i", rewardId);
        object.put("q", 1);
        object.put("f", "steam" + uk + "_hard_level_reward");
        return object;
    }

    private static JSONObject rewardPieceFormat(int rewardId, int uk) {
        JSONObject object = new JSONObject();
        object.put("i", rewardId);
        object.put("q", 300);
        object.put("f", "steam" + uk + "_hard_level_reward");
        return object;
    }
}