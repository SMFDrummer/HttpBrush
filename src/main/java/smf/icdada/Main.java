package smf.icdada;

import java.io.IOException;
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
        startConsole();
        Inter.PreCheck();
        String version = getProperties().getProperty("app.version");
        System.out.println("""
                ██╗  ██╗████████╗████████╗██████╗
                ██║  ██║╚══██╔══╝╚══██╔══╝██╔══██╗
                ███████║   ██║      ██║   ██████╔╝
                ██╔══██║   ██║      ██║   ██╔═══╝
                ██║  ██║   ██║      ██║   ██║
                ╚═╝  ╚═╝   ╚═╝      ╚═╝   ╚═╝
                ██████╗ ██████╗ ██╗   ██╗███████╗██╗  ██╗
                ██╔══██╗██╔══██╗██║   ██║██╔════╝██║  ██║
                ██████╔╝██████╔╝██║   ██║███████╗███████║
                ██╔══██╗██╔══██╗██║   ██║╚════██║██╔══██║
                ██████╔╝██║  ██║╚██████╔╝███████║██║  ██║
                ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝
                """);
        Log.i(String.format("HttpBrush 正式版本：%s", version));
        Log.i(String.format("程序作者：SMF & icdada；协同测试：%s", Inter.getTestBy()));
        Log.v(String.format("请检查 %s 目录下程序运行环境是否存在完整配置", System.getProperty("user.dir")));
        Log.d("-> default.json");
        Log.v("""
                更新日志：
                * 重写default.json
                * 修正自动化发包代码
                * 优化控制台输出
                """);
        Inter.Setting();
        switch (Inter.inter) {
            case 1 -> cryptoGuideLine();
            case 2 -> UserBanner.fileChecker(false);
            case 3 -> UserBanner.bannedFunction();
            case 4, 10 -> UserJsonUtils.measure();
            case 5 -> UserJsonUtils.JsonCutter();
            case 6 -> maker();
            case 7 -> apply(userIdGetter());
            case 8 -> Anniversary.brush();
            case 9 -> Anniversary.measure();
            case 0 -> System.exit(0);
            default -> {
                Log.e("默认值非法，无法执行已知功能，请重新设置");
                System.exit(0);
            }
        }
        System.exit(0);
    }

    private static void startConsole() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable clearConsoleTask = Main::clearConsole;
        scheduler.scheduleAtFixedRate(clearConsoleTask, 15, 5, TimeUnit.MINUTES);
        sleep(1000);
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
            Log.v("已清空控制台，控制台定时触发已加载");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
