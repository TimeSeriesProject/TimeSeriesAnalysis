package cn.InstFS.wkr.NetworkMining.Params.OMParams;

import org.jdom.Element;

public class OMGaussianNodeParams {
	private static double diff = 0.2; //计算异常度阈值时的参数，判断前后2个差值是否满足(d1-d2)/d2 > diff，满足则d1是异常度阈值，否则不是
	public OMGaussianNodeParams(Element paramsConfig){
		String param = "";
    	
    	param = paramsConfig.getChildText("diff");
    	if(param != null){
    		diff = Double.parseDouble(param);
    	}
	}
	public static double getDiff() {
		return diff;
	}
	public static void setDiff(double diff) {
		OMGaussianNodeParams.diff = diff;
	}
	
}
