package cn.InstFS.wkr.NetworkMining.Params.OMParams;

import org.jdom.Element;

public class OMTEOParams {
	
	private double mergeThreshold;      //相邻线段的合并代价阈值
	private int neighborK;    //用模式密度进行异常检测时，k邻域（可达域）的值
	
	public OMTEOParams(Element paramsConfig){
		String param = "";
    	   	
    	param = paramsConfig.getChildText("mergeThreshold");
    	if(param != null){
    		mergeThreshold = Double.parseDouble(param);
    	}
    	
    	param = paramsConfig.getChildText("neighborK");
    	if(param != null){
    		neighborK = Integer.parseInt(param);
    	}
	}
	
	public double getMergeThreshold() {
		return mergeThreshold;
	}
	public void setMergeThreshold(double mergeThreshold) {
		this.mergeThreshold = mergeThreshold;
	}
	public int getNeighborK() {
		return neighborK;
	}
	public void setNeighborK(int neighborK) {
		this.neighborK = neighborK;
	}
	
}
