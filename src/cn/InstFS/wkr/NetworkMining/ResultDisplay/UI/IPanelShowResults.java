package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public interface IPanelShowResults extends IResultsDisplayer{
	
	void displayMinerResults();
	boolean start();
	boolean stop();
	
	void setData(DataItems data);
	TaskElement getTask();
	INetworkMiner getMiner();

}
