package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class TaskCombination {
	private List<TaskElement> tasks;
	private DataItems dataItems;
	private String range;
	private String miningObject;
	private String protocol;
	private String name;
	
	public TaskCombination(){
		tasks=new ArrayList<TaskElement>();
	}

	public List<TaskElement> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskElement> tasks) {
		this.tasks = tasks;
	}

	public DataItems getDataItems() {
		return dataItems;
	}

	public void setDataItems(DataItems dataItems) {
		this.dataItems = dataItems;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getMiningObject() {
		return miningObject;
	}

	public void setMiningObject(String miningObject) {
		this.miningObject = miningObject;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getName(){
		return name;
	}
	public void setName(){
		this.name=range+"_"+protocol;
	}
}
