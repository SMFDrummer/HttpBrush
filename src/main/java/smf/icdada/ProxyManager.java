package smf.icdada;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import static smf.icdada.HttpUtils.Base.sleep;

/**
 * @author SMF & icdada
 * @描述: 代理服务器获取及刷新类
 * <p>
 * 该类包含代理服务器获取及刷新方法。
 * </p>
 */
@SuppressWarnings("unused")
public class ProxyManager {
    public static boolean openConsole = false;

    public static void console(boolean openConsole) {
        ProxyManager.openConsole = openConsole;
    }

    private static String[] proxyGetter() throws IOException, URISyntaxException {
        int invalidRequestCount = 0;
        while (true) {
            String urlString;
            switch (Inter.proxyType) {
                case 1 -> urlString = "http://localhost:8093/get/";
                case 2 -> urlString = "http://demo.spiderpy.cn/get/";
                default -> {
                    Log.e("Back Chooser ERROR, default back Online ProxyPool!");
                    Inter.proxyType = 2;
                    urlString = "http://demo.spiderpy.cn/get/";
                }
            }

            String responseData = fetchData(urlString);
            if (responseData != null && responseData.contains("{") && responseData.contains("}") && !responseData.equals("[]")) {
                JSONObject jsonObject = JSONObject.parse(responseData);
                if (jsonObject.containsKey("proxy")) {
                    String proxyData = jsonObject.getString("proxy");
                    return proxyData.split(":");
                }
            }

            if (openConsole) Log.e("代理服务器地址获取失败，正在重新获取……");
            sleep(1000);
            if (Inter.proxyType == 1) {
                invalidRequestCount++;
            }

            if (invalidRequestCount > 100) {
                Inter.proxyType = 2;
                Log.e("Invalid Request Count TOO MANY, maybe Local doesn't work, Changed to Online ProxyPool!");
            } else if (invalidRequestCount % 2 == 0) {
                Inter.proxyType = (Inter.proxyType == 1) ? 2 : 1;
            }
        }
    }

    /**
     * @描述: 该方法为程序运行时获取代理服务器方法
     */
    public static Result proxy() {
        String tempProxyHost = "";
        int tempProxyPort = 0;
        do {
            try {
                sleep(500);
                String[] proxyData = proxyGetter();
                tempProxyHost = proxyData[0];
                tempProxyPort = Integer.parseInt(proxyData[1]);
                if (openConsole)
                    Log.v("刷新的代理服务器地址为:" + tempProxyHost + "，端口为" + tempProxyPort);
            } catch (Exception ignored) {
            }
        } while (!isProxyAvailable(tempProxyHost, tempProxyPort));
        return new Result(tempProxyHost, tempProxyPort);
    }

    private static String fetchData(String urlString) throws IOException, URISyntaxException {
        String responseString = null;
        boolean success = false;
        while (!success) {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                JSONObject jsonObject = JSONObject.parse(response.toString());
                if (jsonObject.containsKey("proxy")) {
                    success = true;
                    responseString = JSONObject.of("proxy", jsonObject.getString("proxy")).toJSONString(JSONWriter.Feature.WriteMapNullValue);
                } else if (jsonObject.getJSONObject("data").containsKey("proxy_list")) {
                    success = true;
                    String proxy = jsonObject.getJSONObject("data").getJSONArray("proxy_list").getString(0);
                    responseString = JSONObject.of("proxy", proxy).toJSONString(JSONWriter.Feature.WriteMapNullValue);
                } else {
                    sleep(1000);
                }
            } catch (Exception e) {
                sleep(1000);
            }
        }
        return responseString;
    }

    @Deprecated
    private static boolean isProxyAvailable(boolean isAndroid, String proxyHost, int proxyPort, int timeout) {
        try {
            sleep(500);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            String serverUrl = isAndroid ? "http://cloudpvz2android.ditwan.cn/index.php" : "http://cloudpvz2ios.ditwan.cn/index.php";
            URI uri = new URI(serverUrl);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                if (openConsole)
                    Log.e("拓维服务器停止响应（HttpError:" + responseCode + "），正在刷新IP……");
                return false;
            }
            return true;
        } catch (IOException | URISyntaxException e) {
            return false;
        }
    }

    private static boolean isProxyAvailable(String proxyHost, int proxyPort) {
        try {
            sleep(500);
            String responseI4CheckBody = HttpSender.doQuest(Inter.environment, RequestType.I4.getRequestBody(), proxyHost, proxyPort);
            if (responseI4CheckBody != null) {
                JSONObject jsonObject = JSONObject.parse(responseI4CheckBody);
                return jsonObject.containsKey("i") && jsonObject.getIntValue("r") == 0;
            }
            return false;
        } catch (Exception e) {
            if (openConsole) Log.e("I4校验异常，正在刷新IP……");
            return false;
        }
    }
}