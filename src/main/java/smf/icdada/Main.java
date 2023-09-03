package smf.icdada;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static smf.icdada.HttpUtils.base.*;
import static smf.icdada.HttpUtils.strategy.apply;
import static smf.icdada.HttpUtils.strategy.maker;

/**
 * @author SMF & icdada
 * @描述: 主方法类
 * <p>
 * 包含主函数 main 以及初始设定导引函数。
 * </p>
 */

public class Main {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable clearConsoleTask = Main::clearConsole;
        scheduler.scheduleAtFixedRate(clearConsoleTask, 15, 5, TimeUnit.MINUTES);
        sleep(1000);
        System.out.printf("""
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
                                                         
                HttpBrush 正式版本:4.5-Beta
                程序作者：SMF & icdada；协同测试：能奈何尔
                请检查 %s 目录下程序运行环境是否存在完整配置
                user.json    default.json
                
                更新日志：
                *修复了线程池大小问题
                *添加功能9（ForBeta）
                
                ————————————————————————————————————————————————————————————————————————————
                
                """, System.getProperty("user.dir"));
        Inter.defaultSetting();
        switch (Inter.inter) {
            case 1:
                cryptoGuideLine();
                break;
            case 2:
                UserBanner.fileChecker(false);
                break;
            case 3:
                UserBanner.bannedFunction();
                break;
            case 4,10:
                UserJsonUtils.measure();
                break;
            case 5:
                UserJsonUtils.JsonCutter();
                break;
            case 6:
                maker();
                break;
            case 7:
                apply(userIdGetter());
                break;
            case 8:
                Anniversary.brush();
                break;
            case 9:
                Anniversary.measure();
                break;
            case 0:
                System.exit(0);
            default:
                System.out.println("\033[31m" + "默认值非法，无法执行已知功能，请重新设置" + "\033[0m");
                System.exit(0);
        }
        System.exit(0);
    }
    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
            System.out.println("\033[33m" + "已清空控制台，控制台定时触发已加载" + "\033[0m");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
