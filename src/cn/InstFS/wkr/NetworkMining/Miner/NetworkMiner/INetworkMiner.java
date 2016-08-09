package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public interface INetworkMiner {
	boolean start();
	boolean stop();
	boolean isAlive();
	boolean isOver();
	TaskElement getTask();	
	MinerResults getResults();
	void setResultsDisplayer(IResultsDisplayer displayer);

}
