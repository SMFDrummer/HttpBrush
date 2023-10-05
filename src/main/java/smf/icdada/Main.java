package smf.icdada;

import smf.icdada.CreateAccount.Create;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static smf.icdada.HttpUtils.Base.*;
import static smf.icdada.HttpUtils.Strategy.apply;
import static smf.icdada.HttpUtils.Strategy.maker;

/**
 * @author SMF & icdada
 * @描述: 主方法类
 * <p>
 * 包含主函数 main 以及初始设定导引函数。
 * </p>
 */

public class Main {
    public static void main(String[] args) {
        Console.startConsole();
        String version = getProperties().getProperty("app.version");
        Log.i(String.format("HttpBrush 正式版本:%s", version));
        Log.v(String.format("请检查 %s 目录下程序运行环境是否存在完整配置", System.getProperty("user.dir")));
        Log.d("-> default.json");
        Inter.PreCheck();
        Log.i(String.format("程序作者:SMF & icdada；协同测试:%s", Inter.getTestBy()));
        Log.v("""
                更新日志:
                * 去除巨大丑陋的标头
                * 重要更新，重写自定义发包，现在功能更加完善强大
                * 重写枚举，重写基础类、检查类，新增数据包格式化类
                * default.json配置更新，新增IOS版本与控制台输出控制选项
                * 由于自定义发包更新，封号方法等需要绑定文件配置的必须使用新配置才能正常运行
                ●""");
        Inter.Setting();
        switch (Inter.inter) {
            case 1 -> cryptoGuideLine();
            case 2 -> UserBanner.fileChecker(false);
            case 3 -> UserBanner.bannedFunction();
            case 4, 10 -> UserJsonUtils.measure();
            case 5 -> UserJsonUtils.JsonCutter();
            case 6 -> maker();
            case 7 -> apply(smfScanner.Int(true, "^\\d{8,}$"));
            case 8 -> Anniversary.brush();
            case 9 -> Anniversary.measure();
            case 11 -> Create.Single();
            case 12 -> Create.Batch();
            case 99 -> HttpBrushTest.main(args);
            case 0 -> System.exit(0);
            default -> {
                Log.e("默认值非法，无法执行已知功能，请重新设置");
                System.exit(0);
            }
        }
        System.exit(0);
    }
    private static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
        }
        return properties;
    }
}
