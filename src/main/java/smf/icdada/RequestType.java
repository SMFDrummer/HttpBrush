package smf.icdada;

/**
 * @author SMF & icdada
 * @描述: 数据包枚举类
 * <p>
 * 包含核心数据包枚举类，不包含各外部调用，请自行使用外部配置。
 * </p>
 */
public enum RequestType {
    I4("", "--_{{}}_\n" + "Content-Disposition: form-data; name=\"req\"\n" + "\n" + "I4\n" + "--_{{}}_"),
    OI_REAL("", "{\"i\":\"V202\",\"r\":0,\"t\":{\"ci\":\"93\",\"cv\":\"" + Inter.versionName + "." + Inter.versionCode + "\",\"di\":\"\",\"li\":\"\",\"oi\":\"%dX%d\",\"pi\":\"\",\"r\":\"703740081\",\"s\":\"a3fa45b7cfc5c85201dc139bab0ee6fb\",\"ui\":\"\"}}"),
    OI_FAKE("", "{\"i\":\"V202\",\"r\":0,\"t\":{\"ci\":\"93\",\"cv\":\"" + Inter.versionName + "\",\"di\":\"\",\"li\":\"\",\"oi\":\"%dX%d\",\"pi\":\"\",\"r\":\"703740081\",\"s\":\"a3fa45b7cfc5c85201dc139bab0ee6fb\",\"ui\":\"\"}}"),
    ANNI_IN("", "{\"i\":\"V303\",\"r\":0,\"t\":{\"al\":[{\"id\":10868,\"abi\":0,\"type\":1,\"config_version\":1}],\"ci\":\"93\",\"cs\":\"0\",\"pack\":\"com.popcap.pvz2cthdbk\",\"pi\":\"%s\",\"sk\":\"%s\",\"ui\":\"%s\",\"v\":\"" + Inter.versionName + "\"}}"),
    ANNI_BRUSH("", "{\"i\":\"V876\",\"r\":0,\"t\":{\"code\":\"%s\",\"pi\":\"%s\",\"sk\":\"%s\",\"star\":\"50\",\"ui\":\"%s\"}}"),
    GET("", "{\"i\":\"V316\",\"r\":0,\"t\":{\"b\":\"0\",\"n\":\"\",\"pi\":\"%s\",\"sk\":\"%s\",\"ui\":\"%s\"}}"),
    ISNEW("", "{\"i\":\"V437\",\"r\":0,\"t\":{\"pi\":\"%s\",\"sk\":\"%s\",\"ui\":\"%s\"}}\n");
    private final String name;
    private final String requestBody;

    RequestType(String name, String requestBody) {
        this.name = name;
        this.requestBody = requestBody;
    }

    public String getName() {
        return name;
    }

    public String getRequestBody(String ui, String sk) {
        return String.format(requestBody, ui, sk, ui);
    }

    public String getRequestBody() {
        return String.format(requestBody);
    }

    public String getRequestBody(int userId) {
        return String.format(requestBody, userId);
    }

    public String getRequestBody(String inviteCode, String ui, String sk) {
        return String.format(requestBody, inviteCode, ui, sk, ui);
    }

    public String getRequestBody(int oi, int userID) {
        return String.format(requestBody, oi, userID);
    }
}
