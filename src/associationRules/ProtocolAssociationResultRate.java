package associationRules;

import java.util.ArrayList;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * 存储协议关联规则挖掘的结果
 * @author Administrator
 *
 */
public class ProtocolAssociationResultRate extends ProtocolAssociationResult{
	
	double support = 0.0;
	int bias = 0;
	
	
	public List<String> assA1 = new ArrayList<String>();
	public List<String> assA2 = new ArrayList<String>();
	
//	public List<String> assB1 = new ArrayList<String>();
//	public List<String> assB2 = new ArrayList<String>();
	public ProtocolAssociationResultRate(String p1,String p2,DataItems data1,DataItems data2){
		
		super(p1,p2,data1,data2,0,0.0);
//		protocol1 = p1;
//		protocol2 = p2;
//		dataItems1 = data1;
//		dataItems2 = data2;
//		alogrithmType = 0;
	}
	
	public ProtocolAssociationResultRate(String p1,String p2,DataItems data1,DataItems data2,double s,int k)
	{
		super();
		protocol1 = p1;
		protocol2 = p2;
		dataItems1 = data1;
		dataItems2 = data2;
		support = s;
		bias = k;
	}
	
}