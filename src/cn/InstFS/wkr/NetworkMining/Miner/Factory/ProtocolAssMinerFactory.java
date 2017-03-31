package cn.InstFS.wkr.NetworkMining.Miner.Factory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AlgorithmsChooser;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AlgorithmsManager;
import common.Logger;
import weka.gui.beans.Startable;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;

public class ProtocolAssMinerFactory extends MinerFactorySettings {
	private static ProtocolAssMinerFactory inst;
	public static boolean isMining=false;
	public String dataPath;
	private MiningObject miningObject;
	private TaskRange taskRange = TaskRange.SingleNodeRange;
	public static HashMap<String, HashMap<String, DataItems>> eachProtocolItems;
	public static Map<String,DataItems> rawDataList;
	ProtocolAssMinerFactory(){
		super(MinerType.MiningType_ProtocolAssociation.toString());
		dataPath = GlobalConfig.getInstance().getDataPath();

		List<MiningObject> miningObjectList = this.getMiningObjectList();
		miningObjectList.add(MiningObject.MiningObject_Traffic);

		List<MiningObject> miningObjectCheck = this.getMiningObjectsChecked();
		miningObjectCheck.addAll(miningObjectList);

		eachProtocolItems= new HashMap<String, HashMap<String,DataItems>>();
		rawDataList = new HashMap<String,DataItems>();
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

	public TaskRange getTaskRange() {
		return taskRange;
	}

	public void setTaskRange(TaskRange taskRange) {
		this.taskRange = taskRange;
	}

	public MiningObject getMiningObject() {
		return miningObject;
	}

	public void setMiningObject(MiningObject miningObject) {
		this.miningObject = miningObject;
	}

	public void reset(){
		isMining=false;
	}
	
	public void detect(){
		File dataDirectory=new File(dataPath + "\\traffic");

		Logger.log("挖掘类型", getMinerType());
		Logger.log("挖掘对象", miningObject.toString());
		Logger.log("源数据读取目录", dataDirectory.getPath());
		Logger.log("数据读取起止时间", getStartDate().toString() + "--" + getEndDate().toString());
		Logger.log("数据处理时间粒度", getGranularity()+"s");
		Logger.log("源数据读取开始");
		nodePairReader reader=new nodePairReader();
		int granularity= Integer.parseInt(getGranularity());
		if(dataDirectory.isFile()){
//			addTask(dataDirectory,granularity,reader);
		}else{
			File[] dataDirs=dataDirectory.listFiles();
			for(int i=0;i<dataDirs.length;i++){
				//由于数据都存在ip文件夹下，所以应以文件夹目录作为参数进行传递
				//按天读取文件，所以只接受目录
				if(dataDirs[i].isDirectory())
					addTask(dataDirs[i].getAbsoluteFile(),granularity,reader);
			}
		}
		addIpPairTask(granularity,reader);
	}
	
	private void addIpPairTask(int granularity, nodePairReader reader) {
		
		Set<String> set = new HashSet<String>();
		Iterator<String> iter_i = rawDataList.keySet().iterator();
		while(iter_i.hasNext()) {
			
			String ip_i = iter_i.next();
			set.add(ip_i);
			if(rawDataList.get(ip_i).data.size() == 0)
				continue;
			Iterator<String> iter_j = rawDataList.keySet().iterator();
			while(iter_j.hasNext()) {
				
				String ip_j = iter_j.next();
				if(set.contains(ip_j))
					continue;
				if(rawDataList.get(ip_j).data.size() == 0)
					continue;
				TaskCombination taskCombination = new TaskCombination();
				taskCombination.getTasks().add(
						generateIpPairTask(granularity,MiningMethod.MiningMethods_SimilarityMining,ip_i,ip_j));
				taskCombination.getTasks().add(
						generateIpPairTask(granularity,MiningMethod.MiningMethods_FrequenceItemMining,ip_i,ip_j));
				HashMap<String, HashMap<String, DataItems>> ipPairItems = new HashMap<String, HashMap<String, DataItems>>();
				HashMap<String, DataItems> dataIpPair = new HashMap<String, DataItems>();
				dataIpPair.put(ip_i, rawDataList.get(ip_i));
				dataIpPair.put(ip_j, rawDataList.get(ip_j));
				ipPairItems.put(ip_i+"_"+ip_j, dataIpPair);
				taskCombination.setEachIpProtocolItems(ipPairItems);
				
				taskCombination.setMiningObject(miningObject.toString());
				taskCombination.setRange(ip_i+"_"+ip_j);
				taskCombination.setName();
				taskCombination.setMinerType(MinerType.MiningType_ProtocolAssociation);
				TaskElement.add1Task(taskCombination, false);
				Logger.log("添加TaskCombination", taskCombination.getName());
			}
		}
		rawDataList.clear();
	}

	private TaskElement generateIpPairTask(int granularity,
			MiningMethod method, String ip_i, String ip_j) {
		String ip = ip_i +"_"+ ip_j;
		TaskElement task = new TaskElement();
		task.setMiningMethod(method);
		task.setGranularity(granularity);
		task.setRange(ip);
		task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		task.setDiscreteMethod(DiscreteMethod.None);

		AlgorithmsChooser chooser = AlgorithmsManager.getInstance().getAlgoChooserFromManager(MinerType.MiningType_ProtocolAssociation, taskRange);
		String name;
		switch (method) {
		case MiningMethods_FrequenceItemMining:
			task.setMiningAlgo(chooser.getProAssAlgo());
			name = ip+"之间关联规则挖掘";
			task.setTaskName(name);
			task.setComments(ip+"之间关于 "+miningObject.toString()+" 的多元关联规律");
			break;
		case MiningMethods_SimilarityMining:
			task.setMiningAlgo(chooser.getSimAlgo());
			name = ip+"之间关联规则挖掘";
			task.setTaskName(name);
			task.setComments(ip+"之间关于 "+miningObject.toString()+" 的相似度挖掘");
			break;
		default:
			break;
		}
		return task;
	}

	private void addTask(File file,int granularity,nodePairReader reader){
		String ip=file.getName();//.substring(0, file.getName().lastIndexOf("."));
		System.out.println("ip:"+ip);
		parseFile(file.getAbsoluteFile(),reader);
		Logger.log("生成挖掘任务集合");
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
		Logger.log("添加TaskCombination", taskCombination.getName());
		eachProtocolItems.clear();
	}

	private void parseFile(File dataFile,nodePairReader reader){
		//文件按天存取，dataFile对应的是ip
		String ip=dataFile.getName();//.substring(0, dataFile.getName().lastIndexOf("."));
//		HashMap<String, DataItems> rawDataItems=
//						reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
		Date date1 = getStartDate();
		Date date2 = getEndDate();
		Logger.log("源数据读取子目录", dataFile.getPath());
		HashMap<String, DataItems> rawDataItems = 
				reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath(), true, date1, date2, 3600);
		eachProtocolItems.put(ip, rawDataItems);

		DataItems ipData = reader.readIpSumTraffic(dataFile.getAbsolutePath(), true, date1, date2, 3600);
		rawDataList.put(ip, ipData);
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

		AlgorithmsChooser chooser = AlgorithmsManager.getInstance().getAlgoChooserFromManager(MinerType.MiningType_ProtocolAssociation, taskRange);

		switch (method) {
		case MiningMethods_FrequenceItemMining:
			task.setMiningAlgo(chooser.getProAssAlgo());
			name=ip+"_多元时间序列挖掘"+miningObject.toString()+"_auto";
			task.setTaskName(name);
			task.setComments("挖掘  ip为"+ip+" 序列上"+miningObject.toString()+"的多元关联规律");
			break;
		case MiningMethods_SimilarityMining:
			task.setMiningAlgo(chooser.getSimAlgo());
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
