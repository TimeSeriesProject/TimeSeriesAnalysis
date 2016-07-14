package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MinerType;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class TaskCombination implements Serializable{
	private List<TaskElement> tasks;
	private DataItems dataItems;
	private HashMap<String, HashMap<String, DataItems>> eachIpProtocolItems;
	private String range;
	private String miningObject;
	private TaskRange taskRange;
	private String protocol;
	private String name;
	private MinerType minerType;
	
	public TaskCombination(){
		tasks=new ArrayList<TaskElement>();
		eachIpProtocolItems=new HashMap<String, HashMap<String,DataItems>>();
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
	public void setName(MinerType type){
		if (type.equals(MinerType.MiningType_Path))
			this.name=range+"_路径";
	}

	public HashMap<String, HashMap<String, DataItems>> getEachIpProtocolItems() {
		return eachIpProtocolItems;
	}

	public void setEachIpProtocolItems(
			HashMap<String, HashMap<String, DataItems>> eachIpProtocolItems) {
		this.eachIpProtocolItems = eachIpProtocolItems;
	}

	public MinerType getMinerType() {
		return minerType;
	}

	public void setMinerType(MinerType minerType) {
		this.minerType = minerType;
	}

	public TaskRange getTaskRange() {
		return taskRange;
	}

	public void setTaskRange(TaskRange taskRange) {
		this.taskRange = taskRange;
	}
	
	
	
}
