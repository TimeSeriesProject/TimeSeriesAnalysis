package cn.InstFS.wkr.NetworkMining.Params.DistributeParams;

import org.jdom.Element;

/**
 * Created by zsc on 2016/10/28.
 */
public class ServerParams {
    private String port;
    public ServerParams(Element element)
    {
        port= element.getChildText("port");
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
