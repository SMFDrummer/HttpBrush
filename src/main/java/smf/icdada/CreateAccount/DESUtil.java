package smf.icdada.CreateAccount;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class DESUtil {
    public final static char[] HEX = "0123456789ABCDEF".toCharArray();
    /**
     * 偏移变量，固定占8位字节
     */
    private final static byte[] IV_PARAMETER = {1, 2, 3, 4, 5, 6, 7, 8};
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";

    /**
     * 生成key
     *
     * @param password 密码
     * @return key
     * @throws Exception 异常
     */
    private static Key generateKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(StandardCharsets.UTF_8));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        return keyFactory.generateSecret(dks);
    }

    public static String decrypt(String in) {
        StringBuilder result = new StringBuilder();
        if (in.startsWith("head=")) {
            String[] s = in.split("&");
            for (String string : s) {
                if (string.startsWith("md5=")) {
                    result.append(string);
                } else {
                    result.append(string.split("=")[0]).append("=").append(XMLdecrypt("TwPay001", string.split("=")[1])).append("&");
                }
            }
        } else {
            result = new StringBuilder(Objects.requireNonNull(XMLdecrypt("TwPay001", in)));
        }
        return result.toString();
    }

    public static String encrypt(String in) throws Exception {
        StringBuilder result = new StringBuilder();
        String md5 = "";
        if (in.startsWith("head=")) {
            String[] s = in.split("&");
            for (String string : s) {
                if (string.startsWith("md5=")) {
                    result.append("md5=").append(md5);
                } else {
                    result.append(string.split("=")[0]).append("=").append(XMLencrypt("TwPay001", string.split("=")[1])).append("&");
                    if (string.startsWith("verifySMS") || string.startsWith("registerInfo")) {
                        md5 = MD5(string.split("=")[1] + "b0b29851-b8a1-4df5-abcb-a8ea158bea20");
                    }
                }
            }
        } else {
            result = new StringBuilder(Objects.requireNonNull(XMLencrypt("TwPay001", in)));
        }
        return result.toString();
    }

    public static String XMLencrypt(String password, String data) throws Exception {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null)
            return null;

        Key secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));

        //JDK1.8及以上可直接使用Base64，JDK1.7及以下可以使用BASE64Encoder
        //Android平台可以使用android.util.Base64
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(HEX[(aByte & 0xf0) >> 4]);
            sb.append(HEX[aByte & 0xf]);
        }
        return sb.toString();

    }

    /**
     * DES解密字符串
     *
     * @param password 解密密码，长度不能够小于8位
     * @param data     待解密字符串
     * @return 解密后内容
     */
    public static String XMLdecrypt(String password, String data) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            char[] chars = data.toUpperCase().toCharArray();
            byte[] bytes = new byte[chars.length / 2];
            for (int i = 0; i < chars.length; i += 2) {
                int m = chars[i], n = chars[i + 1];
                if (m > 60) {
                    m -= 55;
                } else {
                    m -= 48;
                }
                if (n > 60) {
                    n -= 55;
                } else {
                    n -= 48;
                }
                bytes[i / 2] = (byte) ((m << 4) + n);
            }
            return new String(cipher.doFinal(bytes), CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    public static String MD5(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] digest = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : digest) {
                if (Integer.toHexString(255 & b).length() == 1) {
                    stringBuilder.append("0").append(Integer.toHexString(255 & b));
                } else {
                    stringBuilder.append(Integer.toHexString(255 & b));
                }
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e2) {
            return "";
        }
    }
}
