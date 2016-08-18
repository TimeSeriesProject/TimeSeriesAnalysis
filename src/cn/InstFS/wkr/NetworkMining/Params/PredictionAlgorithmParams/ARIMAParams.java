package cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams;

import java.io.Serializable;
import java.lang.reflect.Field;

import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;

public class ARIMAParams extends IParamsNetworkMining implements Serializable {
	// 神经网络参数列表
	// 参数列表
	private int predictPeriod; // 预测周期

	public ARIMAParams() {
		// 参数列表
		predictPeriod = 20; // 预测周期

	}

	public static NeuralNetworkParams newInstance(NeuralNetworkParams p) {
		NeuralNetworkParams param = new NeuralNetworkParams();

		param.setPredictPeriod(p.getPredictPeriod());

		return param;
	}

	@Override
	public boolean equals(IParamsNetworkMining params) {
		Field[] fields = this.getClass().getFields();
		boolean isSame = true;
		for (Field field : fields)
			try {
				if (!field.get(this).equals(field.get(params))) {
					isSame = false;
					break;
				}
			} catch (IllegalArgumentException e) {
				isSame = false;
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				isSame = false;
				e.printStackTrace();
			}
		return isSame;
	}

	public int getPredictPeriod() {
		return predictPeriod;
	}

	public void setPredictPeriod(int predictPeriod) {
		this.predictPeriod = predictPeriod;
	}

}
