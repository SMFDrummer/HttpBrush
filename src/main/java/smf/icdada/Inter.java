package smf.icdada;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author SMF & icdada
 * @描述: 默认配置方法类
 * <p>
 * &#064;//  TODO: 2023/7/30  即将完善 Inter public 常量组内联及外部调用
 * </p>
 * <p>
 * 包含所有常量的写入、监听、纠错、初始化。
 * </p>
 */
public class Inter {
    public static int oi, versionCode;
    public static String versionName;
    public static int maxGem;
    public static int sleepMillions;
    public static boolean isAndroid;
    public static int inter;
    public static int chooser;
    public static int waiter;
    public static int count;
    public static String betaUrl;

    public static void inter() {
        boolean keepRunning = true;
        System.out.println("""
                功能列表：
                [1] 自适应网络数据包加解密
                [2] 接收拓维UserID区间的banuser.json生成
                [3] 根据banuser.json用户库自动封号
                [4] user.json用户库状态刷新
                [5] user.json用户库拆解
                [6] 发送数据包配置生成器
                [7] 自定义发送数据包组
                [8] 周年庆邀请活动刷取
                [0] 退出程序
                请输入功能序号并按回车键继续……：
                """);
        while (keepRunning) {
            try {
                Scanner scanner = new Scanner(System.in);
                inter = scanner.nextInt();
                switch (inter) {
                    case 1:
                        System.out.println("[1] 自适应网络数据包加解密");
                        keepRunning = false;
                        break;
                    case 2:
                        System.out.println("[2] 接收拓维UserID区间的banuser.json生成");
                        keepRunning = false;
                        break;
                    case 3:
                        System.out.println("[3] 根据banuser.json用户库自动封号");
                        keepRunning = false;
                        break;
                    case 4:
                        System.out.println("[4] user.json用户库状态刷新");
                        keepRunning = false;
                        break;
                    case 5:
                        System.out.println("[5] user.json用户库拆解");
                        keepRunning = false;
                        break;
                    case 6:
                        System.out.println("[6] 发送数据包配置生成器");
                        keepRunning = false;
                        break;
                    case 7:
                        System.out.println("[7] 自定义发送数据包组");
                        keepRunning = false;
                        break;
                    case 8:
                        System.out.println("[8] 周年庆邀请活动刷取");
                        keepRunning = false;
                        break;
                    case 0:
                        System.out.println("[0] 退出程序");
                        keepRunning = false;
                    default:
                        System.out.println("\033[31m" + "输入无效，请重新输入功能序号：" + "\033[0m");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("\n————————————————————————————————————————————————————————————————————————————");
    }

    public static void isAndroid() {
        while (true) {
            System.out.println("\033[33m" + "是否为 Android 渠道？（默认为 Y）：(Y/N)" + "\033[0m");
            Scanner isAndroidScanner = new Scanner(System.in);
            String userInput = isAndroidScanner.nextLine();
            if (userInput.equalsIgnoreCase("N")) {
                isAndroid = false;
                break;
            } else if (userInput.equalsIgnoreCase("Y") || userInput.isEmpty()) {
                isAndroid = true;
                break;
            } else {
                System.out.println("\033[31m" + "输入无效，请重新输入" + "\033[0m");
            }
        }
    }

    public static void count() {
        System.out.println("\033[33m" + "请输入循环次数（循环一次为 320 钻石，不输入则默认为无限循环）：" + "\033[0m");
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile("^\\d+$");
        while (true) {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                count = 99999;
                break;
            } else if (pattern.matcher(input).matches()) {
                count = Integer.parseInt(input);
                if (count > 0) {
                    break;
                } else {
                    System.out.println("\033[31m" + "请输入一个正整数或直接回车" + "\033[0m");
                }
            } else {
                System.out.println("\033[31m" + "输入无效，请输入一个正整数或直接回车" + "\033[0m");
            }
        }
        System.out.println();
    }

    public static void maxGem() {
        System.out.println("\033[33m" + "请输入钻石阈值（默认为 950w）：" + "\033[0m");
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile("^\\d+$");
        while (true) {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                maxGem = 9500000;
                break;
            } else if (pattern.matcher(input).matches()) {
                maxGem = Integer.parseInt(input);
                if (maxGem > 0) {
                    break;
                } else {
                    System.out.println("\033[31m" + "请输入一个正整数或直接回车" + "\033[0m");
                }
            } else {
                System.out.println("\033[31m" + "输入无效，请输入一个正整数或直接回车" + "\033[0m");
            }
        }
        System.out.println();
    }

    public static void sleepMillions() {
        System.out.println("\033[33m" + "请输入刷钻间隔（默认为 200ms）：" + "\033[0m");
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile("^\\d+$");
        while (true) {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                sleepMillions = 200;
                break;
            } else if (pattern.matcher(input).matches()) {
                sleepMillions = Integer.parseInt(input);
                if (sleepMillions > 0) {
                    break;
                } else {
                    System.out.println("\033[31m" + "请输入一个正整数或直接回车" + "\033[0m");
                }
            } else {
                System.out.println("\033[31m" + "输入无效，请输入一个正整数或直接回车" + "\033[0m");
            }
        }
        System.out.println();
    }

    public static void chooser() {
        System.out.println("\033[33m" + "请输入执行代理池类型（值 1 为本地代理池，值 2 为在线代理池，默认为 2）：" + "\033[0m");
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile("^([123])$");
        while (true) {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                chooser = 2;
                break;
            } else if (pattern.matcher(input).matches()) {
                chooser = Integer.parseInt(input);
                break;
            } else {
                System.out.println("\033[31m" + "输入无效，请输入 1 或 2，或直接回车" + "\033[0m");
            }
        }
        System.out.println();
    }

    public static void waiter() {
        System.out.println("\033[33m" + "请输入首次运行代理池时需要对代理池进行等待的时长（单位：毫秒，默认值 10 分钟）：" + "\033[0m");
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile("^\\d+$");
        while (true) {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                waiter = 600000;
                break;
            } else if (pattern.matcher(input).matches()) {
                waiter = Integer.parseInt(input);
                if (waiter > 0) {
                    break;
                } else {
                    System.out.println("\033[31m" + "请输入一个正整数或直接回车" + "\033[0m");
                }
            } else {
                System.out.println("\033[31m" + "输入无效，请输入一个正整数或直接回车" + "\033[0m");
            }
        }
        System.out.println();
    }

    public static void oi() {
        System.out.println("\033[33m" + "请输入程序运行的渠道（109208 为官方渠道，109250 为 Taptap 渠道，默认为官方渠道）：" + "\033[0m");
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile("^(109208|109250)$");
        while (true) {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                oi = 109208;
                break;
            } else if (pattern.matcher(input).matches()) {
                oi = Integer.parseInt(input);
                break;
            } else {
                System.out.println("\033[31m" + "输入无效，请输入 109208 或 109250，或直接回车" + "\033[0m");
            }
        }
        System.out.println();
    }

    public static void versionName() {
        System.out.println("\033[33m" + "请输入版本名：" + "\033[0m");
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile("^\\d\\.\\d\\.\\d$");
        while (true) {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                versionName = "3.1.5";
                break;
            } else if (pattern.matcher(input).matches()) {
                versionName = input;
                break;
            } else {
                System.out.println("\033[31m" + "输入无效，请输入格式为：单个数字.单个数字.单个数字的版本名，或直接回车" + "\033[0m");
            }
        }
        System.out.println();
    }

    public static void versionCode() {
        System.out.println("\033[33m" + "请输入版本号：" + "\033[0m");
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile("^\\d+$");
        while (true) {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                versionCode = 1390;
                break;
            } else if (pattern.matcher(input).matches()) {
                versionCode = Integer.parseInt(input);
                if (versionCode > 0) {
                    break;
                } else {
                    System.out.println("\033[31m" + "请输入一个正整数或直接回车" + "\033[0m");
                }
            } else {
                System.out.println("\033[31m" + "输入无效，请输入一个正整数或直接回车" + "\033[0m");
            }
        }
        System.out.println();
    }

    public static void defaultSetting() {
        try {
            String stringUrl = System.getProperty("user.dir") + File.separator + "default.json";
            String tempFilePath = System.getProperty("user.dir") + File.separator + "temp";
            File tempDir = new File(tempFilePath);
            if (!tempDir.exists()) {
                tempDir.mkdir(); // 如果temp文件夹不存在，则创建它
            }
            Path settingPath = Paths.get(stringUrl);
            if (Files.exists(settingPath)) {
                JSONObject settingData = JSONObject.parse(Files.readString(settingPath));
                if (settingData.containsKey("inter")) {
                    String interValue = settingData.getString("inter");
                    if (interValue.equals("?input")) {
                        inter();
                    } else {
                        try {
                            inter = Integer.parseInt(interValue);
                            System.out.println("已检测到默认执行功能：[" + inter + "]");
                        } catch (NumberFormatException e) {
                            System.out.println("default.json 文件中 inter 值不合法，已重置");
                            settingData.put("inter", "?input");
                            inter();
                        }
                    }
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("inter", "?input");
                    inter();
                }
                if (settingData.containsKey("isAndroid")) {
                    isAndroid = settingData.getBooleanValue("isAndroid");
                    System.out.println("已检测到默认执行配置：isAndroid：" + isAndroid);
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("isAndroid", true);
                    isAndroid();
                }
                if (settingData.containsKey("count")) {
                    String countValue = settingData.getString("count");
                    if (countValue.equals("?input")) {
                        count();
                    } else {
                        try {
                            count = Integer.parseInt(countValue);
                            System.out.println("已检测到默认循环数量：" + count);
                        } catch (NumberFormatException e) {
                            System.out.println("default.json 文件中 count 值不合法，已重置");
                            settingData.put("count", "?input");
                            count();
                        }
                    }
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("count", "?input");
                    count();
                }
                if (settingData.containsKey("maxGem")) {
                    String maxGemValue = settingData.getString("maxGem");
                    if (maxGemValue.equals("?input")) {
                        maxGem();
                    } else {
                        try {
                            maxGem = Integer.parseInt(maxGemValue);
                            System.out.println("已检测到默认钻石阈值：" + maxGem);
                        } catch (NumberFormatException e) {
                            System.out.println("default.json 文件中 maxGem 值不合法，已重置");
                            settingData.put("maxGem", "?input");
                            maxGem();
                        }
                    }
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("maxGem", "?input");
                    maxGem();
                }
                if (settingData.containsKey("sleepMillions")) {
                    String sleepMillionsValue = settingData.getString("sleepMillions");
                    if (sleepMillionsValue.equals("?input")) {
                        sleepMillions();
                    } else {
                        try {
                            sleepMillions = Integer.parseInt(sleepMillionsValue);
                            System.out.println("已检测到默认刷钻间隔：" + sleepMillions);
                        } catch (NumberFormatException e) {
                            System.out.println("default.json 文件中 sleepMillions 值不合法，已重置");
                            settingData.put("sleepMillions", "?input");
                            sleepMillions();
                        }
                    }
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("sleepMillions", "?input");
                    sleepMillions();
                }
                if (settingData.containsKey("chooser")) {
                    String chooserValue = settingData.getString("chooser");
                    if (chooserValue.equals("?input")) {
                        chooser();
                    } else {
                        try {
                            chooser = Integer.parseInt(chooserValue);
                            if (chooser == 3){
                                String betaUrlValue = settingData.getString("betaUrl");
                                if (!betaUrlValue.isBlank()){
                                    betaUrl = betaUrlValue;
                                    System.out.println("已检测到测试代理池：" + betaUrl);
                                } else {
                                    chooser = 2;
                                }
                            }
                            if (chooser == 1||chooser == 2) System.out.println("已检测到默认代理池类型：" + chooser);
                        } catch (NumberFormatException e) {
                            System.out.println("default.json 文件中 chooser 值不合法，已重置");
                            settingData.put("chooser", "?input");
                            chooser();
                        }
                    }
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("chooser", "?input");
                    chooser();
                }
                if (settingData.containsKey("waiter")) {
                    String waiterValue = settingData.getString("waiter");
                    if (waiterValue.equals("?input")) {
                        waiter();
                    } else {
                        try {
                            waiter = Integer.parseInt(waiterValue);
                            System.out.println("已检测到默认代理池刷新时长：" + waiter);
                        } catch (NumberFormatException e) {
                            System.out.println("default.json 文件中 waiter 值不合法，已重置");
                            settingData.put("waiter", "?input");
                            waiter();
                        }
                    }
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("waiter", "?input");
                    waiter();
                }
                if (settingData.containsKey("oi")) {
                    String oiValue = settingData.getString("oi");
                    if (oiValue.equals("?input")) {
                        oi();
                    } else {
                        try {
                            oi = Integer.parseInt(oiValue);
                            System.out.println("已检测到默认渠道：" + oi);
                        } catch (NumberFormatException e) {
                            System.out.println("default.json 文件中 oi 值不合法，已重置");
                            settingData.put("oi", "?input");
                            oi();
                        }
                    }
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("oi", "?input");
                    oi();
                }
                if (settingData.containsKey("versionName")) {
                    String versionNameValue = settingData.getString("versionName");
                    if (versionNameValue.equals("?input")) {
                        versionName();
                    } else {
                        try {
                            versionName = versionNameValue;
                            System.out.println("已检测到默认版本名：" + versionName);
                        } catch (NumberFormatException e) {
                            System.out.println("default.json 文件中 versionName 值不合法，已重置");
                            settingData.put("versionName", "?input");
                            versionName();
                        }
                    }
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("versionName", "?input");
                    versionName();
                }
                if (settingData.containsKey("versionCode")) {
                    String versionCodeValue = settingData.getString("versionCode");
                    if (versionCodeValue.equals("?input")) {
                        versionCode();
                    } else {
                        try {
                            versionCode = Integer.parseInt(versionCodeValue);
                            System.out.println("已检测到默认版本号：" + versionCode);
                        } catch (NumberFormatException e) {
                            System.out.println("default.json 文件中 versionCode 值不合法，已重置");
                            settingData.put("versionCode", "?input");
                            versionCode();
                        }
                    }
                } else {
                    System.out.println("default.json 文件中缺失关键值，已进行补充");
                    settingData.put("versionCode", "?input");
                    versionCode();
                }
                System.out.println("————————————————————————————————————————————————————————————————————————————\n");
                String formattedJson = settingData.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
                try (FileWriter fileWriter = new FileWriter(stringUrl)) {
                    fileWriter.write(formattedJson);
                    fileWriter.flush();
                }
            } else {
                System.out.println("\033[31m" + "default null, please check out the " + System.getProperty("user.dir") + File.separator + "default.json exist" + "\033[0m");
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
