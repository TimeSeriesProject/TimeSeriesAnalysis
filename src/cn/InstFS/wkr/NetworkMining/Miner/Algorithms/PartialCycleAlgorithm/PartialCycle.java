package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;

public class PartialCycle {

	private DataItem dataItems = new DataItem();
	private MinerResults results;
	
	public DataItem getDataItems() {
		return dataItems;
	}
	public void setDataItems(DataItem dataItems) {
		this.dataItems = dataItems;
	}
	public PartialCycle(MinerResults results)
	{
		this.results =results;
	}
	private void run()
	{
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
