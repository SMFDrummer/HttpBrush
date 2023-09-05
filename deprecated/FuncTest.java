import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.SneakyThrows;
import org.junit.Test;
import smf.icdada.HttpUtils.base;
import smf.icdada.test.Req202Thread;
import smf.icdada.test.ThreadBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static smf.icdada.HttpUtils.base.*;
import static smf.icdada.HttpUtils.strategy.apply;
import static smf.icdada.ProxyManager.proxy;

public class FuncTest {
    public static void banned(int userId) {
        Result uiskOfficial = base.accountMap.get(userId);//获取uisk
        {
            Result proxy = proxy();
            String response316GetBody = "{\"r\":12202}";
            boolean keepRunning = true;
            while (keepRunning) {
                try {
                    response316GetBody = HttpCrypto.decryptRES(
                            HttpSender.doQuest(
                                    true,
                                    HttpCrypto.encryptREQ(
                                            RequestType.GET.getRequestBody(uiskOfficial.getUi(), uiskOfficial.getSk())
                                    )));//发送V316
                    //proxy.getProxyHost(),
                    //proxy.getProxyPort()));
                } catch (Exception ignored) {
                    proxy = proxy();
                }
                if (JSONObject.parseObject(response316GetBody).getIntValue("r") != 0) {
                    System.out.println("\033[33m" + "账号：" + userId + "\033[0m" + " || " + "\033[31m" + "读取失败，正在重试……" + "\033[0m" + " || " + response316GetBody);
                    if (JSONObject.parseObject(response316GetBody).getIntValue("r") == 20013) {
                        proxy = proxy();
                        uiskOfficial = null;
                    }
                } else {
                    JSONObject jsonObject = JSONObject.parseObject(response316GetBody);
                    if (jsonObject.containsKey("r") && jsonObject.containsKey("d")) {
                        if (jsonObject.getIntValue("r") == 0) {
                            JSONObject d = jsonObject.getJSONObject("d");
                            JSONArray il = d.getJSONArray("il");
                            for (Object object : il) {
                                JSONObject snailCoinObject = (JSONObject) object;
                                if ("23400".equals(snailCoinObject.getString("i"))) {
                                    int snailCoin = Integer.parseInt(snailCoinObject.getString("q"));
                                    System.out.println("\033[32m" + "账号：" + userId + "\033[0m" + " || " + "\033[32m" + "已获取蜗牛币数量：" + snailCoin + "\033[0m");
                                    if (snailCoin >= 1000000) {
                                        if (apply(userId, true, uiskOfficial)) {//这个地方必须接续上面的uisk不能变，作为参数传入
                                            Result uiskTemp = null;
                                            if ("banned".equals(uiskTemp.getUi()) && "banned".equals(uiskTemp.getSk())) {
                                                JsonUtil(Inter.oi, userId, true, false);
                                                keepRunning = false;
                                            } else {
                                                System.out.println("\033[33m" + "账号：" + userId + "\033[0m" + " || " + "\033[33m" + "尝试封号失败，正在重试……" + "\033[0m");
                                            }
                                        }
                                    } else {
                                        System.out.println("\033[32m" + "账号：" + userId + "\033[0m" + " || " + "\033[32m" + "检测通过！" + "\033[0m");
                                        JsonUtil(Inter.oi, userId, false, true);
                                        keepRunning = false;
                                    }
                                }
                            }
                            if (il.isEmpty()) {
                                System.out.println("\033[32m" + "账号：" + userId + "检测通过！" + "\033[0m");
                                JsonUtil(Inter.oi, userId, false, true);
                                keepRunning = false;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void JsonUtil(int channel, int userId, boolean isBanned, boolean isProtected) {
        try {
            UserBanner.lock.writeLock().lock();
            JSONObject parse = JSONObject.parse(Files.readString(Paths.get(UserBanner.banuserPath)));
            JSONArray bannedUsers = parse.getJSONArray("BannedUsers");
            for (Object object : bannedUsers) {
                JSONObject bannedUser = (JSONObject) object;
                if (bannedUser.getIntValue("userId") == userId) {
                    JSONArray account = bannedUser.getJSONArray("account");
                    for (Object objectAccount : account) {
                        JSONObject userAccount = (JSONObject) objectAccount;
                        if (userAccount.getIntValue("severId") == channel) {
                            userAccount.put("isBanned", isBanned);
                            userAccount.put("isProtected", isProtected);
                        }
                    }
                }
            }
            String formattedJson = parse.toJSONString(JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
            String tempFileName = System.getProperty("user.dir") + File.separator + "temp" + File.separator + UUID.randomUUID() + ".tmp";
            Path tempPath = Paths.get(tempFileName);
            try (FileWriter fileWriter = new FileWriter(tempPath.toFile())) {
                fileWriter.write(formattedJson);
                fileWriter.flush();
            }
            Files.move(tempPath, Paths.get(UserBanner.banuserPath), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(tempPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            UserBanner.lock.writeLock().unlock();
        }
    }

    @Test
    public void Reqtest() throws Exception {
        //第一步是构建202请求并发送
        String V202req = HttpCrypto.encryptREQ(
                RequestType.OI.getRequestBody(109208, 36576332)
        );
        String V202res = HttpCrypto.decryptRES(
                HttpSender.doQuest(true, V202req)
        );
        System.out.println(V202res);
        JSONObject jsonObject = JSONObject.parseObject(V202res);
        JSONObject d = jsonObject.getJSONObject("d");
        Result uisk = new Result(d.getString("ui"), d.getString("sk"));//获取uisk
        //使用获取的uisk构建数据包，如果uisk不是最新，则出现20013错误
        String V316req = HttpCrypto.encryptREQ(
                RequestType.GET.getRequestBody(uisk.getUi(), uisk.getSk())
        );
        System.out.println(V316req);
        String V316res = HttpCrypto.decryptRES(
                HttpSender.doQuest(true, V316req)
        );
        System.out.println(V316res);
    }

    @Test
    public void ThreadPoolTest() {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(10, 10,
                        10000, TimeUnit.MILLISECONDS, new SynchronousQueue<>());


        threadPoolExecutor.execute(new Req202Thread(5000, 0));


        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) ;
    }

    @Test
    public void LoadTestData() {
        base.deadAccount.add(36576332);
    }

    @Test
    @SneakyThrows
    public void UpdateUisk() {
        // Inter.defaultSetting();
        LoadTestData();
        Thread thread = new Thread(() -> {
            for (; ; ) {
                ThreadPoolExecutor threadPoolExecutor =
                        new ThreadPoolExecutor(10, 10,
                                10000, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
                if (!base.deadAccount.isEmpty()) {
                    Integer userid = base.deadAccount.get(0);
                    threadPoolExecutor.execute(() -> {
                        new Req202Thread(5000, userid).run();
                    });
                    Iterator<Integer> iterator = base.deadAccount.iterator();
                    if (iterator.hasNext()) {
                        Integer next = iterator.next();
                        if (next.equals(userid)) {
                            iterator.remove();
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "UISK-UPDATE-THREAD");
        thread.start();
        sleep(2000);
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(10, 10,
                        10000, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        for (Integer i : base.accountMap.keySet()) {
            if (!base.runningAccount.contains(i)) {
                runningAccount.add(i);
                executor.execute(() -> {
                    banned(i);
                });
            }
        }
        for (; ; ) ;
    }

    @Test
    public void printHashmap() {

        for (HashMap.Entry<Integer, Result> entry : base.accountMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }

    }
}
