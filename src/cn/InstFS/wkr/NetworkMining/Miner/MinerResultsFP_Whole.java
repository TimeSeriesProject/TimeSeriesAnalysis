package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.List;

import associationRules.ProtocolAssociationResult;

public class MinerResultsFP_Whole {

	double confidence = 0.0;
	List<ProtocolAssociationResult> protocolPairList = null;
	
	public void setConfidence(double d){
		confidence = d;
	}
	public void setProtocolPairList(List<ProtocolAssociationResult> list){
		protocolPairList = list;
	}
	public double getConfidence(){
		return confidence;
	}
	public List<ProtocolAssociationResult> getProtocolPairList(){
		return protocolPairList;
	}
}
