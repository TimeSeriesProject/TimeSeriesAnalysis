package cn.InstFS.wkr.NetworkMining.Params.OMParams;

import org.jdom.Element;

public class OMPiontPatternParams {
	private int densityK = 3;//序列线段化时,找极值点的参数;判断一个点是极大点（极小点）的依据是这个点后面的densityK个点都比该点小（大）该点（或std）的一定比例（PatternThreshold）
	private double patternThreshold = 0.1 ;//序列线段化时，极大点（极小点）要比后面的densityK个点大（小）PatternThreshold
	private int neighborK = 10;//计算模式P的K-邻域中的k
	private double diff = 0.1; //判断是否为异常的异常度差值,如果阈值一侧的数据与前一个数据的差值小于diff,则阈值向前移,直到差值大于diff
	private double threshold = 0.7;
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
    	
    	param = paramsConfig.getChildText("diff");
    	if(param != null){
    		diff = Double.parseDouble(param);
    	}
    	
    	param = paramsConfig.getChildText("threshold");
    	if(param != null){
    		threshold = Double.parseDouble(param);
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
	public double getDiff() {
		return diff;
	}
	public void setDiff(double diff) {
		this.diff = diff;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
}
