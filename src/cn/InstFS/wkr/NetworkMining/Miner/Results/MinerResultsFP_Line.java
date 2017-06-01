package cn.InstFS.wkr.NetworkMining.Miner.Results;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lineAssociation.Linear;
import lineAssociation.SymbolNode;
import associationRules.ProtoclPair;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;


public class MinerResultsFP_Line implements Serializable {

	String ip = "";
	double confidence = 0.0;
	double inf = 0.0;
	int count = 0;
	
	public List<ProtoclPair> protocolPairList = null;
	List<TreeMap<Integer,Linear>> linesList = new ArrayList<TreeMap<Integer,Linear>>();
	public void setIp(String p){
		ip = p;
	}
	public void setConfidence(double d){
		confidence = d;
	}
	public double getInf() {
		return inf;
	}
	public void setInf(double inf) {
		this.inf = inf;
	}
	public void setProtocolPairList( List<ProtoclPair> list){
		protocolPairList = list;
	}
	public String getIp()
	{
		return ip;
	}
	public double getConfidence(){
		return confidence;
	}
	public List<ProtoclPair> getProtocolPairList(){
		return protocolPairList;
	}
	public List<TreeMap<Integer, Linear>> getLinesList() {
		return linesList;
	}
	public void setLinesList(List<TreeMap<Integer, Linear>> linesList) {
		this.linesList = linesList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
