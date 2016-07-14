package cn.InstFS.wkr.NetworkMining.Miner;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;

import java.io.Serializable;

public class MinerResultsOM implements Serializable{
	ParamsTSA params;	// 或者 java.util.Properties params;
	
	private DataItems outlies;
	private boolean islinkDegree=false;
	private boolean hasOutlies;
	private int confidence;
	
	public DataItems getOutlies() {
		return outlies;
	}
	public void setOutlies(DataItems outlies) {
		this.outlies = outlies;
	}
	public ParamsTSA getParamsTSA(){
		return this.params;
	}
	public void setParamsTSA(ParamsTSA params){
		this.params=params;
	}
	public boolean isIslinkDegree() {
		return islinkDegree;
	}
	public void setIslinkDegree(boolean islinkDegree) {
		this.islinkDegree = islinkDegree;
	}
	public boolean isHasOutlies() {
		return hasOutlies;
	}
	public void setHasOutlies(boolean hasOutlies) {
		this.hasOutlies = hasOutlies;
	}
	public int getConfidence() {
		return confidence;
	}
	public void setConfidence(int confidence) {
		this.confidence = confidence;
	}
	
}