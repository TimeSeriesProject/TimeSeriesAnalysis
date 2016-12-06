package cn.InstFS.wkr.NetworkMining.TaskConfigure;

import org.apache.ibatis.jdbc.Null;

public enum MiningMethod {
	MiningMethods_FrequenceItemMining("多元时间序列挖掘"),
	MiningMethods_SimilarityMining("多元序列相似度挖掘"),
	MiningMethods_SequenceMining("频繁模式挖掘"),
	MiningMethods_PeriodicityMining("周期模式发现"),
	MiningMethods_PathProbilityMining("路径概率发现"),
	MiningMethods_OutliesMining("序列异常检测"),
	MiningMethods_PredictionMining("时间序列预测"),
	MiningMethods_Statistics("统计图"),
	MiningMethods_PartialCycle("局部周期发现"),
	MiningMethods_None("无");
	
	private String value;
	MiningMethod(String value) {
		this.value = value;
	}	
	@Override
	public String toString() {
		return value;
	}
	
	public static MiningMethod fromString(String str){
		if (str.equals(MiningMethods_OutliesMining.toString()))
			return MiningMethods_OutliesMining;
		else if(str.equals(MiningMethods_PredictionMining.toString())){
			return MiningMethods_PredictionMining;
		}else if (str.equals(MiningMethods_PeriodicityMining.toString()))
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
			return MiningMethods_None;
		}
	}
}