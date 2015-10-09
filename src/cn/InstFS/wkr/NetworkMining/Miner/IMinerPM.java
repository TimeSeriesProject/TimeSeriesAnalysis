package cn.InstFS.wkr.NetworkMining.Miner;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;


public interface IMinerPM {
	public void setDataItems(DataItems dataItems);
	public void predictPeriod();
}
