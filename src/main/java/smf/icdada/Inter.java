package smf.icdada;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * @author SMF & icdada
 * @描述: 默认配置方法类
 * <p>
 * 包含所有常量的写入、监听、纠错、初始化。
 * </p>
 */
public class Inter {
    public static int inter;
    public static int proxyType;
    public static String version;
    public static boolean environment;
    public static int appId;
    public static int channelId;
    public static String packageValue;
    protected static String defaultUrl = System.getProperty("user.dir") + File.separator + "default.json";

    public static void PreCheck() {
        try {
            String tempFilePath = System.getProperty("user.dir") + File.separator + "temp";
            File tempDir = new File(tempFilePath);
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            Path defaultJsonPath = Paths.get(defaultUrl);
            if (Files.exists(defaultJsonPath)) {
                JSONObject jsonObject = JSONObject.parse(Files.readString(defaultJsonPath));
                if (
                        !jsonObject.containsKey("PropInfo") ||
                                !jsonObject.getString("PropInfo").equals("HttpBrushPropertySheet") ||
                                !jsonObject.containsKey("AuthorInfo") ||
                                !jsonObject.getJSONObject("AuthorInfo").containsKey("Developer") ||
                                !jsonObject.getJSONObject("AuthorInfo").getString("Developer").equals("SMF")
                ) {
                    Log.e("配置文件异常，程序结束");
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
    }

    public static String getTestBy() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            JSONObject jsonObject = JSONObject.parse(Files.readString(Paths.get(defaultUrl)));
            JSONArray jsonArray = jsonObject.getJSONObject("AuthorInfo").getJSONArray("TestBy");
            for (int i = 0; i < jsonArray.size(); i++) {
                String s = jsonArray.getString(i);
                stringBuilder.append(s);
                if (i < jsonArray.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void Setting() {
        setGlobalSetting();
        setOtherSetting();
    }

    private static void setGlobalSetting() {
        inter = getGlobalSetting("Inter") == null ? inter() : (int) getGlobalSetting("Inter");
        proxyType = getGlobalSetting("ProxyType") == null ? proxyType() : (int) getGlobalSetting("ProxyType");
        version = getGlobalSetting("Version") == null ? version() : (String) getGlobalSetting("Version");
        environment = getGlobalSetting("Environment") == null ? environment() : (boolean) getGlobalSetting("Environment");
    }

    private static void setOtherSetting() {
        try {
            appId = getOtherSetting("PackageId").getIntValue("appId");
            channelId = getOtherSetting("PackageId").get("channelId") == null ? channelId() : getOtherSetting("PackageId").getIntValue("channelId");
            packageValue = packageValue();
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
    }

    private static Object getGlobalSetting(String key) {
        try {
            JSONObject jsonObject = JSONObject.parse(Files.readString(Paths.get(defaultUrl)));
            JSONObject GlobalSettings = jsonObject.getJSONObject("PropData").getJSONObject("GlobalSettings");
            return GlobalSettings.getJSONObject(key).get("value");
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
        return new Object();
    }

    private static JSONObject getOtherSetting(String key) {
        try {
            JSONObject jsonObject = JSONObject.parse(Files.readString(Paths.get(defaultUrl)));
            JSONObject OtherSetting = jsonObject.getJSONObject("PropData").getJSONObject("OtherSettings");
            return OtherSetting.getJSONObject(key);
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private static int inter() {
        int inter = 0;
        boolean keepRunning = true;
        Log.v("""
                功能列表：
                [1] 自适应网络数据包加解密
                [2] 接收拓维UserID区间的banuser.json生成
                [3] 根据banuser.json用户库自动封号
                [4] user.json用户库状态刷新
                [5] user.json用户库拆解
                [6] 发送数据包配置生成器
                [7] 自定义发送数据包组
                [8] 周年庆邀请活动单一刷取
                [9] 周年庆邀请活动批量刷取(user.json)
                [10] user.json用户库保护
                [0] 退出程序""");
        Log.v("请输入序号并按回车键继续……：");
        while (keepRunning) {
            try {
                Scanner scanner = new Scanner(System.in);
                inter = scanner.nextInt();
                switch (inter) {
                    case 1 -> {
                        Log.s("自适应网络数据包加解密");
                        keepRunning = false;
                    }
                    case 2 -> {
                        Log.s("接收拓维UserID区间的banuser.json生成");
                        keepRunning = false;
                    }
                    case 3 -> {
                        Log.s("根据banuser.json用户库自动封号");
                        keepRunning = false;
                    }
                    case 4 -> {
                        Log.s("user.json用户库状态刷新");
                        keepRunning = false;
                    }
                    case 5 -> {
                        Log.s("user.json用户库拆解");
                        keepRunning = false;
                    }
                    case 6 -> {
                        Log.s("发送数据包配置生成器");
                        keepRunning = false;
                    }
                    case 7 -> {
                        Log.s("自定义发送数据包组");
                        keepRunning = false;
                    }
                    case 8 -> {
                        Log.s("周年庆邀请活动单一刷取");
                        keepRunning = false;
                    }
                    case 9 -> {
                        Log.s("周年庆邀请活动批量刷取(user.json)");
                        keepRunning = false;
                    }
                    case 10 -> {
                        Log.s("user.json用户库保护");
                        keepRunning = false;
                    }
                    case 0 -> System.exit(0);
                    default -> Log.e("输入无效，请重新输入功能序号：");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inter;
    }

    private static int proxyType() {
        Log.v("""
                请输入默认执行代理池类型:
                [1] 本地代理池(需要提前开启HttpBrushProxyPool)
                [2] 在线代理池""");
        Log.v("请输入序号并按回车键继续……：");
        return smfScanner.smfInt(false, "^[1|2]+$");
    }

    private static String version() {
        Log.v("请输入版本号，格式为${VersionName}.${VersionCode}，如1.2.3.1234");
        return smfScanner.smfString(false, "^\\d{1}\\.\\d{1}\\.\\d{1}\\.\\d{4}$");
    }

    private static boolean environment() {
        return true;
    }

    private static int channelId() {
        int channelId = 208;
        try {
            JSONObject parse = JSONObject.parse(Files.readString(Paths.get(defaultUrl)));
            JSONArray packages = parse.getJSONObject("PropData").getJSONObject("OtherSettings").getJSONObject("PackageId").getJSONArray("package");
            StringBuilder stringBuilder = new StringBuilder("^[");
            Log.v("请输入渠道，以下是目前所有可用的渠道：");
            for (int i = 0; i < packages.size(); i++) {
                JSONObject jsonObject = packages.getJSONObject(i);
                String description = jsonObject.getString("Description");
                int id = jsonObject.getIntValue("id");
                String value = jsonObject.getString("value");
                stringBuilder.append(id);
                if (i != packages.size() - 1) {
                    stringBuilder.append("|");
                }
                Log.i(String.format("渠道：%s - Id：%s - 包名：%s", description, id, value));
            }
            stringBuilder.append("]+$");
            Log.v("请输入渠道Id，并按回车键继续……：");
            channelId = smfScanner.smfInt(false, stringBuilder.toString());
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
        return channelId;
    }

    private static String packageValue() {
        try {
            JSONObject parse = JSONObject.parse(Files.readString(Paths.get(defaultUrl)));
            JSONArray packages = parse.getJSONObject("PropData").getJSONObject("OtherSettings").getJSONObject("PackageId").getJSONArray("package");
            for (Object object : packages) {
                JSONObject jsonObject = (JSONObject) object;
                if (Integer.parseInt(String.valueOf(appId) + channelId) == jsonObject.getIntValue("id")) {
                    return jsonObject.getString("value");
                }
            }
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
        return null;
    }
}
