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
public class base {
    private static final ConcurrentHashMap<Integer, Result> Account = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Result> Proxy = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(1000);

    /**
     * @param channel 账号所属渠道
     * @param userId  八位用户ID
     * @return Result(ui, sk)
     * @描述: uisk重要数值获取与刷新
     */
    private static CompletableFuture<Result> uisk(int channel, int userId) {
        Result uisk = null;
        Result proxy = proxy();
        Proxy.put(userId, proxy);
        do {
            try {
                String proxyHost = getProxy(userId).getProxyHost();
                int proxyPort = getProxy(userId).getProxyPort();
                Future<String> future = executor.submit(() -> getRes(channel, userId, proxyHost, proxyPort));
                try {
                    String response = future.get(10, TimeUnit.SECONDS);
                    if (JSON.isValidObject(response)) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if (jsonObject.containsKey("r")) {
                            if (jsonObject.getIntValue("r") == 20507) {
                                System.out.println("\033[31m" + "账号：" + userId + "\033[0m" + " || " + "\033[31m" + "账号被封禁，已自动跳出" + "\033[0m");
                                uisk = new Result("banned", "banned");
                            } else if (jsonObject.getIntValue("r") == 0) {
                                JSONObject dObject = jsonObject.getJSONObject("d");
                                String ui = dObject.getString("ui");
                                String sk = dObject.getString("sk");
                                if (Inter.inter == 10) {
                                    System.out.println("\033[34m" + "userId:" + userId + "\033[0m");
                                    System.out.println("\033[34m" + "ui:" + ui + "\033[0m");
                                    System.out.println("\033[34m" + "sk:" + sk + "\n" + "\033[0m");
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

    private static String getRes(int channel, int userId, String proxyHost, int proxyPort) {
        try {
            return HttpCrypto.decryptRES(
                    HttpSender.doQuest(
                            Inter.isAndroid,
                            HttpCrypto.encryptREQ(
                                    RequestType.OI.getRequestBody(channel, userId)
                            ),
                            proxyHost,
                            proxyPort
                    )
            );
        } catch (Exception ignored) {
            return "{\"r\":12202}";
        }
    }

    public static void refresh(int channel, int userId) {
        while (true) {
            Result previous = Account.get(userId);
            try {
                Result latest = uisk(channel, userId).get();
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
        System.out.println("\033[33m" + "请输入拓维UserID，并按回车键继续：" + "\033[0m");
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
                    System.out.println("\033[31m" + "请输入正确的UserID" + "\033[0m");
                }
            } else {
                System.out.println("\033[31m" + "输入无效，请输入正确的UserID" + "\033[0m");
            }
        }
        return userId;
    }

    /**
     * @return userIds
     * @描述: userId批量获取方法
     */
    public static List<Integer> readUserIds() {
        if (Inter.chooser == 4) {
            System.out.println("\033[33m" + "正在等待代理池刷新……" + "\033[0m");
            sleep(Inter.waiter);
        }
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
                        UserJsonUtils.JsonUtil(userId,"activate",true);
                        userIds.add(userId);
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
        return userIds;
    }

    /**
     * @描述: 自动加解密控制台导引方法
     */
    public static void cryptoGuideLine() {
        try {
            System.out.println("\033[33m" + "请输入任意内容或数据包，输入空字段以结束：" + "\033[0m");
            String body = smfScanner.smfLongString(true);
            if (body.isBlank()) System.exit(0);
            if (JSON.isValidObject(body)) {
                JSONObject jsonObject = JSONObject.parse(body);
                if (jsonObject.containsKey("i") && jsonObject.containsKey("r")) {
                    if (jsonObject.containsKey("t")) {
                        System.out.println(HttpCrypto.encryptREQ(body));
                        System.out.println("\033[33m" + "是否发送数据包取得响应？(Y/N)" + "\033[0m");
                        if (smfScanner.smfBoolean(false))
                            System.out.println(HttpCrypto.decryptRES(HttpSender.doQuest(Inter.isAndroid, HttpCrypto.encryptREQ(body))));
                    }
                    if (jsonObject.containsKey("e")) {
                        System.out.println(HttpCrypto.decryptRES(body));
                    }
                    if (jsonObject.containsKey("d")) {
                        System.out.println(HttpCrypto.encryptRES(body));
                    }
                }
            } else if (body.startsWith("--_{{}}_")) {
                System.out.println(HttpCrypto.decryptREQ(body));
                System.out.println("\033[33m" + "是否发送数据包取得响应？(Y/N)" + "\033[0m");
                if (smfScanner.smfBoolean(false))
                    System.out.println(HttpCrypto.decryptRES(HttpSender.doQuest(Inter.isAndroid, body)));
            } else {
                System.out.println("\033[31m" + "自动检测失败，请手动输入数据包标识并选择功能：\n" + "\033[0m" + "\033[33m" + "请输入数据包标识：" + "\033[0m");
                String identifier = smfScanner.smfString(true);
                System.out.println("\033[32m" + "请选择功能：\n[1] 请求加密\n[2] 请求解密\n[3] 响应加密\n[4] 响应解密\n[5] 获取密钥和偏移\n[6] 获取MD5\n" + "\033[0m");
                boolean keepRunning = true;
                while (keepRunning) {
                    int choice = smfScanner.smfInt(false);
                    switch (choice) {
                        case 1:
                            System.out.println(HttpCrypto.encryptREQ(identifier, body));
                            keepRunning = false;
                            break;
                        case 2:
                            System.out.println(HttpCrypto.decryptREQ(identifier, body));
                            keepRunning = false;
                            break;
                        case 3:
                            System.out.println(HttpCrypto.encryptRES(identifier, body));
                            keepRunning = false;
                            break;
                        case 4:
                            System.out.println(HttpCrypto.decryptRES(identifier, body));
                            keepRunning = false;
                            break;
                        case 5:
                            System.out.println("Identifier:" + identifier +
                                    "\nkey:" + new String(HttpCrypto.getKey(identifier), StandardCharsets.UTF_8) +
                                    "\niv:" + new String(HttpCrypto.getIv(identifier), StandardCharsets.UTF_8));
                            keepRunning = false;
                            break;
                        case 6:
                            System.out.println("MD5:" + new String(HttpCrypto.getMD5(body), StandardCharsets.UTF_8));
                            keepRunning = false;
                            break;
                        default:
                            System.out.println("\033[31m" + "输入有误，请重新输入功能序号：" + "\033[0m");
                    }
                }
            }
            cryptoGuideLine();
        } catch (Exception e) {
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
            System.out.println("\033[31m" + "sleep function error:" + "\033[0m");
            e.printStackTrace();
        }
    }
}
