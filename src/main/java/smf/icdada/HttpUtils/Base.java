package smf.icdada.HttpUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import smf.icdada.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static smf.icdada.ProxyManager.proxy;

/**
 * @author SMF & icdada
 * @描述: HttpUtils基类
 * <p>
 * 包含处理Http请求的基础函数。
 * </p>
 */
public class Base {
    private static final ConcurrentHashMap<Integer, Result> Account = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Result> Proxy = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(1000);

    /**
     * @param userId 八位用户ID
     * @return Result(ui, sk)
     * @描述: uisk重要数值获取与刷新
     */
    private static CompletableFuture<Result> uisk(int userId) {
        Result uisk = null;
        Result proxy = proxy();
        Proxy.put(userId, proxy);
        do {
            try {
                Future<String> future = executor.submit(() -> getResponseBody(userId, RequestType.OI.getRequestBodyById(userId)));
                try {
                    String response = future.get(10, TimeUnit.SECONDS);
                    if (JSON.isValidObject(response)) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if (jsonObject.containsKey("r")) {
                            if (jsonObject.getIntValue("r") == 20507) {
                                Log.e("账号：" + userId + " || " + "账号被封禁，已自动跳出");
                                uisk = new Result("banned", "banned");
                            } else if (jsonObject.getIntValue("r") == 0) {
                                JSONObject dObject = jsonObject.getJSONObject("d");
                                String ui = dObject.getString("ui");
                                String sk = dObject.getString("sk");
                                if (Inter.inter == 10) {
                                    Log.i("userId:" + userId);
                                    Log.i("ui:" + ui);
                                    Log.i("sk:" + sk + "\n");
                                }
                                uisk = new Result(ui, sk);
                            }
                        }
                    }
                } catch (Exception ignored) {
                    proxy = proxy();
                    Proxy.put(userId, proxy);
                }
            } catch (Exception ignored) {
            }
        } while (uisk == null);
        Result finalUisk = uisk;
        return CompletableFuture.supplyAsync(() -> finalUisk, executor);
    }

    public static String getResponseBody(int userId, String requestBody) {
        try {
            Result proxy = getProxy(userId);
            return HttpCrypto.decryptRES(
                    HttpSender.doQuest(
                            Inter.environment,
                            HttpCrypto.encryptREQ(
                                    requestBody
                            ),
                            proxy.getProxyHost(),
                            proxy.getProxyPort()
                    )
            );
        } catch (Exception ignored) {
            return "{\"r\":12202}";
        }
    }

    public static void refresh(int userId) {
        while (true) {
            Result previous = Account.get(userId);
            try {
                Result latest = uisk(userId).get();
                if (!latest.equals(previous)) {
                    Account.put(userId, latest);
                    break;
                }
                sleep(5000);
            } catch (Exception ignored) {
            }
        }
    }

    public static Result getUisk(int userId) {
        return Account.get(userId);
    }

    public static Result getProxy(int userId) {
        return Proxy.get(userId);
    }

    /**
     * @return userId
     * @描述: userId监听用户输入获取方法
     */
    public static int userIdGetter() {
        int userId;
        Log.v("请输入拓维UserID，并按回车键继续：");
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile("^\\d+$");
        while (true) {
            input = scanner.nextLine().trim();
            if (pattern.matcher(input).matches() && input.length() == 8) {
                userId = Integer.parseInt(input);
                if (userId > 0) {
                    break;
                } else {
                    Log.e("请输入正确的UserID");
                }
            } else {
                Log.e("输入无效，请输入正确的UserID");
            }
        }
        return userId;
    }

    /**
     * @return userIds
     * @描述: userId批量获取方法
     */
    public static List<Integer> readUserIds() {
        List<Integer> userIds = new ArrayList<>();
        try {
            String stringUrl = System.getProperty("user.dir") + File.separator + "user.json";
            Path userPath = Paths.get(stringUrl);
            if (Files.exists(userPath)) {
                JSONObject userData = JSONObject.parse(Files.readString(userPath));
                JSONArray usersArray = userData.getJSONArray("Users");
                for (Object userElement : usersArray) {
                    JSONObject userObject = (JSONObject) userElement;
                    if (userObject.containsKey("activate")) {
                        if (userObject.getBooleanValue("activate")) {
                            int userId = userObject.getIntValue("userId");
                            userIds.add(userId);
                        }
                    } else {
                        int userId = userObject.getIntValue("userId");
                        UserJsonUtils.JsonUtil(userId, "activate", true);
                        userIds.add(userId);
                    }
                }
            } else {
                Log.e("用户库文件异常，请检查：" + System.getProperty("user.dir") + File.separator + "user.json文件是否存在");
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                System.exit(0);
            }
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
        return userIds;
    }

    /**
     * @描述: 自动加解密控制台导引方法
     */
    public static void cryptoGuideLine() {
        try {
            Log.v("请输入任意内容或数据包，输入空字段以结束：");
            String body = smfScanner.smfLongString(true);
            if (body.isBlank()) System.exit(0);
            if (JSON.isValidObject(body)) {
                JSONObject jsonObject = JSONObject.parse(body);
                if (jsonObject.containsKey("i") && jsonObject.containsKey("r")) {
                    if (jsonObject.containsKey("t")) {
                        Log.a(HttpCrypto.encryptREQ(body));
                        Log.v("是否发送数据包取得响应？(Y/N)");
                        if (smfScanner.smfBoolean(false))
                            Log.a(HttpCrypto.decryptRES(HttpSender.doQuest(Inter.environment, HttpCrypto.encryptREQ(body))));
                    }
                    if (jsonObject.containsKey("e")) {
                        Log.a(HttpCrypto.decryptRES(body));
                    }
                    if (jsonObject.containsKey("d")) {
                        Log.a(HttpCrypto.encryptRES(body));
                    }
                }
            } else if (body.startsWith("--_{{}}_")) {
                Log.a(HttpCrypto.decryptREQ(body));
                Log.v("是否发送数据包取得响应？(Y/N)");
                if (smfScanner.smfBoolean(false))
                    Log.a(HttpCrypto.decryptRES(HttpSender.doQuest(Inter.environment, body)));
            } else {
                Log.e("自动检测失败，请手动输入数据包标识并选择功能：\n" + "\033[33m" + "请输入数据包标识：");
                String identifier = smfScanner.smfString(true);
                Log.d("请选择功能：\n[1] 请求加密\n[2] 请求解密\n[3] 响应加密\n[4] 响应解密\n[5] 获取密钥和偏移\n[6] 获取MD5");
                boolean keepRunning = true;
                while (keepRunning) {
                    int choice = smfScanner.smfInt(false);
                    switch (choice) {
                        case 1 -> {
                            Log.a(HttpCrypto.encryptREQ(identifier, body));
                            keepRunning = false;
                        }
                        case 2 -> {
                            Log.a(HttpCrypto.decryptREQ(identifier, body));
                            keepRunning = false;
                        }
                        case 3 -> {
                            Log.a(HttpCrypto.encryptRES(identifier, body));
                            keepRunning = false;
                        }
                        case 4 -> {
                            Log.a(HttpCrypto.decryptRES(identifier, body));
                            keepRunning = false;
                        }
                        case 5 -> {
                            Log.s("Identifier:" + identifier +
                                    "\nkey:" + new String(HttpCrypto.getKey(identifier), StandardCharsets.UTF_8) +
                                    "\niv:" + new String(HttpCrypto.getIv(identifier), StandardCharsets.UTF_8));
                            keepRunning = false;
                        }
                        case 6 -> {
                            Log.s("MD5:" + new String(HttpCrypto.getMD5(body), StandardCharsets.UTF_8));
                            keepRunning = false;
                        }
                        default -> Log.e("输入有误，请重新输入功能序号：");
                    }
                }
            }
            cryptoGuideLine();
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @描述: 自定义sleep函数，防止编译器谬误
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.e("sleep function error:");
            e.printStackTrace();
        }
    }
}
