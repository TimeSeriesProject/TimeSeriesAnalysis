package cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams;

public class AssociationRuleSimilarityParams {

	double[] supportThresh = {0.4,10};  //设置支持度阈值，每次只会只用其中的一个，有whichAlogrithm指定
	                                    //（=1使用第一种方法，对应第一个参数，=2使用第二种方法,对应第二个参数）
	int bias = 0;
	int whichAlogrithm = 1;
	
	public double[] getSupportThresh() {
		return supportThresh;
	}
	public void setSupportThresh(double[] supportThresh) {
		this.supportThresh = supportThresh;
	}
	public int getBias() {
		return bias;
	}
	public void setBias(int bias) {
		this.bias = bias;
	}
	public int getWhichAlogrithm() {
		return whichAlogrithm;
	}
	public void setWhichAlogrithm(int whichAlogrithm) {
		this.whichAlogrithm = whichAlogrithm;
	}
	
}
