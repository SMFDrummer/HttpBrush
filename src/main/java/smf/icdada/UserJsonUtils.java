package smf.icdada;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static smf.icdada.HttpUtils.base.*;

/**
 * @author SMF & icdada
 * @描述: 用户库账号刷新方法类
 * <p>
 * &#064;//  TODO: 2023/7/30  未完成的注释：用户库账号刷新方法类
 * </p>
 */
public class UserJsonUtils {
    private static final ThreadLocal<Integer> fgThreadLocal = new ThreadLocal<>();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void gemSetter() {
        List<Integer> userIds = readUserIds();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int userId : userIds) {
            sleep(100);
            executorService.submit(() -> {
                System.out.println("\033[33m" + "账号：" + userId + "\033[0m" + " || " + "\033[33m" + " 已读取，开始执行" + "\033[0m");
                gemResponseUtil(userId);
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("所有账号处理完成，程序退出");
        System.exit(0);
    }

    private static void gemResponseUtil(int userId) {
        refresh(Inter.oi, userId);
        Result uisk = getUisk(userId);
        if (!"banned".equals(uisk.getUi()) && !"banned".equals(uisk.getSk())) {
            Result proxy = getProxy(userId);
            int gem = getGemResponse(userId, uisk, proxy);
            JsonUtil(userId, gem);
        }
    }

    private static int getGemResponse(int userId, Result uisk, Result proxy) {
        int fg = fgThreadLocal.get() == null ? 0 : fgThreadLocal.get();
        String response316GemBody = "{\"r\":12202}";
        while (true) {
            boolean isError = false;
            try {
                assert uisk != null;
                response316GemBody = HttpCrypto.decryptRES(
                        HttpSender.doQuest(
                                Inter.isAndroid,
                                HttpCrypto.encryptREQ(
                                        RequestType.GET.getRequestBody(uisk.getUi(), uisk.getSk())
                                ),
                                proxy.getProxyHost(),
                                proxy.getProxyPort()));
            } catch (Exception ignored) {
                refresh(Inter.oi, userId);
                uisk = getUisk(userId);
                proxy = getProxy(userId);
            }
            // 处理响应
            if (JSONObject.parseObject(response316GemBody).getIntValue("r") != 0) {
                System.out.println("\033[33m" + "账号：" + userId + "\033[0m" + " || " + "\033[31m" + "读取失败，正在重试……" + "\033[0m" + " || " + response316GemBody);
                if (JSONObject.parseObject(response316GemBody).getIntValue("r") != 20013) isError = true;
            }
            if (isError) {
                System.out.println("\033[33m" + "账号：" + userId + "\033[0m" + " || " + "\033[33m" + "正在刷新各项数值，请稍后……" + "\033[0m");
                refresh(Inter.oi, userId);
                uisk = getUisk(userId);
                proxy = getProxy(userId);
            }
            JSONObject jsonObject = JSONObject.parseObject(response316GemBody);
            if (jsonObject.containsKey("r") && jsonObject.containsKey("d")) {
                if (jsonObject.getIntValue("r") == 0) {
                    JSONObject dObject = jsonObject.getJSONObject("d");
                    JSONObject pObject = dObject.getJSONObject("p");
                    fg = pObject.getIntValue("fg");
                    fgThreadLocal.set(fg);
                    System.out.println("\033[32m" + "账号：" + userId + "\033[0m" + " || " + "\033[32m" + "已获取钻石数量：" + fg + "\033[0m");
                    return fg;
                }
            }
        }
    }

    private static void JsonUtil(int userId, int gem) {
        String filePath = System.getProperty("user.dir") + File.separator + "user.json";
        String tempFilePath = System.getProperty("user.dir") + File.separator + "temp" + File.separator + UUID.randomUUID() + ".tmp";
        try {
            lock.writeLock().lock(); // 获取写锁
            String jsonString = Files.readString(Path.of(filePath));
            JSONObject parse = JSONObject.parseObject(jsonString);
            JSONArray usersArray = parse.getJSONArray("Users");
            for (int i = 0; i < usersArray.size(); i++) {
                JSONObject userObj = usersArray.getJSONObject(i);
                if (userObj.getIntValue("userId") == userId) {
                    userObj.put("gem", gem);
                    userObj.put("activate", gem <= Inter.maxGem);
                }
            }
            String formattedJson = parse.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
            try (FileWriter fileWriter = new FileWriter(tempFilePath)) {
                fileWriter.write(formattedJson);
                fileWriter.flush();
            }
            Files.move(Paths.get(tempFilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(Paths.get(tempFilePath)); // 删除临时文件
            System.out.println("\033[32m" + "账号：" + userId + "\033[0m" + " || " + "\033[32m" + "处理完成" + "\033[0m");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock(); // 释放写锁
        }
    }

    public static void JsonCutter() {
        String filePath = System.getProperty("user.dir") + File.separator + "user.json";
        File userFile = new File(filePath);
        String folderPath = System.getProperty("user.dir") + File.separator + "user";
        File userFolder = new File(folderPath);
        String passUserPath = System.getProperty("user.dir") + File.separator + "user" + File.separator + "pass.json";
        File passUser = new File(passUserPath);
        String failUserPath = System.getProperty("user.dir") + File.separator + "user" + File.separator + "fail.json";
        File failUser = new File(failUserPath);
        if (!userFolder.exists()) {
            if (!userFolder.mkdir()) {
                System.out.println("无法写出文件，请检查权限！");
                System.exit(0);
            }
        }
        JSONObject parsePass = JSONObject.parse("{}");
        JSONObject parseFail = JSONObject.parse("{}");
        JSONArray passUsers = new JSONArray();
        JSONArray failUsers = new JSONArray();
        try {
            JSONObject parse = JSONObject.parse(new String(Files.readAllBytes(userFile.toPath())));
            JSONArray users = parse.getJSONArray("Users");
            for (Object user : users) {
                JSONObject object = (JSONObject) user;
                if (object.containsKey("activate") && !object.getBoolean("activate")) {
                    passUsers.add(object);
                } else {
                    failUsers.add(object);
                }
            }
            parsePass.put("Users", passUsers);
            parseFail.put("Users", failUsers);
            FileWriter fileWriterPass = new FileWriter(passUser);
            FileWriter fileWriterFail = new FileWriter(failUser);
            fileWriterPass.write(parsePass.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue));
            fileWriterFail.write(parseFail.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue));
            fileWriterPass.flush();
            fileWriterFail.flush();
            fileWriterPass.close();
            fileWriterFail.close();
            passUserCutter(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("所有账号处理完成，程序退出");
        System.exit(0);
    }

    private static void passUserCutter(int num) {
        String passUserPath = System.getProperty("user.dir") + File.separator + "user" + File.separator + "pass.json";
        File file = new File(passUserPath);
        int objectCount = 0;
        int fileCount = 1;
        try {
            JSONObject parse = JSONObject.parse(new String(Files.readAllBytes(file.toPath())));
            JSONArray users = parse.getJSONArray("Users");
            JSONArray users1 = new JSONArray();
            for (Object user : users) {
                objectCount++;
                users1.add(user);
                if (objectCount % num == 0 || objectCount == users.size()) {
                    JSONObject newJson = new JSONObject();
                    newJson.put("Users", users1);
                    File newFile = new File(System.getProperty("user.dir") + File.separator + "user" + File.separator + "pass_" + fileCount + ".json");
                    FileWriter fileWriter = new FileWriter(newFile);
                    fileWriter.write(newJson.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue));
                    fileWriter.flush();
                    fileWriter.close();
                    users1.clear();
                    fileCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
