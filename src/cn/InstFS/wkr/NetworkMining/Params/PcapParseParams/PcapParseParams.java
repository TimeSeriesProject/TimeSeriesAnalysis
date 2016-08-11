package cn.InstFS.wkr.NetworkMining.Params.PcapParseParams;

import org.jdom.Element;

public class PcapParseParams {
	
	private String pcapPath;
	private String outputPath;
	public PcapParseParams(Element element)
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
