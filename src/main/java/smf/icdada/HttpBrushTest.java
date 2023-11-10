package smf.icdada;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import smf.icdada.HttpUtils.Base;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpBrushTest {
    public static void main(String[] args) {
        try {
            Log.d("Debug Start");
            updateUserJson();
            Base.initUsersMap();
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
    }

    private static String XMLEncrypt(byte[] paramArrayOfByteData, byte[] paramArrayOfByteKey) {
        DESKeyGenerator desKeyGenerator = new DESKeyGenerator();
        //desKeyGenerator.init(new KeyGenerationParameters(new SecureRandom(),));
        return null;
    }

    private static void updateUserJson() {
        try {
            Log.v("请输入旧账号库文件完整路径");
            String userPath = Base.getFilePath(Scanner.String(false));
            JSONObject parse = JSON.parseObject(Files.readString(Path.of(userPath)));
            JSONArray Users = new JSONArray();
            if (parse.containsKey("Users")) Users = parse.getJSONArray("Users");
            else if (parse.containsKey("BannedUsers")) Users = parse.getJSONArray("BannedUsers");
            for (Object o : Users) {
                JSONObject userObject = (JSONObject) o;
                int userId = userObject.getIntValue("userId");
                userObject.put("userId", String.valueOf(userId));
            }
            FileWriter fileWriter = new FileWriter(userPath);
            fileWriter.write(parse.toJSONString(JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat));
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }

    }
}
