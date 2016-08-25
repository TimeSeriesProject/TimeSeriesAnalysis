package cn.InstFS.wkr.NetworkMining.Params.SMParams;

import org.jdom.Element;

public class SMParam {
	private double minSupport;	   // 最小支持度
	private int sizeWindow;	       // 时间窗长	（单位为秒）
	private int stepWindow;	       // 步长
	
	private int splitLeastLen;     //分段的最短长度
	private int clusterNum;        //聚类的类数
	
	
	
	public SMParam(Element element){
		String param = "";
	   	
    	param = element.getChildText("minSupport");
    	if(param != null){
    		minSupport = Integer.parseInt(param);
    	}
    	
    	param = element.getChildText("sizeWindow");
    	if(param != null){
    		sizeWindow = Integer.parseInt(param);
    	}
    	
    	param = element.getChildText("stepWindow");
    	if(param != null){
    		stepWindow = Integer.parseInt(param);
    	}
    	

    	param = element.getChildText("splitLeastLen");
    	if(param != null){
    		splitLeastLen = Integer.parseInt(param);
    	}
    	
    	param = element.getChildText("clusterNum");
    	if(param != null){
    		clusterNum = Integer.parseInt(param);
    	}
	}



	public double getMinSupport() {
		return minSupport;
	}



	public void setMinSupport(double minSupport) {
		this.minSupport = minSupport;
	}



	public int getSizeWindow() {
		return sizeWindow;
	}



	public void setSizeWindow(int sizeWindow) {
		this.sizeWindow = sizeWindow;
	}



	public int getStepWindow() {
		return stepWindow;
	}



	public void setStepWindow(int stepWindow) {
		this.stepWindow = stepWindow;
	}



	public int getSplitLeastLen() {
		return splitLeastLen;
	}



	public void setSplitLeastLen(int splitLeastLen) {
		this.splitLeastLen = splitLeastLen;
	}



	public int getClusterNum() {
		return clusterNum;
	}



	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}
}
