package cn.InstFS.wkr.NetworkMining.Params.OMParams;

import org.jdom.Element;

public class OMperiodBasedParams {
	private double GuassK = 3.0;
	private double threshold = 0.8;
	public OMperiodBasedParams(Element paramsConfig){
		String param = "";
    	param = paramsConfig.getChildText("GuassK");
    	if(param != null){
    		GuassK = Double.parseDouble(param);
    	}
    	
    	param = paramsConfig.getChildText("threshold");
    	if(param != null){
    		threshold = Double.parseDouble(param);
    	}   	    	
	}
	public double getGuassK() {
		return GuassK;
	}
	public void setGuassK(double guassK) {
		GuassK = guassK;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
}
