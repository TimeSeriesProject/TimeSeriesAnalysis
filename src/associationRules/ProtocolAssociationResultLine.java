package associationRules;

import java.util.ArrayList;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ProtocolAssociationResultLine extends ProtocolAssociationResult{

	public List<String> assA = new ArrayList<String>();
	public List<String> assB = new ArrayList<String>();
	public ProtocolAssociationResultLine(String p1,String p2,DataItems data1,DataItems data2){
		
		super(p1,p2,data1,data2,1,0.0);
//		protocol1 = p1;
//		protocol2 = p2;
//		dataItems1 = data1;
//		dataItems2 = data2;
//		alogrithmType = 1;
	}
}
