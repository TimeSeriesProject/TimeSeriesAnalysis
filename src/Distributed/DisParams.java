package Distributed;

import cn.InstFS.wkr.NetworkMining.Params.ParamsAPI;

/**
 * Created by zsc on 2016/10/28.
 * 保存配置的变量值，保证下次使用时值不变
 */
public class DisParams {
    //单机pcap
    private static String pcapPath = ParamsAPI.getInstance().getPcapParseParams().getPcapPath();
    private static String outputPath = ParamsAPI.getInstance().getPcapParseParams().getOutputPath();
    private static boolean parseAll = ParamsAPI.getInstance().getPcapParseParams().isParseAll();

    //分布式pcap
    private static String pcapPathDis = ParamsAPI.getInstance().getPcapParseDisParams().getPcapPath();
    private static String outputPathDis = ParamsAPI.getInstance().getPcapParseDisParams().getOutputPath();

    //分布式配置
    private static String port = ParamsAPI.getInstance().getServerParams().getPort();

    //客户端配置
    private static String ip = ParamsAPI.getInstance().getClientParams().getIp();
    private static String clientPort = ParamsAPI.getInstance().getClientParams().getPort();
    private static String cpcapPath = ParamsAPI.getInstance().getClientParams().getPcapPath();
    private static String cOutputPath = ParamsAPI.getInstance().getClientParams().getOutputPath();

    public static String getClientPort() {
        return clientPort;
    }

    public static void setClientPort(String clientPort) {
        DisParams.clientPort = clientPort;
    }

    public static String getcOutputPath() {
        return cOutputPath;
    }

    public static void setcOutputPath(String cOutputPath) {
        DisParams.cOutputPath = cOutputPath;
    }

    public static String getCpcapPath() {
        return cpcapPath;
    }

    public static void setCpcapPath(String cpcapPath) {
        DisParams.cpcapPath = cpcapPath;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        DisParams.ip = ip;
    }

    public static String getOutputPath() {
        return outputPath;
    }

    public static void setOutputPath(String outputPath) {
        DisParams.outputPath = outputPath;
    }

    public static boolean isParseAll() {
        return parseAll;
    }

    public static void setParseAll(boolean parseAll) {
        DisParams.parseAll = parseAll;
    }

    public static String getOutputPathDis() {
        return outputPathDis;
    }

    public static void setOutputPathDis(String outputPathDis) {
        DisParams.outputPathDis = outputPathDis;
    }

    public static String getPcapPath() {
        return pcapPath;
    }

    public static void setPcapPath(String pcapPath) {
        DisParams.pcapPath = pcapPath;
    }

    public static String getPcapPathDis() {
        return pcapPathDis;
    }

    public static void setPcapPathDis(String pcapPathDis) {
        DisParams.pcapPathDis = pcapPathDis;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        DisParams.port = port;
    }
}
