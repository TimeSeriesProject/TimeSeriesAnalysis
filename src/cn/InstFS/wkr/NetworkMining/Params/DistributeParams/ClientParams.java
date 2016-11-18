package cn.InstFS.wkr.NetworkMining.Params.DistributeParams;

import org.jdom.Element;

/**
 * Created by zsc on 2016/10/28.
 */
public class ClientParams {
    private String ip;
    private String port;
    private String pcapPath;
    private String outputPath;
    public ClientParams(Element element)
    {
        ip = element.getChildText("ip");
        port= element.getChildText("port");
        pcapPath = element.getChildText("pcapPath");
        outputPath = element.getChildText("outputPath");
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getPcapPath() {
        return pcapPath;
    }

    public void setPcapPath(String pcapPath) {
        this.pcapPath = pcapPath;
    }
}
