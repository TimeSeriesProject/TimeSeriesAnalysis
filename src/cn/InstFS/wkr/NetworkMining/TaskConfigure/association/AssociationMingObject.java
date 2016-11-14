package cn.InstFS.wkr.NetworkMining.TaskConfigure.association;

public enum AssociationMingObject {

	IPInnerPortMing("IP内端口之间的关联规则挖掘"),
	IPInterMing("IP之间的关联规则挖掘"),
	MiningType_None("无");
	
	
	private String value;
	AssociationMingObject(String value){
		this.value=value;
	}
	@Override
	public String toString() {
		return value;
	};
	
}
