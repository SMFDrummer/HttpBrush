package smf.icdada.HttpUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import smf.icdada.Log;
import smf.icdada.smfScanner;

import java.lang.String;
import java.lang.reflect.Method;

import static smf.icdada.HttpUtils.Format.DataType.String;
import static smf.icdada.HttpUtils.Format.DataType.*;

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

    public static JSONObject write(String keyPath, DataType type) {
        JSONObject jsonObject = new JSONObject();
        Log.v("请输入预设键路径" + keyPath + "的值，类型为" + type);
        Object value = switch (type) {
            case Int -> smfScanner.Int(false);
            case Double -> smfScanner.Double(false);
            case String -> smfScanner.String(false);
            case JSONObject -> smfScanner.JSONObject(false);
            case JSONArray -> smfScanner.JSONArray(false);
        };
        jsonObject.put("keyPath", keyPath);
        jsonObject.put("type", type.toString());
        jsonObject.put("value", value);
        return jsonObject;
    }

    public enum DataType {
        Int,
        Double,
        String,
        JSONObject,
        JSONArray
    }

    public static class V302 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            Log.i("刷取道具配置，格式模板为：[{\"i\":Int,\"q\":Int,\"f\":String}]");
            jsonArray.add(Format.write("$.t.o", JSONArray));
            Log.i("通关数，通常为不超过INT_MAX的整数，但不建议填写过高数值");
            jsonArray.add(Format.write("$.t.uk", Int));
            return jsonArray;
        }
    }

    public static class V303 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            Log.i("欲进入的活动ID，通常为五位整数");
            jsonArray.add(Format.write("$.t.al[0].id", Int));
            return jsonArray;
        }
    }

    public static class V876 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            Log.i("待刷取的邀请码，格式为9字符全大写");
            jsonArray.add(Format.write("$.t.code", String));
            return jsonArray;
        }
    }

    public static class V877 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            Log.i("数据包序号，通常为不超过10的整数");
            jsonArray.add(Format.write("$.t.index", String));
            return jsonArray;
        }
    }

    public static class V878 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            Log.w("不建议该数据包使用默认模板");
            Log.i("抽取魔豆奖励，通常为0或1");
            jsonArray.add(Format.write("$.t.type", String));
            return jsonArray;
        }
    }

    public static class V904 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            Log.i("剧院币刷取，通常为7");
            jsonArray.add(Format.write("$.t.t", String));
            return jsonArray;
        }
    }

    public static class V921 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            Log.i("广告刷新，需填入活动id");
            jsonArray.add(Format.write("$.t.oi", String));
            return jsonArray;
        }
    }

    public static class V927 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            Log.i("无尽检测植物列表，格式模板为：[{\"i\":Int,\"q\":Int}]");
            Log.e("高危险值，请填写前后务必慎重！");
            jsonArray.add(Format.write("$t.pr.pl", JSONArray));
            return jsonArray;
        }
    }

    public static class V993 {
        public static JSONArray write() {
            JSONArray jsonArray = new JSONArray();
            Log.i("获取3000钻石任务，值一般为0到3的其中一个");
            jsonArray.add(Format.write("$.t.giftId", String));
            return jsonArray;
        }
    }
}
