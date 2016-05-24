package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import associationRules.ProtoclPair;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;


public class MinerResultsFP_Line {

	String ip = "";
	double confidence = 0.0;
	public List<ProtoclPair> protocolPairList = null;
	public void setIp(String p){
		ip = p;
	}
	public void setConfidence(double d){
		confidence = d;
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
}
