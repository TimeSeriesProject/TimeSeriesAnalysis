package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum MiningMethod {
	MiningMethods_FrequenceItemMining("频繁项集挖掘"),
	MiningMethods_SequenceMining("序列模式挖掘"),
	MiningMethods_PeriodicityMining("周期模式发现"),
	MiningMethods_TsAnalysis("时间序列分析");
	
	private String value;
	MiningMethod(String value) {
		this.value = value;
	}	
	@Override
	public String toString() {
		return value;
	}
	
	public static MiningMethod fromString(String str){
		if (str.equals(MiningMethods_TsAnalysis.toString()))
			return MiningMethods_TsAnalysis;
		else if (str.equals(MiningMethods_PeriodicityMining.toString()))
			return MiningMethods_PeriodicityMining;
		else
			return MiningMethods_SequenceMining;
	}
}
