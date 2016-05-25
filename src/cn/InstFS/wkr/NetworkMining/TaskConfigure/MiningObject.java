package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum MiningObject {
	MiningObject_Traffic("流量"),
	MiningObject_Times("通信次数"),
	MiningObject_NodeAppearance("结点出现消失"),
	MiningObject_Cluster("网络簇系数"),
	MiningObject_Diameter("网络直径"),
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
		}else if(str.equals(MiningObject_NodeAppearance.toString())){
			return MiningObject_NodeAppearance;
		}else if(str.equals(MiningObject_Cluster.toString())){
			return MiningObject_Cluster;
		}else if(str.equals(MiningObject_Diameter.toString())){
			return MiningObject_Diameter;
		}
		else {
			return MiningObject_None;
		}
	}
}
