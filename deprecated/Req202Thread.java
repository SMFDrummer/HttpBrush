package smf.icdada.test;

import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import smf.icdada.*;
import smf.icdada.HttpUtils.base;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Req202Thread extends Thread{
    int timeout;
    int userid;
    FutureTask<String> deamonFuture=new FutureTask<>(new Callable<String>() {
        @Override
        public String call() throws Exception {
            while (true) {
                String V202req = HttpCrypto.encryptREQ(
                        RequestType.OI.getRequestBody(Inter.oi, userid)
                );
                String V202res = HttpCrypto.decryptRES(
                        HttpSender.doQuest(true, V202req)
                );
                //System.out.println(V202res);
                JSONObject jsonObject = JSONObject.parseObject(V202res);
                JSONObject d = jsonObject.getJSONObject("d");
                Result uisk = new Result(d.getString("ui"), d.getString("sk"));//获取uisk
                base.accountMap.put(userid, uisk);
                //使用获取的uisk构建数据包，如果uisk不是最新，则出现20013错误
                String V316req = HttpCrypto.encryptREQ(
                        RequestType.GET.getRequestBody(uisk.getUi(), uisk.getSk())
                );
                // System.out.println(V316req);
                String V316res = HttpCrypto.decryptRES(
                        HttpSender.doQuest(true, V316req)
                );
                // System.out.println(V316res);
                JSONObject jsonObject2 = JSONObject.parseObject(V316res);
                int r = jsonObject2.getIntValue("r");
                if (r == 0) {
                    base.accountMap.put(userid, uisk);
                    return V316res;
                } else {
                    System.out.println("重新获取");
                }
            }
        }
    });//守护线程
    public Req202Thread(int timeout,int userid){
    this.timeout=timeout; //设置超时时间
        this.userid=userid;
    this.setName("Thread"+ userid); //设置线程名称
    }
    @Override
    @SneakyThrows
    public void run() {
        try {
            super.run();
            Thread deamon = new Thread(deamonFuture);
            deamon.setDaemon(true);
            deamon.start();
            System.out.println(deamonFuture.get(timeout, TimeUnit.MILLISECONDS));
        }catch (TimeoutException timeout){
            timeout.printStackTrace();
            base.deadAccount.add(userid);
        }
    }
}
