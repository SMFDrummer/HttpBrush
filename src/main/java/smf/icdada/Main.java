package smf.icdada;

import smf.icdada.CreateAccount.Create;

import java.io.IOException;
import java.util.Properties;

import static smf.icdada.HttpUtils.Base.cryptoGuideLine;
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
        Log.i(String.format("HttpBrush 版本:%s", version));
        Log.v(String.format("请检查 %s 目录下程序运行环境是否存在完整配置", System.getProperty("user.dir")));
        Log.d("-> default.json");
        Inter.PreCheck();
        Log.i(String.format("程序作者:SMF & icdada；协同测试:%s", Inter.getTestBy()));
        Log.v("""
                更新日志:
                * 重写用户库文件编辑方法，现在更加稳定快速
                * 添加功能13与14，并将基础方法使用异步虚拟线程重写
                * 修复了让人啼笑皆非的Bug
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
            case 13 -> TheaterCoin.theaterMesh();
            case 14 -> TheaterCoin.measure();
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
