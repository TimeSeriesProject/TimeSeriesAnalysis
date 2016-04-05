package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class MinerResultsFP {
	private List<DataItems> originItems;
	private Map<Integer, List<String>> associateRules;
	public MinerResultsFP(){
		originItems=new ArrayList<DataItems>();
		associateRules=new HashMap<Integer, List<String>>();
	}
	public List<DataItems> getOriginItems() {
		return originItems;
	}
	public void setOriginItems(List<DataItems> originItems) {
		this.originItems = originItems;
	}
	public Map<Integer, List<String>> getAssociateRules() {
		return associateRules;
	}
	public void setAssociateRules(Map<Integer, List<String>> associateRules) {
		this.associateRules = associateRules;
	}
}
