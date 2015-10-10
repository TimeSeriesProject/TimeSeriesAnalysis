package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum MiningAlgo {
	MiningAlgo_averageEntropyPM("平均熵周期检测算法"),
	MiningAlgo_ERPDistencePM("ERP距离周期检测算法"),
	MiningAlgo_ARTSA("AR模型序列分析"),
	MiningAlgo_ERPDistTSA("ERP模型序列分析"),
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
