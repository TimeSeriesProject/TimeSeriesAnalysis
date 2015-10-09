package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum AggregateMethod {
	Aggregate_MAX("最大值"),
	Aggregate_MIN("最小值"),
	Aggregate_SUM("求和"),
	Aggregate_MEAN("平均"),
	Aggregate_NONE("无");
	
	final String value;
	AggregateMethod(String val){
		this.value = val;
	}
	
	
	@Override
	public String toString() {
		return value;
	}
	public static AggregateMethod fromString(String str){
		try{
			AggregateMethod ret =  AggregateMethod.valueOf(str);
			return ret;
		}catch(Exception e){
			return Aggregate_SUM;
		}
	}
}
