package smf.icdada.HttpUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import smf.icdada.*;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static smf.icdada.ProxyManager.proxy;
import static smf.icdada.RequestType.V202;

/**
 * @author SMF & icdada
 * @描述: HttpUtils基类
 * <p>
 * 包含处理Http请求的基础函数。
 * </p>
 */
public class Base {
    /**
     * @描述: AccountHashMap储存的是账号uisk的配对信息，随着刷新方法初始化和更新
     */
    private static final ConcurrentHashMap<String, Result> Account = new ConcurrentHashMap<>();
    /**
     * @描述: ProxyHashMap储存的是代理的配对信息，随着刷新方法初始化和更新
     */
    private static final ConcurrentHashMap<String, Result> Proxy = new ConcurrentHashMap<>();
    /**
     * @描述: UsersHashMap储存的是该账号在账号库中的索引位置，使用initUsersMap方法更新
     */
    private static final ConcurrentHashMap<String, Integer> Users = new ConcurrentHashMap<>();
    /**
     * @描述: VirtualThreadPoolHashMap储存的是账号对应的虚拟线程池，得益于优秀的JDK21新功能，程序的核心
     */
    private static final ConcurrentHashMap<String, ExecutorService> Vtp = new ConcurrentHashMap<>();

    /**
     * @param userId 拓维userId
     * @return 异步刷新的账号uisk信息
     * @描述: uisk重要数值获取与刷新
     */
    private static CompletableFuture<Result> uisk(String userId) {
        Result uisk = null;
        if (Inter.proxyType != 0){
            Proxy.put(userId, proxy());
        }
        Check.V202 v202 = new Check.V202();
        do {
            try {
                Future<String> future = getExecutor(userId).submit(() -> getResponseBody(V202, userId));
                try {
                    v202.setResponseBody(future.get(10, TimeUnit.SECONDS));
                    if (v202.isValid(20507)) {
                        Log.e("账号:" + userId + " || " + "账号被封禁，已自动跳出");
                        uisk = new Result("banned", "banned");
                    } else if (v202.isValid(0)) {
                        uisk = v202.getUisk();
                        if (Inter.openConsole) {
                            Log.c(
                                    Log.p("userId:" + userId, Log.Color.BLUE),
                                    Log.Separator,
                                    Log.p("ui:" + uisk.getUi(), Log.Color.BLUE),
                                    Log.Separator,
                                    Log.p("sk:" + uisk.getSk(), Log.Color.BLUE)
                            );
                        }
                    }
                } catch (Exception ignored) {
                    if (Inter.proxyType != 0) {
                        Proxy.put(userId, proxy());
                    }
                }
            } catch (Exception e) {
                if (Inter.openConsole) {
                    Log.w(e.getMessage());
                    e.printStackTrace();
                }
            }
        } while (uisk == null);
        Result finalUisk = uisk;
        return CompletableFuture.supplyAsync(() -> finalUisk, getExecutor(userId));
    }

    public static String getResponseBody(RequestType index, String userId, Object... param) {
        String responseBody;
        try {
            if (Inter.openConsole) Log.d("[SEND] " + index.getRequestBody(index, userId, param));
            if (Inter.proxyType == 0){
                responseBody = HttpCrypto.decryptRES(
                        HttpSender.doQuest(
                                Inter.environment,
                                HttpCrypto.encryptREQ(
                                        index.getRequestBody(index, userId, param)
                                )
                        )
                );
            } else {
                Result proxy = getProxy(userId);
                responseBody = HttpCrypto.decryptRES(
                        HttpSender.doQuest(
                                Inter.environment,
                                HttpCrypto.encryptREQ(
                                        index.getRequestBody(index, userId, param)
                                ),
                                proxy.getProxyHost(),
                                proxy.getProxyPort()
                        )
                );
            }
        } catch (Exception e) {
            if (Inter.openConsole) {
                Log.w(e.getMessage());
                e.printStackTrace();
            }
            responseBody = "{\"r\":12202}";
            if (Inter.proxyType != 0) {
                Proxy.put(userId, proxy());
            }
        }
        if (Inter.openConsole) Log.w("[RECV] " + responseBody);
        int r = JSON.parseObject(responseBody).getIntValue("r");
        if (r == 20013 || r == 20020){
            refresh(userId);
        } else if (r == 12202) {
            sleep(3000);
        }
        return responseBody;
    }

    public static String getResponseBody(String requestBody, String userId, boolean replaceUisk) {
        String responseBody;
        try {
            JSONObject parse = JSON.parseObject(requestBody);
            JSONObject t = parse.getJSONObject("t");
            t.put("ver_",Inter.iosVersion);
            if (replaceUisk) {
                Result uisk = getUisk(userId);
                if (t.containsKey("pi") && t.containsKey("ui") && t.containsKey("sk")) {
                    t.put("pi", uisk.getUi());
                    t.put("sk", uisk.getSk());
                    t.put("ui", uisk.getUi());
                }
                if (Inter.openConsole) Log.d("[SEND] " + parse.toJSONString(JSONWriter.Feature.WriteMapNullValue));
                if (Inter.proxyType == 0){
                    responseBody = HttpCrypto.decryptRES(
                            HttpSender.doQuest(
                                    Inter.environment,
                                    HttpCrypto.encryptREQ(
                                            parse.toJSONString(JSONWriter.Feature.WriteMapNullValue)
                                    )
                            )
                    );
                } else {
                    Result proxy = getProxy(userId);
                    responseBody = HttpCrypto.decryptRES(
                            HttpSender.doQuest(
                                    Inter.environment,
                                    HttpCrypto.encryptREQ(
                                            parse.toJSONString(JSONWriter.Feature.WriteMapNullValue)
                                    ),
                                    proxy.getProxyHost(),
                                    proxy.getProxyPort()
                            )
                    );
                }
            } else {
                if (Inter.openConsole) Log.d("[SEND] " + parse.toJSONString(JSONWriter.Feature.WriteMapNullValue));
                responseBody = HttpCrypto.decryptRES(
                        HttpSender.doQuest(
                                Inter.environment,
                                HttpCrypto.encryptREQ(
                                        parse.toJSONString(JSONWriter.Feature.WriteMapNullValue)
                                )
                        )
                );
            }
        } catch (Exception e) {
            if (Inter.openConsole) {
                Log.w(e.getMessage());
                e.printStackTrace();
            }
            responseBody = "{\"r\":12202}";
            if (replaceUisk && Inter.proxyType != 0) {
                Proxy.put(userId,proxy());
            }
        }
        if (Inter.openConsole) Log.w("[RECV] " + responseBody);
        int r = JSON.parseObject(responseBody).getIntValue("r");
        if (r == 20013 || r == 20020){
            refresh(userId);
        } else if (r == 12202) {
            sleep(3000);
        }
        return responseBody;
    }

    public static void refresh(String userId) {
        while (true) {
            Result previous = Account.get(userId);
            try {
                Result latest = uisk(userId).get();
                if (!latest.equals(previous)) {
                    Account.put(userId, latest);
                    break;
                }
            } catch (Exception e) {
                if (Inter.openConsole) {
                    Log.w(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static ExecutorService getExecutor(String userId) {
        return Vtp.computeIfAbsent(userId, key -> Executors.newVirtualThreadPerTaskExecutor());
    }

    public static Result getUisk(String userId) {
        return Account.get(userId);
    }

    public static Result getProxy(String userId) {
        return Proxy.get(userId);
    }

    public static List<String> getUserList() {
        List<String> userList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : Users.entrySet()) {
            userList.add(entry.getKey());
        }
        return userList;
    }

    public static int getIndex(String userId) {
        return Users.get(userId);
    }

    private static String removeQuotes(String str) {
        if (str != null && str.length() >= 2 && str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    public static String getFilePath() {
        Log.v("请输入完整的文件路径");
        while (true) {
            String filePath = smfScanner.String(false);
            filePath = removeQuotes(filePath);
            if (Files.exists(Paths.get(filePath))) {
                return filePath;
            } else Log.e("文件不存在，请检查路径并重新输入");
        }
    }

    public static String getFilePath(String filePath) {
        filePath = removeQuotes(filePath);
        if (Files.exists(Paths.get(filePath))) {
            return filePath;
        } else {
            Log.e(filePath + "路径文件不存在，传参出现严重错误，程序退出");
            System.exit(0);
            return null;
        }
    }

    /**
     * @描述: UsersMap初始化方法
     */
    public static void initUsersMap() {
        try {
            String stringUrl = System.getProperty("user.dir") + File.separator + "user.json";
            Path userPath = Paths.get(stringUrl);
            if (Files.exists(userPath)) {
                JSONObject userData = JSON.parseObject(Files.readString(userPath));
                JSONArray usersArray = userData.getJSONArray("Users");
                for (int i = 0; i < usersArray.size(); i++) {
                    JSONObject userObject = usersArray.getJSONObject(i);
                    if (userObject.containsKey("activate")) {
                        if (userObject.getBooleanValue("activate")) {
                            String userId = userObject.getString("userId");
                            Users.put(userId, i);
                        }
                    } else {
                        String userId = userObject.getString("userId");
                        userObject.put("activate", true);
                        Users.put(userId, i);
                    }
                }
                try (FileWriter fileWriter = new FileWriter(stringUrl)) {
                    fileWriter.write(userData.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue));
                    fileWriter.flush();
                }
            } else {
                Log.e("用户库文件异常，请检查:" + System.getProperty("user.dir") + File.separator + "user.json文件是否存在");
                System.exit(0);
            }
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @描述: 自动加解密控制台导引方法
     */
    public static void cryptoGuideLine() {
        try {
            Log.v("请输入任意内容或数据包，多次按下回车继续，输入空字段以结束:");
            String body = smfScanner.LongString(true);
            if (body.isBlank()) System.exit(0);
            if (JSON.isValidObject(body)) {
                JSONObject jsonObject = JSONObject.parse(body);
                if (jsonObject.containsKey("i") && jsonObject.containsKey("r")) {
                    if (jsonObject.containsKey("t")) {
                        Log.a(HttpCrypto.encryptREQ(body));
                        Log.v("是否发送数据包取得响应？(Y/N)");
                        if (smfScanner.Boolean(false))
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
                if (smfScanner.Boolean(false))
                    Log.a(HttpCrypto.decryptRES(HttpSender.doQuest(Inter.environment, body)));
            } else {
                Log.e("自动检测失败");
                Log.d("请选择功能:\n[1] 请求加密\n[2] 请求解密\n[3] 响应加密\n[4] 响应解密\n[5] 获取密钥和偏移\n[6] 获取MD5\n[7] 中文版数字加密\n[8] 中文版数字解密");
                boolean keepRunning = true;
                while (keepRunning) {
                    String identifier = null;
                    int choice = smfScanner.Int(false);
                    if (choice != 6 && choice != 7 && choice != 8) {
                        Log.v("请输入数据包标识:");
                        identifier = smfScanner.String(true);
                    }
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
                        case 7 -> {
                            int i = extractInteger(body);
                            if (i != Integer.MAX_VALUE) {
                                Log.s("加密后的数字为:" + NumberCrypto.encrypt(i));
                            } else {
                                Log.e("你看看你输入了点什么东西");
                            }
                            keepRunning = false;
                        }
                        case 8 -> {
                            int i = extractInteger(body);
                            if (i != Integer.MAX_VALUE) {
                                Log.s("解密后的数字为:" + NumberCrypto.decrypt(i));
                            } else {
                                Log.e("你看看你输入了点什么东西");
                            }
                            keepRunning = false;
                        }
                        default -> Log.e("输入有误，请重新输入功能序号:");
                    }
                }
            }
            cryptoGuideLine();
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }

    private static int extractInteger(String str) {
        StringBuilder integerPart = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                integerPart.append(c);
            }
        }
        if (!integerPart.isEmpty()) {
            return Integer.parseInt(integerPart.toString());
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * @描述: 自定义sleep函数，防止编译器谬误
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.w("sleep function error:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
