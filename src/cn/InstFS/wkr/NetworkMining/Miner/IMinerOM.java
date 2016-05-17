package cn.InstFS.wkr.NetworkMining.Miner;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public interface IMinerOM {
	public void TimeSeriesAnalysis();
	public DataItems getOutlies();
}
