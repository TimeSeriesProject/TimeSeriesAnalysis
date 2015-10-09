package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum TaskRange {
	NodePairRange(),
	SingleNodeRange(),
	WholeNetworkRange();
	
	TaskRange(){
	}
	
	public static TaskRange getTaskRange(){
		return WholeNetworkRange;
	}
}
