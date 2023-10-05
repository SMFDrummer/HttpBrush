package smf.icdada;


/**
 * @author SMF & icdada
 * @描述: Log染色输出类
 */
public class Log {
    public enum Color {
        BLACK("\033[30m"),
        RED("\033[31m"),
        GREEN("\033[32m"),
        YELLOW("\033[33m"),
        BLUE("\033[94m"),
        PURPLE("\033[35m"),
        CYAN("\033[36m"),
        WHITE("\033[37m"),
        RESET("\033[0m");
        private final String color;

        Color(String color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return color;
        }
    }
    public static Pair Separator = p(" || ",Color.WHITE);

    /**
     * @描述: Error - 红色 - 错误
     */
    public static void e(String message) {
        System.out.println(Color.RED + "● " + message + Color.RESET);
    }

    /**
     * @描述: Debug - 青色 - 调试
     */
    public static void d(String message) {
        System.out.println(Color.CYAN + "● " + message + Color.RESET);
    }

    /**
     * @描述: Info - 蓝色 - 信息
     */
    public static void i(String message) {
        System.out.println(Color.BLUE + "● " + message + Color.RESET);
    }

    /**
     * @描述: Verbose - 黄色 - 提示
     */
    public static void v(String message) {
        System.out.println(Color.YELLOW + "● " + message + Color.RESET);
    }

    /**
     * @描述: Warning - 紫色 - 警告
     */
    public static void w(String message) {
        System.out.println(Color.PURPLE + "● " + message + Color.RESET);
    }

    /**
     * @描述: Success - 绿色 - 成功
     */
    public static void s(String message) {
        System.out.println(Color.GREEN + "● " + message + Color.RESET);
    }

    /**
     * @描述: Alert - 白色 - 描述
     */
    public static void a(String message) {
        System.out.println(Color.WHITE + message + Color.RESET);
    }

    /**
     * @描述: Back - 黑色 - 背景
     */
    public static void b(String message) {
        System.out.println(Color.BLACK + message + Color.RESET);
    }

    /**
     * @描述: 自定义输出内容以及每段输出的颜色
     */
    public static void c(Pair... pairs) {
        StringBuilder sb = new StringBuilder();
        sb.append("● ");
        for (Pair pair : pairs) {
            sb.append(pair.color).append(pair.content).append(Color.RESET);
        }
        System.out.println(sb);
    }

    /**
     * @描述: 颜色和内容的键值对
     */
    public static class Pair {
        private final Object content;
        private final Color color;

        public Pair(Object content, Color color) {
            this.content = content;
            this.color = color;
        }
    }

    /**
     * @描述: 创建Pair对象
     */
    public static Pair p(Object content, Color color) {
        return new Pair(content, color);
    }
}