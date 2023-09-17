package smf.icdada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Pattern;

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
            while (!scanner.hasNextInt()) {
                Log.e("输入无效，请输入一个整数:");
                scanner.next();
            }
            number = scanner.nextInt();
            scanner.nextLine();
            if (requireConfirmation) {
                Log.i("你输入的是 " + number);
                Log.v("如果确认请输入任意字符，否则请输入N或n:");
                String confirmation = scanner.nextLine().trim();
                isValid = !confirmation.equalsIgnoreCase("N") || !confirmation.equalsIgnoreCase("n");
            } else {
                isValid = true;
            }
        }
        return number;
    }

    public static int smfInt(boolean requireConfirmation, String regex) {
        Scanner scanner = new Scanner(System.in);
        int number = 0;
        boolean isValid = false;
        while (!isValid) {
            while (!scanner.hasNextInt()) {
                Log.e("输入无效，请输入一个整数:");
                scanner.next();
            }
            number = scanner.nextInt();
            scanner.nextLine(); // consume newline left-over
            if (Pattern.matches(regex, Integer.toString(number))) {
                if (requireConfirmation) {
                    Log.i("你输入的是 " + number);
                    Log.v("如果确认请输入任意字符，否则请输入N或n:");
                    String confirmation = scanner.nextLine().trim();
                    isValid = !confirmation.equalsIgnoreCase("N") || !confirmation.equalsIgnoreCase("n");
                } else {
                    isValid = true;
                }
            } else {
                Log.e("输入的整数不符合正则表达式，请重新输入:");
            }
        }
        return number;
    }

    public static boolean smfBoolean(boolean requireConfirmation) {
        Scanner scanner = new Scanner(System.in);
        boolean bool = false;
        boolean isValid = false;
        while (!isValid) {
            Log.v("请输入Y或N:");
            while (!scanner.hasNext("[YyNn]")) {
                Log.e("输入无效，请输入Y或N:");
                scanner.next();
            }
            bool = scanner.next().equalsIgnoreCase("Y");
            scanner.nextLine(); // consume newline left-over
            if (requireConfirmation) {
                Log.i("你输入的是 " + (bool ? "Y" : "N"));
                Log.v("如果确认请输入任意字符，否则请输入N或n:");
                String confirmation = scanner.nextLine().trim();
                isValid = !confirmation.equalsIgnoreCase("N") || !confirmation.equalsIgnoreCase("n");
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
            Log.v("请输入内容，连续两次回车结束输入:");
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
                    Log.i("你输入的内容是:\n" + inputContent);
                    Log.v("如果确认请输入任意字符，否则请输入N或n:");
                    String confirmation = reader.readLine().trim();
                    isValid = !confirmation.equalsIgnoreCase("N") || !confirmation.equalsIgnoreCase("n");
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
            try {
                inputContent = reader.readLine();
                if (requireConfirmation) {
                    Log.i("你输入的内容是:" + inputContent);
                    Log.v("如果确认请输入任意字符，否则请输入N或n:");
                    String confirmation = reader.readLine().trim();
                    isValid = !confirmation.equalsIgnoreCase("N") || !confirmation.equalsIgnoreCase("n");
                } else {
                    isValid = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inputContent;
    }

    public static String smfString(boolean requireConfirmation, String regex) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputContent = "";
        boolean isValid = false;
        while (!isValid) {
            try {
                inputContent = reader.readLine();
                if (Pattern.matches(regex, inputContent)) {
                    if (requireConfirmation) {
                        Log.i("你输入的内容是:" + inputContent);
                        Log.v("如果确认请输入任意字符，否则请输入N或n:");
                        String confirmation = reader.readLine().trim();
                        isValid = !confirmation.equalsIgnoreCase("N") || !confirmation.equalsIgnoreCase("n");
                    } else {
                        isValid = true;
                    }
                } else {
                    Log.e("输入的内容不符合正则表达式，请重新输入:");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inputContent;
    }

}
