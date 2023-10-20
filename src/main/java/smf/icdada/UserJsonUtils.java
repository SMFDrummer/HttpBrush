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
import static smf.icdada.RequestType.V303;
import static smf.icdada.RequestType.V316;

/**
 * @author SMF & icdada
 * @描述: 用户库账号刷新方法类
 */
public class UserJsonUtils {
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void measure() {
        initUsersMap();
        List<Integer> userIds = getUserList();
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
            return;
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
                Future<String> future = executor.submit(() -> getResponseBody(V316, userId));
                String response316Body = future.get(3, TimeUnit.SECONDS);
                v316.setResponseBody(response316Body);
                if (!v316.isValid(0)) {
                    Log.v("账号:" + userId + " || 读取失败，正在重试…… || " + response316Body);
                    if (!v316.isValid(20013)) refresh(userId);
                } else {
                    if (v316.data.containsKey("$.p")) {
                        int fg = (int) v316.data.get("$.p.fg");
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
                Future<String> future = executor.submit(() -> getResponseBody(V303, userId, 10868));
                String response303Body = future.get(3, TimeUnit.SECONDS);
                v303.setResponseBody(response303Body);
                if (!v303.isValid(0)) {
                    Log.v("账号:" + userId + " || 读取失败，正在重试…… || " + response303Body);
                    if (!v303.isValid(20013)) {
                        refresh(userId);
                    }
                } else {
                    if (v303.data.containsKey("$.code") && v303.data.get("$.code") != null) {
                        String inviteCode = v303.data.get("$.code").toString();
                        Log.s("账号:" + userId + " || 已获取邀请码:" + inviteCode);
                        return inviteCode;
                    }
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }

    /**
     * @描述: user.json文件编辑方法，请确保运行该方法前已经初始化Users，否则会回报空指针异常
     */
    public static <T> void JsonUtil(int userId, String key, T value) {
        String filePath = System.getProperty("user.dir") + File.separator + "user.json";
        String tempFilePath = System.getProperty("user.dir") + File.separator + "temp" + File.separator + UUID.randomUUID() + ".tmp";
        try {
            lock.writeLock().lock(); // 获取写锁
            Path target = Paths.get(filePath);
            String jsonString = Files.readString(target);
            JSONObject parse = JSONObject.parseObject(jsonString);
            JSONArray usersArray = parse.getJSONArray("Users");
            JSONObject userObj = usersArray.getJSONObject(getIndex(userId));
            userObj.put(key, value);
            String formattedJson = parse.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
            try (FileWriter fileWriter = new FileWriter(tempFilePath)) {
                fileWriter.write(formattedJson);
                fileWriter.flush();
            }
            Path temp = Paths.get(tempFilePath);
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(temp); // 删除临时文件
            if (Inter.openConsole) Log.s("账号:" + userId + " || 处理完成");
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
