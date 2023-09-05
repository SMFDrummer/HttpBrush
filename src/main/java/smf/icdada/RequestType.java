package smf.icdada;

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
    I4("--_{{}}_\n" + "Content-Disposition: form-data; name=\"req\"\n" + "\n" + "I4\n" + "--_{{}}_"),
    OI("{\"i\":\"V202\",\"r\":0,\"t\":{\"ci\":\"93\",\"cv\":\"" + Inter.versionName + "\",\"di\":\"\",\"li\":\"486efa91941f659207c906eed2e70dfe\",\"oi\":\"" + Inter.oi + "X%d\",\"pi\":\"\",\"r\":\"703740081\",\"s\":\"a3fa45b7cfc5c85201dc139bab0ee6fb\",\"ui\":\"\"}}"),
    IN("{\"i\":\"V303\",\"r\":0,\"t\":{\"al\":[{\"id\":10868,\"abi\":0,\"type\":1,\"config_version\":1}],\"ci\":\"93\",\"cs\":\"0\",\"pack\":\"com.popcap.pvz2cthdbk\",\"pi\":\"%s\",\"sk\":\"%s\",\"ui\":\"%s\",\"v\":\"" + Inter.versionName + "\"}}"),
    ANNI_BRUSH("{\"i\":\"V876\",\"r\":0,\"t\":{\"code\":\"%s\",\"pi\":\"%s\",\"sk\":\"%s\",\"star\":\"50\",\"ui\":\"%s\"}}"),
    GET("{\"i\":\"V316\",\"r\":0,\"t\":{\"b\":\"0\",\"n\":\"\",\"pi\":\"%s\",\"sk\":\"%s\",\"ui\":\"%s\"}}"),
    ISNEW("{\"i\":\"V437\",\"r\":0,\"t\":{\"pi\":\"%s\",\"sk\":\"%s\",\"ui\":\"%s\"}}"),
    ANNI_GET("{\"i\":\"V877\",\"r\":0,\"t\":{\"index\":\"%d\",\"pi\":\"%s\",\"sk\":\"%s\",\"ui\":\"%s\"}}"),
    ANNI_BEAN_1("{\"i\":\"V878\",\"r\":0,\"t\":{\"pi\":\"%s\",\"sk\":\"%s\",\"type\":\"0\",\"ui\":\"%s\"}}"),
    ANNI_BEAN_2("{\"i\":\"V878\",\"r\":0,\"t\":{\"bai\":\"0\",\"gi\":\"0\",\"pi\":\"%s\",\"sk\":\"%s\",\"type\":\"1\",\"ui\":\"%s\"}}");
    private final String requestBody;

    RequestType(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestBody() {
        return String.format(requestBody);
    }

    public String getRequestBody(int userId) {
        Result uisk = getUisk(userId);
        String ui = uisk.getUi();
        String sk = uisk.getSk();
        return String.format(requestBody, ui, sk, ui);
    }

    public String getRequestBody(int userId, int index) {
        Result uisk = getUisk(userId);
        String ui = uisk.getUi();
        String sk = uisk.getSk();
        return String.format(requestBody, index, ui, sk, ui);
    }

    public String getRequestBody(int userId, String inviteCode) {
        Result uisk = getUisk(userId);
        String ui = uisk.getUi();
        String sk = uisk.getSk();
        return String.format(requestBody, inviteCode, ui, sk, ui);
    }

    public String getRequestBodyById(int userId) {
        return String.format(requestBody, userId);
    }
}
