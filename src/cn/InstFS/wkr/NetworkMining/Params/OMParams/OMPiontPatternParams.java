package cn.InstFS.wkr.NetworkMining.Params.OMParams;

import org.jdom.Element;

public class OMPiontPatternParams {
	private int densityK = 2;//序列线段化时,找极值点的参数;判断一个点是极大点（极小点）的依据是这个点后面的densityK个点都比该点小（大）该点（或std）的一定比例（PatternThreshold）
	private double patternThreshold = 0.1 ;//序列线段化时，极大点（极小点）要比后面的densityK个点大（小）PatternThreshold
	private int neighborK = 10;//计算模式P的K-邻域中的k
	
	public OMPiontPatternParams(Element paramsConfig){
		String param = "";
    	param = paramsConfig.getChildText("densityK");
    	if(param != null){
    		densityK = Integer.parseInt(param);
    	}
    	
    	param = paramsConfig.getChildText("patternThreshold");
    	if(param != null){
    		patternThreshold = Double.parseDouble(param);
    	}
    	
    	param = paramsConfig.getChildText("neighborK");
    	if(param != null){
    		neighborK = Integer.parseInt(param);
    	}
	}
	public int getDensityK() {
		return densityK;
	}
	public void setDensityK(int densityK) {
		this.densityK = densityK;
	}
	public int getNeighborK() {
		return neighborK;
	}
	public void setNeighborK(int neighborK) {
		this.neighborK = neighborK;
	}
	public double getPatternThreshold() {
		return patternThreshold;
	}
	public void setPatternThreshold(double patternThreshold) {
		this.patternThreshold = patternThreshold;
	}
	
}
