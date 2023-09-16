package smf.icdada;

import smf.icdada.HttpUtils.Base;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * @author SMF & icdada
 * @描述: Http 发送数据包类
 * <p>
 * 该类包含两种发送数据包方法。
 * </p>
 */
@SuppressWarnings("unused")
public class HttpSender {

    /**
     * @param isAndroid   为安卓类型布尔判断，形参值默认为 smf.icdada.Inter.isAndroid
     * @param requestBody 为数据包包体，具体配合用法可参照 {@link Base#getResponseBody} 方法实现
     * @return 返回数据包响应结果
     * @描述: 该方法为依赖自身网络环境发送数据包方法
     */
    public static String doQuest(boolean isAndroid, String requestBody) throws Exception {
        String serverUrl = isAndroid ? "http://cloudpvz2android.ditwan.cn" : "http://cloudpvz2ios.ditwan.cn";
        URI uri = new URI(serverUrl);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10000); // 设置连接超时时间为10秒
        connection.setReadTimeout(10000); // 设置读取超时时间为10秒
        return HttpPoster(requestBody, serverUrl, connection);
    }

    /**
     * @param isAndroid   为安卓类型布尔判断，形参值默认为 smf.icdada.Inter.isAndroid
     * @param requestBody 为数据包包体，具体配合用法可参照 {@link Base#getResponseBody} 方法实现
     * @param proxyHost   代理服务器域名
     * @param proxyPort   代理服务器端口
     * @return 返回数据包响应结果
     * @描述: 该方法为依赖代理服务器转发域名与端口发送数据包方法
     */
    public static String doQuest(boolean isAndroid, String requestBody, String proxyHost, int proxyPort) throws Exception {
        String serverUrl = isAndroid ? "http://cloudpvz2android.ditwan.cn" : "http://cloudpvz2ios.ditwan.cn";
        URI uri = new URI(serverUrl);
        URL url = uri.toURL();
        // 创建代理对象
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
        connection.setConnectTimeout(10000); // 设置连接超时时间为10秒
        connection.setReadTimeout(10000); // 设置读取超时时间为10秒
        return HttpPoster(requestBody, serverUrl, connection);
    }

    private static String HttpPoster(String requestBody, String serverUrl, HttpURLConnection connection) throws IOException {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "PvZ2/3.0.6.724 CFNetwork/1335.0.3.1 Darwin/21.6.0");
        connection.setRequestProperty("Connection", "close");
        connection.setRequestProperty("Host", serverUrl);
        connection.setRequestProperty("Content-Length", Integer.toString(requestBody.getBytes(StandardCharsets.UTF_8).length));
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=_{{}}_");
        connection.setDoOutput(true);
        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(requestBody);
        }
        return GetString(connection);
    }

    protected static String GetString(HttpURLConnection connection) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            int responseCode = connection.getResponseCode();
            if (responseCode >= 400 && responseCode <= 504) {
                connection.disconnect();
                return "Error: HTTP response code " + responseCode;
            }
            String line;
            if ((line = reader.readLine()) != null) {
                return line;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = connection.getInputStream().read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toString();
        } catch (IOException e) {
            connection.disconnect();
            return "HttpSenderError: " + e.getMessage();
        }
    }
}

class CreateAccountSender extends HttpSender {
    public static String doQuest(String accountType, String requestBody) throws Exception {
        String serverUrl = "http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/" + accountType;
        URI uri = new URI(serverUrl);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10000); // 设置连接超时时间为10秒
        connection.setReadTimeout(10000); // 设置读取超时时间为10秒
        return HttpPoster(requestBody, serverUrl, connection);
    }

    public static String doQuest(String accountType, String requestBody, String proxyHost, int proxyPort) throws Exception {
        String serverUrl = "http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/" + accountType;
        URI uri = new URI(serverUrl);
        URL url = uri.toURL();
        // 创建代理对象
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
        connection.setConnectTimeout(10000); // 设置连接超时时间为10秒
        connection.setReadTimeout(10000); // 设置读取超时时间为10秒
        return HttpPoster(requestBody, serverUrl, connection);
    }

    private static String HttpPoster(String requestBody, String serverUrl, HttpURLConnection connection) throws IOException {
        connection.setRequestMethod("POST");
        //connection.setRequestProperty("User-Agent", "PvZ2/3.0.6.724 CFNetwork/1335.0.3.1 Darwin/21.6.0");
        //connection.setRequestProperty("Connection", "close");
        //connection.setRequestProperty("Host", serverUrl);
        //connection.setRequestProperty("Content-Length", Integer.toString(requestBody.getBytes(StandardCharsets.UTF_8).length));
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(requestBody);
        }
        return GetString(connection);
    }
}
