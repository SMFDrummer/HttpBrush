package smf.icdada.HttpUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import smf.icdada.*;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static smf.icdada.HttpUtils.base.*;

/**
 * @author SMF & icdada
 * @描述: HttpUtils核心方法类
 * <p>
 * 包含配置文件指引器、单一实现方法、批量实现方法、响应获取以及配置文件创建方法。
 * </p>
 */
public class strategy {

    protected static String pathBinder = System.getProperty("user.dir") + File.separator + "bannerConfig.json";

    /**
     * @param useBind 配置绑定判定，目前仅支持bannerConfig.json
     * @return 配置文件路径
     * @描述: 配置文件指引器
     */
    private static String cfgIn(boolean useBind) {
        if (useBind) {
            if (!Files.exists(Paths.get(pathBinder))) {
                System.out.println("未找到绑定配置文件！");
                pathBinder = cfgIn(false);
            }
            return pathBinder;
        } else {
            System.out.println("请输入配置文件完整路径：");
            while (true) {
                String cfgString = smfScanner.smfString(false);
                try {
                    Path cfgPath = Paths.get(cfgString);
                    if (Files.exists(cfgPath)) {
                        return cfgString;
                    } else {
                        System.out.println("配置文件不存在或输入有误，请重新输入");
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * @param userId 用户Id
     * @描述: 单一实现方法
     */
    public static void apply(int userId) {
        try {
            JSONObject parse = JSONObject.parse(Files.readString(Paths.get(cfgIn(false))));
            if (parse.containsKey("configuration") && "HttpUtilSenderProps".equals(parse.get("configuration"))) {
                if (parse.containsKey("openConsole")) ProxyManager.console(parse.getBoolean("openConsole"));
                refresh(Inter.oi, userId);
                Result uisk = getUisk(userId);
                Result proxy = getProxy(userId);
                if (parse.containsKey("sendPackage") && !"banned".equals(uisk.getUi()) && !"banned".equals(uisk.getSk())) {
                    JSONArray sendPackage = parse.getJSONArray("sendPackage");
                    ExecutorService service = Executors.newFixedThreadPool(Math.min(sendPackage.size(), 20));
                    JSONObject[] sendPackages = new JSONObject[sendPackage.size()];
                    for (int i = 0; i < sendPackage.size(); i++) {
                        sendPackages[i] = sendPackage.getJSONObject(i);
                    }
                    Arrays.sort(sendPackages, (o1, o2) -> {
                        int packageOrder1 = o1.getInteger("packageOrder");
                        int packageOrder2 = o2.getInteger("packageOrder");
                        return Integer.compare(packageOrder1, packageOrder2);
                    });
                    for (JSONObject aPackage : sendPackages) {
                        String packageIdentifier = aPackage.getString("packageIdentifier");
                        String packageBody = aPackage.getString("packageBody");
                        int sendDelay = aPackage.getIntValue("sendDelay");
                        boolean checkSuccess = aPackage.getBoolean("checkSuccess");
                        int checkPoint = aPackage.getJSONObject("overrideCheckPoint").getBoolean("apply") ?
                                aPackage.getJSONObject("overrideCheckPoint").getIntValue("r") : 0;
                        while (true) {
                            String proxyHost = proxy.getProxyHost();
                            int proxyPort = proxy.getProxyPort();
                            String ui = uisk.getUi();
                            String sk = uisk.getSk();
                            Future<String> future = service.submit(() -> getRes(packageIdentifier, packageBody, ui, sk, proxyHost, proxyPort));
                            sleep(sendDelay);
                            boolean isTimeOut = false;
                            boolean isError = false;
                            try {
                                String response = future.get(10, TimeUnit.SECONDS);
                                if (checkSuccess) {
                                    if (JSONObject.parse(response).getIntValue("r") != checkPoint) {
                                        if (JSONObject.parse(response).getIntValue("r") != 0) {
                                            if (JSONObject.parse(response).getIntValue("r") == 20013) isError = true;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                isTimeOut = true;
                            }
                            if (isError || isTimeOut) {
                                refresh(Inter.oi, userId);
                                uisk = getUisk(userId);
                                proxy = getProxy(userId);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("程序执行结束，即将退出");
        System.exit(0);

    }

    /**
     * @param userId  用户Id
     * @param useBind 配置绑定判定
     * @return 是否成功完成指定任务
     * @描述: 批量实现方法
     */
    public static boolean apply(int userId, boolean useBind) {
        try {
            JSONObject parse = JSONObject.parse(Files.readString(Paths.get(cfgIn(useBind))));
            if (parse.containsKey("configuration") && "HttpUtilSenderProps".equals(parse.get("configuration"))) {
                if (parse.containsKey("openConsole")) ProxyManager.console(parse.getBoolean("openConsole"));
                Result uisk = getUisk(userId);
                Result proxy = getProxy(userId);
                if (parse.containsKey("sendPackage") && !"banned".equals(uisk.getUi()) && !"banned".equals(uisk.getSk())) {
                    JSONArray sendPackage = parse.getJSONArray("sendPackage");
                    ExecutorService service = Executors.newFixedThreadPool(Math.min(sendPackage.size(), 20));
                    JSONObject[] sendPackages = new JSONObject[sendPackage.size()];
                    for (int i = 0; i < sendPackage.size(); i++) {
                        sendPackages[i] = sendPackage.getJSONObject(i);
                    }
                    Arrays.sort(sendPackages, (o1, o2) -> {
                        int packageOrder1 = o1.getInteger("packageOrder");
                        int packageOrder2 = o2.getInteger("packageOrder");
                        return Integer.compare(packageOrder1, packageOrder2);
                    });
                    int index = 0;
                    for (JSONObject aPackage : sendPackages) {
                        index++;
                        String packageIdentifier = aPackage.getString("packageIdentifier");
                        String packageBody = aPackage.getString("packageBody");
                        int sendDelay = aPackage.getIntValue("sendDelay");
                        boolean checkSuccess = aPackage.getBoolean("checkSuccess");
                        int checkPoint = aPackage.getJSONObject("overrideCheckPoint").getBoolean("apply") ?
                                aPackage.getJSONObject("overrideCheckPoint").getIntValue("r") : 0;
                        while (true) {
                            boolean isTimeOut = false;
                            boolean isError = false;
                            String proxyHost = proxy.getProxyHost();
                            int proxyPort = proxy.getProxyPort();
                            if (uisk != null) {
                                String ui = uisk.getUi();
                                String sk = uisk.getSk();
                                Future<String> future = service.submit(() -> getRes(packageIdentifier, packageBody, ui, sk, proxyHost, proxyPort));
                                sleep(sendDelay);
                                try {
                                    String response = future.get(10, TimeUnit.SECONDS);
                                    if (checkSuccess) {
                                        if (JSONObject.parse(response).getIntValue("r") != checkPoint) {
                                            if (JSONObject.parse(response).getIntValue("r") != 0) {
                                                System.out.println("\033[33m" + "账号：" + userId + "\033[0m" + " || " + "\033[31m" + "发送数据包失败，正在重试……" + "\033[0m" + " || " + response);
                                                if (JSONObject.parse(response).getIntValue("r") == 20013)
                                                    isError = true;
                                            }
                                        } else if (index == sendPackage.size()) {
                                            return true;
                                        }
                                    }
                                } catch (Exception e) {
                                    isTimeOut = true;
                                }
                            } else isError = true;
                            if (isError || isTimeOut) {
                                refresh(Inter.oi, userId);
                                uisk = getUisk(userId);
                                proxy = getProxy(userId);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!useBind) {
            System.out.println("程序执行结束，即将退出");
            System.exit(0);
        }
        return false;
    }

    /**
     * @param packageIdentifier 数据包标识
     * @param packageBody       数据包包体
     * @param ui                关键值ui
     * @param sk                关键值sk
     * @param proxyHost         代理服务器域名
     * @param proxyPort         代理服务器端口
     * @return 明文响应
     * @描述: 明文请求获取响应
     */
    public static String getRes(String packageIdentifier, String packageBody, String ui, String sk, String proxyHost, int proxyPort) {
        JSONObject parse = JSONObject.parse(packageBody);
        if (parse.containsKey("t")) {
            JSONObject t = parse.getJSONObject("t");
            if (t.containsKey("pi")) t.put("pi", ui);
            if (t.containsKey("ui")) t.put("ui", ui);
            if (t.containsKey("sk")) t.put("sk", sk);
            parse.put("t", t);
        } else {
            if (parse.containsKey("pi")) parse.put("pi", ui);
            if (parse.containsKey("ui")) parse.put("ui", ui);
            if (parse.containsKey("sk")) parse.put("sk", sk);
        }
        packageBody = parse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
        try {
            if (parse.containsKey("i")) {
                return HttpCrypto.decryptRES(
                        HttpSender.doQuest(
                                Inter.isAndroid,
                                HttpCrypto.encryptREQ(packageBody),
                                proxyHost,
                                proxyPort
                        )
                );
            } else if (!packageIdentifier.isBlank()) {
                return HttpCrypto.decryptRES(
                        packageIdentifier,
                        HttpSender.doQuest(
                                Inter.isAndroid,
                                HttpCrypto.encryptREQ(packageIdentifier, packageBody),
                                proxyHost,
                                proxyPort
                        )
                );
            } else {
                return "{\"r\":12202}";
            }
        } catch (Exception ignored) {
            return "{\"r\":12202}";
        }
    }

    /**
     * @描述: 配置文件生成器
     */
    public static void maker() {
        System.out.println("欢迎使用配置生成器，请根据指引输入指定内容，并按回车继续：");
        JSONObject parse = JSONObject.parse("{}");
        JSONObject fileInfo = new JSONObject();
        JSONArray author = new JSONArray("SMF", "icdada");
        fileInfo.put("Author", author);
        JSONArray sendPackage = new JSONArray();
        int i = 1;
        do {
            JSONObject aPackage = new JSONObject();
            JSONObject overrideCheckPoint = new JSONObject();
            overrideCheckPoint.put("apply", false);
            overrideCheckPoint.put("r", 0);
            aPackage.put("packageOrder", i++);
            System.out.println("请输入数据包标识，可不填写，但数据包必须包含完整标识：");
            String packageIdentifier = smfScanner.smfString(false);
            aPackage.put("packageIdentifier", packageIdentifier);
            System.out.println("请输入数据包：");
            String packageBody = smfScanner.smfLongString(true);
            aPackage.put("packageBody", packageBody);
            System.out.println("请输入发包延迟（ms）：");
            int sendDelay = smfScanner.smfInt(false);
            aPackage.put("sendDelay", sendDelay);
            System.out.println("是否需要检测改包是否发送成功？（Y/N）：");
            boolean checkSuccess = smfScanner.smfBoolean(false);
            aPackage.put("checkSuccess", checkSuccess);
            System.out.println("是否需要变更发包成功检测的r的值？（Y/N）：");
            boolean apply = smfScanner.smfBoolean(false);
            overrideCheckPoint.put("apply", apply);
            if (apply) {
                System.out.println("请输入发包成功检测的r的值：");
                int r = smfScanner.smfInt(false);
                overrideCheckPoint.put("r", r);
            }
            aPackage.put("overrideCheckPoint", overrideCheckPoint);
            sendPackage.add(aPackage);
            System.out.println("是否继续添加？（Y/N）");
        } while (!smfScanner.smfBoolean(false));
        parse.put("configuration", "HttpUtilSenderProps");
        parse.put("fileInfo", fileInfo);
        System.out.println("是否开启控制台显示？（Y/N）");
        boolean openConsole = smfScanner.smfBoolean(false);
        parse.put("openConsole", openConsole);
        parse.put("sendPackage", sendPackage);
        String newStrategy = System.getProperty("user.dir") + File.separator + "strategyConfig_" + DateTimeFormatter.ofPattern("MM-dd-HH-mm").format(LocalDateTime.now()) + ".json";
        File strategy = new File(newStrategy);
        try (FileWriter fileWriter = new FileWriter(strategy)) {
            fileWriter.write(parse.toJSONString(JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat));
            fileWriter.flush();
        } catch (Exception ignored) {
        }
        System.out.println("配置文件已生成！文件名为：" + strategy.getName());
        System.out.println("生成路径位于：" + strategy.getPath());
        System.exit(0);
    }
}
