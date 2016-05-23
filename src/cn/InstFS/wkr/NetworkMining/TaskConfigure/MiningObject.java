package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum MiningObject {
	MiningObject_Traffic("流量"),
	MiningObject_Times("通信次数"),
	MiningObject_None("无");
	
	private String value;
	MiningObject(String value) {
		this.value = value;
	}	
	@Override
	public String toString() {
		return value;
	}
	
	public static MiningObject fromString(String str){
		if (str.equals(MiningObject_Traffic.toString())){
			return MiningObject_Traffic;
		}else if(str.equals(MiningObject_Times.toString())){
			return MiningObject_Times;
		}else {
			return MiningObject_None;
		}
	}
}
