package associationRules;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ProtocolAssociationResult {

	public int alogrithmType = 1;   // 为0，说明是包含支持度参数的关联规则挖掘方法 ，为1，说明是连段挖掘方法
	String protocol1 = "";
	String protocol2 = "";
	public DataItems dataItems1;
	public DataItems dataItems2;
	public ProtocolAssociationResult(String p1,String p2,DataItems data1,DataItems data2,int type){
		
		protocol1 = p1;
		protocol2 = p2;
		dataItems1 = data1;
		dataItems2 = data2;
		alogrithmType = type;
		
	}
	public ProtocolAssociationResult(){
		
	}
}
