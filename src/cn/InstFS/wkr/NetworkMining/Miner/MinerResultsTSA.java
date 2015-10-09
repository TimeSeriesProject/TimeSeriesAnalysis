package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;

public class MinerResultsTSA{
	ParamsTSA params;	// Лђеп java.util.Properties params;
	
	private DataItems predictItems;
	private DataItems outlies;
	
	
	public DataItems getPredictItems() {
		return predictItems;
	}
	public void setPredictItems(DataItems predictItems) {
		this.predictItems = predictItems;
	}
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
}