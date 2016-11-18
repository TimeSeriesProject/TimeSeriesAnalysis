package cn.InstFS.wkr.NetworkMining.Params.DistributeParams;

import org.jdom.Element;

/**
 * Created by zsc on 2016/10/28.
 */
public class PcapParseDisParams {
    private String pcapPath;
    private String outputPath;
    public PcapParseDisParams(Element element)
    {
        pcapPath= element.getChildText("pcapPath");
        outputPath= element.getChildText("outputPath");
    }

    public String getPcapPath() {
        return pcapPath;
    }
    public void setPcapPath(String pcapPath) {
        this.pcapPath = pcapPath;
    }
    public String getOutputPath() {
        return outputPath;
    }
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
