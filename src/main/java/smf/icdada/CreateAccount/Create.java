package smf.icdada.CreateAccount;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import smf.icdada.Inter;
import smf.icdada.Log;
import smf.icdada.smfScanner;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Create {
    public static void single() {
        try {
            Log.i("默认创建渠道将会读取default中channelId的值");
            Log.v("请输入需要创建账号的手机号:");
            String phone = smfScanner.String(true, "^\\d{11}$");
            Log.v("请输入需要创建账号的密码:");
            String password = smfScanner.String(true);
            makeNewUserWithPhone(phone, password);
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void measure() {
        try {
            Log.i("默认创建渠道将会读取default中channelId的值");
            Log.v("请输入需要创建的账号数量:");
            int num = smfScanner.Int(true);
            makeNewUserQuickly(num);
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void makeNewUserQuickly(final int num) throws Exception {
        JSONObject users = new JSONObject();
        users.put("Users", new JSONArray());
        for (int i = 0; i < num; i++) {
            Log.i(String.valueOf(i + 1));
            String pass = "803666" + DESUtil.MD5((int) (Math.random() * 999999999) + "803666" + DESUtil.MD5(Math.random() * 6666666 + "")).substring(0, 10);
            String pass2 = DESUtil.MD5(pass);
            String r = "head={\"appId\":" + Inter.appId + ",\"channelId\":" + Inter.channelId + ",\"sdkVersion\":\"2.0.0\"}&registerInfo={\"password\":\"" + pass2 + "\",\"registerImei\":\"d4a69032b29bf0cb\",\"smsVerifyCode\":\"466\"}&md5=b97259c1c0b6a077e7b7c6d89e3cf8de";
            JSONObject user = JSONObject.parse(DESUtil.decrypt(JSONObject.parse(DESUtil.decrypt(Objects.requireNonNull(HttpClientClass.SendReqGetRes(DESUtil.encrypt(r).getBytes(), "oneKeyRegister")))).getString("content")));
            user.put("password", pass);
            user.put("severId", String.valueOf(Inter.appId) + Inter.channelId);
            users.getJSONArray("Users").add(user);
        }
        String newUserFolderUrl = System.getProperty("user.dir") + File.separator + "NewUsers";
        File newUserFolder = new File(newUserFolderUrl);
        if (!newUserFolder.exists()) newUserFolder.mkdir();
        String newUserFileUrl = System.getProperty("user.dir") + File.separator + "NewUsers" + File.separator + "NewUsers_" + DateTimeFormatter.ofPattern("MM-dd-HH-mm").format(LocalDateTime.now()) + ".json";
        File newUserFile = new File(newUserFileUrl);
        try (FileWriter fileWriter = new FileWriter(newUserFile)) {
            fileWriter.write(users.toJSONString(JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat));
            fileWriter.flush();
        }
        Log.s("配置文件已生成！文件名为:" + newUserFile.getName());
        Log.i("生成路径位于:" + newUserFile.getPath());
        System.exit(0);
    }

    private static void makeNewUserWithPhone(final String phone, final String password) throws Exception {
        Log.a(DESUtil.decrypt(Objects.requireNonNull(HttpClientClass.SendReqGetRes(DESUtil.encrypt("head={\"appId\":" + Inter.appId + ",\"channelId\":" + Inter.channelId + ",\"sdkVersion\":\"2.0.0\"}&verifySMS={\"phone\":\"" + phone + "\",\"smsType\":\"register\"}&md5=c871511533afd4154ac5576e20a5bdcd").getBytes(), "genVerifySms"))));
        // 创建一个固定大小的线程池，指定并发执行的线程数
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        // 定义一个整数变量i，初始值为0，每次循环后加1，直到达到9999或者注册成功为止
        for (int i = 0; i <= 9999; i++) {
            // 对于每个i，提交一个实现了Callable接口的任务，该任务可以返回一个结果
            int finalI = i;
            Future<String> future = executorService.submit(() -> {
                // 定义一个字符串变量y，初始值为空
                String y = "";
                // 如果i小于1000，那么在y的前面加上一个"0"。如果i小于100，那么再加上一个"0"。如果i小于10，那么再加上一个"0"。这样做是为了保证y的长度为4位
                if (finalI < 1000) {
                    y += "0";
                    if (finalI < 100) {
                        y += "0";
                        if (finalI < 10) {
                            y += "0";
                        }
                    }
                }
                // 将i转换为字符串，并拼接到y的后面。这样y就是一个四位数的验证码
                y += finalI;
                // 使用DESUtil.encrypt方法对一个包含密码、手机号、注册设备号和验证码的字符串进行加密，并发送给HttpClientClass.SendReqGetRes方法，得到一个加密后的响应字符串
                String response = HttpClientClass.SendReqGetRes(DESUtil.encrypt("head={\"appId\":" + Inter.appId + ",\"channelId\":" + Inter.channelId + ",\"sdkVersion\":\"2.0.0\"}&registerInfo={\"password\":\"" + DESUtil.MD5(password) + "\",\"phone\":\"" + phone + "\",\"registerImei\":\"b7cfe56034188670\",\"smsVerifyCode\":\"" + y + "\"}&md5=e6cb038859b93a01e5775be34f6238d4").getBytes(), "register");
                // 使用DESUtil.decrypt方法对响应字符串进行解密，并将其转换为一个JSON对象，从中获取content字段的值，赋给一个字符串变量s
                // 返回注册的结果
                return JSONObject.parse(DESUtil.decrypt(Objects.requireNonNull(response))).getString("content");
            });
            try {
                // 获取任务的返回值，并根据返回值判断是否注册成功
                String result = future.get();
                if (result.equals("用户已存在")) {
                    Log.w("用户已存在");
                    System.exit(0);
                } else if (!result.equals("验证码错误")) {
                    Log.s("创建成功");
                    executorService.shutdownNow();
                } else if (i % 100 == 0) {
                    Log.v(String.valueOf(i));
                }
            } catch (Exception e) {
                // 处理异常
                Log.w(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
