package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum MiningAlgo {
	MiningAlgo_averageEntropyPM("平均熵周期检测算法"),
	MiningAlgo_ERPDistencePM("ERP距离周期检测算法"),
	MiningAlgo_ARTSA("AR模型序列分析"),
	MiningAlgo_ERPDistTSA("ERP模型序列分析"),
	MiningAlgo_FastFourier("FFT异常检测"),
	MiningAlgo_GaussDetection("高斯滑动窗口异常检测"),
	MiningAlgo_TEOTSA("TEO线段异常检测"),
	MiningAlgo_NeuralNetworkTSA("神经网络预测"),
	MiningAlgo_ARIMATSA("ARIMA模型预测"),
	MiningAlgo_NULL("无");
	
	private String value;
	MiningAlgo(String value) {
		this.value=value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public static MiningAlgo fromString(String str){
		if (str.equals(MiningAlgo_averageEntropyPM.toString()))
			return MiningAlgo_averageEntropyPM;
		else if (str.equals(MiningAlgo_ERPDistencePM.toString()))
			return MiningAlgo_ERPDistencePM;
		else if(str.equals(MiningAlgo_ARTSA.toString())){
			return MiningAlgo_ARTSA;
		}else if(str.equals(MiningAlgo_ARTSA.toString())){
			return MiningAlgo_ERPDistTSA;
		}else{
			return MiningAlgo_NULL;
		}
			
	}
	
}
