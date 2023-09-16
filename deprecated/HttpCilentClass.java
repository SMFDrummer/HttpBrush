
import java.io.*;
import java.math.*;
import java.net.*;
import java.security.*;

public class HttpCilentClass
{
	public static String SendReqGetRes(byte[] in,String type) throws Exception {
		HttpURLConnection con = null;
        BufferedReader buffer = null;
        StringBuffer resultBuffer = null;
		URL url = new URL("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/"+type);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		OutputStream os = con.getOutputStream();
		os.write(in);
		int responseCode = con.getResponseCode();
		if (responseCode==HttpURLConnection.HTTP_OK) {
			InputStream inputStream = con.getInputStream();
			resultBuffer = new StringBuffer();
			String line;
			buffer = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			while ((line=buffer.readLine())!=null) {
				resultBuffer.append(line);
			}
			return new String(resultBuffer);
		}
		return null;
	}
}

