package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public interface IPanelShowResults extends IResultsDisplayer{
	
	void displayMinerResults();
	boolean start();
	boolean stop();
	
	void setData(DataItems data);
	TaskElement getTask();
	INetworkMiner getMiner();

}
