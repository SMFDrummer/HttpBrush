package smf.icdada.Deprecated;

import com.alibaba.fastjson2.JSONObject;
import smf.icdada.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static smf.icdada.HttpUtils.base.sleep;
import static smf.icdada.ProxyManager.proxy;

/**
 * @author SMF & icdada
 * @描述: Http 方法实现类
 * <p>
 * 该类包含所有需整合实现 Http 方法。
 * </p>
 * @类全局变量: ui 账号 ui
 * @类全局变量: sk 账号 sk
 * @类全局变量: fg 账号钻石验证值
 * @类全局变量: g1 账号钻石验证值
 * @类全局变量: httpSended 已成功实现方法总计数
 */
@java.lang.Deprecated
public class HttpUtils {
    static final int sleepMiMillions = Inter.sleepMillions / 10;
    private static final ThreadLocal<Integer> fgThreadLocal = new ThreadLocal<>();
    static String ui, sk;
    static int fg;
    static int g1;
    static int httpSended = 0;

    /**
     * @描述: 该方法为钻石刷取方法
     */
    public static void SendPackage() {
        ExecutorService executorService = Executors.newFixedThreadPool(12);
        String[] response316GemBody = new String[1];
        int i;
        while (httpSended < Inter.count) {
            for (i = 0; i <= Inter.count; i++) {
                List<Future<String>> futures = new ArrayList<>();

                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.RELOAD.getRequestBody(ui, sk))));
                sleep(Inter.sleepMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.ENTER.getRequestBody(ui, sk))));
                sleep(Inter.sleepMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.REQ0.getRequestBody(ui, sk))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.REQ1.getRequestBody(ui, sk))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.REQ2.getRequestBody(ui, sk))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.REQ3.getRequestBody(ui, sk))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.REQ4.getRequestBody(ui, sk))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.REQ5.getRequestBody(ui, sk))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.REQ6.getRequestBody(ui, sk))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.REQ7.getRequestBody(ui, sk))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCrypto(RequestType.PASS.getRequestBody(ui, sk))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> response316GemBody[0] = httpGetRESCrypto(RequestType.GET.getRequestBody(ui, sk))));
                sleep(Inter.sleepMillions);
                boolean isTimeOut = false;
                int error = 0;
                for (Future<String> future : futures) {
                    try {
                        String response = future.get(3, TimeUnit.SECONDS);
                        //System.out.println(response);
                        // 处理响应
                        if (JSONObject.parse(response).getIntValue("r") != 0) {
                            System.out.println(response);
                            if (JSONObject.parse(response).getIntValue("r") == 12202) {
                                error++;
                            }
                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        System.out.println("\033[31m" + "请求超时，正在退出循环并重新发起……" + "\033[0m");
                        isTimeOut = true;
                        break;
                    }
                }
                if (error != 0) {
                    System.out.println("\033[33m" + "正在刷新各项数值，请稍后……" + "\033[0m");
                    proxy();
                    uiskRefresher();
                    continue;
                } else if (isTimeOut) {
                    proxy();
                } else {
                    httpSended = httpSended + 1;
                }
                if (response316GemBody[0] != null) {
                    JSONObject jsonObject = JSONObject.parse(response316GemBody[0]);
                    if (jsonObject.containsKey("r") && jsonObject.containsKey("d")) {
                        if (jsonObject.getIntValue("r") == 0) {
                            JSONObject dObject = jsonObject.getJSONObject("d");
                            JSONObject pObject = dObject.getJSONObject("p");
                            fg = pObject.getIntValue("fg");
                            g1 = pObject.getIntValue("g1");
                        }
                    } else {
                        fg = 0;
                        g1 = 0;
                    }
                } else {
                    fg = 0;
                    g1 = 0;
                }
                System.out.print("\033[1A"); // 将光标向上移动一行
                System.out.print("\033[2K"); // 清除当前行
                System.out.print("\033[0G"); // 将光标移动到当前行的首位
                System.out.print("\033[32m" + "已执行" + (i + 1) + "次" + "，成功" + httpSended + "次" + " || 当前钻石：fg值：" + fg + "，g1值：" + g1 + "\n" + "\033[0m");
                if (fg > Inter.maxGem || g1 > Inter.maxGem) {
                    System.out.println("\033[31m" + "\n检测到钻石值超出安全范围，程序即将停止" + "\033[0m");
                    executorService.shutdown();
                    System.exit(0);
                }
            }
        }
    }

    public static void uiskRefresher() {
        String response202BodyString;
        boolean success = false;
        Result proxy = proxy();
        String proxyHost = proxy.getProxyHost();
        int proxyPort = proxy.getProxyPort();
        try {
            while (!success) {
                sleep(5000);
                response202BodyString = HttpSender.doQuest(Inter.isAndroid, RequestBodyBuilder.request202Body, proxyHost, proxyPort);
                if (!response202BodyString.contains("{") || !response202BodyString.contains("}") || !response202BodyString.contains("e") || !response202BodyString.contains("r")) {
                    System.out.println("\033[31m" + "V202返回数据包不完整，正在重试……" + "\033[0m");
                    proxy();
                }
                String response202Body = HttpCrypto.decryptRES(response202BodyString);
                if (response202Body != null) {
                    JSONObject jsonObject = JSONObject.parse(response202Body);
                    if (jsonObject.containsKey("r")) {
                        if (jsonObject.getIntValue("r") == 20013) {
                            System.out.println("\033[31m" + "V202发送数据包出现20013错误，正在重试……" + "\033[0m");
                            proxy();
                        }
                    }
                    JSONObject dObject = jsonObject.getJSONObject("d");
                    ui = dObject.getString("ui");
                    sk = dObject.getString("sk");
                    System.out.println("\033[34m" + "ui:" + ui + "\033[0m");
                    System.out.println("\033[34m" + "sk:" + sk + "\n" + "\033[0m");
                    success = true;
                } else {
                    System.out.println("\033[31m" + "V202返回数据包为空，正在重试……" + "\033[0m");
                    proxy();
                }
            }
        } catch (Exception e) {
            System.out.println("\033[31m" + "服务器已关闭已有链接，正在重试……" + "\033[0m");
            proxy();
            uiskRefresher();
            SendPackage();
        }
    }

    public static String httpGetRESCrypto(String toEncryptRequestBody) throws Exception {
        Result proxy = proxy();
        String proxyHost = proxy.getProxyHost();
        int proxyPort = proxy.getProxyPort();
        return HttpCrypto.decryptRES(
                HttpSender.doQuest(
                        Inter.isAndroid,
                        HttpCrypto.encryptREQ(
                                toEncryptRequestBody
                        ),
                        proxyHost,
                        proxyPort));
    }

    public static String httpGetRESCryptoForBatch(String proxyHost, int proxyPort, String toEncryptRequestBody) throws Exception {
        return HttpCrypto.decryptRES(
                HttpSender.doQuest(
                        Inter.isAndroid,
                        HttpCrypto.encryptREQ(
                                toEncryptRequestBody
                        ),
                        proxyHost,
                        proxyPort));
    }

    public static void SendPackageForBatch(String ui, String sk, int userId, String proxyHost, int proxyPort) {
        int fg = fgThreadLocal.get() == null ? 0 : fgThreadLocal.get();
        int httpSended = 0;
        String uiForBatch = ui;
        String skForBatch = sk;
        String proxyHostForBatch = proxyHost;
        int proxyPortForBatch = proxyPort;
        ExecutorService executorService = Executors.newFixedThreadPool(12);
        String[] response316GemBody = new String[1];
        int i;
        while (httpSended < Inter.count) {
            for (i = 0; i <= Inter.count; i++) {
                String finalUiForBatch = uiForBatch;
                String finalSkForBatch = skForBatch;
                String finalProxyHostForBatch = proxyHostForBatch;
                int finalProxyPortForBatch = proxyPortForBatch;

                List<Future<String>> futures = new ArrayList<>();

                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.RELOAD.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(Inter.sleepMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.ENTER.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(Inter.sleepMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.REQ0.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.REQ1.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.REQ2.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.REQ3.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.REQ4.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.REQ5.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.REQ6.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.REQ7.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.PASS.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(sleepMiMillions);
                futures.add(executorService.submit(() -> response316GemBody[0] = httpGetRESCryptoForBatch(finalProxyHostForBatch, finalProxyPortForBatch, RequestType.GET.getRequestBody(finalUiForBatch, finalSkForBatch))));
                sleep(Inter.sleepMillions);
                boolean isTimeOut = false;
                int error = 0;
                for (Future<String> future : futures) {
                    try {
                        String response = future.get(3, TimeUnit.SECONDS);
                        // 处理响应
                        if (JSONObject.parse(response).getIntValue("r") != 0) {
                            System.out.println("\033[33m" + "账号：" + userId + "\033[0m" + " || " + response + " || " + "\033[32m" + "当前钻石：" + fg + "\033[0m");
                            if (JSONObject.parse(response).getIntValue("r") != 12202) {
                                error++;
                            }
                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        //System.out.println("\033[31m" +"账号："+ userId +"请求超时，正在退出循环并重新发起……" + "\033[0m");
                        isTimeOut = true;
                        break;
                    }
                }
                if (error != 0) {
                    //System.out.println("\033[33m" + "正在刷新各项数值，请稍后……" + "\033[0m");
                    Result resultProxy = proxy();
                    proxyHostForBatch = resultProxy.getProxyHost();
                    proxyPortForBatch = resultProxy.getProxyPort();
                    Result resultUISK = HttpUtils.uiskRefresherForBatch(resultProxy.getProxyHost(), resultProxy.getProxyPort(), userId);
                    uiForBatch = resultUISK.getUi();
                    skForBatch = resultUISK.getSk();
                    continue;
                } else if (isTimeOut) {
                    Result resultProxy = proxy();
                    proxyHostForBatch = resultProxy.getProxyHost();
                    proxyPortForBatch = resultProxy.getProxyPort();
                } else {
                    httpSended = httpSended + 1;
                }
                if (response316GemBody[0] != null) {
                    JSONObject jsonObject = JSONObject.parse(response316GemBody[0]);
                    if (jsonObject.containsKey("r") && jsonObject.containsKey("d")) {
                        if (jsonObject.getIntValue("r") == 0) {
                            JSONObject dObject = jsonObject.getJSONObject("d");
                            JSONObject pObject = dObject.getJSONObject("p");
                            fg = pObject.getIntValue("fg");
                            fgThreadLocal.set(fg);
                        }
                    }
                }
                //System.out.print("\033[1A"); // 将光标向上移动一行
                //System.out.print("\033[2K"); // 清除当前行
                //System.out.print("\033[0G"); // 将光标移动到当前行的首位
                //System.out.print("\033[32m" + "已执行" + (i + 1) + "次" + "，成功" + httpSended + "次" + " || 当前钻石：fg值：" + fg + "，g1值：" + g1 + "\n" + "\033[0m");
                if (fg > Inter.maxGem) {
                    System.out.println("\033[31m" + "账号：" + userId + " 检测到钻石值超出安全范围，账号停止刷取，正在切换账号……" + "\033[0m");
                    executorService.shutdown();
                    //System.exit(0);
                }
            }
        }
    }

    public static Result uiskRefresherForBatch(String proxyHost, int proxyPort, int userId) {
        String ui = null, sk = null;
        String proxyHostForBatch = proxyHost;
        int proxyPortForBatch = proxyPort;
        String response202BodyString;
        boolean success = false;
        try {
            while (!success) {
                sleep(5000);
                response202BodyString = HttpSender.doQuest(Inter.isAndroid, RequestBodyBuilder.request202BodyBuilder(userId), proxyHostForBatch, proxyPortForBatch);
                if (!response202BodyString.contains("{") || !response202BodyString.contains("}") || !response202BodyString.contains("e") || !response202BodyString.contains("r")) {
                    //System.out.println("\033[31m" +"账号："+userId+ " V202返回数据包不完整，正在重试……" + "\033[0m");
                    Result resultProxy = proxy();
                    proxyHostForBatch = resultProxy.getProxyHost();
                    proxyPortForBatch = resultProxy.getProxyPort();
                }
                String response202Body = HttpCrypto.decryptRES(response202BodyString);
                if (response202Body != null) {
                    JSONObject jsonObject = JSONObject.parse(response202Body);
                    if (jsonObject.containsKey("r")) {
                        if (jsonObject.getIntValue("r") == 20013) {
                            //System.out.println("\033[31m" +"账号："+userId+ " 发送数据包V202出现20013错误，正在重试……" + "\033[0m");
                            Result resultProxy = proxy();
                            proxyHostForBatch = resultProxy.getProxyHost();
                            proxyPortForBatch = resultProxy.getProxyPort();
                        }
                    }
                    JSONObject dObject = jsonObject.getJSONObject("d");
                    ui = dObject.getString("ui");
                    sk = dObject.getString("sk");
                    //System.out.println("\033[34m" + "ui:" + ui + "\033[0m");
                    //System.out.println("\033[34m" + "sk:" + sk + "\n" +"\033[0m");
                    success = true;
                } else {
                    //System.out.println("\033[31m" +"账号："+userId+ " V202返回数据包为空，正在重试……" + "\033[0m");
                    Result resultProxy = proxy();
                    proxyHostForBatch = resultProxy.getProxyHost();
                    proxyPortForBatch = resultProxy.getProxyPort();
                }
            }
        } catch (Exception e) {
            //System.out.println("\033[31m" +"账号："+userId+ " 服务器已关闭已有链接，正在重试……" + "\033[0m");
            Result resultProxy = proxy();
            Result resultUISK = HttpUtils.uiskRefresherForBatch(resultProxy.getProxyHost(), resultProxy.getProxyPort(), userId);
            switch (Inter.inter) {
                case 3:
                    HttpUtils.SendPackageForBatch(resultUISK.getUi(), resultUISK.getSk(), userId, resultProxy.getProxyHost(), resultProxy.getProxyPort());
                    break;
                case 4:
                    UserJsonUtils.getGemResponse(resultUISK.getUi(), resultUISK.getSk(), userId, resultProxy.getProxyHost(), resultProxy.getProxyPort());
                    break;
            }
        }
        return new Result(ui, sk);
    }
}
