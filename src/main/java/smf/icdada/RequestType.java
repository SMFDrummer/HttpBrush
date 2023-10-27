package smf.icdada;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import static smf.icdada.HttpUtils.Base.getUisk;

/**
 * @author SMF & icdada
 * @描述: 数据包枚举类
 * <p>
 * 包含核心数据包枚举类，不包含各外部调用，请自行使用外部配置。
 * </p>
 */
@SuppressWarnings("unused")
public enum RequestType {
    I4("{\"i\":\"I4\",\"r\":0,\"t\":{}}"),
    V202("{\"i\":\"V202\",\"r\":0,\"t\":{\"ci\":\"93\",\"cv\":null,\"di\":\"\",\"li\":\"486efa91941f659207c906eed2e70dfe\",\"oi\":null,\"pi\":\"\",\"r\":\"703740081\",\"s\":\"a3fa45b7cfc5c85201dc139bab0ee6fb\",\"ui\":\"\"}}"),
    V302("{\"i\":\"V302\",\"r\":0,\"t\":{\"nfc\":\"1\",\"o\":null,\"pi\":null,\"sk\":null,\"ui\":null,\"uk\":null}}"),
    V303("{\"i\":\"V303\",\"r\":0,\"t\":{\"al\":[{\"id\":null,\"abi\":0,\"type\":1,\"config_version\":1}],\"ci\":\"93\",\"cs\":\"0\",\"pack\":null,\"pi\":null,\"sk\":null,\"ui\":null,\"v\":null}}"),
    V316("{\"i\":\"V316\",\"r\":0,\"t\":{\"b\":\"0\",\"n\":\"\",\"pi\":null,\"sk\":null,\"ui\":null}}"),
    V322("{\"i\":\"V322\",\"r\":0,\"t\":{\"acd\":{\"g\":null,\"ubn\":0,\"uebn\":0,\"upnl\":[]},\"fr\":\"1\",\"pi\":null,\"pr\":{\"pl\":[]},\"ri\":{\"l\":null,\"ml\":9,\"lwml\":0,\"lc\":[1,1,1,1,1],\"eb\":0,\"eub\":0,\"pl\":[],\"dm\":\"\",\"ls\":0,\"ds\":0,\"bn\":1,\"bu\":0,\"m\":65,\"jc\":6,\"jl\":5,\"par\":30,\"pas\":500,\"on\":\"\",\"alt\":15,\"amt\":15,\"cil\":[]},\"sk\":null,\"ui\":null,\"w\":\"4\"}}"),
    V323("{\"i\":\"V323\",\"r\":0,\"t\":{\"ad\":\"0\",\"l\":[1199,1199,0,0,0],\"pi\":null,\"sk\":null,\"t\":\"0\",\"ui\":null}}"),
    V437("{\"i\":\"V437\",\"r\":0,\"t\":{\"pi\":null,\"sk\":null,\"ui\":null}}"),
    V876("{\"i\":\"V876\",\"r\":0,\"t\":{\"code\":null,\"pi\":null,\"sk\":null,\"star\":\"50\",\"ui\":null}}"),
    V877("{\"i\":\"V877\",\"r\":0,\"t\":{\"index\":null,\"pi\":null,\"sk\":null,\"ui\":null}}"),
    V878("{\"i\":\"V878\",\"r\":0,\"t\":{\"pi\":null,\"sk\":null,\"type\":null,\"ui\":null}}"),
    V902("{\"i\":\"V902\",\"r\":0,\"t\":{\"n\":\"20\",\"pi\":null,\"s\":\"1\",\"sk\":null,\"t\":\"4\",\"ui\":null}}"),
    V904("{\"i\":\"V904\",\"r\":0,\"t\":{\"pi\":null,\"sk\":null,\"t\":null,\"ui\":null}}"),
    V921("{\"i\":\"V921\",\"r\":0,\"t\":{\"oi\":null,\"pi\":null,\"sk\":null,\"ui\":null}}"),
    V927("{\"i\":\"V927\",\"r\":0,\"t\":{\"fr\":{\"t\":\"1\",\"l\":\"2\",\"g\":\"3\",\"s\":\"9888\",\"r\":\"1\",\"b\":\"1.000000\"},\"g\":\"1\",\"on\":\"726c0c5a88d349f986085e29ca731151\",\"pi\":null,\"pr\":{\"pl\":null},\"sk\":null,\"ui\":null}}"),
    V993("{\"i\":\"V993\",\"r\":0,\"t\":{\"giftId\":null,\"pi\":null,\"sk\":null,\"ui\":null}}"),
    V9999("");
    private final String requestBody;

    RequestType(String requestBody) {
        this.requestBody = requestBody;
    }

    public static boolean checkRequestBody(String identifier) {
        for (RequestType requestType : RequestType.values()) {
            if (requestType.name().startsWith(identifier)) return true;
        }
        return false;
    }

    public static void printRequestBody(String identifier) {
        for (RequestType requestType : RequestType.values()) {
            if (requestType.name().startsWith(identifier)) {
                Log.a("数据包标识:" + requestType.name() + " || " + requestType.getRequestBody());
            }
        }
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getRequestBody(RequestType index, int userId) {
        JSONObject parse = JSONObject.parse(getRequestBody());
        JSONObject t = parse.getJSONObject("t");
        t.put("ver_", Inter.iosVersion);
        switch (index) {
            case V202 -> {
                t.put("cv", Inter.androidVersion);
                t.put("oi", Inter.oi + "X" + userId);
            }
            case V303 -> {
                t.put("pack", Inter.packageValue);
                t.put("v", Inter.androidVersion);
            }
            default -> {
                return requestBody;
            }
        }
        return parse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
    }

    public String getRequestBody(RequestType index, int userId, Object... param) {
        JSONObject parse = JSONObject.parse(getRequestBody());
        JSONObject t = parse.getJSONObject("t");
        if (index != V202) {
            Result uisk = getUisk(userId);
            String xi = uisk.getUi();
            String sk = uisk.getSk();
            t.put("pi", xi);
            t.put("sk", sk);
            t.put("ui", xi);
        }
        t.put("ver_", Inter.iosVersion);
        switch (index) {
            case V202 -> {
                t.put("cv", Inter.androidVersion);
                t.put("oi", Inter.oi + "X" + userId);
            }
            case V302 -> {
                t.put("o", param[0]);
                t.put("uk", param[1]);
            }
            case V303 -> {
                JSONObject al = t.getJSONArray("al").getJSONObject(0);
                al.put("id", param[0]);
                t.put("pack", Inter.packageValue);
                t.put("v", Inter.androidVersion);
            }
            case V316, V323, V437, V902 -> {
            }
            case V876 -> t.put("code", param[0]);
            case V877 -> t.put("index", param[0]);
            case V878 -> {
                if (param[0].equals("1")) {
                    t.put("bai", "0");
                    t.put("gi", "0");
                }
                t.put("type", param[0]);
            }
            case V904 -> t.put("t", param[0]);
            case V921 -> t.put("oi",param[0]);
            case V927 -> {
                JSONObject pr = t.getJSONObject("pr");
                t.put("pl", param[0]);
            }
            case V993 -> t.put("giftId", param[0]);
            default -> {
                return requestBody;
            }
        }
        return parse.toJSONString(JSONWriter.Feature.WriteMapNullValue);
    }

}
