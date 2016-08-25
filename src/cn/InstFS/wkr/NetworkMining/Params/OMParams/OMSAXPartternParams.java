package cn.InstFS.wkr.NetworkMining.Params.OMParams;

import org.jdom.Element;

public class OMSAXPartternParams {
	private int patternLen = 24;
	private int patternAlphaNum = 5;
	public OMSAXPartternParams(Element paramsConfig){
    	String param = "";
    	param = paramsConfig.getChildText("patternLen");
    	if(param != null){
    		patternLen = Integer.parseInt(param);
    	}
    	
    	param = paramsConfig.getChildText("patternAlphaNum");
    	if(param != null){
    		patternAlphaNum = Integer.parseInt(param);
    	}
    }
	public int getPatternLen() {
		return patternLen;
	}
	public void setPatternLen(int patternLen) {
		this.patternLen = patternLen;
	}
	public int getPatternAlphaNum() {
		return patternAlphaNum;
	}
	public void setPatternAlphaNum(int patternAlphaNum) {
		this.patternAlphaNum = patternAlphaNum;
	}
	
}
