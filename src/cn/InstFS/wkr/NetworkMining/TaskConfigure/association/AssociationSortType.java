package cn.InstFS.wkr.NetworkMining.TaskConfigure.association;

public enum AssociationSortType {

	IPPartConfidence("按ip端口部分置信度排序"),
	IPIntegerMing("按ip端口整体置信度排序"),
	IPCount("按关联计数排序"),
	IPIP("按ip排序");
	
	private String value;
	AssociationSortType(String value){
		this.value=value;
	}
	@Override
	public String toString() {
		return value;
	};
}
