package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;

import associationRules.ProtocolAssociationResult;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MinerType;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class ProtocolAssMinerFactory {
	private static ProtocolAssMinerFactory inst;
	public static boolean isMining=false;
	public String dataPath="./tasks1/";
	private MiningObject miningObject;
	public static HashMap<String, HashMap<String, DataItems>> eachProtocolItems;
	
	ProtocolAssMinerFactory(){
		eachProtocolItems= new HashMap<String, HashMap<String,DataItems>>();
	}
	
	public static ProtocolAssMinerFactory getInstance(){
		if(inst==null){
			isMining=false;
			inst=new ProtocolAssMinerFactory();
		}
		return inst;
	}
	public HashMap<String, HashMap<String,DataItems>> getData(){
		return eachProtocolItems;
	}
	
	
	
    public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public MiningObject getMiningObject() {
		return miningObject;
	}

	public void setMiningObject(MiningObject miningObject) {
		this.miningObject = miningObject;
	}
	
	public void detect(){
		File dataDirectory=new File(dataPath);
		nodePairReader reader=new nodePairReader();
		int granularity=3600;
		if(dataDirectory.isFile()){
			addTask(dataDirectory,granularity,reader);
		}else{
			File[] dataDirs=dataDirectory.listFiles();
			for(int i=0;i<dataDirs.length;i++){
				addTask(dataDirs[i],granularity,reader);
			}
		}
	}
	
	private void addTask(File file,int granularity,nodePairReader reader){
		String ip=file.getName().substring(0, file.getName().lastIndexOf("."));
		parseFile(file,reader);
		TaskCombination taskCombination=new TaskCombination();
		taskCombination.getTasks().add(
				generateTask(file,granularity,MiningMethod.MiningMethods_SimilarityMining));
		taskCombination.getTasks().add(
				generateTask(file,granularity,MiningMethod.MiningMethods_FrequenceItemMining));
		HashMap<String, HashMap<String, DataItems>> ipProtocolItems=null;
		ipProtocolItems=pretreatment(granularity,eachProtocolItems);
		taskCombination.setEachIpProtocolItems(ipProtocolItems);
		taskCombination.setMiningObject(miningObject.toString());
		taskCombination.setRange(ip);
		taskCombination.setName();
		taskCombination.setMinerType(MinerType.MiningType_ProtocolAssociation);
		TaskElement.add1Task(taskCombination, false);
		eachProtocolItems.clear();
	}

	private void parseFile(File dataFile,nodePairReader reader){
		String ip=dataFile.getName().substring(0, dataFile.getName().lastIndexOf("."));
		HashMap<String, DataItems> rawDataItems=
						reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
		eachProtocolItems.put(ip, rawDataItems);
	}
	
	private TaskElement generateTask(File file,int granularity,MiningMethod method){
		String ip=file.getName().substring(0, file.getName().lastIndexOf("."));
		TaskElement task=new TaskElement();
		task.setMiningMethod(method);
		task.setGranularity(granularity);
		task.setRange(ip);
		task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		task.setDiscreteMethod(DiscreteMethod.None);
		String name;
		switch (method) {
		case MiningMethods_FrequenceItemMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_LineProtocolASS);
			name=ip+"_多元时间序列挖掘"+miningObject.toString()+"_auto";
			task.setTaskName(name);
			task.setComments("挖掘  ip为"+ip+" 序列上"+miningObject.toString()+"的多元关联规律");
			break;
		case MiningMethods_SimilarityMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_RtreeProtocolASS);
			name=ip+"_时间序列相似度挖掘"+miningObject.toString()+"_auto";
			task.setTaskName(name);
			task.setComments("挖掘  ip为"+ip+" 序列上"+miningObject.toString()+"的相似度挖掘");
			break;
		default:
			break;
		}
		return task;
	}
	
	private HashMap<String, HashMap<String, DataItems>> pretreatment(int granularity,
			HashMap<String, HashMap<String, DataItems>> protocolItems){
		HashMap<String, HashMap<String, DataItems>> pretreatmentMap=
				new HashMap<String, HashMap<String,DataItems>>();
		Iterator<Entry<String, HashMap<String, DataItems>>>iterator=
				eachProtocolItems.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, HashMap<String, DataItems>> entry=iterator.next();
			HashMap<String, DataItems> itemMap=new HashMap<String, DataItems>();
			Iterator<Entry<String, DataItems>> itemIterator=entry.getValue().entrySet().iterator();
			while(itemIterator.hasNext()){
				Entry<String, DataItems> itemEntry=itemIterator.next();
				DataItems dataItems=itemEntry.getValue();
				dataItems=DataPretreatment.aggregateData(dataItems, granularity, 
						AggregateMethod.Aggregate_SUM, false);
				itemMap.put(itemEntry.getKey(), dataItems);
			}
			pretreatmentMap.put(entry.getKey(), itemMap);
		}
		protocolItems.clear();
		return pretreatmentMap;
	}
}
