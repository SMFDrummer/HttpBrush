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
                * 净化软件主体
                * 修复自定义发包问题
                * 修复bug""");
        Log.w("使用含有user.json批量刷取功能前请确保账号库文件已经通过功能99升级");
        Inter.Setting();
        switch (Inter.inter) {
            case 1 -> cryptoGuideLine();
            case 2, 6 -> UserJsonUtils.measure();
            case 3 -> UserJsonUtils.JsonCutter();
            case 4 -> maker();
            case 5 -> {
                Log.v("请输入拓维userId，如果确认配置文件中不含有需要刷新账号信息的项，也可以直接回车以继续");
                apply(Scanner.String(true));
            }
            case 7 -> Create.single();
            case 8 -> Create.measure();
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
