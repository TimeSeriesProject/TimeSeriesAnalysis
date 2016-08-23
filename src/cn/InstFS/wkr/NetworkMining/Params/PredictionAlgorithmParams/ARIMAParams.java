package cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams;

import java.io.Serializable;
import java.lang.reflect.Field;

import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import org.jdom.Element;

public class ARIMAParams {
	// 神经网络参数列表
	// 参数列表
	private int predictPeriod; // 预测周期

	public ARIMAParams() {
		// 参数列表
		predictPeriod = 20; // 预测周期
	}

	public ARIMAParams(Element paramsConfig) {
		String param = "";

		param = paramsConfig.getChildText("predictPeriod");
		if(param != null){
			predictPeriod = Integer.parseInt(param);
		}
	}

	public int getPredictPeriod() {
		return predictPeriod;
	}

	public void setPredictPeriod(int predictPeriod) {
		this.predictPeriod = predictPeriod;
	}

}
