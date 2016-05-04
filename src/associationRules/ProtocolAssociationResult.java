package associationRules;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * 存储协议关联规则挖掘的结果
 * @author Administrator
 *
 */
public class ProtocolAssociationResult{
	
	double support = 0.0;
	int bias = 0;
	String protocol1 = "";
	String protocol2 = "";
	DataItems dataItems1;
	DataItems dataItems2;
	public ProtocolAssociationResult(String p1,String p2,DataItems data1,DataItems data2,double s,int k)
	{
		protocol1 = p1;
		protocol2 = p2;
		dataItems1 = data1;
		dataItems2 = data2;
		support = s;
		bias = k;
	}
	
}