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

public class Anniversary {
    public static void measure() {
        Map<Integer, String> userObjects = readUserObjects();
        ExecutorService executorService = Executors.newFixedThreadPool(userObjects.size());
        for (Map.Entry<Integer, String> userObject : userObjects.entrySet()) {
            sleep(100);
            executorService.submit(() -> {
                System.out.println("\033[33m" + "账号：" + userObject.getKey() + "\033[0m" + " || " + "\033[33m" + "已读取，开始执行" + "\033[0m");
                brush(userObject.getKey(), userObject.getValue());
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("所有账号处理完成，程序退出");
        System.exit(0);
    }

    private static Map<Integer, String> readUserObjects() {
        if (Inter.chooser == 4) {
            System.out.println("\033[33m" + "正在等待代理池刷新……" + "\033[0m");
            sleep(Inter.waiter);
        }
        Map<Integer, String> userObjects = new HashMap<>();
        try {
            String stringUrl = System.getProperty("user.dir") + File.separator + "user.json";
            Path userPath = Paths.get(stringUrl);
            if (Files.exists(userPath)) {
                JSONObject userData = JSONObject.parse(Files.readString(userPath));
                JSONArray usersArray = userData.getJSONArray("Users");
                for (Object userElement : usersArray) {
                    JSONObject userObject = (JSONObject) userElement;
                    if (userObject.containsKey("activate")) {
                        if (userObject.getBooleanValue("activate") && userObject.containsKey("inviteCode")) {
                            int userId = userObject.getIntValue("userId");
                            String inviteCode = userObject.getString("inviteCode");
                            userObjects.put(userId, inviteCode);
                        }
                    } else if (userObject.containsKey("inviteCode")) {
                        int userId = userObject.getIntValue("userId");
                        String inviteCode = userObject.getString("inviteCode");
                        UserJsonUtils.JsonUtil(userId, "activate", true);
                        userObjects.put(userId, inviteCode);
                    }
                }
            } else {
                System.out.println("\033[31m" + "用户库文件异常，请检查：" + System.getProperty("user.dir") + File.separator + "user.json文件是否存在" + "\033[0m");
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                System.exit(0);
            }
        } catch (Exception e) {
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
                System.out.println("\033[33m" + "邀请码：" + inviteCode + "\033[0m" + " || " + "\033[31m" + "刷取失败" + "\033[0m" + " || " + "共成功：" + count);
            } else {
                System.out.println("\033[33m" + "邀请码：" + inviteCode + "\033[0m" + " || " + "\033[32m" + "刷取成功" + "\033[0m" + " || " + "共成功：" + count);
            }
        } while (count < 13);
        System.out.println("\033[33m" + "邀请码：" + inviteCode + "\033[0m" + " || " + "\033[32m" + "邀请结束" + "\033[0m");
    }

    private static void brush(int userId, String inviteCode) {
        int count = checkInviteCode(userId);
        if (count == 12202) {
            UserJsonUtils.JsonUtil(userId, "isBanned", true);
            UserJsonUtils.JsonUtil(userId, "activate", false);
        } else while (count < 13) {
            count += brushInviteCode(inviteCode, getRandomId());
        }
        System.out.println("\033[33m" + "邀请码：" + inviteCode + "\033[0m" + " || " + "\033[32m" + "邀请结束，开始刷取" + "\033[0m");
        anniversaryGacha(userId);
    }

    private static String getInviteCode() {
        System.out.println("\033[33m" + "请输入活动邀请码，并按回车键继续：" + "\033[0m");
        return smfScanner.smfString(true);
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
            Future<String> future = executor.submit(() -> getResponseBody(userId, RequestType.IN.getRequestBodyById(userId)));
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
                futures.add(executor.submit(() -> getResponseBody(randomUserId, RequestType.IN.getRequestBody(randomUserId))));
                sleep(350);
                futures.add(executor.submit(() -> getResponseBody(randomUserId, RequestType.ANNI_BRUSH.getRequestBody(randomUserId, inviteCode))));
                sleep(350);
                try {
                    String response303Body = futures.get(0).get(3, TimeUnit.SECONDS);
                    if (JSON.parseObject(response303Body).getIntValue("r") == 0) {
                        String response876Body = futures.get(1).get(3, TimeUnit.SECONDS);
                        if (JSON.parseObject(response876Body).getIntValue("r") != 0) {
                            futures.add(executor.submit(() -> getResponseBody(randomUserId, RequestType.ANNI_BRUSH.getRequestBody(randomUserId, inviteCode))));
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
        ExecutorService executor = Executors.newFixedThreadPool(10);
        refresh(userId);
        Result uisk = getUisk(userId);
        if ("banned".equals(uisk.getUi()) && "banned".equals(uisk.getSk())) {
            UserJsonUtils.JsonUtil(userId, "isBanned", true);
            UserJsonUtils.JsonUtil(userId, "activate", false);
        } else {
            while (true) try {
                Future<String> futureV878 = executor.submit(() -> getResponseBody(userId, RequestType.ANNI_BEAN_2.getRequestBody(userId)));
                String response878BodyPreCheck = futureV878.get(3, TimeUnit.SECONDS);
                if (JSON.parseObject(response878BodyPreCheck).getIntValue("r") == 46009) {
                    List<Future<String>> futures;
                    while (true) {
                        futures = new ArrayList<>();
                        futures.add(executor.submit(() -> getResponseBody(userId, RequestType.ANNI_GET.getRequestBody(userId, 1))));
                        sleep(350);
                        futures.add(executor.submit(() -> getResponseBody(userId, RequestType.ANNI_GET.getRequestBody(userId, 2))));
                        sleep(350);
                        futures.add(executor.submit(() -> getResponseBody(userId, RequestType.ANNI_GET.getRequestBody(userId, 3))));
                        sleep(350);
                        futures.add(executor.submit(() -> getResponseBody(userId, RequestType.ANNI_GET.getRequestBody(userId, 6))));
                        sleep(350);
                        futures.add(executor.submit(() -> getResponseBody(userId, RequestType.ANNI_GET.getRequestBody(userId, 7))));
                        sleep(350);
                        futures.add(executor.submit(() -> getResponseBody(userId, RequestType.ANNI_GET.getRequestBody(userId, 8))));
                        sleep(350);
                        futures.add(executor.submit(() -> getResponseBody(userId, RequestType.ANNI_GET.getRequestBody(userId, 9))));
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
                        futures.add(executor.submit(() -> getResponseBody(userId, RequestType.ANNI_BEAN_1.getRequestBody(userId))));
                        sleep(350);
                        futures.add(executor.submit(() -> getResponseBody(userId, RequestType.ANNI_BEAN_2.getRequestBody(userId))));
                        sleep(350);
                        try {
                            String response878Body = futures.get(futures.size() - 1).get(3, TimeUnit.SECONDS);
                            if (
                                    JSON.parseObject(response878Body).getIntValue("r") == 0 ||
                                            JSON.parseObject(response878Body).getIntValue("r") == 46342
                            ) {
                                UserJsonUtils.JsonUtil(userId, "activate", false);
                                break;
                            }
                        } catch (Exception ignored) {
                            refresh(userId);
                        }
                    }
                } else if (
                        JSON.parseObject(response878BodyPreCheck).getIntValue("r") == 0 ||
                                JSON.parseObject(response878BodyPreCheck).getIntValue("r") == 46342
                ) {
                    UserJsonUtils.JsonUtil(userId, "activate", false);
                    break;
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }
}
