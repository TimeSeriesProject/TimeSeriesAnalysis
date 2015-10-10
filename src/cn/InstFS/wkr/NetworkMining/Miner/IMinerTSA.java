package cn.InstFS.wkr.NetworkMining.Miner;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public interface IMinerTSA {
	public void TimeSeriesAnalysis();
	public DataItems getOutlies();
	public DataItems getPredictItems();
}
