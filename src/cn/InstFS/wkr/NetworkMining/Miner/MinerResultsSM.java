package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.Choose;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;

public class MinerResultsSM {
	ParamsSM params;
	private DataItems data_curTime;
	private DataItems forcasts_curTime;
	private DataItems forcasts_futureTime;
	private DataItems patterns;
	private double recallRatio = -1;
	private double accuracyRatio = -1;
	private Map<Integer, List<String>> frequentItem=new HashMap<Integer, List<String>>();
	private boolean hasFreItems=false;

	public Map<Integer, List<String>> getFrequentItem() {
		return frequentItem;
	}

	public void setFrequentItem(Map<Integer, List<String>> frequentItem) {
		this.frequentItem = frequentItem;
	}

	public DataItems getForcasts_curTime() {
		return forcasts_curTime;
	}

	public void setForcasts_curTime(DataItems forcasts_curTime) {
		this.forcasts_curTime = forcasts_curTime;
	}

	public DataItems getForcasts_futureTime() {
		return forcasts_futureTime;
	}

	public void setForcasts_futureTime(DataItems forcasts_futureTime) {
		this.forcasts_futureTime = forcasts_futureTime;
	}

	public DataItems getData_curTime() {
		return data_curTime;
	}

	public void setData_curTime(DataItems data_curTime) {
		this.data_curTime = data_curTime;
	}

	public DataItems getPatterns() {
		return patterns;
	}

	public void setPatterns(DataItems patterns) {
		this.patterns = patterns;
	}
	
	public void setPatters(List<ArrayList<String>> items){
		patterns=new DataItems();
		List<String> data=new ArrayList<String>();
		StringBuilder sb=new StringBuilder();
		for(List<String> itemList:items){
			for(String item:itemList){
				sb.append(",").append(item);
			}
			sb.deleteCharAt(0);
			data.add(sb.toString());
			sb.delete(0, sb.length());
		}
		patterns.setData(data);
	}

	public double getRecallRatio() {
		if (recallRatio == -1)
			calculateRecallAndAccuracy();
		return recallRatio;
	}

	public void setRecallRatio(double recallRatio) {
		this.recallRatio = recallRatio;
	}

	public double getAccuracyRatio() {
		if (accuracyRatio == -1)
			calculateRecallAndAccuracy();
		return accuracyRatio;
	}

	public void setAccuracyRatio(double accuracyRatio) {
		this.accuracyRatio = accuracyRatio;
	}

	private void calculateRecallAndAccuracy() {

	}

	public boolean isHasFreItems() {
		return hasFreItems;
	}

	public void setHasFreItems(boolean hasFreItems) {
		this.hasFreItems = hasFreItems;
	}
	
	
	
	
}