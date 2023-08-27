package smf.icdada.Deprecated;

import smf.icdada.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static smf.icdada.HttpUtils.base.sleep;

@java.lang.Deprecated
public class RequestBodyBuilder {

    static String request202Body;


    public static void GetPackage(List<Integer> userIds) {
        if (Inter.chooser == 1) {
            System.out.println("正在等待代理池刷新");
            sleep(Inter.waiter);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int userId : userIds) {
            sleep(100);
            executorService.submit(() -> {
                System.out.println("\033[33m" + "账号: " + userId + " 已读取，开始执行" + "\033[0m");
                settingGetterForBatch(userId);
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("所有账号刷取完成，程序退出");
        System.exit(0);
    }

    public static void GetPackage() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder inputContent = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        System.out.println("\033[33m" + "请输入V202请求包体，当完成输入后，请按多次回车键结束输入：" + "\033[0m");
        try {
            int emptyLineCount = 0;
            while (true) {
                String line = reader.readLine();
                if (line == null || line.isEmpty()) {
                    emptyLineCount++;
                } else {
                    emptyLineCount = 0;
                }
                if (emptyLineCount == 2) {
                    break;
                }
                inputContent.append(line);
                inputContent.append(lineSeparator);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        request202Body = inputContent.toString();
        settingGetter();
    }

    public static void GetPackage(int userId) {
        request202Body = HttpCrypto.encryptREQ("V202", RequestType.OI.getRequestBody(userId));
        settingGetter();
    }

    private static void settingGetter() {
        ProxyManager.proxy();
        HttpUtils.uiskRefresher();
        HttpUtils.SendPackage();
    }

    private static void settingGetterForBatch(int userId) {
        Result resultProxy = ProxyManager.proxy();
        Result resultUISK = HttpUtils.uiskRefresherForBatch(resultProxy.getProxyHost(), resultProxy.getProxyPort(), userId);
        HttpUtils.SendPackageForBatch(resultUISK.getUi(), resultUISK.getSk(), userId, resultProxy.getProxyHost(), resultProxy.getProxyPort());
    }

    public static String request202BodyBuilder(int userId) {
        return HttpCrypto.encryptREQ("V202", RequestType.OI.getRequestBody(userId));
    }
}
