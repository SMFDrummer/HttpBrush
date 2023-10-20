package smf.icdada.HttpUtils;

import com.alibaba.fastjson2.*;
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
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.stream.IntStream;

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
            if (filePathString != null && filePathString.length > 0) {
                filePath = getFilePath(filePathString[0].toString());
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
                        for (JSONObject Package : sendPackages) {
                            String identifier = Package.getString("Identifier");
                            JSONObject body = Package.getJSONObject("Body");
                            if (!RequestType.checkRequestBody(identifier) && !body.getBooleanValue("override")) {
                                Log.w("由于未找到默认模板且未启用覆写，程序无法执行，即将退出");
                                System.exit(0);
                            }
                            String requestBody = formatRequestBody(Package, userId);
                            int cycleIndex = Package.getIntValue("CycleIndex");
                            boolean replaceUisk = (boolean) JSONPath.eval(Package, "$.Body.account");
                            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
                            Check.Any any = new Check.Any();
                            IntStream.range(0, cycleIndex).forEach(i -> {
                                while (true) {
                                    Future<String> future = executor.submit(() -> getResponseBody(requestBody, userId, replaceUisk));
                                    try {
                                        any.setResponseBody(future.get(3, TimeUnit.SECONDS));
                                        if (Package.getBooleanValue("CheckSuccess")) {
                                            boolean success = any.isValid(0);
                                            if ((boolean) JSONPath.eval(Package, "$.CheckPoint.override")) {
                                                success = any.isValid((int) JSONPath.eval(Package, "$.CheckPoint.value"));
                                            }
                                            if (success) break;
                                        } else break;
                                    } catch (Exception ignored) {
                                        refresh(userId);
                                    }
                                }
                            });
                        }
                        return true;
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

    private static String formatRequestBody(JSONObject Package, int userId) {
        RequestType V = RequestType.valueOf(JSONPath.eval(Package, "$.Identifier").toString());
        JSONObject requestBody = (boolean) JSONPath.eval(Package, "$.Body.override") ?
                (JSONObject) JSONPath.eval(Package, "$.Body.value") :
                JSON.parseObject(V.getRequestBody(V, userId));
        ConcurrentHashMap<String, Object> configs = new ConcurrentHashMap<>();
        for (Object o : (JSONArray) JSONPath.eval(Package, "$.Body.config")) {
            JSONObject config = (JSONObject) o;
            Object value = switch (Format.DataType.valueOf(config.getString("type"))) {
                case Int -> (Integer) config.get("value");
                case Double -> (Double) config.get("value");
                case String -> config.get("value").toString();
                case JSONObject -> (JSONObject) config.get("value");
                case JSONArray -> (JSONArray) config.get("value");
            };
            configs.put(config.getString("keyPath"), value);
        }
        if (!configs.isEmpty()) {
            configs.forEach((keyPath, value) -> JSONPath.of(keyPath).set(requestBody, value));
        }
        return requestBody.toJSONString(JSONWriter.Feature.WriteMapNullValue);
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
            boolean override;
            String value = "";
            JSONArray config = new JSONArray();
            if (RequestType.checkRequestBody(packageIdentifier)) {
                Log.s("找到默认模板如下");
                RequestType.printRequestBody(packageIdentifier);
                Log.v("是否使用默认模板？");
                override = !smfScanner.Boolean(false);
                if (!override) {
                    if (Format.hasSubclass(packageIdentifier)) {
                        config = Format.writeFormat(packageIdentifier);
                    }
                } else {
                    value = processRequestBody(packageIdentifier);
                    config = processConfig();
                }
            } else {
                Log.e("未找到默认模板");
                override = true;
                value = processRequestBody(packageIdentifier);
                config = processConfig();
            }
            Log.v("是否需要程序自动更新账号信息？");
            boolean account = smfScanner.Boolean(false);
            packageBody.put("override", override);
            packageBody.put("value", value);
            packageBody.put("account", account);
            packageBody.put("config", config);
            aPackage.put("Identifier", packageIdentifier);
            aPackage.put("Body", packageBody);
            Log.v("请输入执行次数:");
            int cycleIndex = smfScanner.Int(false, "^[1-9]\\d*$");
            aPackage.put("CycleIndex", cycleIndex);
            Log.v("是否需要检测数据包是否发送成功？");
            boolean checkSuccess = smfScanner.Boolean(false);
            aPackage.put("CheckSuccess", checkSuccess);
            boolean overrideR = false;
            int valueR = 0;
            if (checkSuccess) {
                Log.v("是否需要变更发包成功检测的r的值？");
                overrideR = smfScanner.Boolean(false);
            }
            CheckPoint.put("override", overrideR);
            if (overrideR) {
                Log.v("请输入发包成功检测的r的值:");
                valueR = smfScanner.Int(false, "^\\d+$");
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

    private static String processRequestBody(String identifier) {
        String value;
        Log.v("请输入数据包，格式为标准irt结构或t结构");
        while (true) {
            value = smfScanner.LongString(true);
            if (JSON.isValidObject(value)) {
                JSONObject parseBody = JSON.parseObject(value);
                if (!parseBody.containsKey("i") || !parseBody.getString("i").equals(identifier)) {
                    JSONObject parseValue = new JSONObject();
                    parseValue.put("i", identifier);
                    parseValue.put("r", 0);
                    parseValue.put("t", parseBody);
                    parseBody = parseValue;
                }
                value = parseBody.toJSONString(JSONWriter.Feature.WriteMapNullValue);
                break;
            } else Log.e("输入的数据包不符合规范，请重新输入");
        }
        return value;
    }

    private static JSONArray processConfig() {
        JSONArray config = new JSONArray();
        Log.i("数据包内容自定义，请根据提示输入正确的路径配置、类型以及自定义值");
        Log.v("是否添加自定义配置？");
        while (smfScanner.Boolean(false)) {
            Log.v("请输入正确的键路径，以$.t开始");
            String keyPath = smfScanner.String(true, "^\\$.t");
            Log.v("请输入键值类型");
            Log.i("Int(整数)、Double(小数)、String(字符串)、JSONObject(JSON对象{})、JSONArray(JSON数组[])");
            String type = smfScanner.String(false, "^[Int|Double|String|JSONObject|JSONArray]$");
            JSONObject configElement = Format.write(keyPath, Format.DataType.valueOf(type));
            config.add(configElement);
            Log.v("是否要继续添加？");
            if (!smfScanner.Boolean(false)) break;
        }
        return config;
    }
}
