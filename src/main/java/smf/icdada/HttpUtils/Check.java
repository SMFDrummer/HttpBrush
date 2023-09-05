package smf.icdada.HttpUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public class Check {
    public static class V303 {
        private String responseBody;

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        public boolean isValid(int r) {
            if (JSON.isValidObject(responseBody)) {
                JSONObject jsonObject = JSON.parseObject(responseBody);
                if (r == 0) return jsonObject.getIntValue("r") == r &&
                        jsonObject.containsKey("d") &&
                        JSON.isValidArray(jsonObject.getString("d")) &&
                        JSON.isValidObject(jsonObject.getJSONArray("d").getJSONObject(0).getString("data"));
                else return jsonObject.getIntValue("r") == r;
            } else return false;
        }

        public class data {
            private final JSONObject data = JSON.parseObject(JSON.parseObject(responseBody).getJSONArray("d").getJSONObject(0).getString("data"));

            public boolean containsKey(String key) {
                return data.containsKey(key);
            }

            public String getString(String key) {
                return data.getString(key);
            }

            public int getIntValue(String key) {
                return data.getIntValue(key);
            }

            public JSONObject getData() {
                return data;
            }

            public JSONObject getJSONObject(String key) {
                return data.getJSONObject(key);
            }

            public JSONArray getJSONArray(String key) {
                return data.getJSONArray(key);
            }
        }
    }

    public static class V316 {
        private String responseBody;

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        public boolean isValid(int r) {
            if (JSON.isValidObject(responseBody)) {
                JSONObject jsonObject = JSON.parseObject(responseBody);
                if (r == 0) return jsonObject.getIntValue("r") == r &&
                        jsonObject.containsKey("d") &&
                        JSON.isValidObject(jsonObject.getString("d"));
                else return jsonObject.getIntValue("r") == r;
            } else return false;
        }

        public class d {
            private final JSONObject d = JSON.parseObject(responseBody).getJSONObject("d");

            public boolean containsKey(String key) {
                return d.containsKey(key);
            }

            public JSONObject getData() {
                return d;
            }

            public JSONObject getJSONObject(String key) {
                return d.getJSONObject(key);
            }

            public JSONArray getJSONArray(String key) {
                return d.getJSONArray(key);
            }
        }
    }

    public static class V437 {
        private String responseBody;

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        public boolean isValid(int r) {
            if (JSON.isValidObject(responseBody)) {
                JSONObject jsonObject = JSON.parseObject(responseBody);
                if (r == 0) return jsonObject.getIntValue("r") == r &&
                        jsonObject.containsKey("d") &&
                        JSON.isValidObject(jsonObject.getString("d"));
                else return jsonObject.getIntValue("r") == r;
            } else return false;
        }

        public boolean isNew() {
            JSONObject d = JSON.parseObject(responseBody).getJSONObject("d");
            if (d.containsKey("isnew")) return d.getBooleanValue("isnew");
            else return true;
        }
    }
}
