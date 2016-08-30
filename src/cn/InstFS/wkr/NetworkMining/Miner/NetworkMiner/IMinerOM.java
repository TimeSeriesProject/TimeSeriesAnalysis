package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public interface IMinerOM {
	public void TimeSeriesAnalysis();
	public DataItems getOutlies(); //异常点
	public List<DataItems> getOutlinesSet(); //异常线段
	public DataItems getOutDegree(); //异常度
}
