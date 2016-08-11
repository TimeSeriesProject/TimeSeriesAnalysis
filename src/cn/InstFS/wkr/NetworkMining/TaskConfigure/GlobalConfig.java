package cn.InstFS.wkr.NetworkMining.TaskConfigure;

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
        }

        return classElement;
    }

    private void initParam(Element config) {
        dataPath = config.getChildText("dataPath");
        granularityList = config.getChildText("granularityList");
        pmParamPath = config.getChildText("pmParamPath");
        omParamPath = config.getChildText("omParamPath");
        arParamPath = config.getChildText("arParamPath");
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

    public static void main (String[] args) {
        GlobalConfig config = GlobalConfig.getInstance();
    }
}

