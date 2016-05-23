package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;


public class MinerResultsFP_Line {

	String ip = "";
	double confidence = 0.0;
	List<ProtoclPair> protocolPairList = null;
}
class ProtoclPair{
	
	String protocol1 = "";
	String protocol2 = "";
	double confidence = 0.0;
	DataItems dataItems1 = null;  //包括协议1和协议2的原始序列
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
}
class LinePos{
	
	double confidence = 0.0;
	int A_start = 0;
	int A_end = 0;
	int B_start = 0;
	int B_end = 0;
}