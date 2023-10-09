package smf.icdada;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import smf.icdada.HttpUtils.Check;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static smf.icdada.HttpUtils.Base.*;
import static smf.icdada.RequestType.*;

public class Anniversary {
    public static void measure() {
        Map<Integer, String> userObjects = readUserObjects();
        ExecutorService executorService = Executors.newFixedThreadPool(userObjects.size() + 1);
        for (Map.Entry<Integer, String> userObject : userObjects.entrySet()) {
            sleep(100);
            executorService.submit(() -> {
                Log.v("账号:" + userObject.getKey() + " || " + "已读取，开始执行");
                brush(userObject.getKey(), userObject.getValue());
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

    private static Map<Integer, String> readUserObjects() {
        Map<Integer, String> userObjects = new HashMap<>();
        try {
            String stringUrl = System.getProperty("user.dir") + File.separator + "user.json";
            Path userPath = Paths.get(stringUrl);
            if (Files.exists(userPath)) {
                JSONObject userData = JSONObject.parse(Files.readString(userPath));
                JSONArray usersArray = userData.getJSONArray("Users");
                for (Object userElement : usersArray) {
                    JSONObject userObject = (JSONObject) userElement;
                    int userId = userObject.getIntValue("userId");
                    if (userObject.containsKey("activate")) {
                        if (userObject.getBooleanValue("activate") && userObject.containsKey("inviteCode")) {
                            String inviteCode = userObject.getString("inviteCode");
                            userObjects.put(userId, inviteCode);
                        } else {
                            Log.w("账号:" + userId + " || " + "未检测到邀请码，请确保已经完整运行功能4");
                        }
                    } else if (userObject.containsKey("inviteCode")) {
                        String inviteCode = userObject.getString("inviteCode");
                        UserJsonUtils.JsonUtil(userId, "activate", true);
                        userObjects.put(userId, inviteCode);
                    } else {
                        Log.w("账号:" + userId + " || " + "未检测到邀请码，请确保已经完整运行功能4");
                    }
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
        return userObjects;
    }

    public static void brush() {
        String inviteCode = getInviteCode();
        int count = 0;
        do {
            int i = count;
            count += brushInviteCode(inviteCode, getRandomId());
            if (count == i) {
                Log.e("邀请码:" + inviteCode + " || 刷取失败 || 共成功:" + count);
            } else {
                Log.s("邀请码:" + inviteCode + " || 刷取成功 || 共成功:" + count);
            }
        } while (count < 13);
        Log.v("邀请码:" + inviteCode + " || 邀请结束");
    }

    private static void brush(int userId, String inviteCode) {
        int count = checkInviteCode(userId);
        if (count == 12202) {
            UserJsonUtils.JsonUtil(userId, "isBanned", true);
            UserJsonUtils.JsonUtil(userId, "activate", false);
        } else {
            while (count < 13) {
                count += brushInviteCode(inviteCode, getRandomId());
            }
            Log.v("邀请码:" + inviteCode + " || 邀请结束，开始刷取");
            anniversaryGacha(userId);
        }
    }

    private static String getInviteCode() {
        Log.v("请输入活动邀请码，并按回车键继续:");
        return smfScanner.String(true);
    }

    private static int getRandomId() {
        Random random = new Random();
        return random.nextInt(500000) + 37500001;
    }

    private static int checkInviteCode(int userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Check.V303 v303 = new Check.V303();
        refresh(userId);
        Result uisk = getUisk(userId);
        if ("banned".equals(uisk.getUi()) && "banned".equals(uisk.getSk())) {
            return 12202;
        } else while (true) try {
            Future<String> future = executor.submit(() -> getResponseBody(V303, userId, 10868));
            String response303Body = future.get(3, TimeUnit.SECONDS);
            v303.setResponseBody(response303Body);
            if (v303.isValid(0)) {
                Check.V303.data data = v303.new data();
                if (data.containsKey("inviteInfo")) {
                    return data.getJSONObject("inviteInfo").getIntValue("inviteCount");
                } else return 0;
            }
        } catch (Exception ignored) {
            refresh(userId);
        }
    }


    private static int brushInviteCode(String inviteCode, int randomUserId) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        refresh(randomUserId);
        Result uisk = getUisk(randomUserId);
        if ("banned".equals(uisk.getUi()) && "banned".equals(uisk.getSk())) {
            return 0;
        } else {
            while (true) {
                List<Future<String>> futures = new ArrayList<>();
                futures.add(executor.submit(() -> getResponseBody(V303, randomUserId, 10868)));
                sleep(350);
                futures.add(executor.submit(() -> getResponseBody(V876, randomUserId, inviteCode)));
                sleep(350);
                try {
                    String response303Body = futures.get(0).get(3, TimeUnit.SECONDS);
                    if (JSON.parseObject(response303Body).getIntValue("r") == 0) {
                        String response876Body = futures.get(1).get(3, TimeUnit.SECONDS);
                        if (JSON.parseObject(response876Body).getIntValue("r") != 0) {
                            futures.add(executor.submit(() -> getResponseBody(V876, randomUserId, inviteCode)));
                            sleep(350);
                            String response876BodyResend = futures.get(2).get(3, TimeUnit.SECONDS);
                            return JSON.parseObject(response876BodyResend).getIntValue("r") == 0 ? 1 : 0;
                        } else return 1;
                    } else return 0;
                } catch (Exception ignored) {
                    refresh(randomUserId);
                }
            }
        }
    }

    private static void anniversaryGacha(int userId) {
        ExecutorService executor = Executors.newFixedThreadPool(30);
        Check.V316 v316 = new Check.V316();
        refresh(userId);
        Result uisk = getUisk(userId);
        if ("banned".equals(uisk.getUi()) && "banned".equals(uisk.getSk())) {
            UserJsonUtils.JsonUtil(userId, "isBanned", true);
            UserJsonUtils.JsonUtil(userId, "activate", false);
        } else {
            while (true) try {
                Future<String> futureV316 = executor.submit(() -> getResponseBody(V316, userId));
                String response316Body = futureV316.get(3, TimeUnit.SECONDS);
                v316.setResponseBody(response316Body);
                if (!v316.isValid(0)) refresh(userId);
                else {
                    Check.V316.d d = v316.new d();
                    if (!d.checkJSONArray("al", "i", 60011)) {
                        List<Future<String>> futures;
                        while (true) {
                            futures = new ArrayList<>();
                            futures.add(executor.submit(() -> getResponseBody(V877, userId, 1)));
                            sleep(350);
                            futures.add(executor.submit(() -> getResponseBody(V877, userId, 2)));
                            sleep(350);
                            futures.add(executor.submit(() -> getResponseBody(V877, userId, 3)));
                            sleep(350);
                            futures.add(executor.submit(() -> getResponseBody(V877, userId, 6)));
                            sleep(350);
                            futures.add(executor.submit(() -> getResponseBody(V877, userId, 7)));
                            sleep(350);
                            futures.add(executor.submit(() -> getResponseBody(V877, userId, 8)));
                            sleep(350);
                            futures.add(executor.submit(() -> getResponseBody(V877, userId, 9)));
                            sleep(350);
                            try {
                                int success = 0;
                                for (Future<String> future : futures) {
                                    String response877Body = future.get(3, TimeUnit.SECONDS);
                                    if (
                                            JSON.parseObject(response877Body).getIntValue("r") == 0 ||
                                                    JSON.parseObject(response877Body).getIntValue("r") == 20000
                                    ) success++;
                                }
                                if (success == 7) break;
                            } catch (Exception ignored) {
                                refresh(userId);
                            }
                        }
                        while (true) {
                            futures.add(executor.submit(() -> getResponseBody(V878, userId, "0")));
                            sleep(350);
                            try {
                                String response878GachaBody = futures.get(futures.size() - 1).get(3, TimeUnit.SECONDS);
                                if (JSON.parseObject(response878GachaBody).getIntValue("r") == 46343) {
                                    futures.add(executor.submit(() -> getResponseBody(V878, userId, "1")));
                                    sleep(350);
                                    String response878GetBody = futures.get(futures.size() - 1).get(3, TimeUnit.SECONDS);
                                    if (
                                            JSON.parseObject(response878GetBody).getIntValue("r") == 0 ||
                                                    JSON.parseObject(response878GetBody).getIntValue("r") == 46342
                                    ) {
                                        UserJsonUtils.JsonUtil(userId, "activate", false);
                                        break;
                                    }
                                }
                            } catch (Exception ignored) {
                                refresh(userId);
                            }
                        }
                    } else {
                        UserJsonUtils.JsonUtil(userId, "activate", false);
                        break;
                    }
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }
}
