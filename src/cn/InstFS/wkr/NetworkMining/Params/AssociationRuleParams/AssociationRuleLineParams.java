package cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams;

public class AssociationRuleLineParams {

	/**
	 * 以下两个参数是线段树的合并代价的参数
	 */
	private double mergerPrice = 0.025; //线段两两合并代价阈值
	private double compressionRatio = 0.65;//压缩率
    
    /**
     * 线段聚类参数聚类
     */
    private double t = 0.1;//在聚类是确定每个样本的平均邻居数，取值为[0,1],建议取值0.1-0.2，用于确定参数截断距离dc
    private double centerLine = -1.0;//聚类中心划界线，为 -1 时通过函数computeCenterLine自动确定
    private int way = 1;//聚类中心线自动确定方法选择( 1:高斯 or 2:间隔 )
    private double gaosi = 3;//聚类中心线自动确定方法选择高斯分布方法时有效，表示中心线距离均值gaosi倍标准差（一般gaosi = 3）
    private boolean multiOrMin = false;//求gamma时采用相乘或求最小的方式(true表示相乘，false表示求最小)
    
    public double getMergerPrice() {
		return mergerPrice;
	}
	public void setMergerPrice(double mergerPrice) {
		this.mergerPrice = mergerPrice;
	}
	public double getCompressionRatio() {
		return compressionRatio;
	}
	public void setCompressionRatio(double compressionRatio) {
		this.compressionRatio = compressionRatio;
	}
	public double getT() {
		return t;
	}
	public void setT(double t) {
		this.t = t;
	}
	public double getCenterLine() {
		return centerLine;
	}
	public void setCenterLine(double centerLine) {
		this.centerLine = centerLine;
	}
	public int getWay() {
		return way;
	}
	public void setWay(int way) {
		this.way = way;
	}
	public double getGaosi() {
		return gaosi;
	}
	public void setGaosi(double gaosi) {
		this.gaosi = gaosi;
	}
	public boolean isMultiOrMin() {
		return multiOrMin;
	}
	public void setMultiOrMin(boolean multiOrMin) {
		this.multiOrMin = multiOrMin;
	}
	

}
