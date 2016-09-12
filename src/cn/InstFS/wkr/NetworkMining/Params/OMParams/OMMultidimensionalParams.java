package cn.InstFS.wkr.NetworkMining.Params.OMParams;

import org.jdom.Element;

public class OMMultidimensionalParams {
	private static int GuassK = 4; //混合高斯中高斯个数
	private int piontK = 5; //滑动平均参数，滑动窗口大小

	private static int densityK = 2; //PointSegment线段化参数，找极值点的参数
	private static double patternThreshold = 0.1; //PointSegment线段化参数
	private double mergerPrice = 0.05; //LinePattern线段化参数
	private static double diff = 0.2; //判断是否为异常的异常度差值,如果阈值一侧的数据与前一个数据的差值小于diff,则阈值向前移,直到差值大于diff
	
	public OMMultidimensionalParams(Element paramsConfig){
		String param = "";
    	param = paramsConfig.getChildText("GuassK");
    	if(param != null){
    		GuassK = Integer.parseInt(param);
    	}
    	
    	param = paramsConfig.getChildText("piontK");
    	if(param != null){
    		piontK = Integer.parseInt(param);
    	}
    	
    	param = paramsConfig.getChildText("densityK");
    	if(param != null){
    		densityK = Integer.parseInt(param);
    	}
    	
    	param = paramsConfig.getChildText("patternThreshold");
    	if(param != null){
    		patternThreshold = Double.parseDouble(param);
    	}
    	param = paramsConfig.getChildText("mergerPrice");
    	if(param != null){
    		mergerPrice = Double.parseDouble(param);
    	}
    	param = paramsConfig.getChildText("diff");
    	if(param != null){
    		diff = Double.parseDouble(param);
    	}
	}

	public static int getGuassK() {
		return GuassK;
	}

	public static void setGuassK(int guassK) {
		GuassK = guassK;
	}

	public int getPiontK() {
		return piontK;
	}

	public void setPiontK(int piontK) {
		this.piontK = piontK;
	}

	public static int getDensityK() {
		return densityK;
	}

	public static void setDensityK(int densityK) {
		OMMultidimensionalParams.densityK = densityK;
	}

	public static double getPatternThreshold() {
		return patternThreshold;
	}

	public static void setPatternThreshold(double patternThreshold) {
		OMMultidimensionalParams.patternThreshold = patternThreshold;
	}

	public double getMergerPrice() {
		return mergerPrice;
	}

	public void setMergerPrice(double mergerPrice) {
		this.mergerPrice = mergerPrice;
	}

	public static double getDiff() {
		return diff;
	}

	public static void setDiff(double diff) {
		OMMultidimensionalParams.diff = diff;
	}
	
	
	
}
