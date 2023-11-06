package smf.icdada;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

import static smf.icdada.HttpUtils.Base.*;
import static smf.icdada.RequestType.*;

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

    public static void fileChecker(boolean check) {
        Path path = Paths.get(banuserPath);
        if (!Files.exists(path) || check) {
            Log.v("请输入账号UserId开始");
            int start = smfScanner.Int(false, "^\\d{8,}$");
            Log.v("请输入账号UserId结束");
            int end = smfScanner.Int(false, "^\\d{8,}$");
            File file = new File(path.toUri());
            JSONObject parse = JSONObject.parse("{}");
            JSONArray bannedUsers = new JSONArray();
            JSONArray account = new JSONArray();
            JSONObject object1 = new JSONObject();
            object1.put("serverId", Integer.parseInt(String.valueOf(Inter.appId) + Inter.channelId));
            object1.put("isBanned", false);
            object1.put("isProtected", false);
            account.add(object1);
            IntStream.range(start, end + 1).forEach(i -> {
                JSONObject user = new JSONObject();
                user.put("userId", String.valueOf(i));
                user.put("account", account);
                bannedUsers.add(user);
            });
            parse.put("BannedUsers", bannedUsers);
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(parse.toJSONString(JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat));
                fileWriter.flush();
                Log.s("配置文件已生成！文件名为:" + file.getName());
                Log.i("生成路径位于:" + file.getPath());
                System.exit(0);
            } catch (Exception e) {
                Log.w(e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.v("封号区间文件已存在，是否覆盖？");
            if (smfScanner.Boolean(false)) {
                fileChecker(true);
            } else {
                System.exit(0);
            }
        }
    }

    private static List<String> readBannedUser() {
        List<String> bannedUserIds = new ArrayList<>();
        Path path = Paths.get(banuserPath);
        try {
            if (Files.exists(path)) {
                JSONObject parse = JSONObject.parse(Files.readString(path));
                JSONArray bannedUsers = parse.getJSONArray("BannedUsers");
                for (Object bannedUser : bannedUsers) {
                    JSONObject jsonObject = (JSONObject) bannedUser;
                    JSONArray account = jsonObject.getJSONArray("account");
                    JSONObject a1 = (JSONObject) account.get(0);
                    if (a1.getIntValue("serverId") != Integer.parseInt(String.valueOf(Inter.appId) + Inter.channelId)) {
                        Log.w("警告:封号方法配置为" + Inter.appId + Inter.channelId + "渠道，但封号账号:" + jsonObject.getIntValue("userId") + "配置为" + a1.getIntValue("serverId") + "渠道，你确定要这样做吗？");
                        if (smfScanner.Boolean(false)) {
                            if (!a1.getBooleanValue("isBanned") && !a1.getBooleanValue("isProtected")) {
                                bannedUserIds.add(jsonObject.getString("userId"));
                            }
                        }
                    } else if (!a1.getBooleanValue("isBanned") && !a1.getBooleanValue("isProtected")) {
                        bannedUserIds.add(jsonObject.getString("userId"));
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
        List<String> bannedUserIds = readBannedUser();
        List<Future<?>> futures = new ArrayList<>();
        do {
            for (String bannedUserId : bannedUserIds) {
                sleep(100);
                futures.add(getExecutor(bannedUserId).submit(() -> {
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
        Log.s("所有账号封禁完成，程序退出");
        System.exit(0);
    }

    private static void banned(String userId) {
        refresh(userId);
        Result uisk = getUisk(userId);
        if ("banned".equals(uisk.getUi()) && "banned".equals(uisk.getSk())) {
            JsonUtil(userId, true, false);
        } else {
            Check.V437 v437 = new Check.V437();
            Check.V316 v316 = new Check.V316();
            JSONPath snailCoinPath = JSONPath.of("$.il[?(@.i == '23400')].q");
            JSONPath chestnutPiecePath = JSONPath.of("$.pcl[?(@.i == '22000090')].q");
            JSONPath gemPath = JSONPath.of("$.p.fg");
            int i = 0;
            while (true) {
                i++;
                try {
                    Future<String> futureV437 = getExecutor(userId).submit(() -> getResponseBody(V437, userId));
                    String response437CheckBody = futureV437.get(3, TimeUnit.SECONDS);
                    v437.setResponseBody(response437CheckBody);
                    if (!v437.isValid(0)) {
                        if (v437.isValid(10800)) break;
                        else {
                            Log.v("账号:" + userId + " || 检查失败，正在重试…… || " + response437CheckBody);
                            refresh(userId);
                        }
                    } else if (v437.isNew() || i >= 10) {
                        checkPrint(userId);
                        JsonUtil(userId, false, true);
                        break;
                    } else while (true) {
                        try {
                            Future<String> futureV316 = getExecutor(userId).submit(() -> getResponseBody(V316, userId));
                            String response316GetBody = futureV316.get(3, TimeUnit.SECONDS);
                            v316.setResponseBody(response316GetBody);
                            if (!v316.isValid(0)) {
                                if (v316.isValid(10800)) break;
                                else {
                                    Log.v("账号:" + userId + " || 读取失败，正在重试…… || " + response316GetBody);
                                    refresh(userId);
                                }
                            } else {
                                int snailCoin = Optional.ofNullable((Integer) snailCoinPath.eval(v316.data)).orElse(0);
                                int chestnutPiece = Optional.ofNullable((Integer) chestnutPiecePath.eval(v316.data)).orElse(0);
                                int gem = Optional.ofNullable((Integer) gemPath.eval(v316.data)).orElse(0);
                                if (snailCoin >= 500000 || chestnutPiece >= 3000 || gem >= 2000000) {
                                    checkPrint(userId, gem, snailCoin, chestnutPiece);
                                    if (applyBanner(userId)) {
                                        refresh(userId);
                                        uisk = getUisk(userId);
                                        if ("banned".equals(uisk.getUi()) && "banned".equals(uisk.getSk())) {
                                            JsonUtil(userId, true, false);
                                            break;
                                        } else {
                                            Log.v("账号:" + userId + " || 封号失败，正在重试……");
                                        }
                                    }
                                } else {
                                    checkPrint(userId, gem, snailCoin, chestnutPiece);
                                    JsonUtil(userId, false, true);
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

    private static boolean applyBanner(String userId) {
        try {
            Future<String> futureV303 = getExecutor(userId).submit(() -> getResponseBody(V303, userId, 10800));
            Check.V303 v303 = new Check.V303();
            v303.setResponseBody(futureV303.get(3, TimeUnit.SECONDS));
            if (v303.isValid(0)) {
                int count = 0;
                Check.V927 v927 = new Check.V927();
                JSONArray pl = new JSONArray("[{\"i\":111067,\"q\":1},{\"i\":200030,\"q\":2},{\"i\":200053,\"q\":2},{\"i\":200054,\"q\":2},{\"i\":200055,\"q\":1}]");
                List<Future<String>> futures = new ArrayList<>();
                IntStream.range(0, 4).forEach(i -> futures.add(getExecutor(userId).submit(() -> getResponseBody(V927, userId, pl))));
                for (Future<String> future : futures) {
                    v927.setResponseBody(future.get(3, TimeUnit.SECONDS));
                    if (v927.isValid(0) || v927.isValid(20013)) count++;
                }
                return count == 4;
            } else {
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void JsonUtil(String userId, boolean isBanned, boolean isProtected) {
        try {
            lock.writeLock().lock();
            Path path = Paths.get(banuserPath);
            JSONObject parse = JSONObject.parse(Files.readString(path));
            JSONPath userPath = JSONPath.of("$.BannedUsers[?(@.userId == '" + userId + "')].account");
            JSONPath accountPath = JSONPath.of("$.BannedUsers[?(@.userId == '" + userId + "')].account[?(@.serverId == " + Integer.parseInt(Inter.oi) + ")]");
            Object userAccount = accountPath.eval(parse);
            if (userAccount != null) {
                JSONObject user = (JSONObject) userAccount;
                user.put("isBanned", isBanned);
                user.put("isProtected", isProtected);
            } else {
                JSONArray users = (JSONArray) userPath.eval(parse);
                JSONObject accountObject = new JSONObject();
                accountObject.put("serverId", Integer.parseInt(Inter.oi));
                accountObject.put("isBanned", isBanned);
                accountObject.put("isProtected", isProtected);
                users.add(accountObject);
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

    public static void checkPrint(String userId, Object... gem_snailCoin_chestnutPiece) {
        boolean abnormal = false;
        Integer gem = (Integer) gem_snailCoin_chestnutPiece[0];
        Integer snailCoin = (Integer) gem_snailCoin_chestnutPiece[1];
        Integer chestnutPiece = (Integer) gem_snailCoin_chestnutPiece[2];
        Log.Pair userIdPair, gemPair, snailCoinPair, chestnutPiecePair;
        if (gem >= 2000000) {
            abnormal = true;
            gemPair = Log.p("钻石:" + gem, Log.Color.RED);
        } else gemPair = Log.p("钻石:" + gem, Log.Color.WHITE);
        if (snailCoin >= 500000) {
            abnormal = true;
            snailCoinPair = Log.p("蜗牛币:" + snailCoin, Log.Color.RED);
        } else snailCoinPair = Log.p("蜗牛币:" + snailCoin, Log.Color.WHITE);
        if (chestnutPiece >= 3000) {
            abnormal = true;
            chestnutPiecePair = Log.p("荸荠碎片:" + chestnutPiece, Log.Color.RED);
        } else chestnutPiecePair = Log.p("荸荠碎片:" + chestnutPiece, Log.Color.WHITE);
        if (abnormal) {
            userIdPair = Log.p("账号:" + userId + " || 检测异常", Log.Color.PURPLE);
        } else {
            userIdPair = Log.p("账号:" + userId + " || 检测通过", Log.Color.GREEN);
        }
        Log.c(userIdPair, Log.Separator, gemPair, Log.Separator, snailCoinPair, Log.Separator, chestnutPiecePair);
    }
}
