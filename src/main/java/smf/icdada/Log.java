package smf.icdada;

/**
 * @author SMF & icdada
 * @描述: Log染色输出类
 */
public class Log {
    // Reset
    private static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    private static final String BLACK = "\033[30m";   // BLACK
    private static final String RED = "\033[31m";     // RED
    private static final String GREEN = "\033[32m";   // GREEN
    private static final String YELLOW = "\033[33m";  // YELLOW
    private static final String BLUE = "\033[94m";    // BLUE
    private static final String PURPLE = "\033[35m";  // PURPLE
    private static final String CYAN = "\033[36m";    // CYAN
    private static final String WHITE = "\033[37m";   // WHITE

    /**
     * @描述: Error - 红色 - 错误
     */
    public static void e(String message) {
        System.out.println(RED + "● " + message + RESET);
    }

    /**
     * @描述: Debug - 青色 - 调试
     */
    public static void d(String message) {
        System.out.println(CYAN + "● " + message + RESET);
    }

    /**
     * @描述: Info - 蓝色 - 信息
     */
    public static void i(String message) {
        System.out.println(BLUE + "● " + message + RESET);
    }

    /**
     * @描述: Verbose - 黄色 - 提示
     */
    public static void v(String message) {
        System.out.println(YELLOW + "● " + message + RESET);
    }

    /**
     * @描述: Warning - 紫色 - 警告
     */
    public static void w(String message) {
        System.out.println(PURPLE + "● " + message + RESET);
    }

    /**
     * @描述: Success - 绿色 - 成功
     */
    public static void s(String message) {
        System.out.println(GREEN + "● " + message + RESET);
    }

    /**
     * @描述: Alert - 白色 - 描述
     */
    public static void a(String message) {
        System.out.println(WHITE + message + RESET);
    }

    /**
     * @描述: Back - 黑色 - 背景
     */
    public static void b(String message) {
        System.out.println(BLACK + message + RESET);
    }
}


