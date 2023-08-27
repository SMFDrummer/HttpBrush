package smf.icdada;

import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static smf.icdada.HttpUtils.base.*;

public class Anniversary {
    public static void brush(){
        String inviteCode = getInviteCode();
        int count = 0;
        do {
            int i = count;
            count += brushInviteCode(inviteCode,getRandomId());
            if (count == i) {
                System.out.println("\033[33m" + "邀请码：" + inviteCode + "\033[0m" + " || " + "\033[31m" + "刷取失败" + "\033[0m" + " || " +"共成功：" + count);
            } else {
                System.out.println("\033[33m" + "邀请码：" + inviteCode + "\033[0m" + " || " + "\033[32m" + "刷取成功" + "\033[0m" + " || " +"共成功：" + count);
            }
        } while (count >= 20);
        System.out.println("\033[33m" + "邀请码：" + inviteCode + "\033[0m" + " || " + "\033[32m" + "刷取结束" + "\033[0m");
    }
    private static String getInviteCode(){
        System.out.println("\033[33m" + "请输入活动邀请码，并按回车键继续：" + "\033[0m");
        return smfScanner.smfString(true);
    }
    private static int getRandomId(){
        Random random = new Random();
        return random.nextInt(500000) + 37500001;
    }

    private static int brushInviteCode(String inviteCode,int randomUserId){
        ExecutorService executor = Executors.newFixedThreadPool(3);
        refresh(Inter.oi,randomUserId);
        Result uisk = getUisk(randomUserId);
        if ("banned".equals(uisk.getUi()) && "banned".equals(uisk.getSk())) {
            return 0;
        } else {
            Result proxy = getProxy(randomUserId);
            while (true){
                List<Future<String>> futures = new ArrayList<>();
                Result finaluisk = uisk;
                Result finalproxy = proxy;
                futures.add(executor.submit(() -> getRes(finalproxy,RequestType.ANNI_IN.getRequestBody(finaluisk.getUi(),finaluisk.getSk()))));
                sleep(350);
                futures.add(executor.submit(() -> getRes(finalproxy,RequestType.ANNI_BRUSH.getRequestBody(inviteCode,finaluisk.getUi(),finaluisk.getSk()))));
                sleep(350);
                try {
                    String response303Body = futures.get(0).get(3, TimeUnit.SECONDS);
                    if (JSON.parseObject(response303Body).getIntValue("r") == 0){
                        String response876Body = futures.get(1).get(3,TimeUnit.SECONDS);
                        if (JSON.parseObject(response876Body).getIntValue("r") != 0){
                            futures.add(executor.submit(() -> getRes(finalproxy,RequestType.ANNI_BRUSH.getRequestBody(inviteCode,finaluisk.getUi(),finaluisk.getSk()))));
                            sleep(350);
                            String response876BodyResend = futures.get(2).get(3,TimeUnit.SECONDS);
                            return JSON.parseObject(response876BodyResend).getIntValue("r") == 0 ? 1 : 0;
                        } else return 1;
                    } else {
                        refresh(Inter.oi,randomUserId);
                        uisk = getUisk(randomUserId);
                        proxy = getProxy(randomUserId);
                    }
                } catch (Exception ignored) {
                    refresh(Inter.oi,randomUserId);
                    uisk = getUisk(randomUserId);
                    proxy = getProxy(randomUserId);
                }
            }
        }
    }

    private static String getRes(Result proxy,String body){
        try {
            return HttpCrypto.decryptRES(
                    HttpSender.doQuest(
                            Inter.isAndroid,
                            HttpCrypto.encryptREQ(body),
                            proxy.getProxyHost(),
                            proxy.getProxyPort()
                    )
            );
        } catch (Exception ignored) {
            return "{\"r\":12202}";
        }
    }

}
