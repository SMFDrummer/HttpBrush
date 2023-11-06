package smf.icdada;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static smf.icdada.Inter.SettingType.GlobalSettings;
import static smf.icdada.Inter.SettingType.OtherSettings;


/**
 * @author SMF & icdada
 * @描述: 默认配置方法类
 * <p>
 * 包含所有常量的写入、监听、纠错、初始化。
 * </p>
 */
public class Inter {
    private static final String defaultUrl = System.getProperty("user.dir") + File.separator + "default.json";
    public static boolean openConsole;
    public static int inter;
    public static int proxyType;
    public static String androidVersion;
    public static String iosVersion;
    public static boolean environment;
    public static int appId;
    public static int channelId;
    public static String packageValue;
    public static String oi;
    private static JSONObject defaultParse = new JSONObject();

    public static void PreCheck() {
        try {
            String tempFilePath = System.getProperty("user.dir") + File.separator + "temp";
            File tempDir = new File(tempFilePath);
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            Path defaultJsonPath = Paths.get(defaultUrl);
            if (Files.exists(defaultJsonPath)) {
                defaultParse = JSONObject.parse(Files.readString(defaultJsonPath));
                if (
                        !defaultParse.containsKey("PropInfo") ||
                                !defaultParse.getString("PropInfo").equals("HttpBrushPropertySheet") ||
                                !defaultParse.containsKey("AuthorInfo") ||
                                !defaultParse.getJSONObject("AuthorInfo").containsKey("Developer") ||
                                !defaultParse.getJSONObject("AuthorInfo").getString("Developer").equals("SMF")
                ) {
                    Log.e("配置文件异常，程序结束");
                    throw new Exception();
                }
            } else {
                Log.w("配置文件缺失，请检查默认配置是否存在，程序退出");
                System.exit(0);
            }
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
    }

    public static String getTestBy() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            JSONArray jsonArray = (JSONArray) JSONPath.eval(defaultParse, "$.AuthorInfo.TestBy");
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
        openConsole = getSetting(GlobalSettings, ".OpenConsole.value") == null ? openConsole() : (boolean) getSetting(GlobalSettings, ".OpenConsole.value");
        inter = getSetting(GlobalSettings, ".Inter.value") == null ? inter() : (int) getSetting(GlobalSettings, ".Inter.value");
        proxyType = getSetting(GlobalSettings, ".ProxyType.value") == null ? proxyType() : (int) getSetting(GlobalSettings, ".ProxyType.value");
        androidVersion = getSetting(GlobalSettings, ".AndroidVersion.value") == null ? androidVersion() : getSetting(GlobalSettings, ".AndroidVersion.value").toString();
        iosVersion = getSetting(GlobalSettings, ".IOSVersion.value") == null ? iosVersion() : getSetting(GlobalSettings, ".IOSVersion.value").toString();
        environment = getSetting(GlobalSettings, ".Environment.value") == null ? environment() : (boolean) getSetting(GlobalSettings, ".Environment.value");
    }

    private static void setOtherSetting() {
        try {
            JSONArray packages = (JSONArray) getSetting(OtherSettings, ".PackageId.package");
            appId = (int) getSetting(OtherSettings, ".PackageId.appId");
            channelId = getSetting(OtherSettings, ".PackageId.channelId") == null ? channelId(packages) : (int) getSetting(OtherSettings, ".PackageId.channelId");
            packageValue = packageValue(packages);
            oi = String.valueOf(Inter.appId) + Inter.channelId;
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
    }

    private static Object getSetting(SettingType type, String keyPathElement) {
        try {
            return JSONPath.eval(defaultParse, "$.PropData." + type + keyPathElement);
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
            return new Object();
        }
    }

    private static boolean openConsole() {
        return false;
    }

    private static int inter() {
        int inter = 0;
        boolean keepRunning = true;
        Log.v("""
                功能列表:
                [1] 自适应网络数据包加解密
                [2] 接收拓维UserID区间的banuser.json生成
                [3] 根据banuser.json用户库自动封号
                [4] user.json用户库状态刷新
                [5] user.json用户库拆解
                [6] 发送数据包配置生成器
                [7] 自定义发送数据包组(单一用户)
                [8] 周年庆邀请活动刷取(单一用户)
                [9] 周年庆邀请活动刷取(user.json)
                [10] user.json用户库保护
                [11] 拓维官服创建账号(使用手机号与密码)
                [12] 拓维官服创建账号(批量创建)
                [13] 剧院币一键成号刷取(单一用户)
                [14] 剧院币一键成号刷取(user.json)
                [15] 完美存档刷取(单一用户)
                [16] 完美存档刷取(user.json)
                [17] 快速刷钻(单一用户)
                [18] 快速刷钻(user.json)
                [0] 退出程序""");
        Log.v("请输入序号并按回车键继续……:");
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
                        Log.s("自定义发送数据包组(单一用户)");
                        keepRunning = false;
                    }
                    case 8 -> {
                        Log.s("周年庆邀请活动刷取(单一用户)");
                        keepRunning = false;
                    }
                    case 9 -> {
                        Log.s("周年庆邀请活动刷取(user.json)");
                        keepRunning = false;
                    }
                    case 10 -> {
                        Log.s("user.json用户库保护");
                        keepRunning = false;
                    }
                    case 11 -> {
                        Log.s("拓维官服创建账号(使用手机号与密码)");
                        keepRunning = false;
                    }
                    case 12 -> {
                        Log.s("拓维官服创建账号(批量创建)");
                        keepRunning = false;
                    }
                    case 13 -> {
                        Log.s("剧院币一键成号刷取(单一用户)");
                        keepRunning = false;
                    }
                    case 14 -> {
                        Log.s("剧院币一键成号刷取(user.json)");
                        keepRunning = false;
                    }
                    case 15 -> {
                        Log.s("完美存档刷取(单一用户)");
                        keepRunning = false;
                    }
                    case 16 -> {
                        Log.s("完美存档刷取(user.json)");
                        keepRunning = false;
                    }
                    case 17 -> {
                        Log.s("[17] 快速刷钻(单一用户)");
                        keepRunning = false;
                    }
                    case 18 -> {
                        Log.s("[18] 快速刷钻(user.json)");
                        keepRunning = false;
                    }
                    case 99 -> {
                        Log.d("Debug - HttpBrushTest - main");
                        keepRunning = false;
                    }
                    case 0 -> System.exit(0);
                    default -> Log.e("输入无效，请重新输入功能序号:");
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
        Log.v("请输入序号并按回车键继续……:");
        return smfScanner.Int(false, "^[1|2]$");
    }

    private static String androidVersion() {
        Log.v("请输入安卓版本号，格式为${VersionName}，如1.2.3");
        return smfScanner.String(false, "^\\d{1}\\.\\d{1}\\.\\d{1}$");
    }

    private static String iosVersion() {
        Log.v("请输入苹果版本号，格式为${VersionName}.${VersionCode}，如1.2.3.123");
        return smfScanner.String(false, "^\\d{1}\\.\\d{1}\\.\\d{1}\\.\\d{3,}$");
    }

    private static boolean environment() {
        return true;
    }

    private static int channelId(JSONArray packages) {
        int channelId = 208;
        try {
            StringBuilder stringBuilder = new StringBuilder("^[");
            Log.v("请输入渠道，以下是目前所有可用的渠道:");
            for (int i = 0; i < packages.size(); i++) {
                JSONObject jsonObject = packages.getJSONObject(i);
                String description = jsonObject.getString("Description");
                int id = jsonObject.getIntValue("id");
                String value = jsonObject.getString("value");
                stringBuilder.append(id);
                if (i != packages.size() - 1) {
                    stringBuilder.append("|");
                }
                Log.i(String.format("渠道:%s - Id:%s - 包名:%s", description, id, value));
            }
            stringBuilder.append("]+$");
            Log.v("请输入渠道Id，并按回车键继续……:");
            channelId = smfScanner.Int(false, stringBuilder.toString());
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
        }
        return channelId;
    }

    private static String packageValue(JSONArray packages) {
        try {
            return JSONPath.eval(packages, "$[?(@.id == " + channelId + ")][0].value").toString();
        } catch (Exception e) {
            Log.e("配置文件异常:");
            e.printStackTrace();
            return null;
        }
    }

    enum SettingType {
        GlobalSettings,
        OtherSettings
    }
}
