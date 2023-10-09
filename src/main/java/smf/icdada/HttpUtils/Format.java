package smf.icdada.HttpUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import smf.icdada.Log;
import smf.icdada.smfScanner;

import java.lang.reflect.Method;

/**
 * @author SMF & icdada
 * @描述: Format请求生成类
 * <p>
 * 包含必要数据包的特征值获取以及标识检查方法。
 * </p>
 */
public class Format {
    public static boolean hasSubclass(String className) {
        Class<?>[] subclasses = Format.class.getDeclaredClasses();
        for (Class<?> subclass : subclasses) {
            if (subclass.getSimpleName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    private static Class<?> getSubclass(String className) {
        Class<?>[] subclasses = Format.class.getDeclaredClasses();
        for (Class<?> subclass : subclasses) {
            if (subclass.getSimpleName().equals(className)) {
                return subclass;
            }
        }
        return null;
    }

    public static JSONArray writeFormat(String className) {
        Class<?> subclass = Format.getSubclass(className);
        if (subclass != null) {
            try {
                Method writeMethod = subclass.getDeclaredMethod("write");
                return (JSONArray) writeMethod.invoke(null);
            } catch (Exception e) {
                Log.w(e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.e(className + " 子类不存在");
        }
        return null;
    }

    protected static JSONObject write(String key, String type) {
        JSONObject jsonObject = new JSONObject();
        Log.v("请输入自定义键" + key + "的值，类型为" + type);
        Object value = switch (type) {
            case "int" -> smfScanner.Int(false);
            case "double" -> smfScanner.Double(false);
            case "String" -> smfScanner.String(false);
            case "JSONObject" -> smfScanner.JSONObject(false);
            case "JSONArray" -> smfScanner.JSONArray(false);
            default -> null;
        };
        jsonObject.put("key", key);
        jsonObject.put("type", type);
        jsonObject.put("value", value);
        return jsonObject;
    }

    public static class V303 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(Format.write("id", "int"));
            return jsonArray;
        }
    }

    public static class V876 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(Format.write("code", "String"));
            return jsonArray;
        }
    }

    public static class V877 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(Format.write("index", "String"));
            return jsonArray;
        }
    }

    public static class V878 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(Format.write("type", "String"));
            return jsonArray;
        }
    }

    public static class V904 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(Format.write("t", "String"));
            return jsonArray;
        }
    }

    public static class V927 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(Format.write("pl", "JSONArray"));
            return jsonArray;
        }
    }

    public static class V993 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(Format.write("giftId", "String"));
            return jsonArray;
        }
    }
}
