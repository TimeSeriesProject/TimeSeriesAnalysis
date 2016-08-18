package cn.InstFS.wkr.NetworkMining.Params.OMParams;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.jdom.Element;

import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.FastFourierOutliesDetection;

public class OMFastFourierParams {
	//傅里叶变换算法参数
	private static double sizeK = 8; //对数据进行分段处理时，每段数据的长度len = 2^sizeK
    private static double amplitudeRatio = 0.9;//频域内振幅接收比例，剩下的高频过滤为0
    private static double varK = 3.0;//Xi = (si-si')服从高斯分布，异常标准差倍数阈值   |Xi - mean|/std < varK
    public OMFastFourierParams(Element paramsConfig){
    	String param = "";
    	param = paramsConfig.getChildText("sizeK");
    	if(param != null){
    		sizeK = Double.parseDouble(param);
    	}
    	
    	param = paramsConfig.getChildText("amplitudeRatio");
    	if(param != null){
    		amplitudeRatio = Double.parseDouble(param);
    	}
    	
    	param = paramsConfig.getChildText("varK");
    	if(param != null){
    		varK = Double.parseDouble(param);
    	}
    }
    public  double getAmplitudeRatio() {
		return amplitudeRatio;
	}

	public void setAmplitudeRatio(double amplitudeRatio) {
		this.amplitudeRatio = amplitudeRatio;
	}

	public  double getVarK() {
		return varK;
	}

	public  void setVarK(double varK) {
		this.varK = varK;
	}

	public static double getSizeK() {
		return sizeK;
	}

	public static void setSizeK(double sizeK) {
		OMFastFourierParams.sizeK = sizeK;
	}

}
