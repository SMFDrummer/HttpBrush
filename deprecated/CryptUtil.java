import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class DESUtil {

	private static final byte[] IV_PARAMETER = {1, 2, 3, 4, 5, 6, 7, 8};
	private static final String ALGORITHM = "DES";
	private static final String CHARSET = "utf-8";

	private static KeyParameter generateKey(String password) throws Exception {
		return new KeyParameter(password.getBytes(CHARSET));
	}

	public static String decrypt(String in) throws Exception {
		String result = "";
		if (in.startsWith("head=")) {
			String[] s = in.split("&");
			for (int i = 0; i < s.length; i++) {
				if (s[i].startsWith("md5=")) {
					result += s[i];
				} else {
					result += s[i].split("=")[0] + "=" + XMLdecrypt("TwPay001", s[i].split("=")[1]) + "&";
				}
			}
		} else {
			result = XMLdecrypt("TwPay001", in);
		}
		return result;
	}

	public static String encrypt(String in) throws Exception {
		String result = "";
		String md5 = "";
		if (in.startsWith("head=")) {
			String[] s = in.split("&");
			for (int i = 0; i < s.length; i++) {
				if (s[i].startsWith("md5=")) {
					result += "md5=" + md5;
				} else {
					result += s[i].split("=")[0] + "=" + XMLencrypt("TwPay001", s[i].split("=")[1]) + "&";
					if (s[i].startsWith("verifySMS") || s[i].startsWith("registerInfo")) {
						md5 = MD5(s[i].split("=")[1] + "b0b29851-b8a1-4df5-abcb-a8ea158bea20");
					}
				}
			}
		} else {
			result = XMLencrypt("TwPay001", in);
		}
		return result;
	}

	public static String XMLencrypt(String password, String data) throws Exception {
		if (password == null || password.length() < 8) {
			throw new RuntimeException("加密失败，key不能小于8位");
		}
		if (data == null)
			return null;

		byte[] keyBytes = password.getBytes(CHARSET);
		byte[] dataBytes = data.getBytes(CHARSET);

		byte[] encryptedData = encryptBase64(dataBytes, keyBytes, IV_PARAMETER);

		return Base64.toBase64String(encryptedData);
	}

	public static String XMLdecrypt(String password, String data) {
		if (password == null || password.length() < 8) {
			throw new RuntimeException("加密失败，key不能小于8位");
		}
		if (data == null)
			return null;

		try {
			byte[] keyBytes = password.getBytes(CHARSET);
			byte[] dataBytes = Base64.decode(data);

			byte[] decryptedData = decryptBase64(dataBytes, keyBytes, IV_PARAMETER);

			return new String(decryptedData, CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
			return data;
		}
	}

	public static String MD5(String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(str.getBytes(CHARSET));
			byte[] digest = messageDigest.digest();
			return Hex.toHexString(digest);
		} catch (Exception e) {
			return "";
		}
	}

	private static byte[] encryptBase64(byte[] data, byte[] key, byte[] iv) throws Exception {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESEngine()));
		cipher.init(true, new ParametersWithIV(new KeyParameter(key), iv));
		byte[] out = new byte[cipher.getOutputSize(data.length)];
		int len1 = cipher.processBytes(data, 0, data.length, out, 0);
		int len2 = cipher.doFinal(out, len1);
		byte[] result = new byte[len1 + len2];
		System.arraycopy(out, 0, result, 0, result.length);
		return result;
	}

	private static byte[] decryptBase64(byte[] data, byte[] key, byte[] iv) throws Exception {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESEngine()));
		cipher.init(false, new ParametersWithIV(new KeyParameter(key), iv));
		byte[] out = new byte[cipher.getOutputSize(data.length)];
		int len1 = cipher.processBytes(data, 0, data.length, out, 0);
		int len2 = cipher.doFinal(out, len1);
		byte[] result = new byte[len1 + len2];
		System.arraycopy(out, 0, result, 0, result.length);
		return result;
	}
}
