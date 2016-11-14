package cn.InstFS.wkr.NetworkMining.Params.PcapParseParams;

import org.jdom.Element;

public class PcapParseParams {
	
	private String pcapPath;
	private String outputPath;
	private boolean parseAll;
	public PcapParseParams(Element element)
	{
		pcapPath= element.getChildText("pcapPath");
		outputPath= element.getChildText("outputPath");
		parseAll = element.getChildText("parseAll").equals("true");
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

	public boolean isParseAll() {
		return parseAll;
	}

	public void setParseAll(boolean parseAll) {
		this.parseAll = parseAll;
	}
}
