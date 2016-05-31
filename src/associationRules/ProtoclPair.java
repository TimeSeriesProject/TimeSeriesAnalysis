package associationRules;

import java.util.ArrayList;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ProtoclPair{
	
	String protocol1 = "";
	String protocol2 = "";
	public double confidence = 0.0;
	DataItems dataItems1 = null;  //包括协议1和协议2的原始序列
	
	public DataItems getDataItems1() {
		return dataItems1;
	}
	public void setDataItems1(DataItems dataItems1) {
		this.dataItems1 = dataItems1;
	}
	public DataItems getDataItems2() {
		return dataItems2;
	}
	public void setDataItems2(DataItems dataItems2) {
		this.dataItems2 = dataItems2;
	}
	DataItems dataItems2 = null;  //包括协议1和协议2的原始序列
	
	Map<String, ArrayList<LinePos>> mapAB = null;  //记录序列1与序列2 各个关联符号所在的位置 A,B表示在哪个序列上，12表示序列的比较顺序
	
	Map<String, ArrayList<LinePos>> mapBA = null;  //记录序列2与序列1 各个关联符号所在的位置
	
	public ProtoclPair(String name1, String name2,
			DataItems data1, DataItems data2) {
		protocol1 = name1;
		protocol2 = name2;
		
		dataItems1 = data1;
		dataItems2 = data2;
	}
	public void setConfidence(double d){
		confidence = d;
	}
	public void setMapAB(Map<String, ArrayList<LinePos>> map){
		mapAB = map;
	}
	public void setMapBA(Map<String, ArrayList<LinePos>> map){
		mapBA = map;
	}
	public double getConfidence(){
		return confidence;
	}
	public String getProtocol1(){
		return protocol1;
	}
	public String getProtocol2(){
		return protocol2;
	}
	public Map<String, ArrayList<LinePos>> getMapAB(){
		return mapAB;
	}
	public Map<String, ArrayList<LinePos>> getMapBA(){
		return mapBA;
	}
}
