package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum MiningMethod {
	MiningMethods_FrequenceItemMining("频繁项集挖掘"),
	MiningMethods_SequenceMining("序列模式挖掘"),
	MiningMethods_PeriodicityMining("周期模式发现"),
	MiningMethods_PathProbilityMining("路径概率发现"),
	MiningMethods_TsAnalysis("时间序列分析"),
	MiningMethods_Statistics("统计图");
	
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
		else if(str.equals(MiningMethods_FrequenceItemMining.toString())){
			return MiningMethods_FrequenceItemMining;
		}else if(str.equals(MiningMethods_SequenceMining.toString())){
			return MiningMethods_SequenceMining;
		}else if(str.equals(MiningMethods_PathProbilityMining.toString())){
			return MiningMethods_PathProbilityMining;
		}else if(str.equals(MiningMethods_Statistics.toString()))
			return MiningMethods_Statistics;
		else{
			return MiningMethods_TsAnalysis;
		}
	}
}
