package smf.icdada.HttpUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import smf.icdada.Result;

/**
 * @author SMF & icdada
 * @描述: Check响应检查类
 * <p>
 * 包含必要数据包的取值及检查方法。
 * </p>
 */
public class Check {
    private static class V {
        public Data data;
        protected String responseBody;

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
            data = new Data(setData());
        }

        public int getErrorCode() {
            return JSON.parseObject(responseBody).getIntValue("r");
        }

        public boolean isValid(int r) {
            if (JSON.isValidObject(responseBody)) {
                JSONObject jsonObject = JSON.parseObject(responseBody);
                if (r == 0) return jsonObject.getIntValue("r") == r && jsonObject.containsKey("d");
                else return jsonObject.getIntValue("r") == r;
            } else return false;
        }

        public JSONObject setData() {
            String parseString = JSON.parseObject(responseBody).getString("d");
            if (JSON.isValidObject(parseString)) return JSON.parseObject(responseBody).getJSONObject("d");
            else return new JSONObject();
        }

        public record Data(JSONObject data) {
            public boolean containsKey(String keyPath) {
                return JSONPath.contains(data, keyPath);
            }

            public Object get(String keyPath) {
                return JSONPath.eval(data, keyPath);
            }

            public Object get(JSONPath keyPath) {
                return keyPath.eval(data);
            }
        }
    }

    public static class Any {
        private String responseBody;

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        public boolean isValid(int r) {
            if (JSON.isValidObject(responseBody)) {
                JSONObject jsonObject = JSON.parseObject(responseBody);
                if (r == 0) return jsonObject.getIntValue("r") == r && jsonObject.containsKey("d");
                else return jsonObject.getIntValue("r") == r;
            } else return false;
        }
    }

    public static class V202 extends V {
        public Result getUisk() {
            return new Result(data.get("$.ui").toString(), data.get("$.sk").toString());
        }
    }

    public static class V302 extends V {

    }

    public static class V303 extends V {
        @Override
        public boolean isValid(int r) {
            if (JSON.isValidObject(responseBody)) {
                JSONObject jsonObject = JSON.parseObject(responseBody);
                if (r == 0) return jsonObject.getIntValue("r") == r &&
                        jsonObject.containsKey("d") &&
                        JSON.isValidArray(jsonObject.getString("d"));
                else return jsonObject.getIntValue("r") == r;
            } else return false;
        }

        @Override
        public JSONObject setData() {
            if (JSON.parseObject(responseBody).getJSONArray("d").getJSONObject(0).containsKey("data")) {
                return JSON.parseObject(JSON.parseObject(responseBody).getJSONArray("d").getJSONObject(0).getString("data"));
            } else return new JSONObject();
        }
    }

    public static class V316 extends V {
    }

    public static class V437 extends V {
        public boolean isNew() {
            if (data.containsKey("isnew")) return (boolean) data.get("isnew");
            else return true;
        }
    }

    public static class V792 extends V {
    }

    public static class V877 extends V {
    }

    public static class V878 extends V {
    }

    public static class V904 extends V {
    }

    public static class V921 extends V {
    }

    public static class V927 extends V {
    }

    public static class V940 extends V {
    }
}
