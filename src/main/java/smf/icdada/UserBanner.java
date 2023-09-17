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
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static smf.icdada.HttpUtils.Base.*;
import static smf.icdada.HttpUtils.Strategy.apply;

/**
 * @author SMF & icdada
 * @描述: 封号方法类
 * <p>
 * &#064;//  TODO: 2023/7/30  未完成的注释:封号方法类
 * </p>
 */
public class UserBanner {
    public static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final String banuserPath = System.getProperty("user.dir") + File.separator + "banuser.json";
    private static final int oi = Integer.parseInt(String.valueOf(Inter.appId) + Inter.channelId);
    private static final Print print = new Print();

    public static void fileChecker(boolean check) {
        Path path = Paths.get(banuserPath);
        if (!Files.exists(path) || check) {
            int i;
            Log.v("请输入账号UserId开始:");
            int start = smfScanner.smfInt(false, "^\\d{8}$");
            Log.v("请输入账号UserId结束:");
            int end = smfScanner.smfInt(false, "^\\d{8}$");
            File file = new File(path.toUri());
            JSONObject parse = JSONObject.parse("{}");
            JSONArray bannedUsers = new JSONArray();
            JSONArray account = new JSONArray();
            JSONObject object1 = new JSONObject();
            JSONObject object2 = new JSONObject();
            object1.put("severId", 109208);
            object1.put("isBanned", false);
            object1.put("isProtected", false);
            object2.put("severId", 109250);
            object2.put("isBanned", false);
            object2.put("isProtected", false);
            account.add(object1);
            account.add(object2);
            for (i = start; i <= end; i++) {
                JSONObject user = new JSONObject();
                user.put("userId", i);
                user.put("account", account);
                bannedUsers.add(user);
            }
            parse.put("BannedUsers", bannedUsers);
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(parse.toJSONString(JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat));
                fileWriter.flush();
                Log.s("配置文件已生成！文件名为:" + file.getName());
                Log.i("生成路径位于:" + file.getPath());
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.v("封号区间文件已存在，是否覆盖？(Y/N)");
            if (smfScanner.smfBoolean(false)) {
                fileChecker(true);
            } else {
                System.exit(0);
            }
        }
    }

    private static List<Integer> readBannedUser() {
        List<Integer> bannedUserIds = new ArrayList<>();
        Path path = Paths.get(banuserPath);
        try {
            if (Files.exists(path)) {
                JSONObject parse = JSONObject.parse(Files.readString(path));
                JSONArray bannedUsers = parse.getJSONArray("BannedUsers");
                for (Object bannedUser : bannedUsers) {
                    JSONObject jsonObject = (JSONObject) bannedUser;
                    JSONArray account = jsonObject.getJSONArray("account");
                    JSONObject a1 = (JSONObject) account.get(0);
                    JSONObject a2 = (JSONObject) account.get(1);
                    if (
                            !a1.getBooleanValue("isBanned") && !a1.getBooleanValue("isProtected") &&
                                    !a2.getBooleanValue("isBanned") && !a2.getBooleanValue("isProtected")
                    ) {
                        bannedUserIds.add(jsonObject.getIntValue("userId"));

                    }
                }
            } else {
                Log.v("未找到封号区间文件，正在进行引导创建……");
                fileChecker(true);
            }
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
        return bannedUserIds;
    }

    public static void bannedFunction() {
        List<Integer> bannedUserIds = readBannedUser();
        ExecutorService executorService = Executors.newFixedThreadPool(25000);
        List<Future<?>> futures = new ArrayList<>();
        do {
            for (int bannedUserId : bannedUserIds) {
                sleep(100);
                futures.add(executorService.submit(() -> {
                    Log.v("账号:" + bannedUserId + " || 已读取，开始封禁");
                    banned(bannedUserId);
                }));
            }
            for (Future<?> future : futures)
                try {
                    future.get();
                } catch (Exception ignored) {
                }
            bannedUserIds = readBannedUser();
        } while (!bannedUserIds.isEmpty());
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.s("所有账号封禁完成，程序退出");
        System.exit(0);
    }

    private static void banned(int userId) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        refresh(userId);
        Result uisk = getUisk(userId);
        if ("banned".equals(uisk.getUi()) && "banned".equals(uisk.getSk())) {
            JsonUtil(oi, userId, true, false);
        } else {
            Check.V437 v437 = new Check.V437();
            Check.V316 v316 = new Check.V316();
            int i = 0;
            while (true) {
                i++;
                try {
                    Future<String> futureV437 = executor.submit(() -> getResponseBody(userId, RequestType.ISNEW.getRequestBody(userId)));
                    String response437CheckBody = futureV437.get(3, TimeUnit.SECONDS);
                    v437.setResponseBody(response437CheckBody);
                    if (!v437.isValid(0)) {
                        if (v437.isValid(10800)) break;
                        else {
                            Log.v("账号:" + userId + " || 检查失败，正在重试…… || " + response437CheckBody);
                            refresh(userId);
                        }
                    } else if (v437.isNew() || i >= 10) {
                        print.normalPrint(userId, null, null, null);
                        JsonUtil(oi, userId, false, true);
                        break;
                    } else while (true) {
                        try {
                            Future<String> futureV316 = executor.submit(() -> getResponseBody(userId, RequestType.GET.getRequestBody(userId)));
                            String response316GetBody = futureV316.get(3, TimeUnit.SECONDS);
                            v316.setResponseBody(response316GetBody);
                            if (!v316.isValid(0)) {
                                if (v316.isValid(10800)) break;
                                else {
                                    Log.v("账号:" + userId + " || 读取失败，正在重试…… || " + response316GetBody);
                                    refresh(userId);
                                }
                            } else {
                                Check.V316.d d = v316.new d();
                                int snailCoin = 0, chestnutPiece = 0, gem = 0;
                                //钻石
                                if (d.containsKey("p")) {
                                    JSONObject p = d.getJSONObject("p");
                                    if (p.containsKey("fg")) {
                                        gem = Integer.parseInt(p.getString("fg"));
                                    }
                                }
                                //蜗牛币
                                if (d.containsKey("il")) {
                                    JSONArray il = d.getJSONArray("il");
                                    for (Object object : il) {
                                        JSONObject ilObject = (JSONObject) object;
                                        if ("23400".equals(ilObject.getString("i"))) {
                                            snailCoin = Integer.parseInt(ilObject.getString("q"));
                                        }
                                    }
                                }
                                //荸荠碎片
                                if (d.containsKey("pcl")) {
                                    JSONArray blank = d.getJSONArray("pcl");
                                    for (Object object : blank) {
                                        JSONObject blankObject = (JSONObject) object;
                                        if ("22000090".equals(blankObject.getString("i"))) {
                                            chestnutPiece = Integer.parseInt(blankObject.getString("q"));
                                        }
                                    }
                                }
                                if (snailCoin >= 500000 || chestnutPiece >= 3000 || gem >= 2000000) {
                                    print.abnormalPrint(userId, gem, snailCoin, chestnutPiece);
                                    if (apply(userId, true)) {
                                        refresh(userId);
                                        uisk = getUisk(userId);
                                        if ("banned".equals(uisk.getUi()) && "banned".equals(uisk.getSk())) {
                                            JsonUtil(oi, userId, true, false);
                                            break;
                                        } else {
                                            Log.v("账号:" + userId + " || 封号失败，正在重试……");
                                        }
                                    }
                                } else {
                                    print.normalPrint(userId, gem, snailCoin, chestnutPiece);
                                    JsonUtil(oi, userId, false, true);
                                    break;
                                }
                            }
                        } catch (Exception ignored) {
                            refresh(userId);
                        }
                    }
                } catch (Exception ignored) {
                    refresh(userId);
                }
            }
        }
    }

    private static void JsonUtil(int channel, int userId, boolean isBanned, boolean isProtected) {
        try {
            lock.writeLock().lock();
            Path path = Paths.get(banuserPath);
            JSONObject parse = JSONObject.parse(Files.readString(path));
            JSONArray bannedUsers = parse.getJSONArray("BannedUsers");
            for (Object object : bannedUsers) {
                JSONObject bannedUser = (JSONObject) object;
                if (bannedUser.getIntValue("userId") == userId) {
                    JSONArray account = bannedUser.getJSONArray("account");
                    for (Object objectAccount : account) {
                        JSONObject userAccount = (JSONObject) objectAccount;
                        if (userAccount.getIntValue("severId") == channel) {
                            userAccount.put("isBanned", isBanned);
                            userAccount.put("isProtected", isProtected);
                        }
                    }
                }
            }
            String formattedJson = parse.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
            String tempFileName = System.getProperty("user.dir") + File.separator + "temp" + File.separator + UUID.randomUUID() + ".tmp";
            Path tempPath = Paths.get(tempFileName);
            try (FileWriter fileWriter = new FileWriter(tempPath.toFile())) {
                fileWriter.write(formattedJson);
                fileWriter.flush();
            }
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(tempPath);
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
