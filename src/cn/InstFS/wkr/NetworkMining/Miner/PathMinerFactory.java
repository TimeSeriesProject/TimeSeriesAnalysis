package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class PathMinerFactory {
	private static PathMinerFactory inst;
	public static boolean isMining=false;
	public String dataPath="F:\\parsePcap\\route";
	private PathMinerFactory(){}
	public static PathMinerFactory getInstance(){
		if(inst==null){
			isMining=false;
			inst=new PathMinerFactory();
		}
		return inst;
	}
	public void minerPathPeriod(){
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
	}
	
	private void generateTask(File file){
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
	}
}
