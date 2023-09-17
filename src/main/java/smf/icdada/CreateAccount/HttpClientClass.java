package smf.icdada.CreateAccount;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClientClass {
    public static String SendReqGetRes(byte[] in, String type) throws Exception {
        HttpURLConnection con;
        BufferedReader buffer;
        StringBuilder stringBuilder;
        URI uri = new URI("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/" + type);
        URL url = uri.toURL();
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setUseCaches(false);
        OutputStream os = con.getOutputStream();
        os.write(in);
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = con.getInputStream();
            stringBuilder = new StringBuilder();
            String line;
            buffer = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((line = buffer.readLine()) != null) {
                stringBuilder.append(line);
            }
            return new String(stringBuilder);
        }
        return null;
    }
}

