package cn.InstFS.wkr.NetworkMining.TaskConfigure;

import common.ErrorLogger;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Arbor vlinyq@gmail.com
 * @version 2016/8/11
 */
public class GlobalConfig {
    private static class GlobalConfigHolder {
        private static final GlobalConfig INSTANCE = new GlobalConfig();
    }
    private String configPath = "./configs/globalConfig.xml"; //配置文件路径
    private String dataPath; //源数据路径
    private String granularityList; //可选时间粒度

    private String pmParamPath; //周期参数文件路径
    private String omParamPath; //异常参数文件路径
    private String arParamPath; //关联规则参数文件路径
    private String pcapParseParamPath; //pcap解析参数文件路径
    private String forecastParamPath; //预测参数文件路径
    private String seriesStatisticParamPath; // 统计参数文件路径
    private String sequencePatternParamPath; //频繁项参数文件路径
    private String loggerPath;  //日志存储路径

    private String pcapParseDisParamPath;//分布式pcap解析参数文件路径
    private String serverParamPath;//分布式配置路径
    private String clientParamPath;//客户端配置路径
    private String portParamPath;//所有端口（协议）路径

    public String getLoggerPath() {
		return loggerPath;
	}

	public void setLoggerPath(String loggerPath) {
		this.loggerPath = loggerPath;
	}

	private GlobalConfig() {
        Element config = getRootElement();
        initParam(config);
    }

    public static final GlobalConfig getInstance() {
        return GlobalConfigHolder.INSTANCE;
    }

    private Element getRootElement() {
        Element classElement = null;
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            File inputFile = new File(configPath);
            Document document = saxBuilder.build(inputFile);

            classElement = document.getRootElement();
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
            ErrorLogger.log("全局基本配置信息文件读取错误，或因文件不存在或文档结构错误");
            ErrorLogger.log("文件地址",configPath);
        }

        return classElement;
    }

    private void initParam(Element config) {
        dataPath = config.getChildText("dataPath");
        granularityList = config.getChildText("granularityList");
        pmParamPath = config.getChildText("pmParamPath");
        omParamPath = config.getChildText("omParamPath");
        arParamPath = config.getChildText("arParamPath");
        pcapParseParamPath = config.getChildText("pcapParseParamPath");
        seriesStatisticParamPath = config.getChildText("statisticsParamPath");
        sequencePatternParamPath = config.getChildText("sequencePatternParamPath");
        loggerPath = config.getChildText("loggerPath");
        forecastParamPath=config.getChildText("forecastParamsPath");
        pcapParseDisParamPath = config.getChildText("pcapParseDisParamsParamsPath");
        serverParamPath = config.getChildText("serverParamsParamsPath");
        clientParamPath = config.getChildText("clientParamsParamsPath");
        portParamPath = config.getChildText("portParamPath");
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getGranularityList() {
        return granularityList;
    }

    public void setGranularityList(String granularityList) {
        this.granularityList = granularityList;
    }

    public String getPmParamPath() {
        return pmParamPath;
    }

    public void setPmParamPath(String pmParamPath) {
        this.pmParamPath = pmParamPath;
    }

    public String getOmParamPath() {
        return omParamPath;
    }

    public void setOmParamPath(String omParamPath) {
        this.omParamPath = omParamPath;
    }

    public String getArParamPath() {
        return arParamPath;
    }

    public void setArParamPath(String arParamPath) {
        this.arParamPath = arParamPath;
    }

    public String getPcapParseParamPath() {
        return pcapParseParamPath;
    }

    public void setPcapParseParamPath(String pcapParseParamPath) {
        this.pcapParseParamPath = pcapParseParamPath;
    }

    public String getForecastParamPath() {
        return forecastParamPath;
    }

    public void setForecastParamPath(String forecastParamPath) {
        this.forecastParamPath = forecastParamPath;
    }

    public String getSeriesStatisticParamPath() {
        return seriesStatisticParamPath;
    }

    public void setSeriesStatisticParamPath(String seriesStatisticParamPath) {
        this.seriesStatisticParamPath = seriesStatisticParamPath;
    }

    public String getSequencePatternParamPath() {
        return sequencePatternParamPath;
    }

    public void setSequencePatternParamPath(String sequencePatternParamPath) {
        this.sequencePatternParamPath = sequencePatternParamPath;
    }

    public String getPcapParseDisParamPath() {
        return pcapParseDisParamPath;
    }

    public void setPcapParseDisParamPath(String pcapParseDisParamPath) {
        this.pcapParseDisParamPath = pcapParseDisParamPath;
    }

    public String getServerParamPath() {
        return serverParamPath;
    }

    public void setServerParamPath(String serverParamPath) {
        this.serverParamPath = serverParamPath;
    }

    public String getClientParamPath() {
        return clientParamPath;
    }

    public void setClientParamPath(String clientParamPath) {
        this.clientParamPath = clientParamPath;
    }

    public String getPortParamPath() {
        return portParamPath;
    }

    public void setPortParamPath(String portParamPath) {
        this.portParamPath = portParamPath;
    }

    public static void main (String[] args) {
        GlobalConfig config = GlobalConfig.getInstance();
    }
}

