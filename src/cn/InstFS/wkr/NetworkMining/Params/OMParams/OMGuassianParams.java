package cn.InstFS.wkr.NetworkMining.Params.OMParams;

import org.jdom.Element;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class OMGuassianParams {
	//基于高斯滑动窗口的异常检测参数
	private int initWindowSize = 30;//初始窗口大小
    private int maxWindowSize = 60;//最大窗口大小
    private int expWindowSize = 3;//扩展窗口大小
    private double windowVarK=3.0;//异常标准差倍数阈值 |Xi - mean|/std < windowVarK
    private static double diff = 0.2; //计算异常度阈值时的参数，判断前后2个差值是否满足(d1-d2)/d2 > diff，满足则d1是异常度阈值，否则不是
    private double minthreshod = 0.7;
    public OMGuassianParams(Element paramsConfig){
    	String param = "";
    	param = paramsConfig.getChildText("initWindowSize");
    	if(param != null){
    		initWindowSize = Integer.parseInt(param);
    	}
    	
    	param = paramsConfig.getChildText("maxWindowSize");
    	if(param != null){
    		maxWindowSize = Integer.parseInt(param);
    	}
    	
    	param = paramsConfig.getChildText("expWindowSize");
    	if(param != null){
    		expWindowSize = Integer.parseInt(param);
    	}
    	
    	param = paramsConfig.getChildText("windowVarK");
    	if(param != null){
    		windowVarK = Double.parseDouble(param);
    	}
    	
    	param = paramsConfig.getChildText("diff");
    	if(param != null){
    		diff = Double.parseDouble(param);
    	}
    	
    	param = paramsConfig.getChildText("minthreshod");
    	if(param != null){
    		minthreshod = Double.parseDouble(param);
    	}
    }
    public int getInitWindowSize() {
        return initWindowSize;
    }

    public void setInitWindowSize(int initWindowSize) {
        this.initWindowSize = initWindowSize;
    }

    public int getMaxWindowSize() {
        return maxWindowSize;
    }

    public void setMaxWindowSize(int maxWindowSize) {
        this.maxWindowSize = maxWindowSize;
    }

    public int getExpWindowSize() {
        return expWindowSize;
    }

    public void setExpWindowSize(int expWindowSize) {
        this.expWindowSize = expWindowSize;
    }
	public double getWindowVarK() {
		return windowVarK;
	}
	public void setWindowVarK(double k) {
		this.windowVarK = k;
	}
	public static double getDiff() {
		return diff;
	}
	public static void setDiff(double diff) {
		OMGuassianParams.diff = diff;
	}
	public double getMinthreshod() {
		return minthreshod;
	}
	public void setMinthreshod(double minthreshod) {
		this.minthreshod = minthreshod;
	}
	
}
