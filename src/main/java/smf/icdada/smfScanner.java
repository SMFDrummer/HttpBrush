package smf.icdada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * @author SMF & icdada
 * @描述: 自定义 Scanner 类
 * <p>
 * 更加智能的 Scanner，相对弥补了 java 官方 Scanner 的不足，并修复一些问题。
 * </p>
 */
public class smfScanner {
    public static int smfInt(boolean requireConfirmation) {
        Scanner scanner = new Scanner(System.in);
        int number = 0;
        boolean isValid = false;
        while (!isValid) {
            //System.out.println("\033[33m" +"请输入一个整数："+"\033[0m");
            while (!scanner.hasNextInt()) {
                System.out.println("\033[31m" + "输入无效，请输入一个整数：" + "\033[0m");
                scanner.next();
            }
            number = scanner.nextInt();
            scanner.nextLine(); // consume newline left-over
            if (requireConfirmation) {
                System.out.println("\033[33m" + "你输入的是 " + number + "\n如果确认请输入任意字符，否则请输入N或n：" + "\033[0m");
                String confirmation = scanner.nextLine().trim();
                isValid = !confirmation.equalsIgnoreCase("N");
            } else {
                isValid = true;
            }
        }
        return number;
    }

    public static boolean smfBoolean(boolean requireConfirmation) {
        Scanner scanner = new Scanner(System.in);
        boolean bool = false;
        boolean isValid = false;
        while (!isValid) {
            System.out.println("\033[33m" + "请输入Y或N：" + "\033[0m");
            while (!scanner.hasNext("[YyNn]")) {
                System.out.println("\033[31m" + "输入无效，请输入Y或N：" + "\033[0m");
                scanner.next();
            }
            bool = scanner.next().equalsIgnoreCase("Y");
            scanner.nextLine(); // consume newline left-over
            if (requireConfirmation) {
                System.out.println("\033[33m" + "你输入的是 " + (bool ? "Y" : "N") + "\n如果确认请输入任意字符，否则请输入N或n：" + "\033[0m");
                String confirmation = scanner.nextLine().trim();
                isValid = !confirmation.equalsIgnoreCase("N");
            } else {
                isValid = true;
            }
        }
        return bool;
    }

    public static String smfLongString(boolean requireConfirmation) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder inputContent = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        boolean isValid = false;
        while (!isValid) {
            inputContent.setLength(0); // clear the StringBuilder
            System.out.println("\033[33m" + "请输入内容，连续两次回车结束输入：" + "\033[0m");
            try {
                int emptyLineCount = 0;
                while (true) {
                    String line = reader.readLine();
                    if (line == null || line.isEmpty()) {
                        emptyLineCount++;
                    } else {
                        emptyLineCount = 0;
                    }
                    if (emptyLineCount == 2) {
                        break;
                    }
                    inputContent.append(line);
                    inputContent.append(lineSeparator);
                }
                if (requireConfirmation) {
                    System.out.println("\033[33m" + "你输入的内容是：\n" + inputContent + "\n如果确认请输入任意字符，否则请输入N或n：" + "\033[0m");
                    String confirmation = reader.readLine().trim();
                    isValid = !confirmation.equalsIgnoreCase("N");
                } else {
                    isValid = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inputContent.toString();
    }

    public static String smfString(boolean requireConfirmation) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputContent = "";
        boolean isValid = false;
        while (!isValid) {
            //System.out.println("\033[33m" +"请输入一行内容："+"\033[0m");
            try {
                inputContent = reader.readLine();
                if (requireConfirmation) {
                    System.out.println("\033[33m" + "你输入的内容是：" + inputContent + "\n如果确认请输入任意字符，否则请输入N或n：" + "\033[0m");
                    String confirmation = reader.readLine().trim();
                    isValid = !confirmation.equalsIgnoreCase("N");
                } else {
                    isValid = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inputContent;
    }
}
