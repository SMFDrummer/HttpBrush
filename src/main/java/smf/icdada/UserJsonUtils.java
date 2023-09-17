package smf.icdada;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import smf.icdada.HttpUtils.Check;

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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static smf.icdada.HttpUtils.Base.*;

/**
 * @author SMF & icdada
 * @描述: 用户库账号刷新方法类
 */
public class UserJsonUtils {
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void measure() {
        List<Integer> userIds = readUserIds();
        ExecutorService executorService = Executors.newFixedThreadPool(userIds.size());
        for (int userId : userIds) {
            sleep(100);
            executorService.submit(() -> {
                Log.v("账号:" + userId + " || 已读取，开始执行");
                JsonUtilInterface(userId);
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.s("所有账号处理完成，程序退出");
        System.exit(0);
    }

    private static void JsonUtilInterface(int userId) {
        if (Inter.inter == 10) while (true) try {
            refresh(userId);
        } catch (Exception e) {
            e.printStackTrace();
            break;
        }
        refresh(userId);
        Result uisk = getUisk(userId);
        if (!"banned".equals(uisk.getUi()) && !"banned".equals(uisk.getSk())) {
            int gem = getGem(userId);
            JsonUtil(userId, "gem", gem);
            String inviteCode = getInviteCode(userId);
            JsonUtil(userId, "inviteCode", inviteCode);
        } else {
            JsonUtil(userId, "isBanned", true);
            JsonUtil(userId, "activate", false);
        }
    }

    private static int getGem(int userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Check.V316 v316 = new Check.V316();
        while (true) {
            try {
                Future<String> future = executor.submit(() -> getResponseBody(userId, RequestType.GET.getRequestBody(userId)));
                String response316Body = future.get(3, TimeUnit.SECONDS);
                v316.setResponseBody(response316Body);
                if (!v316.isValid(0)) {
                    Log.v("账号:" + userId + " || 读取失败，正在重试…… || " + response316Body);
                    if (!v316.isValid(20013)) refresh(userId);
                } else {
                    Check.V316.d d = v316.new d();
                    if (d.containsKey("p")) {
                        int fg = d.getJSONObject("p").getIntValue("fg");
                        Log.s("账号:" + userId + " || 已获取钻石数量:" + fg);
                        return fg;
                    }
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }

    private static String getInviteCode(int userId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Check.V303 v303 = new Check.V303();
        while (true) {
            try {
                Future<String> future = executor.submit(() -> getResponseBody(userId, RequestType.IN.getRequestBody(userId)));
                String response303Body = future.get(3, TimeUnit.SECONDS);
                v303.setResponseBody(response303Body);
                if (!v303.isValid(0)) {
                    Log.v("账号:" + userId + " || 读取失败，正在重试…… || " + response303Body);
                    if (!v303.isValid(20013)) {
                        refresh(userId);
                    }
                } else {
                    Check.V303.data data = v303.new data();
                    if (data.containsKey("code") && !data.getString("code").isBlank()) {
                        String inviteCode = data.getString("code");
                        Log.s("账号:" + userId + " || 已获取邀请码:" + inviteCode);
                        return inviteCode;
                    }
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }

    public static <T> void JsonUtil(int userId, String key, T value) {
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
                    userObj.put(key, value);
                }
            }
            String formattedJson = parse.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
            try (FileWriter fileWriter = new FileWriter(tempFilePath)) {
                fileWriter.write(formattedJson);
                fileWriter.flush();
            }
            Files.move(Paths.get(tempFilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(Paths.get(tempFilePath)); // 删除临时文件
            Log.s("账号:" + userId + " || 处理完成");
        } catch (Exception e) {
            Log.w(e.getMessage());
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
                Log.e("无法写出文件，请检查权限！");
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
            Log.w(e.getMessage());
            e.printStackTrace();
        }
        Log.v("所有账号处理完成，程序退出");
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
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }
}
