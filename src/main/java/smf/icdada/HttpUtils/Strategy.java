package smf.icdada.HttpUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import smf.icdada.Log;
import smf.icdada.RequestType;
import smf.icdada.Result;
import smf.icdada.smfScanner;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static smf.icdada.HttpUtils.Base.*;

/**
 * @author SMF & icdada
 * @描述: HttpUtils核心方法类
 * <p>
 * 包含配置文件指引器、单一实现方法、批量实现方法、响应获取以及配置文件创建方法。
 * </p>
 */
public class Strategy {
    /**
     * @描述: 配置文件解析方法
     */
    public static boolean apply(int userId, Object... filePathString) {
        try {
            String filePath;
            if (filePathString != null) {
                filePath = getFilePath((String) filePathString[0]);
            } else {
                filePath = getFilePath();
            }
            JSONObject parse = JSONObject.parse(Files.readString(Paths.get(filePath)));
            if (parse.containsKey("Configuration") && "HttpUtilSenderProps".equals(parse.get("Configuration"))) {
                if (parse.containsKey("SendPackage")) {
                    refresh(userId);
                    Result uisk = getUisk(userId);
                    Result proxy = getProxy(userId);
                    if (!"banned".equals(uisk.getUi()) && !"banned".equals(uisk.getSk())) {
                        JSONArray sendPackage = parse.getJSONArray("SendPackage");
                        JSONObject[] sendPackages = new JSONObject[sendPackage.size()];
                        for (int i = 0; i < sendPackage.size(); i++) {
                            sendPackages[i] = sendPackage.getJSONObject(i);
                        }
                        Arrays.sort(sendPackages, (o1, o2) -> {
                            int packageOrder1 = o1.getInteger("Order");
                            int packageOrder2 = o2.getInteger("Order");
                            return Integer.compare(packageOrder1, packageOrder2);
                        });
                        for (JSONObject aPackage : sendPackages) {
                            String identifier = aPackage.getString("Identifier");
                            JSONObject body = aPackage.getJSONObject("Body");
                            if (!RequestType.checkRequestBody(identifier) && !body.getBooleanValue("override")) {
                                Log.w("由于未找到默认模板且未启用覆写，程序无法执行，即将退出");
                                System.exit(0);
                            } else {
                                int cycleIndex = aPackage.getIntValue("CycleIndex");
                                JSONArray config = body.getJSONArray("config");
                                Object[] param = getParam(config);
                                ExecutorService service = Executors.newFixedThreadPool(cycleIndex + 1);
                                boolean keepRunning = true;
                                while (keepRunning) {
                                    List<Future<String>> futures = new ArrayList<>();
                                    if (body.getBooleanValue("override")) {
                                        if (cycleIndex > 0) {
                                            for (int i = 1; i <= cycleIndex; i++) {
                                                futures.add(service.submit(() -> getResponseBody(userId, body.getString("value"))));
                                                if (aPackage.getBooleanValue("CheckSuccess")) {
                                                    JSONObject checkPoint = aPackage.getJSONObject("CheckPoint");
                                                    int r = 0;
                                                    if (checkPoint.getBooleanValue("override")) {
                                                        r = checkPoint.getIntValue("value");
                                                    }
                                                    try {
                                                        int p = 0;
                                                        for (Future<String> future : futures) {
                                                            String responseBody = future.get(3, TimeUnit.SECONDS);
                                                            if (
                                                                    JSONObject.parseObject(responseBody).getIntValue("r") == r ||
                                                                            JSONObject.parseObject(responseBody).getIntValue("r") == 0
                                                            )
                                                                p++;
                                                        }
                                                        if (p == cycleIndex) keepRunning = false;
                                                    } catch (Exception e) {
                                                        refresh(userId);
                                                    }
                                                } else {
                                                    keepRunning = false;
                                                }
                                            }
                                        } else {
                                            keepRunning = false;
                                        }
                                    } else {
                                        if (cycleIndex > 0) {
                                            for (int i = 1; i <= cycleIndex; i++) {
                                                futures.add(service.submit(() -> getResponseBody(RequestType.valueOf(identifier), userId, param)));
                                                if (aPackage.getBooleanValue("CheckSuccess")) {
                                                    JSONObject checkPoint = aPackage.getJSONObject("CheckPoint");
                                                    int r = 0;
                                                    if (checkPoint.getBooleanValue("override")) {
                                                        r = checkPoint.getIntValue("value");
                                                    }
                                                    try {
                                                        int p = 0;
                                                        for (Future<String> future : futures) {
                                                            String responseBody = future.get(3, TimeUnit.SECONDS);
                                                            if (
                                                                    JSONObject.parseObject(responseBody).getIntValue("r") == r ||
                                                                    JSONObject.parseObject(responseBody).getIntValue("r") == 0
                                                            )
                                                                p++;
                                                        }
                                                        if (p == cycleIndex) keepRunning = false;
                                                    } catch (Exception e) {
                                                        refresh(userId);
                                                    }
                                                } else {
                                                    keepRunning = false;
                                                }
                                            }
                                        } else {
                                            keepRunning = false;
                                        }
                                    }
                                }
                            }
                        } return true;
                    } else {
                        System.exit(0);
                    }
                } else {
                    Log.e("配置文件损坏，请重新生成或尝试修复配置");
                    System.exit(0);
                }
            } else {
                Log.e("选择的配置不是Strategy标准配置，程序无法执行");
                System.exit(0);
            }
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static Object[] getParam(JSONArray config) {
        if (!config.isEmpty()) {
            Object[] param = new Object[config.size()];
            int i = 0;
            for (Object configElement : config) {
                JSONObject jsonObject = (JSONObject) configElement;
                param[i] = switch (jsonObject.getString("type")) {
                    case "int" -> (Integer) jsonObject.get("value");
                    case "double" -> (double) jsonObject.get("value");
                    case "String" -> (String) jsonObject.get("value");
                    case "JSONObject" -> (JSONObject) jsonObject.get("value");
                    case "JSONArray" -> (JSONArray) jsonObject.get("value");
                    default -> jsonObject.get("value");
                };
                i++;
            }
            return param;
        } else {
            return null;
        }
    }

    /**
     * @描述: 配置文件生成器
     */
    public static void maker() {
        Log.d("欢迎使用配置生成器，请根据指引输入指定内容，并按回车继续:");
        JSONObject parse = new JSONObject();
        JSONObject fileInfo = new JSONObject();
        JSONArray author = new JSONArray("SMF", "icdada");
        fileInfo.put("Author", author);
        JSONArray sendPackage = new JSONArray();
        int i = 1;
        do {
            JSONObject aPackage = new JSONObject();
            JSONObject CheckPoint = new JSONObject();
            CheckPoint.put("override", false);
            CheckPoint.put("value", 0);
            aPackage.put("Order", i++);
            Log.v("请输入数据包标识:");
            String packageIdentifier = smfScanner.String(false, "^[V|I]\\d{1,}$");
            JSONObject packageBody = new JSONObject();
            boolean override = false;
            String value = "";
            JSONArray config = new JSONArray();
            if (RequestType.checkRequestBody(packageIdentifier)) {
                Log.s("找到默认模板如下");
                RequestType.printRequestBody(packageIdentifier);
                Log.v("是否使用默认模板？");
                if (!smfScanner.Boolean(false)) {
                    override = true;
                    Log.v("请输入数据包，格式为标准irt结构或t结构");
                    Log.w("数据包仅支持替换pi、ui与sk，其他必要值请自行填入，程序不支持替换");
                    while (true) {
                        value = smfScanner.LongString(true);
                        if (JSON.isValidObject(value)) {
                            JSONObject parseBody = JSONObject.parse(value);
                            if (!parseBody.containsKey("i") || !parseBody.getString("i").startsWith(packageIdentifier)) {
                                JSONObject parseValue = new JSONObject();
                                parseValue.put("i", packageIdentifier);
                                parseValue.put("r", 0);
                                parseValue.put("t", parseBody);
                                parseBody = parseValue;
                            }
                            JSONObject t = parseBody.getJSONObject("t");
                            if (
                                    t.containsKey("pi") &&
                                            t.containsKey("sk") &&
                                            t.containsKey("ui")
                            ) {
                                t.put("pi", "%s");
                                t.put("sk", "%s");
                                t.put("ui", "%s");
                                parseBody.put("t", t);
                            }
                            value = parseBody.toJSONString(JSONWriter.Feature.WriteMapNullValue);
                            break;
                        } else Log.e("输入的数据包不符合规范，请重新输入");
                    }
                } else if (Format.hasSubclass(packageIdentifier)) {
                    config = Format.writeFormat(packageIdentifier);
                }
            } else {
                Log.e("未找到默认模板");
                override = true;
                Log.v("请输入数据包，格式为标准irt结构或t结构");
                Log.w("数据包仅支持替换pi、ui与sk，其他必要值请自行填入，程序不支持替换");
                while (true) {
                    value = smfScanner.LongString(true);
                    if (JSON.isValidObject(value)) {
                        JSONObject parseBody = JSONObject.parse(value);
                        if (!parseBody.containsKey("i") || !parseBody.getString("i").startsWith(packageIdentifier)) {
                            JSONObject parseValue = new JSONObject();
                            parseValue.put("i", packageIdentifier);
                            parseValue.put("r", 0);
                            parseValue.put("t", parseBody);
                            parseBody = parseValue;
                        }
                        JSONObject t = parseBody.getJSONObject("t");
                        if (
                                t.containsKey("pi") &&
                                        t.containsKey("sk") &&
                                        t.containsKey("ui")
                        ) {
                            t.put("pi", "%s");
                            t.put("sk", "%s");
                            t.put("ui", "%s");
                            parseBody.put("t", t);
                        }
                        value = parseBody.toJSONString(JSONWriter.Feature.WriteMapNullValue);
                        break;
                    } else Log.e("输入的数据包不符合规范，请重新输入");
                }
            }
            packageBody.put("override", override);
            packageBody.put("value", value);
            packageBody.put("config", config);
            aPackage.put("Identifier", packageIdentifier);
            aPackage.put("Body", packageBody);
            Log.v("请输入执行次数:");
            int cycleIndex = smfScanner.Int(false, "^[1-9]\\d*$");
            aPackage.put("CycleIndex", cycleIndex);
            Log.v("是否需要检测改包是否发送成功？");
            boolean checkSuccess = smfScanner.Boolean(false);
            aPackage.put("CheckSuccess", checkSuccess);
            Log.v("是否需要变更发包成功检测的r的值？");
            boolean overrideR = smfScanner.Boolean(false);
            CheckPoint.put("override", overrideR);
            if (overrideR) {
                Log.v("请输入发包成功检测的r的值:");
                int valueR = smfScanner.Int(false, "^\\d+$");
                CheckPoint.put("value", valueR);
            }
            aPackage.put("CheckPoint", CheckPoint);
            sendPackage.add(aPackage);
            Log.v("是否继续添加？");
        } while (smfScanner.Boolean(false));
        parse.put("Configuration", "HttpUtilSenderProps");
        parse.put("FileInfo", fileInfo);
        parse.put("SendPackage", sendPackage);
        String newStrategy = System.getProperty("user.dir") + File.separator + "StrategyConfig_" + DateTimeFormatter.ofPattern("MM-dd-HH-mm").format(LocalDateTime.now()) + ".json";
        File strategy = new File(newStrategy);
        try (FileWriter fileWriter = new FileWriter(strategy)) {
            fileWriter.write(parse.toJSONString(JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat));
            fileWriter.flush();
        } catch (Exception ignored) {
        }
        Log.s("配置文件已生成！文件名为:" + strategy.getName());
        Log.i("生成路径位于:" + strategy.getPath());
        System.exit(0);
    }
}
