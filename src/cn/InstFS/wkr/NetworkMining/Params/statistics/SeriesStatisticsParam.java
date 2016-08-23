package cn.InstFS.wkr.NetworkMining.Params.statistics;

import org.jdom.Element;

public class SeriesStatisticsParam {

	private int splitM;
	
	private double ratio;
	public SeriesStatisticsParam(int splitM,double ratio){
		
		this.splitM = splitM;
		this.ratio = ratio;
	}
	public SeriesStatisticsParam(Element paramsConfig){
		
		String param = "";
    	param = paramsConfig.getChildText("splitM");
    	if(param != null){
    		splitM = Integer.parseInt(param);
    	}
    	param = paramsConfig.getChildText("ratio");
		if(param != null){
			ratio = Double.parseDouble(param);
		}
	}
	public int getSplitM() {
		return splitM;
	}
	public void setSplitM(int splitM) {
		this.splitM = splitM;
	}
	public double getRatio() {
		return ratio;
	}
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
}
