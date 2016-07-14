package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.Serializable;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;

public class MinerResultsFM implements Serializable {
	ParamsTSA params;	// 或者 java.util.Properties params;
	
	private DataItems predictItems;
	private boolean islinkDegree=false;
	
	
	public DataItems getPredictItems() {
		return predictItems;
	}
	public void setPredictItems(DataItems predictItems) {
		this.predictItems = predictItems;
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
}