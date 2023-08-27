package smf.icdada;

/**
 * @author SMF & icdada
 * @描述: 绑定输出类
 * <p>
 * 包含程序自定义返回值类型 Result，该类型可绑定两个互关值进行捆绑输出。
 * </p>
 */
public class Result {
    private String ui, sk, proxyHost;
    private int proxyPort;

    public Result(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    public Result(String ui, String sk) {
        this.ui = ui;
        this.sk = sk;
    }

    public String getUi() {
        return ui;
    }

    public String getSk() {
        return sk;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }
}