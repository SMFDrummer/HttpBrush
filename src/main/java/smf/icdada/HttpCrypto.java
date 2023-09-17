package smf.icdada;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author SMF & icdada
 * @描述: Http 数据包加解密类
 * <p>
 * 该类包含完整的 Http 数据包加解密方法，仅调用使用。
 * </p>
 */

@SuppressWarnings("ALL")
public class HttpCrypto {

    protected static final byte[] IV_PARAMETER = {1, 2, 3, 4, 5, 6, 7, 8};
    private static final char[] BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();

    /**
     * @param identifier           数据包标识，如 "V303"
     * @param toEncryptRequestBody 待加密的请求包部分，格式为标准的 Json
     * @return 加密后的请求数据包
     * @描述: 请求加密方法
     */
    public static String encryptREQ(String identifier, String toEncryptRequestBody) {
        JSONObject parse = JSONObject.parse(toEncryptRequestBody);
        if (parse.containsKey("t")) {
            parse = parse.getJSONObject("t");
        }
        toEncryptRequestBody = GZIPEncode(parse).toJSONString(JSONWriter.Feature.WriteMapNullValue);
        return
                "--_{{}}_\r\nContent-Disposition: form-data; name=\"req\"\r\n\r\n" +
                        identifier +
                        "\r\n--_{{}}_\r\nContent-Disposition: form-data; name=\"e\"\r\n\r\n" +
                        toBase64(encryptBase64(toEncryptRequestBody.getBytes(), getKey(identifier), getIv(identifier))) +
                        "\r\n--_{{}}_\r\nContent-Disposition: form-data; name=\"ev\"\r\n\r\n1\r\n--_{{}}_";
    }

    /**
     * @param toEncryptRequestBody 待加密的请求包部分，格式为含有完整 irt 结构的标准 Json
     * @return 加密后的请求数据包
     * @描述: 请求加密方法 - 自动
     */
    public static String encryptREQ(String toEncryptRequestBody) {
        String identifier;
        JSONObject parse = JSONObject.parse(toEncryptRequestBody);
        if (parse.containsKey("i")) {
            identifier = parse.getString("i");
        } else {
            return toEncryptRequestBody;
        }
        if (parse.containsKey("t")) {
            parse = parse.getJSONObject("t");
            toEncryptRequestBody = GZIPEncode(parse).toJSONString(JSONWriter.Feature.WriteMapNullValue);
        }
        return
                "--_{{}}_\r\nContent-Disposition: form-data; name=\"req\"\r\n\r\n" +
                        identifier +
                        "\r\n--_{{}}_\r\nContent-Disposition: form-data; name=\"e\"\r\n\r\n" +
                        toBase64(encryptBase64(toEncryptRequestBody.getBytes(), getKey(identifier), getIv(identifier))) +
                        "\r\n--_{{}}_\r\nContent-Disposition: form-data; name=\"ev\"\r\n\r\n1\r\n--_{{}}_";
    }


    /**
     * @param identifier           数据包标识，如 "V303"
     * @param toDecryptRequestBody 待解密的请求包部分，格式为包含完整数据包标识及换行符的数据包体
     * @return 解密后的请求数据包
     * @描述: 请求解密方法
     */
    public static String decryptREQ(String identifier, String toDecryptRequestBody) {
        String[] arrayOfString = toDecryptRequestBody.split("\n");
        arrayOfString[3] = arrayOfString[3].replaceAll("\r", "");
        arrayOfString[7] = arrayOfString[7].replaceAll("\r", "");
        String decryptREQBody = new String(decryptBase64(toEnByte(arrayOfString[7]), getKey(identifier), getIv(identifier)));
        JSONObject jsonObject = JSONObject.parseObject(decryptREQBody);
        JSONObject parse = JSONObject.parse("{}");
        parse.put("i", identifier);
        parse.put("r", 0);
        parse.put("t", GZIPDecode(jsonObject));
        return parse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * @param toDecryptRequestBody 待解密的请求包部分，格式为包含完整数据包标识及换行符的数据包体
     * @return 解密后的请求数据包
     * @描述: 请求解密方法 - 自动
     */
    public static String decryptREQ(String toDecryptRequestBody) {
        String[] arrayOfString = toDecryptRequestBody.split("\n");
        String identifier = arrayOfString[3].replaceAll("\r", "");
        arrayOfString[7] = arrayOfString[7].replaceAll("\r", "");
        String decryptREQBody = new String(decryptBase64(toEnByte(arrayOfString[7]), getKey(identifier), getIv(identifier)));
        JSONObject jsonObject = JSONObject.parseObject(decryptREQBody);
        JSONObject parse = JSONObject.parse("{}");
        parse.put("i", identifier);
        parse.put("r", 0);
        parse.put("t", GZIPDecode(jsonObject));
        return parse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
    }


    /**
     * @param identifier            数据包标识，如 "V303"
     * @param toEncryptResponseBody 待加密的响应包部分，格式为标准的 Json，且必须包含完整 ird 结构
     * @return 加密后的响应数据包
     * @描述: 响应加密方法
     */
    public static String encryptRES(String identifier, String toEncryptResponseBody) {
        if (!JSON.isValid(toEncryptResponseBody)) {
            return null;
        }
        JSONObject parse = JSONObject.parse(toEncryptResponseBody);
        JSONObject encryptParse = JSONObject.parse("{}");
        String str = parse.getString("i");
        int i = parse.getIntValue("r");
        if (parse.containsKey("d")) {
            JSONObject d = parse.getJSONObject("d");
            parse.put("d", GZIPEncode(d));
            toEncryptResponseBody = parse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
        }
        byte[] arrayOfByte = toEncryptResponseBody.getBytes();
        encryptParse.put("i", str);
        encryptParse.put("r", i);
        encryptParse.put("e", toBase64(encryptBase64(arrayOfByte, getKey(identifier), getIv(identifier))));
        return encryptParse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * @param toEncryptResponseBody 待加密的响应包部分，格式为标准的 Json，且必须包含完整 ird 结构
     * @return 加密后的响应数据包
     * @描述: 响应加密方法 - 自动
     */
    public static String encryptRES(String toEncryptResponseBody) {
        if (!JSON.isValid(toEncryptResponseBody)) {
            return null;
        }
        JSONObject parse = JSONObject.parse(toEncryptResponseBody);
        JSONObject encryptParse = JSONObject.parse("{}");
        String identifier = parse.getString("i");
        int i = parse.getIntValue("r");
        if (parse.containsKey("d") && JSON.isValidObject(parse.getString("d"))) {
            JSONObject d = parse.getJSONObject("d");
            parse.put("d", GZIPEncode(d));
            toEncryptResponseBody = parse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
        }
        byte[] arrayOfByte = toEncryptResponseBody.getBytes();
        encryptParse.put("i", identifier);
        encryptParse.put("r", i);
        encryptParse.put("e", toBase64(encryptBase64(arrayOfByte, getKey(identifier), getIv(identifier))));
        return encryptParse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * @param identifier            数据包标识，如 "V303"
     * @param toDecryptResponseBody 待解密的响应包部分，格式为标准的 Json，且必须包含完整 ire 结构
     * @return 解密后的响应数据包
     * @描述: 响应解密方法
     */
    public static String decryptRES(String identifier, String toDecryptResponseBody) {
        if (!JSON.isValid(toDecryptResponseBody)) {
            return "{\"r\":12202}";
        }
        JSONObject parse = JSONObject.parse(toDecryptResponseBody);
        if (parse.containsKey("e")) {
            String decryptRESBody = new String(decryptBase64(toEnByte(parse.getString("e")), getKey(identifier), getIv(identifier)));
            JSONObject jsonObject = JSONObject.parseObject(decryptRESBody);
            if (jsonObject.containsKey("d") && JSON.isValidObject(jsonObject.getString("d"))) {
                JSONObject d = jsonObject.getJSONObject("d");
                jsonObject.put("d", GZIPDecode(d));
                decryptRESBody = jsonObject.toJSONString(JSONWriter.Feature.WriteMapNullValue);
            }
            return decryptRESBody;
        } else {
            return parse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
        }
    }

    /**
     * @param toDecryptResponseBody 待解密的响应包部分，格式为标准的 Json，且必须包含完整 ire 结构
     * @return 解密后的响应数据包
     * @描述: 响应解密方法 - 自动
     */
    public static String decryptRES(String toDecryptResponseBody) {
        if (!JSON.isValid(toDecryptResponseBody)) {
            return "{\"r\":12202}";
        }
        JSONObject parse = JSONObject.parse(toDecryptResponseBody);
        if (parse.containsKey("i")) {
            String identifier = parse.getString("i");
            if (parse.containsKey("e")) {
                String decryptRESBody = new String(decryptBase64(toEnByte(parse.getString("e")), getKey(identifier), getIv(identifier)));
                JSONObject jsonObject = JSONObject.parseObject(decryptRESBody);
                if (jsonObject.containsKey("d") && JSON.isValidObject(jsonObject.getString("d"))) {
                    JSONObject d = jsonObject.getJSONObject("d");
                    jsonObject.put("d", GZIPDecode(d));
                    decryptRESBody = jsonObject.toJSONString(JSONWriter.Feature.WriteMapNullValue);
                }
                return decryptRESBody;
            }
        }
        return parse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * @param parse 待处理的数据包包体
     * @return 处理后的 JSONObject
     * @描述: GZIP 数据段加密方法
     */
    private static JSONObject GZIPEncode(JSONObject parse) {
        if (parse.containsKey("pr") && JSON.isValidObject(parse.getString("pr")) && parse.getJSONObject("pr").containsKey("sd")) {
            String pr = parse.getString("pr");
            if (JSON.isValidObject(pr)) {
                parse.put("m", new String(getMD5(pr), StandardCharsets.UTF_8));
                parse.put("pr", GZIPEnCrypto(pr));
            }
        }
        if (
                parse.containsKey("ri") &&
                        JSON.isValidObject(parse.getString("ri")) &&
                        JSON.isValid(parse.getJSONObject("ri").getString("pl"))
        ) {
            String pl = parse.getJSONObject("ri").getString("pl");
            if (JSON.isValid(pl)) {
                parse.getJSONObject("ri").put("pl", GZIPEnCrypto(pl));
            }
        }
        return parse;
    }

    /**
     * @param parse 待处理的数据包包体
     * @return 处理后的 JSONObject
     * @描述: GZIP 数据段解密方法
     */
    private static JSONObject GZIPDecode(JSONObject parse) {
        if (parse.isEmpty()) return new JSONObject();
        if (parse.containsKey("pr") && parse.getString("pr").startsWith("H4sIA")) {
            String pr = parse.getString("pr");
            if (!JSON.isValid(pr)) {
                parse.put("pr", GZIPDeCrypto(pr));
            }
        }
        if (
                parse.containsKey("ri") &&
                        JSON.isValidObject(parse.getString("ri")) &&
                        parse.getJSONObject("ri").containsKey("pl") &&
                        parse.getJSONObject("ri").getString("pl").startsWith("H4sIA")
        ) {
            String pl = parse.getJSONObject("ri").getString("pl");
            if (!JSON.isValid(pl)) {
                parse.getJSONObject("ri").put("pl", GZIPDeCrypto(pl));
            }
        }
        return parse;
    }

    /**
     * @param paramArrayOfbyte1 待解密的数据
     * @param paramArrayOfbyte2 密钥
     * @param paramArrayOfbyte3 初始化向量
     * @return 解密后的原始数据
     * @描述: 这个方法用于解密Base64编码的数据。
     */
    public static byte[] decryptBase64(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
        PaddedBufferedBlockCipher paddedBufferedBlockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new RijndaelEngine(192)), new ZeroBytePadding());
        ParametersWithIV parametersWithIV = new ParametersWithIV(new KeyParameter(paramArrayOfbyte2), paramArrayOfbyte3);
        paramArrayOfbyte1 = DecryptRTON(paramArrayOfbyte1, paddedBufferedBlockCipher, parametersWithIV);
        return paramArrayOfbyte1;
    }

    /**
     * @param paramArrayOfbyte               待解密的数据
     * @param paramPaddedBufferedBlockCipher PaddedBufferedBlockCipher对象
     * @param paramParametersWithIV          初始化向量
     * @return 解密后的原始数据
     * @描述: 这个方法是decryptBase64的辅助方法，它初始化cipher并调用CD方法进行解密
     */
    protected static byte[] DecryptRTON(byte[] paramArrayOfbyte, PaddedBufferedBlockCipher paramPaddedBufferedBlockCipher, ParametersWithIV paramParametersWithIV) {
        try {
            paramPaddedBufferedBlockCipher.init(false, paramParametersWithIV);
            return CD(paramPaddedBufferedBlockCipher, paramArrayOfbyte);
        } catch (InvalidCipherTextException invalidCipherTextException) {
            throw new RuntimeException(invalidCipherTextException);
        }
    }

    /**
     * @param paramPaddedBufferedBlockCipher PaddedBufferedBlockCipher对象
     * @param paramArrayOfbyte               待处理的数据
     * @return 处理后的数据
     * @描述: 这个方法处理cipher的解密或加密过程，它接受一个cipher和待处理的数据，然后返回处理后的数据
     */
    private static byte[] CD(PaddedBufferedBlockCipher paramPaddedBufferedBlockCipher, byte[] paramArrayOfbyte) throws InvalidCipherTextException {
        int i = paramPaddedBufferedBlockCipher.getOutputSize(paramArrayOfbyte.length);
        byte[] arrayOfByte = new byte[i];
        i = paramPaddedBufferedBlockCipher.processBytes(paramArrayOfbyte, 0, paramArrayOfbyte.length, arrayOfByte, 0);
        int j = paramPaddedBufferedBlockCipher.doFinal(arrayOfByte, i);
        return Arrays.copyOfRange(arrayOfByte, 0, i + j);
    }

    /**
     * @param paramArrayOfbyte1 待加密的数据
     * @param paramArrayOfbyte2 密钥
     * @param paramArrayOfbyte3 初始化向量
     * @return 加密后的Base64编码数据
     * @描述: 这个方法用于加密数据并返回Base64编码的结果
     */
    public static byte[] encryptBase64(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
        PaddedBufferedBlockCipher paddedBufferedBlockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new RijndaelEngine(192)), new ZeroBytePadding());
        ParametersWithIV parametersWithIV = new ParametersWithIV(new KeyParameter(paramArrayOfbyte2), paramArrayOfbyte3);
        paramArrayOfbyte1 = EncryptRTON(paramArrayOfbyte1, paddedBufferedBlockCipher, parametersWithIV);
        return paramArrayOfbyte1;
    }

    /**
     * @param paramArrayOfbyte               待加密的数据
     * @param paramPaddedBufferedBlockCipher PaddedBufferedBlockCipher对象
     * @param paramParametersWithIV          初始化向量
     * @return 加密后的数据
     * @描述: 这个方法是encryptBase64的辅助方法，它初始化cipher并调用CD方法进行加密
     */
    protected static byte[] EncryptRTON(byte[] paramArrayOfbyte, PaddedBufferedBlockCipher paramPaddedBufferedBlockCipher, ParametersWithIV paramParametersWithIV) {
        try {
            paramPaddedBufferedBlockCipher.init(true, paramParametersWithIV);
            return CD(paramPaddedBufferedBlockCipher, paramArrayOfbyte);
        } catch (InvalidCipherTextException invalidCipherTextException) {
            throw new RuntimeException(invalidCipherTextException);
        }
    }

    /**
     * @param paramArrayOfbyte 待编码的字节数组
     * @return Base64编码的字符串
     * @描述: 这个方法接受一个字节数组并返回其Base64编码的字符串表示形式
     */
    protected static String toBase64(byte[] paramArrayOfbyte) {
        int[] arrayOfInt = new int[paramArrayOfbyte.length];
        int b = 0;
        while (b < paramArrayOfbyte.length) {
            if (paramArrayOfbyte[b] < 0) {
                arrayOfInt[b] = paramArrayOfbyte[b] + 256;
            } else {
                arrayOfInt[b] = paramArrayOfbyte[b];
            }
            b++;
        }
        StringBuilder stringBuilder = new StringBuilder();
        b = 0;
        while (b < paramArrayOfbyte.length) {
            stringBuilder.append(BASE[arrayOfInt[b] >> 2]);
            stringBuilder.append(BASE[((arrayOfInt[b] & 0x3) << 4) + (arrayOfInt[b + 1] >> 4)]);
            stringBuilder.append(BASE[((arrayOfInt[b + 1] & 0xF) << 2) + (arrayOfInt[b + 2] >> 6)]);
            stringBuilder.append(BASE[arrayOfInt[b + 2] & 0x3F]);
            b += 3;
        }
        return stringBuilder.toString();
    }

    /**
     * @param paramString Base64编码的字符串
     * @return 解码后的字节数组
     * @描述: 这个方法接受一个Base64编码的字符串并返回其字节数组表示形式
     */
    protected static byte[] toEnByte(String paramString) {
        char[] arrayOfChar = paramString.toCharArray();
        byte[] arrayOfByte = new byte[3 * arrayOfChar.length / 4];
        int b = 0;
        while (true) {
            if (b >= arrayOfChar.length) {
                return arrayOfByte;
            }
            int[] arrayOfInt = new int[4];
            arrayOfInt[0] = charIndex(arrayOfChar[b]);
            arrayOfInt[1] = charIndex(arrayOfChar[b + 1]);
            arrayOfInt[2] = charIndex(arrayOfChar[b + 2]);
            arrayOfInt[3] = charIndex(arrayOfChar[b + 3]);
            arrayOfByte[b / 4 * 3] = (byte) ((arrayOfInt[0] << 2) + (arrayOfInt[1] >> 4));
            arrayOfByte[b / 4 * 3 + 1] = (byte) (((arrayOfInt[1] & 0xF) << 4) + (arrayOfInt[2] >> 2));
            arrayOfByte[b / 4 * 3 + 2] = (byte) (((arrayOfInt[2] & 0x3) << 6) + arrayOfInt[3]);
            b += 4;
        }
    }

    /**
     * @param paramChar 输入的字符
     * @return 字符在Base64编码中的索引位置
     * @描述: 这个方法接受一个字符并返回其在Base64编码中的索引位置
     */
    protected static int charIndex(char paramChar) {
        char c = paramChar;
        if ('A' <= c && c <= 'Z') {
            return c - 65;
        }
        if ('a' <= c && c <= 'z') {
            return c - 71;
        }
        if ('0' <= c && c <= '9') {
            return c + 4;
        }
        if (c == '-') {
            paramChar = '>';
            return paramChar;
        }
        if (c == '_') {
            paramChar = '?';
        }
        return paramChar;
    }

    /**
     * @param identifier 为数据包标识，如 "V303"
     * @return 密钥
     * @描述: 该方法为秘钥计算方法，此方法会根据数据包标识自动计算密钥
     */
    public static byte[] getKey(String identifier) {
        String toBeHashed = "`jou*" + identifier + ")xoj'";
        return getMD5(toBeHashed);
    }

    /**
     * @param toBeHashed 待计算的字符串
     * @return 32位保持小写 MD5
     * @描述: 该方法为 MD5 计算方法，此方法会根据字符串自动计算 MD5
     */
    public static byte[] getMD5(String toBeHashed) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(toBeHashed.getBytes());
            byte[] digest = messageDigest.digest();
            return Hex.toHexString(digest).getBytes();
        } catch (Exception ignored) {
            return new String().getBytes();
        }
    }

    /**
     * @param identifier 为数据包标识，如 "V303"
     * @return 偏移
     * @描述: 该方法为偏移计算方法，此方法会根据数据包标识自动计算偏移
     */
    public static byte[] getIv(String identifier) {
        byte[] keyBytes = getKey(identifier);
        String key = new String(keyBytes, StandardCharsets.UTF_8);
        int param = Integer.parseInt(identifier.replaceAll("\\D", ""));
        String iv = key.substring(param % 7, Math.min(key.length(), param % 7 + 24));
        return iv.getBytes();
    }

    /**
     * @param originalString 为GZIP明文，格式为标准JSON
     * @return 加密后的GZIP数据包
     * @描述: 该方法为GZIP数据包编码方法
     */
    private static String GZIPEnCrypto(String originalString) {
        try {
            String encodedString;
            boolean isAndroid = true;
            Object parse = JSON.parse(originalString);
            if (parse instanceof JSONObject jsonObject) {
                if (jsonObject.containsKey("sd")) {
                    JSONObject sd = jsonObject.getJSONObject("sd");
                    isAndroid = sd.get("ffc").toString().length() == 5;
                }
            }
            byte[] encodedBytes = Base64.encode(originalString.getBytes());
            if (isAndroid) {
                encodedString = "\"" + replaceCharactersBack(new String(encodedBytes, StandardCharsets.UTF_8)) + "\"";
            } else {
                encodedString = replaceCharactersBack(new String(encodedBytes, StandardCharsets.UTF_8));
            }
            return replaceCharactersBack(Objects.requireNonNull(encode(encodedString)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param GZIPString 为GZIP密文
     * @return 解码后的GZIP数据包
     * @描述: 该方法为GZIP数据包解码方法
     */
    private static Object GZIPDeCrypto(String GZIPString) {
        String base64GzipString = replaceCharacters(GZIPString);//字符替换
        String decodedString = decode(base64GzipString);//GZIP解码
        if (decodedString != null) {
            if (decodedString.startsWith("\"") && decodedString.endsWith("\"")) {
                decodedString = decodedString.substring(1, decodedString.length() - 1);//有引号去掉
            }
            byte[] decodedBytes = Base64.decode(replaceCharacters(decodedString));
            decodedString = new String(decodedBytes);//先字符替换然后base64解码
        }
        return JSON.parse(decodedString);
    }

    /**
     * @param base64GzipString 为GZIP密文
     * @return 解密后的GZIP数据包
     * @描述: 该方法为GZIP数据包解密方法
     */
    protected static String decode(String base64GzipString) {
        try {
            // 解码Base64字符串
            byte[] base64DecodedBytes = Base64.decode(base64GzipString);
            // 解压缩Gzip数据
            ByteArrayInputStream inputStream = new ByteArrayInputStream(base64DecodedBytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            // 转换为可读的字符串
            String decodedString = outputStream.toString(StandardCharsets.UTF_8);
            // 关闭流
            gzipInputStream.close();
            outputStream.close();
            return decodedString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param decodedString 为GZIP明文
     * @return 加密后的GZIP数据包
     * @描述: 该方法为GZIP数据包加密方法
     */
    protected static String encode(String decodedString) {
        try {
            byte[] originalBytes = decodedString.getBytes(StandardCharsets.UTF_8);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(originalBytes);
            gzipOutputStream.close();
            byte[] gzipBytes = byteArrayOutputStream.toByteArray();
            String base64GzipString = Base64.toBase64String(gzipBytes);
            byteArrayOutputStream.close();
            return base64GzipString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected static String replaceCharacters(String input) {
        return input.replace(",", "=")
                .replace("-", "+")
                .replace("_", "/");
    }

    protected static String replaceCharactersBack(String input) {
        return input.replace("=", ",")
                .replace("+", "-")
                .replace("/", "_");
    }
}
