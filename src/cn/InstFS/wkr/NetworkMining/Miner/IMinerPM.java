package cn.InstFS.wkr.NetworkMining.Miner;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;


public interface IMinerPM {
	public void setDataItems(DataItems dataItems);
	public void predictPeriod();
	public boolean hasPeriod();
	public int getPredictPeriod();
	public int getFirstPossiblePeriod();
	public DataItems getItemsInPeriod();
	public Double getMinEntropy();
	public Double[] getEntropies();
	public int getLastNumberIndexInperiod();
}
