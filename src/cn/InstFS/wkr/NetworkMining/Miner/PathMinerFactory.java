package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;
import org.apache.commons.math3.analysis.function.Min;

public class PathMinerFactory extends MinerFactorySettings{
	private static PathMinerFactory inst;
	public static boolean isMining=false;
	public String dataPath="E:\\parsePcap\\route\\10.0.13.2_10.0.2.2.csv";
	
	private MiningObject miningObject;
	private TaskRange taskRange = TaskRange.NodePairRange;
	private MiningMethod method;
	
	private PathMinerFactory(){
		super();
		List<MiningObject> miningObjectList = this.getMiningObjectList();
		miningObjectList.add(MiningObject.MiningObject_Times);
		miningObjectList.add(MiningObject.MiningObject_Traffic);

		List<MiningObject> miningObjectCheck = this.getMiningObjectsChecked();
		miningObjectCheck.add(MiningObject.MiningObject_Times);
		miningObjectCheck.add(MiningObject.MiningObject_Traffic);
	}
	public static PathMinerFactory getInstance(){
		if(inst==null){
			isMining=false;
			inst=new PathMinerFactory();
		}
		return inst;
	}
	
/*	public void minerPathPeriod(){
		if(isMining)
			return;
		isMining=true;
		
        File dicFile=new File(dataPath);
        if(dicFile.isFile()){
        	generateTask(dicFile);
        }else{
        	File[] files=dicFile.listFiles();
        	for(File file:files){
        		generateTask(file);
        	}
        }
	}*/
	public void reset(){
	isMining=false;
}
	
	public void detect(){
		if(isMining)
			return;
		isMining=true;
		
		File dataDirectory=new File(dataPath);
		nodePairReader reader=new nodePairReader();
		if(dataDirectory.isFile()){
			parseFile(dataDirectory,reader);
		}else{
			File[] dataDirs=dataDirectory.listFiles();
			for(int i=0;i<dataDirs.length;i++){
				parseFile(dataDirs[i],reader);
			}
		}
	}
	
	private void parseFile(File dataFile, nodePairReader reader){
		Map<String, DataItems> dataMap = new HashMap<String, DataItems>();
		DataItems di = new DataItems();
		switch(miningObject){
		case MiningObject_Traffic:
		case MiningObject_Times:
			dataMap = reader.readPath(dataFile.getAbsolutePath(), miningObject.toString());
			break;
		default:
			break;
		}
		
		Iterator<Entry<String, DataItems>> iterator=dataMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, DataItems> entry=iterator.next();
			di = entry.getValue();
		}
		
		TaskCombination taskCombination = new TaskCombination();
		taskCombination.getTasks().add(generateTask(dataFile,TaskRange.NodePairRange,MiningMethod.MiningMethods_Statistics));
		taskCombination.getTasks().add(generateTask(dataFile,TaskRange.NodePairRange,MiningMethod.MiningMethods_PeriodicityMining));
		taskCombination.getTasks().add(generateTask(dataFile,TaskRange.NodePairRange,MiningMethod.MiningMethods_OutliesMining));
		taskCombination.setDataItems(di);
		taskCombination.setTaskRange(TaskRange.NodePairRange);
		taskCombination.setRange(dataFile.getName().substring(0, dataFile.getName().lastIndexOf(".")));
		taskCombination.setMinerType(MinerType.MiningType_Path);
		taskCombination.setMiningObject(miningObject.toString());
		taskCombination.setName(MinerType.MiningType_Path);
		TaskElement.add1Task(taskCombination, false);
	}
	
	private TaskElement generateTask(File file, TaskRange taskRange, MiningMethod method){
		String fileName = file.getName();
		
		TaskElement task = new TaskElement();
		task.setDataSource("File");
		task.setSourcePath(file.getAbsolutePath());
		task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		task.setDiscreteMethod(DiscreteMethod.None);
		task.setMiningMethod(method);
		task.setTaskRange(taskRange);
		task.setRange(fileName.substring(0, fileName.lastIndexOf(".")));
		
		String taskName = null;
		switch (method) {
		case MiningMethods_PeriodicityMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
			taskName = fileName + "_路径_"+miningObject.toString()+"_周期挖掘_auto";
			task.setTaskName(taskName);
			task.setComments("ip为"+file.getName()+"的路径"+miningObject.toString()+"周期规律挖掘");
			break;
		case MiningMethods_OutliesMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_TEOTSA);
			taskName = fileName + "_路径_" + miningObject.toString() + "_异常检测_auto";
			task.setTaskName(taskName);
			task.setComments("ip为"+file.getName()+"的路径"+ miningObject.toString()+"异常检测");
			break;
		case MiningMethods_Statistics:
			taskName = fileName + "路径" + miningObject.toString() + "_统计_auto";
			task.setTaskName(taskName);
			task.setComments("ip为"+file.getName()+"的路径"+ miningObject.toString()+"统计");
			break;
		default:
			break;
		}
		task.setMiningObject(miningObject.toString());
		
		return task;
	}
	
/*	private void generateTask(File file){
		String fileName=file.getName();
		
		TaskElement task=new TaskElement();
		task.setSourcePath(file.getAbsolutePath());
		task.setTaskName(file.getName()+"_路径挖掘_auto");
		task.setComments("ip为"+file.getName()+"的路径规律挖掘");
		task.setDataSource("File");
		task.setRange(fileName.substring(0, fileName.lastIndexOf(".")));
		task.setGranularity(3600);
		task.setMiningObject("allpath");
		task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		task.setDiscreteMethod(DiscreteMethod.None);
		task.setTaskRange(TaskRange.NodePairRange);
		task.setMiningMethod(MiningMethod.MiningMethods_PathProbilityMining);
		task.setSourcePath(file.getAbsolutePath());
		TaskElement.add1Task(task, false);
	}*/
	
	public MiningMethod getMethod() {
		return method;
	}
	public void setMethod(MiningMethod method) {
		this.method = method;
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
	public String getDataPath() {
		return dataPath;
	}
	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}
	
	public static void main(String[] args) {
		NetworkMinerFactory.getInstance();
		PathMinerFactory pathFactory=PathMinerFactory.getInstance();
		pathFactory.setMiningObject(MiningObject.MiningObject_Traffic);
		pathFactory.detect();
		NetworkMinerFactory.getInstance().startAllPathMiners(MiningObject.MiningObject_Traffic);
	}
}
