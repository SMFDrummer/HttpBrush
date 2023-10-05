package smf.icdada;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

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
    public static int Int(boolean requireConfirmation) {
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
                String confirmation = scanner.nextLine();
                isValid = !confirmation.equalsIgnoreCase("N");
            } else {
                isValid = true;
            }
        }
        return number;
    }

    public static int Int(boolean requireConfirmation, String regex) {
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
            if (Pattern.matches(regex, Integer.toString(number))) {
                if (requireConfirmation) {
                    Log.i("你输入的是 " + number);
                    Log.v("如果确认请输入任意字符，否则请输入N或n:");
                    String confirmation = scanner.nextLine();
                    isValid = !confirmation.equalsIgnoreCase("N");
                } else {
                    isValid = true;
                }
            } else {
                Log.e("输入的整数不符合正则表达式，请重新输入:");
            }
        }
        return number;
    }

    public static double Double(boolean requireConfirmation) {
        Scanner scanner = new Scanner(System.in);
        double number = 0.0;
        boolean isValid = false;
        while (!isValid) {
            while (!scanner.hasNextDouble()) {
                Log.e("输入无效，请输入一个小数:");
                scanner.next();
            }
            number = scanner.nextDouble();
            scanner.nextLine();
            if (requireConfirmation) {
                Log.i("你输入的是 " + number);
                Log.v("如果确认请输入任意字符，否则请输入N或n:");
                String confirmation = scanner.nextLine();
                isValid = !confirmation.equalsIgnoreCase("N");
            } else {
                isValid = true;
            }
        }
        return number;
    }

    public static double Double(boolean requireConfirmation, String regex) {
        Scanner scanner = new Scanner(System.in);
        double number = 0.0;
        boolean isValid = false;
        while (!isValid) {
            while (!scanner.hasNextDouble()) {
                Log.e("输入无效，请输入一个小数:");
                scanner.next();
            }
            number = scanner.nextDouble();
            scanner.nextLine();
            if (Pattern.matches(regex, Double.toString(number))) {
                if (requireConfirmation) {
                    Log.i("你输入的是 " + number);
                    Log.v("如果确认请输入任意字符，否则请输入N或n:");
                    String confirmation = scanner.nextLine();
                    isValid = !confirmation.equalsIgnoreCase("N");
                } else {
                    isValid = true;
                }
            } else {
                Log.e("输入的小数不符合正则表达式，请重新输入:");
            }
        }
        return number;
    }

    public static boolean Boolean(boolean requireConfirmation) {
        Scanner scanner = new Scanner(System.in);
        boolean bool = false;
        boolean isValid = false;
        while (!isValid) {
            Log.v("请输入Y或N:");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("N")) {
                bool = input.equalsIgnoreCase("Y");
                isValid = true;
            } else {
                Log.e("输入无效，请输入Y或N:");
            }
            if (requireConfirmation && isValid) {
                Log.v("你输入的是 " + (bool ? "Y" : "N"));
                Log.v("如果确认请输入任意字符，否则请输入N或n:");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("N")) {
                    isValid = false;
                }
            }
        }
        return bool;
    }

    public static String LongString(boolean requireConfirmation) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder inputContent = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        boolean isValid = false;
        while (!isValid) {
            inputContent.setLength(0);
            Log.v("请输入内容，连续多次回车结束输入:");
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
                    inputContent.setLength(inputContent.length() - lineSeparator.length());
                    while (inputContent.charAt(inputContent.length() - 1) == '\n' || inputContent.charAt(inputContent.length() - 1) == '\r' || inputContent.charAt(inputContent.length() - 1) == '\t') {
                        inputContent.setLength(inputContent.length() - 1);
                    }
                }
                if (requireConfirmation) {
                    Log.i("你输入的内容是:\n" + inputContent);
                    Log.v("如果确认请输入任意字符，否则请输入N或n:");
                    String confirmation = reader.readLine();
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

    public static String String(boolean requireConfirmation) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputContent = "";
        boolean isValid = false;
        while (!isValid) {
            try {
                inputContent = reader.readLine();
                if (requireConfirmation) {
                    Log.i("你输入的内容是:" + inputContent);
                    Log.v("如果确认请输入任意字符，否则请输入N或n:");
                    String confirmation = reader.readLine();
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

    public static String String(boolean requireConfirmation, String regex) {
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
                        String confirmation = reader.readLine();
                        isValid = !confirmation.equalsIgnoreCase("N");
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

    public static JSONObject JSONObject(boolean requireConfirmation) {
        JSONObject jsonObject = new JSONObject();
        boolean isValid = false;
        while (!isValid) {
            String s = LongString(false);
            if (!JSON.isValidObject(s)) {
                Log.e("输入的值不是正确格式的JSONObject，请重新输入");
            } else {
                jsonObject = JSON.parseObject(s);
                if (requireConfirmation) {
                    Log.i("你输入的内容是:" + jsonObject);
                    Log.v("如果确认请输入任意字符，否则请输入 N 或 n:");
                    String confirmation = new Scanner(System.in).nextLine();
                    isValid = !confirmation.equalsIgnoreCase("N");
                } else {
                    isValid = true;
                }
            }
        }
        return jsonObject;
    }

    public static JSONArray JSONArray(boolean requireConfirmation) {
        JSONArray jsonArray = new JSONArray();
        boolean isValid = false;
        while (!isValid) {
            String s = LongString(false);
            if (!JSON.isValidArray(s)) {
                Log.e("输入的值不是正确格式的JSONArray，请重新输入");
            } else {
                jsonArray = JSON.parseArray(s);
                if (requireConfirmation) {
                    Log.i("你输入的内容是:" + jsonArray);
                    Log.v("如果确认请输入任意字符，否则请输入 N 或 n:");
                    String confirmation = new Scanner(System.in).nextLine();
                    isValid = !confirmation.equalsIgnoreCase("N");
                } else {
                    isValid = true;
                }
            }
        }
        return jsonArray;
    }
}
