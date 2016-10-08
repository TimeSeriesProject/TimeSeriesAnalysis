package cn.InstFS.wkr.NetworkMining.Miner.Factory;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
	
	ProtocolAssMinerFactory(){
		super(MinerType.MiningType_ProtocolAssociation.toString());
		dataPath = GlobalConfig.getInstance().getDataPath() + "\\traffic";

		List<MiningObject> miningObjectList = this.getMiningObjectList();
		miningObjectList.add(MiningObject.MiningObject_Traffic);

		List<MiningObject> miningObjectCheck = this.getMiningObjectsChecked();
		miningObjectCheck.addAll(miningObjectList);

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
		File dataDirectory=new File(dataPath);
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
	}
	
	private void addTask(File file,int granularity,nodePairReader reader){
		String ip=file.getName();//.substring(0, file.getName().lastIndexOf("."));
		System.out.println("ip:"+ip);
		parseFile(file.getAbsoluteFile(),reader);
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
		//文件按天存取，dataFile对应的是ip
		String ip=dataFile.getName();//.substring(0, dataFile.getName().lastIndexOf("."));
//		HashMap<String, DataItems> rawDataItems=
//						reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
		Date date1 = getStartDate();
		Date date2 = getEndDate();
		HashMap<String, DataItems> rawDataItems = 
				reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath(), false, date1, date2, 3600);
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
