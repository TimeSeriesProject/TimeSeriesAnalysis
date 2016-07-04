package associationRules;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ProtocolAssociationResult {

	public int alogrithmType = 1;   // 为0，说明是包含支持度参数的关联规则挖掘方法 ，为1，说明是连段挖掘方法
	String protocol1 = "";
	String protocol2 = "";
	double confidence = 0;
	public DataItems dataItems1;
	public DataItems dataItems2;
	public ProtocolAssociationResult(String p1,String p2,DataItems data1,DataItems data2,int type,double thresh){
		
		protocol1 = p1;
		protocol2 = p2;
		dataItems1 = data1;
		dataItems2 = data2;
		alogrithmType = type;
		confidence = thresh;
	}
	public ProtocolAssociationResult(){
		
	}
	public void setAlogrithmType(int type){
		alogrithmType = type;
	}
	public void setProtocol1(String p){
		protocol1 = p;
	}
	public void setProtocol2(String p){
		protocol2 = p;
	}
	public void setConfidence(double d){
		confidence = d;
	}
	public void setDataItems1(DataItems data){
		dataItems1 = data;
	}
	public void setDataItems2(DataItems data){
		dataItems2 = data;
	}
	public int getAlogrithmType(){
		return alogrithmType ;
	}
	public String getProtocol1(){
		return protocol1;
	}
	public String getProtocol2(){
		return protocol2;
	}
	public double getConfidence(){
		return confidence;
	}
	public DataItems getDataItems1(){
		return dataItems1;
	}
	public DataItems getDataItems2(){
		return dataItems2;
	}
	
	
}
