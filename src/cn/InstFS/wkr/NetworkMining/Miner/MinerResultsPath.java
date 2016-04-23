package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.HashMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class MinerResultsPath {
	private HashMap<String, Integer> periodPath;
	private HashMap<String, Integer> firstPeriodOfPath;
	private HashMap<String, DataItems> itemsInPeriod;
	public MinerResultsPath() {
		periodPath=new HashMap<String, Integer>();
		firstPeriodOfPath=new HashMap<String, Integer>();
		itemsInPeriod=new HashMap<String, DataItems>();
	}
	public HashMap<String, Integer> getPeriodPath() {
		return periodPath;
	}
	public void setPeriodPath(HashMap<String, Integer> periodPath) {
		this.periodPath = periodPath;
	}
	public HashMap<String, Integer> getFirstPeriodOfPath() {
		return firstPeriodOfPath;
	}
	public void setFirstPeriodOfPath(HashMap<String, Integer> firstPeriodOfPath) {
		this.firstPeriodOfPath = firstPeriodOfPath;
	}
	public HashMap<String, DataItems> getItemsInPeriod() {
		return itemsInPeriod;
	}
	public void setItemsInPeriod(HashMap<String, DataItems> itemsInPeriod) {
		this.itemsInPeriod = itemsInPeriod;
	}
	
	
}
