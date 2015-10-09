package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public interface INetworkMiner {
	boolean start();
	boolean stop();
	boolean isAlive();
	TaskElement getTask();	
	MinerResults getResults();
	void setResultsDisplayer(IResultsDisplayer displayer);

}
