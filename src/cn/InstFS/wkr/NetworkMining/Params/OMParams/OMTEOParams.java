package cn.InstFS.wkr.NetworkMining.Params.OMParams;

public class OMTEOParams {
	
	private double mergeThreshold;      //相邻线段的合并代价阈值
	private int neighborK;    //用模式密度进行异常检测时，k邻域（可达域）的值
	
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
