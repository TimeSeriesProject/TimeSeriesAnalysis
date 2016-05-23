package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum MinerType {
	MiningType_SinglenodeOrNodePair("节点与链路规律挖掘"),
	MiningTypes_WholeNetwork("网络结构规律挖掘"),
	MiningType_ProtocolAssociation("多元时间序列规律挖掘"),
	MiningType_Path("承载路径规律挖掘"),
	MiningType_None("无");
	
	
	private String value;
	MinerType(String value){
		this.value=value;
	}
	@Override
	public String toString() {
		return value;
	};
	
	public static MinerType fromString(String str){
		if(str.equals(MinerType.MiningType_SinglenodeOrNodePair.toString()))
			return MiningType_SinglenodeOrNodePair;
		else if(str.equals(MinerType.MiningTypes_WholeNetwork.toString()))
				return MiningTypes_WholeNetwork;
		else if(str.equals(MinerType.MiningType_ProtocolAssociation.toString()))
			return MiningType_ProtocolAssociation;
		else if(str.equals(MinerType.MiningType_Path.toString()))
			return MiningType_Path;
		else 
			return MiningType_None;
	}
}
