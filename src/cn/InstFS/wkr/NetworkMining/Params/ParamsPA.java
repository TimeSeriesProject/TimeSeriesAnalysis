package cn.InstFS.wkr.NetworkMining.Params;


import cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams.ARIMAParams;
import cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams.NeuralNetworkParams;

public class ParamsPA {

	NeuralNetworkParams nnp = null;
	ARIMAParams ap = null;

	public NeuralNetworkParams getNnp() {
		return nnp;
	}

	public void setNnp(NeuralNetworkParams nnp) {
		this.nnp = nnp;
	}

	public ARIMAParams getAp() {
		return ap;
	}

	public void setAp(ARIMAParams ap) {
		this.ap = ap;
	}

}
